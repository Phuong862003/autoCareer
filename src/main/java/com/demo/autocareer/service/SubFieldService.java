package com.demo.autocareer.service;

import java.util.List;

import com.demo.autocareer.dto.SubFieldDTO;
import com.demo.autocareer.model.Field;

public interface SubFieldService {
    List<SubFieldDTO> getAll();
    List<Field> getAllWithSubFields();
}
