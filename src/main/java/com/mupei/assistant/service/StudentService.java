package com.mupei.assistant.service;

import java.util.ArrayList;

import com.mupei.assistant.model.File;

public interface StudentService {

    Boolean saveFile(String fileName, String filePath, Long fileSize, Long roleId, String createTime);

	ArrayList<File> getFiles();

	ArrayList<File> findFilesBySort(String sort);

}
