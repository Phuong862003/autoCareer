package com.demo.autocareer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.service.LocationService;
import com.demo.autocareer.service.SubFieldService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class PortalController {
    private final SubFieldService subFieldService;

    public PortalController(SubFieldService subFieldService) {
        this.subFieldService = subFieldService;
    }

    @GetMapping("/subfield")
    public ResponseData<?> getSubField() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("GET SubField SUCCESS")
                .data(subFieldService.getAll())
                .build();
    }

    @GetMapping("/field")
    public ResponseData<?> getFields() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("GET Field SUCCESS")
                .data(subFieldService.getAllWithSubFields())
                .build();
    }
    
}
