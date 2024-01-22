package com.ulca.dataset.service;

import java.time.Instant;
import java.util.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulca.dataset.constants.DatasetConstants;
import com.ulca.dataset.dao.DatasetDao;
import com.ulca.dataset.dao.DatasetKafkaTransactionErrorLogDao;
import com.ulca.dataset.dao.FileIdentifierDao;
import com.ulca.dataset.dao.ProcessTrackerDao;
import com.ulca.dataset.dao.TaskTrackerDao;
import com.ulca.dataset.exception.ServiceRequestNumberNotFoundException;
import com.ulca.dataset.kakfa.model.FileDownload;
import com.ulca.dataset.model.Dataset;
import com.ulca.dataset.model.DatasetKafkaTransactionErrorLog;
import com.ulca.dataset.model.Fileidentifier;
import com.ulca.dataset.model.ProcessTracker;
import com.ulca.dataset.model.ProcessTracker.ServiceRequestActionEnum;
import com.ulca.dataset.model.ProcessTracker.ServiceRequestTypeEnum;
import com.ulca.dataset.model.ProcessTracker.StatusEnum;
import com.ulca.dataset.model.TaskTracker;
import com.ulca.dataset.model.TaskTracker.ToolEnum;
import com.ulca.dataset.request.DatasetCorpusSearchRequest;
import com.ulca.dataset.request.DatasetSubmitRequest;
import com.ulca.dataset.response.DatasetByIdResponse;
import com.ulca.dataset.response.DatasetByServiceReqNrResponse;
import com.ulca.dataset.response.DatasetCorpusSearchResponse;
import com.ulca.dataset.response.DatasetListByUserIdResponse;
import com.ulca.dataset.response.DatasetListByUserIdResponseDto;
import com.ulca.dataset.response.DatasetSearchStatusResponse;
import com.ulca.dataset.response.DatasetSubmitResponse;
import com.ulca.dataset.response.SearchListByUserIdResponse;
import com.ulca.dataset.response.SearchListByUserIdResponseDto;
import com.ulca.dataset.util.Utility;

