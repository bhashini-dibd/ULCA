package com.ulca.dataset.download.kafka.listener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Arrays;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.ulca.dataset.download.constants.DatasetDownloadConstants;
import com.ulca.dataset.dao.DatasetKafkaTransactionErrorLogDao;
import com.ulca.dataset.dao.FileIdentifierDao;
import com.ulca.dataset.dao.TaskTrackerDao;
import com.ulca.dataset.service.DatasetErrorPublishService;
import com.ulca.dataset.service.NotificationService;
import com.ulca.dataset.kakfa.model.DatasetIngest;
import com.ulca.dataset.kakfa.model.FileDownload;
import com.ulca.dataset.model.DatasetKafkaTransactionErrorLog;
import com.ulca.dataset.model.Error;
import com.ulca.dataset.model.ProcessTracker.StatusEnum;
import com.ulca.dataset.model.TaskTracker;
import com.ulca.dataset.model.TaskTracker.ToolEnum;
import com.ulca.dataset.service.ProcessTaskTrackerService;
import com.ulca.dataset.util.UnzipUtility;

import io.swagger.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Service
public class KafkaFileDownloadConsumer {

	@Autowired
	UnzipUtility unzipUtility;

	@Autowired
	ProcessTaskTrackerService processTaskTrackerService;
	
	
	@Autowired
	DatasetErrorPublishService datasetErrorPublishService;

	@Value("${file.download.folder}")
    private String downloadFolder;
	
	@Autowired
	TaskTrackerDao taskTrackerDao;
	
	@Autowired
	FileIdentifierDao fileIdentifierDao;
	

	@Autowired
	DatasetKafkaTransactionErrorLogDao datasetKafkaTransactionErrorLogDao;
	
	@Autowired
	private KafkaTemplate<String, DatasetIngest> datasetIngestKafkaTemplate;

	@Value("${kafka.ulca.ds.ingest.ip.topic}")
	private String datasetIngestTopic;
	
	@Autowired
	NotificationService notificationService;
																																						
