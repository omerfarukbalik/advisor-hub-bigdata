package com.sau.project2.repository;

import com.sau.project2.dto.SupervisionView;
import com.sau.project2.model.Supervision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SupervisionRepository extends JpaRepository<Supervision, Integer> {

    @Query("""
        select new com.sau.project2.dto.SupervisionView(
            sv.id, a.name, a.department, a.photo, s.title, sv.student, sv.performance)
        from Supervision sv
        join Adviser a on a.id = sv.adviserId
        join Study s on s.id = sv.studyId
        order by a.name
        """)
    List<SupervisionView> findAllWithAdviserAndStudy();
}