import io.swagger.model.DatasetType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DatasetService {

	private int  PAGE_SIZE = 10;
	
	@Autowired
	DatasetDao datasetDao;
	
	@Autowired
	FileIdentifierDao fileIdentifierDao;

	@Autowired
	ProcessTrackerDao processTrackerDao;
	
	@Autowired
	DatasetKafkaTransactionErrorLogDao datasetKafkaTransactionErrorLogDao;


	@Autowired
	KafkaService kafkaService;

	@Autowired
	TaskTrackerDao taskTrackerDao;

	@Autowired
	SearchKafkaPublishService searchKafkaPublish;

	@Autowired
	private KafkaTemplate<String, FileDownload> datasetFiledownloadKafkaTemplate;

	@Value("${kafka.ulca.ds.filedownload.ip.topic}")
	private String fileDownloadTopic;

	@Transactional
	public DatasetSubmitResponse datasetSubmit(DatasetSubmitRequest request) {

		String userId = request.getUserId();
		Dataset dataset = new Dataset();
		dataset.setDatasetName(request.getDatasetName());
		dataset.setSubmitterId(userId);
		//dataset.setDatasetType(request.getType().toString());
		dataset.setCreatedOn(Instant.now().toEpochMilli());

		Fileidentifier fileIndetifier = new Fileidentifier();
		fileIndetifier.setFileLocationURL(request.getUrl());

		fileIndetifier.setCreatedOn(new Date().toString());
		fileIdentifierDao.insert(fileIndetifier);

		dataset.setDatasetFileIdentifier(fileIndetifier);

		try {
			datasetDao.insert(dataset);
		} catch(DuplicateKeyException ex) {
			
			throw new DuplicateKeyException(DatasetConstants.datasetNameUniqueErrorMsg);
			
		}

		ProcessTracker processTracker = new ProcessTracker();
		processTracker.setUserId(userId);
		processTracker.setDatasetId(dataset.getDatasetId());
		processTracker.setServiceRequestNumber(Utility.getDatasetSubmitReferenceNumber());
		processTracker.setServiceRequestAction(ServiceRequestActionEnum.submit);
		processTracker.setServiceRequestType(ServiceRequestTypeEnum.dataset);
		processTracker.setStatus(StatusEnum.pending.toString());
		processTracker.setStartTime(Instant.now().toEpochMilli());

		processTrackerDao.insert(processTracker);

		FileDownload fileDownload = new FileDownload();
		fileDownload.setUserId(userId);
		fileDownload.setDatasetId(dataset.getDatasetId());
		fileDownload.setDatasetName(dataset.getDatasetName());
		//fileDownload.setDatasetType(request.getType());
		fileDownload.setFileUrl(request.getUrl());
		fileDownload.setServiceRequestNumber(processTracker.getServiceRequestNumber());
		
		//datasetFiledownloadKafkaTemplate.send(fileDownloadTopic, fileDownload);


		kafkaService.datasetFiledownload(fileDownloadTopic, fileDownload,processTracker);


		String message = "Dataset Submit success";
		return new DatasetSubmitResponse(message,processTracker.getServiceRequestNumber(), dataset.getDatasetId(),
				dataset.getCreatedOn());
	}

	public DatasetListByUserIdResponse datasetListByUserId(String userId, Integer startPage, Integer endPage,Integer pgSize,String name) {
		log.info("******** Entry DatasetService:: datasetListByUserId *******");
		DatasetListByUserIdResponse response = null;
		Integer count = datasetDao.countBySubmitterId(userId);
		log.info("Number of datatsets :: "+count);
		List<Dataset> list = new ArrayList<Dataset>();
		if (startPage != null) {
			int startPg = startPage - 1;
			for (int i = startPg; i < endPage; i++) {
				Pageable paging = null;
				if (pgSize != null) {
					paging = PageRequest.of(i, pgSize, Sort.by("createdOn").descending());
					log.info("paging :: "+paging);
				} else {
					paging = PageRequest.of(i, PAGE_SIZE, Sort.by("createdOn").descending());
					log.info("paging :: "+paging);

				}
				Page<Dataset> datasetList = null;
				if (name != null) {
					Dataset dataset = new Dataset();
					dataset.setSubmitterId(userId);
					dataset.setDatasetName(name);
					Example<Dataset> example = Example.of(dataset);
                    log.info("example :: "+example);
					datasetList = datasetDao.findAll(example, paging);
					log.info("datasetList :: "+datasetList);
					count = datasetDao.countBySubmitterIdAndDatasetName(userId, name);
					log.info("count :: "+count);
				} else {
					datasetList = datasetDao.findBySubmitterId(userId, paging);
					log.info("datasetList :: "+datasetList);

				}
				list.addAll(datasetList.toList());
			}
		} else {
			if (name != null) {
				Dataset dataset = new Dataset();
				dataset.setSubmitterId(userId);
				dataset.setDatasetName(name);
				Example<Dataset> example = Example.of(dataset);
				list = datasetDao.findAll(example);
				count = list.size();
			} else {
				list = datasetDao.findBySubmitterId(userId);

			}
		}
         
		log.info("list of datasets :: "+list);

		List<DatasetListByUserIdResponseDto> datasetDtoList = new ArrayList<DatasetListByUserIdResponseDto>();
		if(!list.isEmpty()) {
		for (Dataset dataset : list) {
			log.info("dataset name :: "+dataset.getDatasetName());
			ProcessTracker processTracker = processTrackerDao.findByDatasetId(dataset.getDatasetId()).get(0);
			log.info("processTracker :: "+processTracker);
			String serviceRequestNumber = processTracker.getServiceRequestNumber();
			log.info("serviceRequestNumber :: "+serviceRequestNumber);
			
			String status = processTracker.getStatus();
			log.info("status :: "+status);
			if(status.equalsIgnoreCase(TaskTracker.StatusEnum.failed.toString()) || status.equalsIgnoreCase(TaskTracker.StatusEnum.completed.toString())) {
				datasetDtoList.add(new DatasetListByUserIdResponseDto(dataset.getDatasetId(), serviceRequestNumber,
						dataset.getDatasetName(), dataset.getDatasetType(), dataset.getCreatedOn(), status));
			} else  {

				List<TaskTracker> taskTrackerList = taskTrackerDao
						.findAllByServiceRequestNumber(serviceRequestNumber);

				HashMap<String, String> map = new HashMap<String, String>();
				for (TaskTracker tTracker : taskTrackerList) {
					map.put(tTracker.getTool().toString(), tTracker.getStatus().toString());
				}
				if(map.containsValue(TaskTracker.StatusEnum.failed.toString())) {
					status = ProcessTracker.StatusEnum.failed.toString();
				}else if(map.containsValue(ProcessTracker.StatusEnum.inprogress.toString())) {
					status = ProcessTracker.StatusEnum.inprogress.toString();
				}else if (map.containsKey(TaskTracker.ToolEnum.publish.toString())) {
					status = map.get(TaskTracker.ToolEnum.publish.toString());
				}

				datasetDtoList.add(new DatasetListByUserIdResponseDto(dataset.getDatasetId(), serviceRequestNumber,
						dataset.getDatasetName(), dataset.getDatasetType(), dataset.getCreatedOn(), status));
			}

		}
	}
		log.info("******** Exit DatasetService:: datasetListByUserId *******");


		datasetDtoList.sort(Comparator.comparing(DatasetListByUserIdResponseDto::getSubmittedOn).reversed());
		return new DatasetListByUserIdResponse("Dataset List By userId", datasetDtoList, startPage, endPage, count);
	}
		public DatasetByIdResponse datasetById(String datasetId) {

		Map<String, ArrayList<TaskTracker>> map = new HashMap<String, ArrayList<TaskTracker>>();

		List<ProcessTracker> processTrackerList = processTrackerDao.findByDatasetId(datasetId);

		if (processTrackerList != null && processTrackerList.size() > 0) {

			for (ProcessTracker pt : processTrackerList) {

				String serviceRequestNumber = pt.getServiceRequestNumber();

				List<TaskTracker> taskTrackerList = taskTrackerDao.findAllByServiceRequestNumber(serviceRequestNumber);

				
				List<TaskTracker> taskTrackerListUpdated = getStatusUpdatedTaskTrackerList(taskTrackerList, serviceRequestNumber);
				map.put("details", (ArrayList<TaskTracker>) taskTrackerListUpdated);
			}

		}
		DatasetByIdResponse response = new DatasetByIdResponse("Dataset Details", map);
		return response;
	}

	public DatasetByServiceReqNrResponse datasetByServiceRequestNumber(String serviceRequestNumber) {
		
		ProcessTracker processTrackerList = processTrackerDao.findByServiceRequestNumber(serviceRequestNumber);
		if(processTrackerList == null) {
			throw new ServiceRequestNumberNotFoundException("serviceRequestNumber :: " + serviceRequestNumber + " not found");
		}
		Dataset dataset = datasetDao.findByDatasetId(processTrackerList.getDatasetId());
		List<TaskTracker> taskTrackerList = taskTrackerDao.findAllByServiceRequestNumber(serviceRequestNumber);
		
		List<TaskTracker> taskTrackerListUpdated = getStatusUpdatedTaskTrackerList(taskTrackerList, serviceRequestNumber);
		
		DatasetByServiceReqNrResponse response = new DatasetByServiceReqNrResponse("Dataset details",dataset.getDatasetName(), taskTrackerListUpdated);
		return response;
	}
	
	public DatasetCorpusSearchResponse corpusSearch(DatasetCorpusSearchRequest request)
			throws JsonProcessingException {

		log.info("******** Entry DatasetService:: corpusSearch *******");
		
		String userId = request.getUserId();
		String serviceRequestNumber = searchKafkaPublish.searchPublish(request, userId);

		String message = "Search has been initiated";
		DatasetCorpusSearchResponse response = new DatasetCorpusSearchResponse(message, serviceRequestNumber);
				
		return response;
	}
	

	public DatasetSearchStatusResponse searchStatus(String serviceRequestNumber) {

		ProcessTracker processTracker = processTrackerDao.findByServiceRequestNumber(serviceRequestNumber);
		if(processTracker == null) {
			throw new ServiceRequestNumberNotFoundException("serviceRequestNumber :: " + serviceRequestNumber + " not found");
		}

		List<TaskTracker> taskTrackerList = taskTrackerDao.findAllByServiceRequestNumber(serviceRequestNumber);

		String msg = "Search status";
		return new DatasetSearchStatusResponse(msg,processTracker.getServiceRequestNumber(), processTracker.getStartTime(),
				processTracker.getSearchCriterion(), taskTrackerList);
	}
	
	public SearchListByUserIdResponse searchListByUserId(String userId, Integer startPage, Integer endPage) {
		
		log.info("******** Entry DatasetService:: searchListByUserId *******" );
		
		SearchListByUserIdResponse response = null;
		if(startPage == null) {
			response = searchListByUserIdFetchAll(userId);
			
		}else {
			response = searchListByUserIdPagination(userId, startPage, endPage);
		}
		
		log.info("******** Exit DatasetService:: searchListByUserId *******" );
		return response;
	}
	

	public SearchListByUserIdResponse searchListByUserIdPagination(String userId, Integer startPage, Integer endPage) {
		
		log.info("******** Entry DatasetService:: searchListByUserIdPagination *******" );

		List<SearchListByUserIdResponseDto> searchList = new ArrayList<SearchListByUserIdResponseDto>();
		
		int startPg = startPage - 1;
		for(int i= startPg; i< endPage; i++) {
			
			Pageable paging = PageRequest.of(i, PAGE_SIZE,Sort.by("startTime").descending());
			Page<ProcessTracker> processTrackerPage = processTrackerDao.findByUserIdAndServiceRequestTypeAndServiceRequestAction(userId,ServiceRequestTypeEnum.dataset,ServiceRequestActionEnum.search,paging);
			List<ProcessTracker> processTrackerList = processTrackerPage.getContent();
			
				for (ProcessTracker processTracker : processTrackerList) {
					String serviceRequestNumber = processTracker.getServiceRequestNumber();
					
					if (processTracker.getSearchCriterion() != null) {
						
						List<TaskTracker> taskTrackerList = taskTrackerDao
								.findAllByServiceRequestNumber(serviceRequestNumber);

						searchList.add(new SearchListByUserIdResponseDto(processTracker.getServiceRequestNumber(),
								processTracker.getStartTime(), processTracker.getSearchCriterion(), taskTrackerList));
					}
				}
		}
		
		
		//Start Get Total count
		int totalCount=0;
		List<ProcessTracker> processTrackerList = processTrackerDao.findByUserIdAndServiceRequestTypeAndServiceRequestAction(userId,ServiceRequestTypeEnum.dataset,ServiceRequestActionEnum.search);
		for (ProcessTracker processTracker : processTrackerList) {
			
			
			if (processTracker.getSearchCriterion() != null) {
				
				totalCount=totalCount+1;
			}
		}
		
		//End Get Total count
		
		log.info("startPage :: "+startPage);
		log.info("endPage :: "+endPage);

		log.info("totalCount :: "+totalCount);
        log.info("search list size :: "+searchList.size());
		
		String msg = "Search List";
		SearchListByUserIdResponse response = new SearchListByUserIdResponse( msg, searchList,startPage ,endPage,totalCount ) ;

		log.info("******** Exit DatasetService:: searchListByUserIdPagination *******" );
		return response;

	}

	public SearchListByUserIdResponse searchListByUserIdFetchAll(String userId) {

		log.info("******** Entry DatasetService:: searchListByUserIdFetchAll *******" );
		
		List<SearchListByUserIdResponseDto> searchList = new ArrayList<SearchListByUserIdResponseDto>();
		List<ProcessTracker> processTrackerList = processTrackerDao.findByUserIdAndServiceRequestTypeAndServiceRequestAction(userId,ServiceRequestTypeEnum.dataset,ServiceRequestActionEnum.search);
		
			for (ProcessTracker processTracker : processTrackerList) {
				String serviceRequestNumber = processTracker.getServiceRequestNumber();
				if (processTracker.getSearchCriterion() != null) {
					
					List<TaskTracker> taskTrackerList = taskTrackerDao
							.findAllByServiceRequestNumber(serviceRequestNumber);
					
					searchList.add(new SearchListByUserIdResponseDto(processTracker.getServiceRequestNumber(),
							processTracker.getStartTime(), processTracker.getSearchCriterion(), taskTrackerList));
				}
			}
		
		String msg = "Search List";
		SearchListByUserIdResponse response = new SearchListByUserIdResponse( msg, searchList) ;

		log.info("******** Exit DatasetService:: searchListByUserIdFetchAll *******" );
		
		return response;

	}
	
	
	public void updateDataset(String datasetId, String userId, JSONObject schema, String md5hash) {
		
		
				
		Optional<Dataset> datasetOps = datasetDao.findById(datasetId);
		if(!datasetOps.isEmpty()) {
			Dataset dataset = datasetOps.get();
			dataset.setDatasetType(schema.get("datasetType").toString());
			dataset.setCollectionSource(schema.get("collectionSource").toString());
			if(!schema.get("datasetType").toString().equalsIgnoreCase(DatasetType.DOCUMENT_LAYOUT_CORPUS.toString())) {
				dataset.setLanguages(schema.get("languages").toString());
			}
			
			dataset.setDomain(schema.get("domain").toString());
			dataset.setContributors(schema.get("submitter").toString());	
			dataset.setSubmitterId(userId);
			dataset.setLicense(schema.get("license").toString());
			
			/*Fileidentifier fileidentifier = dataset.getDatasetFileIdentifier();
			fileidentifier.setMd5hash(md5hash);
			*/
			
			if(schema.has("collectionMethod")) {
				dataset.setCollectionMethod(schema.get("collectionMethod").toString());
			}
			//fileIdentifierDao.save(fileidentifier);
			datasetDao.save(dataset);	
			
			
		}
		
	}
	
	
	public List<TaskTracker> getStatusUpdatedTaskTrackerList(List<TaskTracker> taskTrackerList, String serviceRequestNumber) {
		
		HashMap<String, String> mapTemp = new HashMap<String, String>();
		for (TaskTracker tTracker : taskTrackerList) {
			mapTemp.put(tTracker.getTool().toString(), tTracker.getStatus().toString());
		}
		
		if(!mapTemp.containsKey(TaskTracker.ToolEnum.download.toString())) {
			TaskTracker download = new TaskTracker();
			download.serviceRequestNumber(serviceRequestNumber);
			download.setTool(ToolEnum.download.toString());
			download.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(download);
			
			TaskTracker ingest = new TaskTracker();
			ingest.serviceRequestNumber(serviceRequestNumber);
			ingest.setTool(ToolEnum.ingest.toString());
			ingest.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(ingest);
			
			TaskTracker validate = new TaskTracker();
			validate.serviceRequestNumber(serviceRequestNumber);
			validate.setTool(ToolEnum.validate.toString());
			validate.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(validate);
			
			TaskTracker publish = new TaskTracker();
			publish.serviceRequestNumber(serviceRequestNumber);
			publish.setTool(ToolEnum.publish.toString());
			publish.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(publish);
			
			return taskTrackerList;
			
		}else if(mapTemp.get(TaskTracker.ToolEnum.download.toString()).equals(TaskTracker.StatusEnum.inprogress.toString())) {
			TaskTracker ingest = new TaskTracker();
			ingest.serviceRequestNumber(serviceRequestNumber);
			ingest.setTool(ToolEnum.ingest.toString());
			ingest.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(ingest);
			
			TaskTracker validate = new TaskTracker();
			validate.serviceRequestNumber(serviceRequestNumber);
			validate.setTool(ToolEnum.validate.toString());
			validate.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(validate);
			
			TaskTracker publish = new TaskTracker();
			publish.serviceRequestNumber(serviceRequestNumber);
			publish.setTool(ToolEnum.publish.toString());
			publish.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(publish);
			
			return taskTrackerList;
			
		
		} else if(mapTemp.get(TaskTracker.ToolEnum.download.toString()).equals(TaskTracker.StatusEnum.failed.toString())) {
			TaskTracker ingest = new TaskTracker();
			ingest.serviceRequestNumber(serviceRequestNumber);
			ingest.setTool(ToolEnum.ingest.toString());
			ingest.setStatus(TaskTracker.StatusEnum.na.toString());
			taskTrackerList.add(ingest);
			
			TaskTracker validate = new TaskTracker();
			validate.serviceRequestNumber(serviceRequestNumber);
			validate.setTool(ToolEnum.validate.toString());
			validate.setStatus(TaskTracker.StatusEnum.na.toString());
			taskTrackerList.add(validate);
			
			TaskTracker publish = new TaskTracker();
			publish.serviceRequestNumber(serviceRequestNumber);
			publish.setTool(ToolEnum.publish.toString());
			publish.setStatus(TaskTracker.StatusEnum.na.toString());
			taskTrackerList.add(publish);
			
			return taskTrackerList;
			
		}else if(mapTemp.containsKey(TaskTracker.ToolEnum.precheck.toString()) && mapTemp.get(TaskTracker.ToolEnum.precheck.toString()).equals(TaskTracker.StatusEnum.failed.toString())) {
			TaskTracker ingest = new TaskTracker();
			ingest.serviceRequestNumber(serviceRequestNumber);
			ingest.setTool(ToolEnum.ingest.toString());
			ingest.setStatus(TaskTracker.StatusEnum.na.toString());
			taskTrackerList.add(ingest);
			
			TaskTracker validate = new TaskTracker();
			validate.serviceRequestNumber(serviceRequestNumber);
			validate.setTool(ToolEnum.validate.toString());
			validate.setStatus(TaskTracker.StatusEnum.na.toString());
			taskTrackerList.add(validate);
			
			TaskTracker publish = new TaskTracker();
			publish.serviceRequestNumber(serviceRequestNumber);
			publish.setTool(ToolEnum.publish.toString());
			publish.setStatus(TaskTracker.StatusEnum.na.toString());
			taskTrackerList.add(publish);
			
			return taskTrackerList;
			
		} else if(mapTemp.containsKey(TaskTracker.ToolEnum.precheck.toString()) && mapTemp.get(TaskTracker.ToolEnum.precheck.toString()).equals(TaskTracker.StatusEnum.inprogress.toString())) {
			TaskTracker ingest = new TaskTracker();
			ingest.serviceRequestNumber(serviceRequestNumber);
			ingest.setTool(ToolEnum.ingest.toString());
			ingest.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(ingest);
			
			TaskTracker validate = new TaskTracker();
			validate.serviceRequestNumber(serviceRequestNumber);
			validate.setTool(ToolEnum.validate.toString());
			validate.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(validate);
			
			TaskTracker publish = new TaskTracker();
			publish.serviceRequestNumber(serviceRequestNumber);
			publish.setTool(ToolEnum.publish.toString());
			publish.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(publish);
			
			return taskTrackerList;
			
		}else if(!mapTemp.containsKey(TaskTracker.ToolEnum.ingest.toString())) {
			TaskTracker ingest = new TaskTracker();
			ingest.serviceRequestNumber(serviceRequestNumber);
			ingest.setTool(ToolEnum.ingest.toString());
			ingest.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(ingest);
			
			TaskTracker validate = new TaskTracker();
			validate.serviceRequestNumber(serviceRequestNumber);
			validate.setTool(ToolEnum.validate.toString());
			validate.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(validate);
			
			TaskTracker publish = new TaskTracker();
			publish.serviceRequestNumber(serviceRequestNumber);
			publish.setTool(ToolEnum.publish.toString());
			publish.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(publish);
			
			return taskTrackerList;
			
		} else if(mapTemp.get(TaskTracker.ToolEnum.ingest.toString()).equals(TaskTracker.StatusEnum.failed.toString())) {
			
			if(!mapTemp.containsKey(TaskTracker.ToolEnum.validate.toString())) {
				TaskTracker validate = new TaskTracker();
				validate.serviceRequestNumber(serviceRequestNumber);
				validate.setTool(ToolEnum.validate.toString());
				validate.setStatus(TaskTracker.StatusEnum.na.toString());
				taskTrackerList.add(validate);
			}
			if(!mapTemp.containsKey(TaskTracker.ToolEnum.publish.toString())) {
				TaskTracker publish = new TaskTracker();
				publish.serviceRequestNumber(serviceRequestNumber);
				publish.setTool(ToolEnum.publish.toString());
				publish.setStatus(TaskTracker.StatusEnum.na.toString());
				taskTrackerList.add(publish);
			}
			
			
			return taskTrackerList;
			
		}else if(mapTemp.get(TaskTracker.ToolEnum.ingest.toString()).equals(TaskTracker.StatusEnum.pending.toString()) ) {
				
			TaskTracker validate = new TaskTracker();
			validate.serviceRequestNumber(serviceRequestNumber);
			validate.setTool(ToolEnum.validate.toString());
			validate.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(validate);
			
			TaskTracker publish = new TaskTracker();
			publish.serviceRequestNumber(serviceRequestNumber);
			publish.setTool(ToolEnum.publish.toString());
			publish.setStatus(TaskTracker.StatusEnum.pending.toString());
			taskTrackerList.add(publish);
			return taskTrackerList;
			
		} else if(mapTemp.get(TaskTracker.ToolEnum.ingest.toString()).equals(TaskTracker.StatusEnum.inprogress.toString())
				|| mapTemp.get(TaskTracker.ToolEnum.ingest.toString()).equals(TaskTracker.StatusEnum.completed.toString())
				) {
			
			if(!mapTemp.containsKey(TaskTracker.ToolEnum.validate.toString())) {
				TaskTracker validate = new TaskTracker();
				validate.serviceRequestNumber(serviceRequestNumber);
				validate.setTool(ToolEnum.validate.toString());
				validate.setStatus(TaskTracker.StatusEnum.pending.toString());
				taskTrackerList.add(validate);
			}
			if(!mapTemp.containsKey(TaskTracker.ToolEnum.publish.toString())) {
				TaskTracker validate = new TaskTracker();
				validate.serviceRequestNumber(serviceRequestNumber);
				validate.setTool(ToolEnum.publish.toString());
				validate.setStatus(TaskTracker.StatusEnum.pending.toString());
				taskTrackerList.add(validate);
			}
			
			return taskTrackerList;
		}
		
		
		return taskTrackerList;
	}
	
	public void updateDatasetFileLocation(String serviceRequestNumber, String localUrl) {
		
		ProcessTracker processTracker = processTrackerDao.findByServiceRequestNumber(serviceRequestNumber);
		Dataset dataset = datasetDao.findByDatasetId(processTracker.getDatasetId());
		dataset.getDatasetFileIdentifier().setFileUlcaUrl(localUrl);
		
		Fileidentifier fileidentifier = dataset.getDatasetFileIdentifier();
		fileidentifier.setFileUlcaUrl(localUrl);
		fileIdentifierDao.save(fileidentifier);
	}

}
