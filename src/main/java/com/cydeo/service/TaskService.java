package com.cydeo.service;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.enums.Status;

import java.util.List;

public interface TaskService {

    List<TaskDTO> findAll();
    TaskDTO findById(Long id) throws TicketingProjectException;
    TaskDTO save(TaskDTO dto);
    void deleteById(Long id);
    TaskDTO update(TaskDTO dto) throws TicketingProjectException;

    Integer totalNonCompletedTasks(String projectCode);
    Integer totalCompletedTasks(String projectCode);

    void deleteByProject(ProjectDTO projectDTO);

    List<TaskDTO> listAllTasksByStatusIsNot(Status status) throws TicketingProjectException;

    List<TaskDTO> listAllTasksByStatusIs(Status status);

    TaskDTO updateTaskStatus(TaskDTO dto) throws TicketingProjectException;

    List<TaskDTO> listAllTasksByProjectManager() throws TicketingProjectException;
}
