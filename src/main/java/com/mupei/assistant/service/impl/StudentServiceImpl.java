package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.FileDao;
import com.mupei.assistant.dao.RoleDao;
import com.mupei.assistant.model.File;
import com.mupei.assistant.model.Role;
import com.mupei.assistant.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
	@Autowired
	private FileDao fileDao;
	@Autowired
	private RoleDao roleDao;

	@Override
	@Transactional
	public Boolean saveFile(String fileName, String filePath, Long fileSize, Long roleId, String createTime) {
		File file = new File();

		file.setFileName(fileName);
		file.setFilePath(filePath);
		file.setFileSize(fileSize);
		file.setCreateTime(createTime);

		Optional<Role> optionalRole = roleDao.findById(roleId);
//        if(StringUtils.isEmpty(role))
		if (optionalRole.isPresent()) {
			file.setRoleName(optionalRole.get().getNickname());
		} else {
			return false;
		}
		fileDao.save(file);

		return true;
	}

	@Override
	public ArrayList<File> getFiles() {
		Iterable<File> iterable = fileDao.findAll();
		ArrayList<File> list = new ArrayList<>();
		/*
		 * iterable.forEach(single -> { list.add(single); });
		 */
		iterable.forEach(list::add);
		return list;
	}

	@Override
	public ArrayList<File> findFilesBySort(String sort) {
		ArrayList<File> list = fileDao.findBySort(sort);
		return list;
	}

}
