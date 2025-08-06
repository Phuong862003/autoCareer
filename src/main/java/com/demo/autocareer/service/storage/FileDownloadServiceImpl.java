package com.demo.autocareer.service.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FileDownloadServiceImpl implements FileDownloadService{
    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public Resource downloadStudentCV(String path){
        String[] parts = path.split("/");
        if (parts.length < 2) {
            throw new RuntimeException("Đường dẫn file không hợp lệ.");
        }

        String folder = parts[0];
        String fileName = parts[1];

        return fileStorageService.loadFile(folder, fileName);
    }
}
