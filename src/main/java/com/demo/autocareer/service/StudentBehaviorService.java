package com.demo.autocareer.service;

import java.util.List;

import com.demo.autocareer.dto.request.StudentBehaviorDTORequest;
import com.demo.autocareer.dto.response.StudentBehaviorDTOResponse;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.enums.BehaviorType;

public interface StudentBehaviorService {
    Student getStudentFromToken();
    List<StudentBehaviorDTOResponse> recordBehavior(StudentBehaviorDTORequest request);
}
