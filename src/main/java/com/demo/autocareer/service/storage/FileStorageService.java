package com.demo.autocareer.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface FileStorageService {
    /**
     * Lưu file vào thư mục chỉ định và trả về đường dẫn tương đối đã lưu.
     *
     * @param file   file upload
     * @param folder thư mục con (ví dụ: "cv", "avatar", ...)
     * @return đường dẫn tương đối (ví dụ: "cv/abc123_cv.pdf")
     */
    String storeFile(MultipartFile file, String folder);

    Resource loadFile(String folder, String fileName);
}
