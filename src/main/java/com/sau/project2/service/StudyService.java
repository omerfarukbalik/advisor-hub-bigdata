package com.sau.project2.service;

import com.sau.project2.model.Study;
import com.sau.project2.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;

    @Cacheable(value = "studies", key = "'all'")
    public List<Study> findAll() {
        return new ArrayList<>(studyRepository.findAll());
    }
}