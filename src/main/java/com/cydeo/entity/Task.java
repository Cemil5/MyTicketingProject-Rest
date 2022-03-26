package com.cydeo.entity;

import com.cydeo.utils.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "tasks")
@Where(clause = "is_Deleted = false")
@ToString
public class Task extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;    // columnname is project_id by default

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User assignedEmployee;

    private String taskSubject;
    private String taskDetail;

    @Enumerated(EnumType.STRING)
    private Status taskStatus;
    private LocalDate assignedDate;
}
