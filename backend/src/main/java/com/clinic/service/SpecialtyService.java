package com.clinic.service;

import com.clinic.dto.request.CreateSpecialtyRequest;
import com.clinic.dto.response.SpecialtyResponse;

import java.util.List;

public interface SpecialtyService {
    List<SpecialtyResponse> listActive();
    List<SpecialtyResponse> listAll();
    SpecialtyResponse create(CreateSpecialtyRequest request);
    void delete(Long id);
}

