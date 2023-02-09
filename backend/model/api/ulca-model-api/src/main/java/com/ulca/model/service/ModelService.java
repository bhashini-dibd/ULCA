package com.ulca.model.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulca.benchmark.dao.BenchmarkDao;
import com.ulca.benchmark.dao.BenchmarkProcessDao;
import com.ulca.benchmark.model.BenchmarkProcess;
import com.ulca.benchmark.util.ModelConstants;
import com.ulca.model.dao.ModelDao;
import com.ulca.model.dao.ModelExtended;
import com.ulca.model.dao.ModelFeedback;
import com.ulca.model.dao.ModelFeedbackDao;
import com.ulca.model.dao.ModelHealthStatus;
import com.ulca.model.dao.ModelHealthStatusDao;
import com.ulca.model.exception.FileExtensionNotSupportedException;
import com.ulca.model.exception.ModelNotFoundException;
import com.ulca.model.exception.ModelStatusChangeException;
import com.ulca.model.exception.ModelValidationException;
import com.ulca.model.exception.RequestParamValidationException;
import com.ulca.model.request.ModelComputeRequest;
import com.ulca.model.request.ModelFeedbackSubmitRequest;
import com.ulca.model.request.ModelSearchRequest;
import com.ulca.model.request.ModelStatusChangeRequest;
import com.ulca.model.response.GetModelFeedbackListResponse;
import com.ulca.model.response.GetTransliterationModelIdResponse;
import com.ulca.model.response.ModelComputeResponse;
import com.ulca.model.response.ModelFeedbackSubmitResponse;
import com.ulca.model.response.ModelHealthStatusResponse;
import com.ulca.model.response.ModelListByUserIdResponse;
import com.ulca.model.response.ModelListResponseDto;
import com.ulca.model.response.ModelSearchResponse;
import com.ulca.model.response.ModelStatusChangeResponse;
import com.ulca.model.response.UploadModelResponse;

