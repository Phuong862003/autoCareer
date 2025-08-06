package com.demo.autocareer.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.service.StudentService;
import com.demo.autocareer.service.storage.FileDownloadService;

@RestController
@RequestMapping("/api/file")
public class FileController {
    private final StudentService studentService;

    private final FileDownloadService fileDownloadService;

    public FileController(StudentService studentService, FileDownloadService fileDownloadService) {
        this.studentService = studentService;
        this.fileDownloadService = fileDownloadService;
    }

    @GetMapping("/download/cv")
    public ResponseEntity<Resource> download(@RequestParam String path) {
        Resource resource = fileDownloadService.downloadStudentCV(path);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
