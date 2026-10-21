package com.sau.project2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supervises")
public class Supervision implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "adviser_id")
    private Integer adviserId;
    @Column(name = "study_id")
    private Integer studyId;
    private String student;
    private String performance;
}