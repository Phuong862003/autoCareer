package com.demo.autocareer.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.service.StudentService;
import com.demo.autocareer.service.storage.FileDownloadService;
import com.demo.autocareer.service.storage.FileStorageService;

@RestController
@RequestMapping("/api/file")
public class FileController {
    private final StudentService studentService;

    private final FileDownloadService fileDownloadService;

    private final FileStorageService fileStorageService;

    public FileController(StudentService studentService, FileDownloadService fileDownloadService, FileStorageService fileStorageService) {
        this.studentService = studentService;
        this.fileDownloadService = fileDownloadService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/download/cv")
    public ResponseEntity<Resource> download(@RequestParam String path) {
        Resource resource = fileDownloadService.downloadStudentCV(path);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/view")
    public ResponseEntity<Resource> viewCv(@RequestParam String path) {
        String folder = getFolderFromPath(path);
        String fileName = getFileNameFromPath(path);

        Resource file = fileStorageService.loadFile(folder, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(file);
    }


    // Tách folder từ đường dẫn DB
    private String getFolderFromPath(String path) {
        int slashIndex = path.lastIndexOf("/");
        return path.substring(0, slashIndex);
    }

    // Tách tên file từ đường dẫn DB
    private String getFileNameFromPath(String path) {
        int slashIndex = path.lastIndexOf("/");
        return path.substring(slashIndex + 1);
    }
    
}