import io.swagger.model.ASRInference;
import io.swagger.model.AsyncApiDetails;
import io.swagger.model.ImageFormat;
import io.swagger.model.InferenceAPIEndPoint;
import io.swagger.model.LanguagePair;
import io.swagger.model.LanguagePairs;
import io.swagger.model.License;
import io.swagger.model.ModelProcessingType;
import io.swagger.model.ModelTask;
import io.swagger.model.NerInference;
import io.swagger.model.OCRInference;
import io.swagger.model.OneOfInferenceAPIEndPointSchema;
import io.swagger.model.Submitter;
import io.swagger.model.SupportedLanguages;
import io.swagger.model.SupportedTasks;
import io.swagger.model.TTSInference;
import io.swagger.model.TranslationInference;
import io.swagger.model.TranslationResponse;
import io.swagger.model.TransliterationInference;
import io.swagger.model.TransliterationRequest;
import io.swagger.model.TxtLangDetectionInference;
import io.swagger.model.TxtLangDetectionRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ModelService {

	private int PAGE_SIZE = 10;

	@Autowired
	ModelDao modelDao;

	@Autowired
	BenchmarkProcessDao benchmarkProcessDao;
	
	@Autowired
	BenchmarkDao benchmarkDao;
	
	@Autowired
	ModelFeedbackDao modelFeedbackDao;

	@Autowired
	ModelHealthStatusDao modelHealthStatusDao;

	@Value("${ulca.model.upload.folder}")
	private String modelUploadFolder;

	@Autowired
	ModelInferenceEndPointService modelInferenceEndPointService;
	
	@Autowired
	WebClient.Builder builder;
	
	@Autowired
	ModelConstants modelConstants;
	
	@Autowired
	MongoTemplate mongoTemplate;

	public ModelExtended modelSubmit(ModelExtended model) {

		modelDao.save(model);
		return model;
	}

	public ModelListByUserIdResponse modelListByUserId(String userId, Integer startPage, Integer endPage,Integer pgSize,String name) {
		log.info("******** Entry ModelService:: modelListByUserId *******");
        Integer count = modelDao.countByUserId(userId);
		List<ModelExtended> list = new ArrayList<ModelExtended>();

		if (startPage != null) {
			int startPg = startPage - 1;
			for (int i = startPg; i < endPage; i++) {
				Pageable paging = null;
				if (pgSize!=null) {
				paging =	PageRequest.of(i, pgSize, Sort.by("submittedOn").descending());
				} else {
					paging = PageRequest.of(i,PAGE_SIZE, Sort.by("submittedOn").descending());

				}

				Page<ModelExtended> modelList = null;
				if (name!=null){
					ModelExtended modelExtended = new ModelExtended();
					modelExtended.setUserId(userId);
					modelExtended.setName(name);
					Example<ModelExtended> example = Example.of(modelExtended);

					modelList = modelDao.findAll(example,paging);
					count = modelDao.countByUserIdAndName(userId,name);
				} else {
					modelList = modelDao.findByUserId(userId, paging);
				}
				list.addAll(modelList.toList());
			}
		} else {
			if (name != null) {
				ModelExtended modelExtended = new ModelExtended();
				modelExtended.setUserId(userId);
				modelExtended.setName(name);
				Example<ModelExtended> example = Example.of(modelExtended);

				list = modelDao.findAll(example);
				count = list.size();

			} else {
				list = modelDao.findByUserId(userId);

			}
		}

		List<ModelListResponseDto> modelDtoList = new ArrayList<ModelListResponseDto>();
		for (ModelExtended model : list) {
			ModelListResponseDto modelDto = new ModelListResponseDto();
			BeanUtils.copyProperties(model, modelDto);
			List<BenchmarkProcess> benchmarkProcess = benchmarkProcessDao.findByModelId(model.getModelId());
			modelDto.setBenchmarkPerformance(benchmarkProcess);
			modelDtoList.add(modelDto);
		}
		modelDtoList.sort(Comparator.comparing(ModelListResponseDto::getSubmittedOn).reversed());
		return new ModelListByUserIdResponse("Model list by UserId", modelDtoList, modelDtoList.size(),count);
	}

	public ModelListResponseDto getModelByModelId(String modelId) {
		log.info("******** Entry ModelService:: getModelDescription *******");
		Optional<ModelExtended> result = modelDao.findById(modelId);

		if (!result.isEmpty()) {
			
			ModelExtended model = result.get();
			
			log.info("model : "+model.toString());
			log.info("model TaskType : "+model.getTask().getType());

			ModelListResponseDto modelDto = new ModelListResponseDto();
			BeanUtils.copyProperties(model, modelDto);
			
			log.info("modelDto : "+modelDto.toString());
			log.info("modelDto TaskType : "+modelDto.getTask().getType());

			List<String> metricList = modelConstants.getMetricListByModelTask(model.getTask().getType().toString());
			modelDto.setMetric(metricList);
			
			List<BenchmarkProcess> benchmarkProcess = benchmarkProcessDao.findByModelIdAndStatus(model.getModelId(), "Completed");
			modelDto.setBenchmarkPerformance(benchmarkProcess);
			log.info("modelDto1 : "+modelDto.toString());

			
			return modelDto;
		}
		return null;
	}

	public String storeModelFile(MultipartFile file) throws Exception {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		String uploadFolder = modelUploadFolder + "/model";
		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new Exception("Filename contains invalid path sequence " + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = Paths.get(uploadFolder).toAbsolutePath().normalize();

			try {
				Files.createDirectories(targetLocation);
			} catch (Exception ex) {
				throw new Exception("Could not create the directory where the uploaded files will be stored.", ex);
			}
			Path filePath = targetLocation.resolve(fileName);

			log.info("filePath :: " + filePath.toAbsolutePath());

			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			return filePath.toAbsolutePath().toString();
		} catch (IOException ex) {
			throw new Exception("Could not store file " + fileName + ". Please try again!", ex);
		}
	}
	
	public String storeModelTryMeFile(MultipartFile file) throws Exception {
		
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		/*
		 * check file extension
		 */
		String fileExtension = FilenameUtils.getExtension(fileName);
		try {
			ImageFormat imageformat = ImageFormat.fromValue(fileExtension);
			if(imageformat == null && !fileExtension.equalsIgnoreCase("jpg")) {
				log.info("Extension " + fileExtension + " not supported. It should be jpg/jpeg/bmp/png/tiff format");
				throw new FileExtensionNotSupportedException("Extension " + fileExtension + " not supported. It should be jpeg/bmp/png/tiff format");
			}
		}catch(FileExtensionNotSupportedException ex) {
			
			throw new FileExtensionNotSupportedException("Extension " + fileExtension + " not supported. It should be jpg/jpeg/bmp/png/tiff format");
			
		}
		
		String uploadFolder = modelUploadFolder + "/model/tryme";
		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new Exception("Filename contains invalid path sequence " + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = Paths.get(uploadFolder).toAbsolutePath().normalize();

			try {
				Files.createDirectories(targetLocation);
			} catch (Exception ex) {
				throw new Exception("Could not create the directory where the uploaded files will be stored.", ex);
			}
			Path filePath = targetLocation.resolve(fileName);

			log.info("filePath :: " + filePath.toAbsolutePath());

			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			return filePath.toAbsolutePath().toString();
		} catch (IOException ex) {
			throw new Exception("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	@Transactional
	public UploadModelResponse uploadModel(MultipartFile file, String userId) throws Exception {

		String modelFilePath = storeModelFile(file);
		ModelExtended modelObj = getUploadedModel(modelFilePath);
		
		
		
		if (modelObj != null) {
			validateModel(modelObj);
		} else {
			throw new ModelValidationException("Model validation failed. Check uploaded file syntax");
		}

		ModelTask taskType = modelObj.getTask();
		if (taskType.getType().equals(SupportedTasks.TXT_LANG_DETECTION)) {
			LanguagePair lp = new LanguagePair();
			lp.setSourceLanguage(SupportedLanguages.MIXED);
			LanguagePairs lps = new LanguagePairs();
			lps.add(lp);
			modelObj.setLanguages(lps);
		}
           
		modelObj.setUserId(userId);
		modelObj.setSubmittedOn(Instant.now().toEpochMilli());
		modelObj.setPublishedOn(Instant.now().toEpochMilli());
		modelObj.setStatus("unpublished");
		modelObj.setUnpublishReason("Newly submitted model");
        
		
		
		
		InferenceAPIEndPoint inferenceAPIEndPoint = modelObj.getInferenceEndPoint();
		OneOfInferenceAPIEndPointSchema schema = modelObj.getInferenceEndPoint().getSchema();
		
		if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.ASRInference")
				|| schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TTSInference")) {
			if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.ASRInference")) {
				ASRInference asrInference = (ASRInference) schema;
				
				if (!asrInference.getModelProcessingType().getType().equals(ModelProcessingType.TypeEnum.STREAMING)) {
					inferenceAPIEndPoint = modelInferenceEndPointService.validateCallBackUrl(inferenceAPIEndPoint);
				}
			} else {
				TTSInference ttsInference = (TTSInference) schema;

				if (!ttsInference.getModelProcessingType().getType().equals(ModelProcessingType.TypeEnum.STREAMING)) {
					inferenceAPIEndPoint = modelInferenceEndPointService.validateCallBackUrl(inferenceAPIEndPoint);
				}
			}
		} else {
			inferenceAPIEndPoint = modelInferenceEndPointService.validateCallBackUrl(inferenceAPIEndPoint);
		}

		modelObj.setInferenceEndPoint(inferenceAPIEndPoint);

		if (modelObj != null) {
			try {
				modelDao.save(modelObj);
			} catch (DuplicateKeyException ex) {
				ex.printStackTrace();
				throw new DuplicateKeyException("Model with same name and version exist in system");
			}
		}

		return new UploadModelResponse("Model Saved Successfully", modelObj);
	}

	
	
	public ModelExtended getUploadedModel(String modelFilePath) {

		ModelExtended modelObj = null;
		ObjectMapper objectMapper = new ObjectMapper();
		File file = new File(modelFilePath);
		try {
			modelObj = objectMapper.readValue(file, ModelExtended.class);
			OneOfInferenceAPIEndPointSchema schema = modelObj.getInferenceEndPoint().getSchema();
			setTaskTypeToInference(schema);

			return modelObj;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return modelObj;
	}
	
	private Boolean validateModel(ModelExtended model) throws ModelValidationException {
		
		if(model.getName() == null || model.getName().isBlank()) 
			throw new ModelValidationException("name is required field");
		
		if(model.getVersion() == null || model.getVersion().isBlank())
			throw new ModelValidationException("version is required field");
		
		if(model.getDescription() == null ||  model.getDescription().isBlank())
			throw new ModelValidationException("description is required field");
		
		if(model.getTask() == null)
			throw new ModelValidationException("task is required field");
		
		if(model.getLanguages() == null) {
			ModelTask taskType = model.getTask();
			if(!taskType.getType().equals(SupportedTasks.TXT_LANG_DETECTION)) {
				throw new ModelValidationException("languages is required field");
			}
		}
			
		
		if(model.getLicense() == null)
			throw new ModelValidationException("license is required field");
		
		if(model.getLicense() == License.CUSTOM_LICENSE) {
			if(model.getLicenseUrl().isBlank())
				throw new ModelValidationException("custom licenseUrl is required field");
		}
		
		if(model.getDomain() == null)
			throw new ModelValidationException("domain is required field");
		
		if(model.getSubmitter() == null)
			throw new ModelValidationException("submitter is required field");
		
		if(model.getInferenceEndPoint() == null)
			throw new ModelValidationException("inferenceEndPoint is required field");
		
		InferenceAPIEndPoint inferenceAPIEndPoint = model.getInferenceEndPoint();
		
		if(inferenceAPIEndPoint.isIsSyncApi()!=null && !inferenceAPIEndPoint.isIsSyncApi()) {
			AsyncApiDetails asyncApiDetails = inferenceAPIEndPoint.getAsyncApiDetails();
			if(asyncApiDetails.getPollingUrl().isBlank()) {
				throw new ModelValidationException("PollingUrl is required field for async model");
			}
		}else {
			if(inferenceAPIEndPoint.getCallbackUrl().isBlank()) {
				throw new ModelValidationException("callbackUrl is required field for sync model");
			}
		}
		
		if(model.getTrainingDataset() == null)
			throw new ModelValidationException("trainingDataset is required field");
		
		return true;
	}

//	public ModelSearchResponse searchModel(ModelSearchRequest request) {
//
//		ModelExtended model = new ModelExtended();
//
//		if (request.getTask() != null && !request.getTask().isBlank()) {
//			ModelTask modelTask = new ModelTask();
//			SupportedTasks modelTaskType = SupportedTasks.fromValue(request.getTask());
//			if(modelTaskType == null) {
//				throw new RequestParamValidationException("task type is not valid");
//			}
//			modelTask.setType(modelTaskType);
//			model.setTask(modelTask);
//		}else {
//			throw new RequestParamValidationException("task is required field");
//		}
//		
//		LanguagePairs lprs = new LanguagePairs();
//		LanguagePair lp = null;
//		if (request.getSourceLanguage() != null && !request.getSourceLanguage().isBlank()) {
//			 lp = new LanguagePair();
//			lp.setSourceLanguage(SupportedLanguages.fromValue(request.getSourceLanguage()));
//			
//		}
//		
//		if (request.getTargetLanguage() != null && !request.getTargetLanguage().isBlank()) {
//			if(lp==null) {
//				lp = new LanguagePair();
//				
//			}
//			 
//			lp.setTargetLanguage(SupportedLanguages.fromValue(request.getTargetLanguage()));
//		}
//		
//		if(lp!=null) {
//			
//			lprs.add(lp);
//			model.setLanguages(lprs);
//		}
//		
//	
//		/*
//		 * seach only published model
//		 */
//		model.setStatus("published");
//
//		Example<ModelExtended> example = Example.of(model);
//		List<ModelExtended> list = modelDao.findAll(example);
//		
//		if(list != null) {
//			Collections.shuffle(list); // randomize the search
//			return new ModelSearchResponse("Model Search Result", list, list.size());
//		}
//		return new ModelSearchResponse("Model Search Result", list, 0);
//		
//		
//
//	}
	
	public ModelSearchResponse searchModel(ModelSearchRequest request) {

		Query dynamicQuery = new Query();
		
		if (request.getTask() != null && !request.getTask().isBlank()) {
			SupportedTasks modelTaskType = SupportedTasks.fromValue(request.getTask());
			if(modelTaskType == null) {
				throw new RequestParamValidationException("task type is not valid");
			}
			Criteria nameCriteria = Criteria.where("task.type").is(modelTaskType.name());
			dynamicQuery.addCriteria(nameCriteria);
		}else {
			throw new RequestParamValidationException("task is required field");
		}
		
		if (request.getSourceLanguage() != null && !request.getSourceLanguage().isBlank() && !request.getSourceLanguage().equalsIgnoreCase("All")) {
			Criteria nameCriteria = Criteria.where("languages.0.sourceLanguage").is(SupportedLanguages.fromValue(request.getSourceLanguage()).name());
			dynamicQuery.addCriteria(nameCriteria);
		}
		
		if (request.getTargetLanguage() != null && !request.getTargetLanguage().isBlank() && !request.getTargetLanguage().equalsIgnoreCase("All") ) {
			Criteria nameCriteria = Criteria.where("languages.0.targetLanguage").is(SupportedLanguages.fromValue(request.getTargetLanguage()).name());
			dynamicQuery.addCriteria(nameCriteria);
		}
		
		//domain
		
		if (request.getDomain() != null && !request.getDomain().isBlank() && !request.getDomain().equalsIgnoreCase("All")) {
			Criteria nameCriteria = Criteria.where("domain.0").is(request.getDomain());
			dynamicQuery.addCriteria(nameCriteria);
		}
		
		
		
		// submitter
		if (request.getSubmitter()!= null && !request.getSubmitter().isBlank() && !request.getSubmitter().equalsIgnoreCase("All")) {
			Criteria nameCriteria = Criteria.where("submitter.name").is(request.getSubmitter());
			dynamicQuery.addCriteria(nameCriteria);
		}
		
		
		// userId
		
		if (request.getUserId()!= null && !request.getUserId().isBlank() && !request.getUserId().equalsIgnoreCase("All")) {
			Criteria nameCriteria = Criteria.where("userId").is(request.getUserId());
			dynamicQuery.addCriteria(nameCriteria);
		}

		
		
		
		Criteria nameCriteria = Criteria.where("status").is("published");
		dynamicQuery.addCriteria(nameCriteria);
		
	    	log.info("dynamicQuery : "+dynamicQuery.toString());
	
		List<ModelExtended> list = mongoTemplate.find(dynamicQuery, ModelExtended.class);
         
		log.info("modelList : "+list);
		
		
		if(list != null) {
			Collections.shuffle(list); // randomize the search
			return new ModelSearchResponse("Model Search Result", list, list.size());
		}
		return new ModelSearchResponse("Model Search Result", list, 0);

	}
	
	public ModelComputeResponse computeModel(ModelComputeRequest compute)
			throws URISyntaxException, IOException, KeyManagementException, NoSuchAlgorithmException, InterruptedException {

		String modelId = compute.getModelId();
		ModelExtended modelObj = modelDao.findById(modelId).get();
		InferenceAPIEndPoint inferenceAPIEndPoint = modelObj.getInferenceEndPoint();

		return modelInferenceEndPointService.compute(inferenceAPIEndPoint, compute);
	}
	
	public ModelComputeResponse tryMeOcrImageContent(MultipartFile file, String modelId) throws Exception {

		String imageFilePath = storeModelTryMeFile(file);
		
		ModelExtended modelObj = modelDao.findById(modelId).get();
		InferenceAPIEndPoint inferenceAPIEndPoint = modelObj.getInferenceEndPoint();
		//String callBackUrl = inferenceAPIEndPoint.getCallbackUrl();
		OneOfInferenceAPIEndPointSchema schema = inferenceAPIEndPoint.getSchema();
		
		ModelComputeResponse response = modelInferenceEndPointService.compute(inferenceAPIEndPoint, schema, imageFilePath);
		
		return response;
	}
	

	public ModelStatusChangeResponse changeStatus(@Valid ModelStatusChangeRequest request) {
		
		String userId = request.getUserId();
		String modelId = request.getModelId();
		String status = request.getStatus().toString();
		ModelExtended model = modelDao.findByModelId(modelId);
		if(model == null) {
			throw new ModelNotFoundException("model with modelId : " + modelId + " not found");
		}
		if(!model.getUserId().equalsIgnoreCase(userId)) {
			throw new ModelStatusChangeException("Not the submitter of model. So, can not " + status + " it.", status);
		}
		model.setStatus(status);
		if (status.equalsIgnoreCase("unpublished") ) {
			if(request.getUnpublishReason() == null || ( request.getUnpublishReason() != null && request.getUnpublishReason().isBlank())){
				throw new ModelStatusChangeException("unpublishReason field should not be empty" ,status);

			}

			model.setUnpublishReason(request.getUnpublishReason());
		}
		modelDao.save(model);
		
		return new ModelStatusChangeResponse("Model " + status +  " successfull.");
	}
	
	public ModelFeedbackSubmitResponse modelFeedbackSubmit(ModelFeedbackSubmitRequest request) {
		
		String taskType = request.getTaskType();
		if(taskType == null || (!taskType.equalsIgnoreCase("translation") && !taskType.equalsIgnoreCase("asr") && !taskType.equalsIgnoreCase("ocr") && !taskType.equalsIgnoreCase("tts") && !taskType.equalsIgnoreCase("sts"))) {
			
			throw new RequestParamValidationException("Model taskType should be one of { translation, asr, ocr, tts or sts }");
		}
		
		ModelFeedback feedback = new ModelFeedback();
		BeanUtils.copyProperties(request, feedback);
		
		feedback.setCreatedAt(new Date().toString());
		feedback.setUpdatedAt(new Date().toString());
		
		modelFeedbackDao.save(feedback);
		
		String feedbackId = feedback.getFeedbackId();
		String userId = feedback.getUserId();
		
		
		if(request.getTaskType() != null && !request.getTaskType().isBlank() && request.getTaskType().equalsIgnoreCase("sts")) {
			
			List<ModelFeedbackSubmitRequest> detailedFeedback = request.getDetailedFeedback();
			for(ModelFeedbackSubmitRequest modelFeedback : detailedFeedback ) {
				
				ModelFeedback mfeedback = new ModelFeedback();
				BeanUtils.copyProperties(modelFeedback, mfeedback);
				
				mfeedback.setStsFeedbackId(feedbackId);	
				mfeedback.setUserId(userId);
				mfeedback.setCreatedAt(new Date().toString());
				mfeedback.setUpdatedAt(new Date().toString());
				
				modelFeedbackDao.save(mfeedback);
				
			}
		}
		
		ModelFeedbackSubmitResponse response = new ModelFeedbackSubmitResponse("model feedback submitted successful", feedbackId);
		return response;
	}
	
	public List<ModelFeedback>  getModelFeedbackByModelId(String modelId) {
		
		return modelFeedbackDao.findByModelId(modelId);
		
	}

	public List<GetModelFeedbackListResponse>  getModelFeedbackByTaskType(String taskType) {
		
		List<GetModelFeedbackListResponse>  response = new ArrayList<GetModelFeedbackListResponse>();
		List<ModelFeedback>  feedbackList =  modelFeedbackDao.findByTaskType(taskType);
		
			for(ModelFeedback feedback : feedbackList) {
				
				GetModelFeedbackListResponse res = new GetModelFeedbackListResponse();
				BeanUtils.copyProperties(feedback, res);
				
				if(taskType.equalsIgnoreCase("sts")) {
					List<ModelFeedback>  stsDetailedFd =  modelFeedbackDao.findByStsFeedbackId(feedback.getFeedbackId());
					res.setDetailedFeedback(stsDetailedFd);
				}
				response.add(res);
			}
		return response;
	}
	public ModelHealthStatusResponse modelHealthStatus(String taskType, Integer startPage, Integer endPage) {
		log.info("******** Entry ModelService:: modelHealthStatus *******");

		List<ModelHealthStatus> list = new ArrayList<ModelHealthStatus>();
		if(taskType==null || taskType.isBlank()){
			list = modelHealthStatusDao.findAll();
		} else {

			if (startPage != null) {
				int startPg = startPage - 1;
				for (int i = startPg; i < endPage; i++) {
					Pageable paging = PageRequest.of(i, PAGE_SIZE);
					Page<ModelHealthStatus> modelHealthStatusesList = modelHealthStatusDao.findByTaskType(taskType, paging);
					list.addAll(modelHealthStatusesList.toList());
				}
			} else {
				list = modelHealthStatusDao.findByTaskType(taskType);
			}
		}
		log.info("******** Exit ModelService:: modelHealthStatus *******");

		return new ModelHealthStatusResponse("ModelHealthStatus", list, list.size());
	}

	
	public GetTransliterationModelIdResponse getTransliterationModelId(String sourceLanguage, String targetLanguage) {
		
		ModelExtended model = new ModelExtended();
		
		ModelTask modelTask = new ModelTask();
		modelTask.setType(SupportedTasks.TRANSLITERATION);
		model.setTask(modelTask);

		
		LanguagePairs lprs = new LanguagePairs();
		LanguagePair lp = new LanguagePair();
		lp.setSourceLanguage(SupportedLanguages.fromValue(sourceLanguage));
		if (targetLanguage != null && !targetLanguage.isBlank()) {
			lp.setTargetLanguage(SupportedLanguages.fromValue(targetLanguage));
		}
		lprs.add(lp);
		model.setLanguages(lprs);
		
		Submitter submitter = new Submitter();
		submitter.setName("AI4Bharat");
		model.setSubmitter(submitter);
		
		/*
		 * seach only published model
		 */
		model.setStatus("published");

		Example<ModelExtended> example = Example.of(model);
		List<ModelExtended> list = modelDao.findAll(example);
		
		if(list != null && list.size() > 0) {
			GetTransliterationModelIdResponse response = new GetTransliterationModelIdResponse();
			ModelExtended obj = list.get(0);
			response.setModelId(obj.getModelId());
			return response;
		}
		
		return null;
	}
	
	public static void setTaskTypeToInference(OneOfInferenceAPIEndPointSchema schema) {
		
		if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.ASRInference")){
			
			io.swagger.model.ASRInference asrInference=(io.swagger.model.ASRInference)schema;
			if(asrInference.getTaskType()==null) {
				
				asrInference.setTaskType(SupportedTasks.ASR);
			}
		}else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TTSInference")){
			
			io.swagger.model.TTSInference asrInference=(io.swagger.model.TTSInference)schema;
			if(asrInference.getTaskType()==null) {
				
				asrInference.setTaskType(SupportedTasks.TTS);
			}
		}else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TransliterationInference")){
			
			io.swagger.model.TransliterationInference asrInference=(io.swagger.model.TransliterationInference)schema;
			if(asrInference.getTaskType()==null) {
				
				asrInference.setTaskType(SupportedTasks.TRANSLITERATION);
			}
		}else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TranslationInference")){
			
			io.swagger.model.TranslationInference asrInference=(io.swagger.model.TranslationInference)schema;
			if(asrInference.getTaskType()==null) {
				
				asrInference.setTaskType(SupportedTasks.TRANSLATION);
			}
		}else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.OCRInference")){
			
			io.swagger.model.OCRInference asrInference=(io.swagger.model.OCRInference)schema;
			if(asrInference.getTaskType()==null) {
				
				asrInference.setTaskType(SupportedTasks.OCR);
			}
		}else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.NerInference")){
			
			io.swagger.model.NerInference asrInference=(io.swagger.model.NerInference)schema;
			if(asrInference.getTaskType()==null) {
				
				asrInference.setTaskType(SupportedTasks.NER);
			}
		}else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TxtLangDetectionInference")){
			
			io.swagger.model.TxtLangDetectionInference asrInference=(io.swagger.model.TxtLangDetectionInference)schema;
			if(asrInference.getTaskType()==null) {
				
				asrInference.setTaskType(SupportedTasks.TXT_LANG_DETECTION);
			}
		}

		
		
	}
	
	

}
