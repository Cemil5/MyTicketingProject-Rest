package com.cydeo.dto;

import com.cydeo.utils.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class TaskDTO {

    private Long id;
    private ProjectDTO project;
    private UserDTO assignedEmployee;
    private String taskSubject;
    private String taskDetail;
    private Status taskStatus;
    private LocalDate assignedDate;

    // we need this only for data generator
//    public TaskDTO(ProjectDTO project, UserDTO assignedEmployee, String taskSubject, String taskDetail, Status taskStatus, LocalDate assignedDate) {
//        this.project = project;
//        this.assignedEmployee = assignedEmployee;
//        this.taskSubject = taskSubject;
//        this.taskDetail = taskDetail;
//        this.taskStatus = taskStatus;
//        this.assignedDate = assignedDate;
//        this.taskId = UUID.randomUUID().getLeastSignificantBits();
//    }
}
