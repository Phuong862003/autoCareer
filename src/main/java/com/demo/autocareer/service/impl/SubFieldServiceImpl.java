package com.demo.autocareer.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.SubFieldDTO;
import com.demo.autocareer.mapper.SubFieldMapper;
import com.demo.autocareer.model.Field;
import com.demo.autocareer.repository.FieldRepository;
import com.demo.autocareer.repository.SubFieldRepository;
import com.demo.autocareer.service.SubFieldService;

@Service
public class SubFieldServiceImpl implements  SubFieldService{
    @Autowired
    private SubFieldRepository subFieldRepository;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private SubFieldMapper subFieldMapper;

    @Override
    public List<SubFieldDTO> getAll(){
        return subFieldRepository.findAll()
                .stream()
                .map(subFieldMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Field> getAllWithSubFields(){
        return fieldRepository.findAllWithSubfields();
    }
}