	@KafkaListener(groupId = "${kafka.ulca.ds.filedownload.ip.topic.group.id}", topics = "${kafka.ulca.ds.filedownload.ip.topic}" , containerFactory = "filedownloadKafkaListenerContainerFactory")
	public void downloadFile(FileDownload file) {

		String datasetId = file.getDatasetId();
		String fileUrl = file.getFileUrl();
		String serviceRequestNumber = file.getServiceRequestNumber();
		String datasetName = file.getDatasetName();
		//DatasetType datasetType = file.getDatasetType();
		DatasetType datasetType = null;
		String userId = file.getUserId();
		
		DatasetIngest datasetIngest = new DatasetIngest();
		
		
		Map<String,String> fileMap = null;
		
		try {
			log.info("************ Entry KafkaFileDownloadConsumer :: downloadFile *********");
			log.info("datasetId :: " + datasetId);
			log.info("fileUrl :: " + fileUrl);
			log.info("serviceRequestNumber :: " + serviceRequestNumber);
			
			List<TaskTracker> list = taskTrackerDao.findAllByServiceRequestNumber(serviceRequestNumber);
			if(list.size() > 0) {
				log.info("duplicated processing of serviceRequestNumber :: " + serviceRequestNumber);
				return;
			}
			processTaskTrackerService.updateProcessTracker(serviceRequestNumber, StatusEnum.inprogress);
			processTaskTrackerService.createTaskTracker(serviceRequestNumber, ToolEnum.download, com.ulca.dataset.model.TaskTracker.StatusEnum.inprogress);
			
			try {
				
				String fileName = serviceRequestNumber+".zip";
				String filePath = downloadUsingNIO(fileUrl, downloadFolder,fileName);
				
				log.info("file download complete");
				log.info("file path in downloadFile servide ::" + filePath);
				
				String md5hash = downloadFileSanityCheck(filePath);
				
				fileMap = unzipUtility.unzip(filePath, downloadFolder, serviceRequestNumber);
				
				datasetType = getDatasetType(fileMap);
				
				log.info("file unzip complete");
				processTaskTrackerService.updateTaskTracker(serviceRequestNumber, ToolEnum.download, com.ulca.dataset.model.TaskTracker.StatusEnum.completed);
				
				datasetIngest.setDatasetId(datasetId);
				datasetIngest.setServiceRequestNumber(serviceRequestNumber);
				datasetIngest.setDatasetName(datasetName);
				datasetIngest.setBaseLocation(fileMap.get("baseLocation"));
				datasetIngest.setMd5hash(md5hash);
				datasetIngest.setDatasetType(datasetType);
				datasetIngest.setUserId(userId);
				
				datasetIngest.setMode(DatasetDownloadConstants.INGEST_PRECHECK_MODE);
				
				

			} catch (IOException e) {
				
				//update error
				Error error = new Error();
				error.setCause(e.getMessage());
				error.setMessage("file download failed");
				error.setCode("1000_FILE_DOWNLOAD_FAILURE");
				processTaskTrackerService.updateTaskTrackerWithErrorAndEndTime(serviceRequestNumber, ToolEnum.download, com.ulca.dataset.model.TaskTracker.StatusEnum.failed, error);
				processTaskTrackerService.updateProcessTracker(serviceRequestNumber, StatusEnum.failed);
				
				//send error event for download failure
				datasetErrorPublishService.publishDatasetError("dataset-training", "1000_FILE_DOWNLOAD_FAILURE", e.getMessage(), serviceRequestNumber, datasetName,"download" , null, null) ;
				
				notificationService.notifyDatasetFailed(serviceRequestNumber, datasetName, userId);
				e.printStackTrace();
				
				return;
			}
			
			
			//datasetIngestKafkaTemplate.send(datasetIngestTopic, datasetIngest);
			
			try {
				
				 ListenableFuture<SendResult<String, DatasetIngest>> future = datasetIngestKafkaTemplate.send(datasetIngestTopic, datasetIngest);
					
					 future.addCallback(new ListenableFutureCallback<SendResult<String, DatasetIngest>>() {

						    public void onSuccess(SendResult<String, DatasetIngest> result) {
						    	log.info("message sent successfully to datasetIngestTopic, serviceRequestNumber :: "+ serviceRequestNumber);
						    }

						    @Override
						    public void onFailure(Throwable ex) {
						    	log.info("Error occured while sending message to datasetIngestTopic, serviceRequestNumber :: "+ serviceRequestNumber);
						    	log.info("Error message :: " + ex.getMessage());
						    	
						    	DatasetKafkaTransactionErrorLog error = new DatasetKafkaTransactionErrorLog();
						    	error.setServiceRequestNumber(serviceRequestNumber);
						    	error.setAttempt(0);
						    	error.setCreatedOn(new Date().toString());
						    	error.setLastModifiedOn(new Date().toString());
						    	error.setFailed(false);
						    	error.setSuccess(false);
						    	error.setStage("ingest");
						    	List<String> er = new ArrayList<String>();
						    	er.add(ex.getMessage());
						    	error.setErrors(er);
						    	ObjectMapper mapper = new ObjectMapper();
							
									String dataRow;
									try {
										dataRow = mapper.writeValueAsString(datasetIngest);
								    	error.setData(dataRow);
								    	datasetKafkaTransactionErrorLogDao.save(error);
								    	
									} catch (JsonProcessingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
						    	
						    }
						});
					 
			}catch ( KafkaException ex) {
				log.info("Error occured while sending message to datasetIngestTopic, serviceRequestNumber :: "+serviceRequestNumber);
				log.info("Error message :: " + ex.getMessage());
				DatasetKafkaTransactionErrorLog error = new DatasetKafkaTransactionErrorLog();
		    	error.setServiceRequestNumber(serviceRequestNumber);
		    	error.setAttempt(0);
		    	error.setCreatedOn(new Date().toString());
		    	error.setLastModifiedOn(new Date().toString());
		    	error.setFailed(false);
		    	error.setSuccess(false);
		    	error.setStage("ingest");
		    	List<String> er = new ArrayList<String>();
		    	er.add(ex.getMessage());
		    	error.setErrors(er);
		    	ObjectMapper mapper = new ObjectMapper();
			
					String dataRow;
					try {
						dataRow = mapper.writeValueAsString(datasetIngest);
				    	error.setData(dataRow);
				    	datasetKafkaTransactionErrorLogDao.save(error);
				    	
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				throw ex;
			}

			//datasetIngestKafkaTemplate.send(datasetIngestTopic,0,null, datasetIngest);
			
			log.info("************ Exit KafkaFileDownloadConsumer :: downloadFile *********");
			
		}catch (Exception e) {
			log.info("Unhadled Exception :: " + e.getMessage());
			log.info("cause :: " + e.getClass());
			e.printStackTrace();
		}
	}

	private String downloadUsingNIO(String urlStr, String downloadFolder, String fileName) throws IOException {
		log.info("************ Entry KafkaFileDownloadConsumer :: downloadUsingNIO *********");
		URL url = new URL(urlStr);
		String file = downloadFolder +"/"+ fileName;
		log.info("file path indownloadUsingNIO" );
		log.info(file);
		log.info(url.getPath());
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		log.info(url.getContent().toString());
		log.info(rbc.getClass().toString());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();

		log.info("************ Exit KafkaFileDownloadConsumer :: downloadUsingNIO *********");
		return file;
	}
	
	private String downloadFileSanityCheck(String filePath) throws IOException {
		File f = new File(filePath);
		
		//check if file is executable
		/*if(f.canExecute()) {
			throw new IOException("Executable File Not allowed");
			
		}*/
		//check if md5hash of file exist
		String md5hash = fileMD5hash(filePath);
		
		
		return md5hash;
	}
	
	private String fileMD5hash(String filePath) throws IOException {
		
		 HashCode hash = null;
		 String myChecksum  = null;
		try {
			hash = com.google.common.io.Files
				      .hash(new File(filePath), Hashing.md5());
			
			
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			 
		if( hash != null) {
			
			 myChecksum = hash.toString()
				      .toUpperCase();
			 /*
			 Fileidentifier  fileIdentifier = fileIdentifierDao.findByMd5hash(myChecksum);
			 if(fileIdentifier != null) {
				 throw new IOException("Same File Already exists in system");
			 }*/
			 return myChecksum;
		}
		
		return myChecksum;
	}
	
	private DatasetType getDatasetType(Map<String,String> fileMap) throws IOException {
		DatasetType datasetType = null;
		String paramsFilePath = fileMap.get("baseLocation")  + File.separator + "params.json";
		Object rowObj = new Gson().fromJson(new FileReader(paramsFilePath), Object.class);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String dataRow = mapper.writeValueAsString(rowObj);
			JSONObject params =  new JSONObject(dataRow);
			
			if(params.isEmpty() || !params.has("datasetType")) {
				 throw new IOException("params.json does not contain datasetType");
			}
			String type = params.get("datasetType").toString();
			if (type.equalsIgnoreCase("null") ||  type.isBlank()){
				throw new IOException("datasetType value should not be null or empty");
			}

			datasetType = DatasetType.fromValue(type);

			if(datasetType==null){
				throw new IOException("Invalid datasetType");
			}

			if (params.has("languages") && !params.isNull("languages")) {
				JSONObject languages = params.getJSONObject("languages");
				String sourceLanguage = languages.isNull("sourceLanguage") ? null
						: languages.getString("sourceLanguage");
				log.info("sourceLanguage :: " + sourceLanguage);
				String sourceScriptCode = languages.isNull("sourceScriptCode") ? null
						: languages.getString("sourceScriptCode");
				log.info("sourceScriptCode :: " + sourceScriptCode);
				SupportedLanguages langMatched = null;
				if (sourceLanguage != null && !StringUtils.isEmpty(sourceLanguage)) {
					langMatched = SupportedLanguages.fromValue(sourceLanguage);
					if (langMatched != null) {
						validateScript(sourceLanguage, sourceScriptCode);
						String targetLanguage = languages.isNull("targetLanguage") ? null
								: languages.getString("targetLanguage");
						log.info("targetLanguage :: " + targetLanguage);
						if (targetLanguage != null && !StringUtils.isEmpty(targetLanguage)) {
							langMatched = SupportedLanguages.fromValue(targetLanguage);
							if (langMatched != null) {
								String targetScriptCode = languages.isNull("targetScriptCode") ? null
										: languages.getString("targetScriptCode");
								log.info("targetScriptCode :: " + targetScriptCode);
								validateScript(targetLanguage, targetScriptCode);
							} else {
								throw new IOException("Invalid targetLanguage !");
							}
						}
					} else {
						throw new IOException("Invalid sourceLanguage !");
					}
				} else {
					throw new IOException(" sourceLanguage value should not be null or empty");
				}

			} else {
				throw new IOException("languages value should not be null or empty");
			}

			return datasetType;

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException("params.json not valid");

		}

	}

	public boolean validateScript(String lang, String script) throws IOException {
		String multiScriptSupportedLangsArr[] = { "ks", "mni", "doi" };
		ArrayList<String> multiScriptSupportedLangsArrayList = new ArrayList<>(
				Arrays.asList(multiScriptSupportedLangsArr));
		if (multiScriptSupportedLangsArrayList.contains(lang)) {
			if (script != null && !StringUtils.isEmpty(script)) {
				SupportedScripts scriptMatched = SupportedScripts.fromValue(script);
				if (scriptMatched != null) {
					if (lang.equals("ks")) {
						String kashmiriScripts[] = { "Deva", "Arab" };
						ArrayList<String> kashmiriScriptsList = new ArrayList<>(Arrays.asList(kashmiriScripts));
						if (!kashmiriScriptsList.contains(script)) {
							throw new IOException(script + " is not valid script code for Kashmiri!");

						}

					}
					if (lang.equals("mni")) {
						String manipuriScripts[] = { "Beng", "Mtei" };
						ArrayList<String> manipuriScriptsList = new ArrayList<>(Arrays.asList(manipuriScripts));
						if (!manipuriScriptsList.contains(script)) {
							throw new IOException(script + " is not valid script code for Manipuri!");
						}
					}
					if (lang.equals("doi")) {
						String dogriScripts[] = { "Dogr", "Arab", "Deva" };
						ArrayList<String> dogriScriptsList = new ArrayList<>(Arrays.asList(dogriScripts));
						if (!dogriScriptsList.contains(script)) {
							throw new IOException(script + " is not valid script code for Dogri!");
						}
					}
				} else {
					throw new IOException("Invalid scriptCode : " + script + " !");
				}
			} else {
				throw new IOException("scriptCode value should not be null or empty for : " + lang + " language !");

			}
		}
		return true;
	}

}
