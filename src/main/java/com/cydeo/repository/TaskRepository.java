package com.cydeo.repository;

import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select count (t) from tasks t where t.project.projectCode = ?1 and t.taskStatus <>'COMPLETE'")
    Integer totalNonCompleteTasks(String projectCode);

    @Query(value = "select count(*) from tasks t join projects p on t.project_id = p.id " +
            "where p.project_code = ?1 and t.task_status = 'COMPLETE'", nativeQuery = true)
    Integer totalCompleteTasks(String projectCode);

    List<Task> findAllByProject(Project project);

    //same = List<Task> findAllByTaskStatusIsNotAndAssignedEmployee(Status status, User user);
    @Query("select t from tasks t where t.assignedEmployee.userName = ?1 and t.taskStatus <>'COMPLETE'")
    List<Task>  getNonCompleteTasks(String userName);

    //same = List<Task> findAllByTaskStatusAndAssignedEmployee(Status status, User user);
    @Query("select t from tasks t where t.assignedEmployee.userName = ?1 and t.taskStatus ='COMPLETE'")
    List<Task>  getCompletedTasks(String userName);

    List<Task> findAllByAssignedEmployee(User user);
}
