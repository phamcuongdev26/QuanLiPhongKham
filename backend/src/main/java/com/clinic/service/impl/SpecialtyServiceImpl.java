package com.clinic.service.impl;

import com.clinic.dto.request.CreateSpecialtyRequest;
import com.clinic.dto.request.UpdateSpecialtyRequest;
import com.clinic.dto.response.SpecialtyResponse;
import com.clinic.entity.Specialty;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.SpecialtyRepository;
import com.clinic.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    private SpecialtyResponse toResponse(Specialty specialty) {
        return SpecialtyResponse.builder()
                .id(specialty.getId())
                .name(specialty.getName())
                .description(specialty.getDescription())
                .isActive(specialty.isActive())
                .build();
    }

    @Override
    public List<SpecialtyResponse> listActive() {
        return specialtyRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<SpecialtyResponse> listAll() {
        return specialtyRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public SpecialtyResponse getById(Long id) {
        return specialtyRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));
    }

    @Override
    public SpecialtyResponse create(CreateSpecialtyRequest request) {
        if (specialtyRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.SPECIALTY_ALREADY_EXISTS);
        }
        Specialty specialty = Specialty.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .build();
        return toResponse(specialtyRepository.save(specialty));
    }

    @Override
    public SpecialtyResponse update(Long id, UpdateSpecialtyRequest request) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));
        specialty.setName(request.getName());
        specialty.setDescription(request.getDescription());
        if (request.getIsActive() != null) specialty.setActive(request.getIsActive());
        return toResponse(specialtyRepository.save(specialty));
    }

    @Override
    public void delete(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));
        specialty.setActive(false);
        specialtyRepository.save(specialty);
    }
}
