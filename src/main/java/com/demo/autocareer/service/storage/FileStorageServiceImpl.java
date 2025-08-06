package com.demo.autocareer.service.storage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService{
    private final String rootDir = "uploads";

    @Override
    public String storeFile(MultipartFile file, String folder){
        if(file.isEmpty()){
            throw new RuntimeException("Tệp tin trống.");
        }

        try{
            Path dirPath = Paths.get(rootDir, folder);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            String fileName = UUID.randomUUID()+"_"+file.getOriginalFilename();
            Path filePath = dirPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return folder + "/" + fileName;
        }catch(IOException e){
            throw new RuntimeException("Không thể lưu file", e);
        }
    }

    @Override
    public Resource loadFile(String folder, String fileName){
        try{
            Path filePath = Paths.get(rootDir, folder).resolve(fileName).normalize();
            if (!Files.exists(filePath)) {
                throw new RuntimeException("Không tìm thấy file: " + fileName);
            }
            return new UrlResource(filePath.toUri());
        }catch(MalformedURLException e){
            throw new RuntimeException("Lỗi khi tải file: " + fileName, e);
        }
    }
}
