package com.demo.autocareer.utils;

import org.springframework.data.domain.Page;
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
}