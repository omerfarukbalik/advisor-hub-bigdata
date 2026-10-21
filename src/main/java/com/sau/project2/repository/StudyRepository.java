package com.sau.project2.repository;

import com.sau.project2.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Integer> {
}