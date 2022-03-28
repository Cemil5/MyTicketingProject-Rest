package com.cydeo.service;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;

import java.util.List;

public interface ProjectService {

    ProjectDTO getByProjectCode(String code);
    List<ProjectDTO> listAllProjects();
    ProjectDTO save(ProjectDTO dto) throws TicketingProjectException;
    ProjectDTO update(ProjectDTO dto) throws TicketingProjectException;
    void delete(String code) throws TicketingProjectException;

    ProjectDTO complete(String code) throws TicketingProjectException;

    List<ProjectDTO> listAllProjectDetails() throws TicketingProjectException;

    List<ProjectDTO> listAllNonCompletedProjects();

    List<ProjectDTO> readAllByAssignedManager(User user);
}
