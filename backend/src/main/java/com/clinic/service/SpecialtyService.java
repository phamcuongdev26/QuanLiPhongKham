package com.clinic.service;

import com.clinic.dto.request.CreateSpecialtyRequest;
import com.clinic.dto.request.UpdateSpecialtyRequest;
import com.clinic.dto.response.SpecialtyResponse;

import java.util.List;

public interface SpecialtyService {
    List<SpecialtyResponse> listActive();
    List<SpecialtyResponse> listAll();
    SpecialtyResponse getById(Long id);
    SpecialtyResponse create(CreateSpecialtyRequest request);
    SpecialtyResponse update(Long id, UpdateSpecialtyRequest request);
    void delete(Long id);
}

