package com.demo.autocareer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.service.JobDetailService;
import com.demo.autocareer.service.LocationService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/provinces")
    public ResponseData<?> getProvince() {
         return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("GET PROVINCE SUCCESS")
                .data(locationService.findAll())
                .build();
    }

    @GetMapping("/districts")
    public ResponseData<?> getDistricts(@RequestParam Long id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("GET DISTRICT SUCCESS")
                .data(locationService.findByProvinceId(id))
                .build();
    }
    
    @GetMapping("/semester")
    public ResponseData<?> getSemester() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("GET  SUCCESS")
                .data(locationService.findAllSemester())
                .build();
    }
}
