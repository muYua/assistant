package com.mupei.assistant.controller;

import com.mupei.assistant.annotation.NoVerifyToken;
import com.mupei.assistant.service.StudentService;
import com.mupei.assistant.utils.EncryptUtil;
import com.mupei.assistant.vo.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Value("${file.uploadFolder}")
    private String uploadFolder;

    @Autowired
    private EncryptUtil encryptUtil;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

    @NoVerifyToken
    @GetMapping("getFiles")
    public Json getHomeworkFiles(@RequestParam("sort") String sort) {
    	Json json = new Json();
    	
    	ArrayList<com.mupei.assistant.model.File> files = studentService.findFilesBySort(sort);
    	log.debug("【/student/getHomeworkFiles】获取所有文件列表>>{}，类别>>{}", files, sort);
    	
    	json.setData(files);
    	json.setCode(0);
    	json.setCount((long) files.size());
    	
		return json;
    }
    
    @NoVerifyToken
    @PostMapping("/uploadFiles")
    public Json uploadFiles(@RequestParam("file") MultipartFile[] files, @RequestParam("roleId") String encryptRoleId) throws IOException {
        Json json = new Json();

        for(MultipartFile file : files){
            String fileName = file.getOriginalFilename();// 获取到上传文件的名字
//            String filePath = request.getSession().getServletContext().getRealPath("upload") + File.separator + "file";//存储在"项目名/upload/file"
            String filePath = uploadFolder; //存储上传文件的路径
            File dir = new File(filePath, Objects.requireNonNull(fileName));
            if (!dir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }
            // MultipartFile自带的解析方法
            file.transferTo(dir);
            String directory = dir.getCanonicalPath();//获取文档路径
            Long fileSize = file.getSize();//获取到上传文件的大小
            //获取时间
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
            String createTime = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

            //得到创建人ID
            String decryptId = encryptUtil.decryptWithAES(encryptRoleId, encryptUtil.getKeyOfAES(), encryptUtil.getVI());
            Long roleId = Long.valueOf(decryptId);

            studentService.saveFile(fileName, filePath, fileSize, roleId, createTime);
            log.debug("【/student/uploadFiles】文件上传成功，directory>>{}", directory);
        }

        json.setSuccess(true);
        return json;
    }

}
