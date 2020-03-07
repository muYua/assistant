package com.mupei.assistant.dao;

import com.mupei.assistant.model.File;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
 
public interface FileDao extends CrudRepository<File, Long> {

	ArrayList<File> findBySort(String sort);

}
