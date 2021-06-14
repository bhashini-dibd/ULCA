package com.ulca.dataset.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ulca.dataset.model.Dataset;
import com.ulca.dataset.model.ProcessTracker;

@Repository
public interface ProcessTrackerDao extends MongoRepository<ProcessTracker, String>{

	List<ProcessTracker> findByDatasetId(String datasetId);

	List<ProcessTracker> findByUserId(String userId);
	ProcessTracker findByServiceRequestNumber(String serviceRequestNumber);

	

}
