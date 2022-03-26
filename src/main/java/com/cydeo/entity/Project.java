package com.cydeo.entity;

import com.cydeo.utils.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "projects")
@Where(clause = "is_Deleted = false")
public class Project extends BaseEntity{
    @Column(unique = true)
    private String projectCode;

    private String projectName;

    @ManyToOne(fetch = FetchType.LAZY)//, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "manager_id")
    private User assignedManager;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String projectDetail;

    @Enumerated(value = EnumType.STRING)
    private Status projectStatus;

    private int completeTaskCounts;
    private int inCompleteTaskCounts;
}
