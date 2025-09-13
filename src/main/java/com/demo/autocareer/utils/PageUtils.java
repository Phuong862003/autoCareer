package com.demo.autocareer.utils;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.response.BasePageResponse;


public class PageUtils {
    public static <T> BasePageResponse<T> fromPage(Page<T> page) {
        return new BasePageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );

    }

    public static <T> BasePageResponse<T> fromList(List<T> content, int total, Pageable pageable) {
        int totalPages = (int) Math.ceil((double) total / pageable.getPageSize());
        return new BasePageResponse<>(
            content,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            total,
            totalPages
        );
    }
}