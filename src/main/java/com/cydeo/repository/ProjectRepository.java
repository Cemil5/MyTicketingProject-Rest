package com.cydeo.repository;

import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.utils.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Project findByProjectCode(String code);

    List<Project> findAllByAssignedManager(User manager);

    List<Project> findAllByProjectStatusIsNot(Status status);




    // benim manager çözümüm :
    @Query("select count (t) from users  u join projects p on u.id = p.assignedManager.id join tasks t on t.project.id = p.id " +
            "where p.id=?1 and t.taskStatus='COMPLETE' and p.isDeleted=false")
    Integer countAllByProjectIdAndTaskStatusComplete(Long projectId);

    @Query("select count (t)  from users  u join projects p on u.id = p.assignedManager.id join tasks t on t.project.id = p.id " +
            "where p.id=?1 and t.taskStatus<>'COMPLETE' and p.isDeleted=false")
    Integer countAllByProjectIdAndTaskStatusNotComplete(Long projectId);


}
