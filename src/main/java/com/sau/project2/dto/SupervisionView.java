package com.sau.project2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupervisionView implements Serializable {
    private Integer id;            // supervises.id
    private String adviserName;    // advisers.name
    private String department;     // advisers.department
    private String photo;          // advisers.photo (file name in HDFS)
    private String studyTitle;     // studies.title
    private String student;        // supervises.student
    private String performance;    // supervises.performance
}