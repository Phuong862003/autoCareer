package com.demo.autocareer.service.storage;

import org.springframework.core.io.Resource;

public interface  FileDownloadService {
    Resource downloadStudentCV(String fullPath);
}
