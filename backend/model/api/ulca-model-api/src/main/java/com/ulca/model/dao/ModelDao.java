package com.ulca.model.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelDao extends MongoRepository<ModelExtended, String> {

	Page<ModelExtended> findByUserId(String userId, Pageable paging);
	List<ModelExtended> findByUserId(String userId);
	ModelExtended findByModelId(String modelId);
	Integer countByUserId(String userId);
	Integer countByUserIdAndName(String userId,String name);

	List<ModelExtended> findByStatus(String status);
    List<ModelExtended> findAllByModelIdIn(Set<String> modelIds);


}
