package com.cydeo.implementation;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import com.cydeo.utils.Status;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    ProjectMapper projectMapper;
    TaskService taskService;
    UserService userService;
    UserMapper userMapper;

    UserRepository userRepository;

    @Override
    public ProjectDTO getByProjectCode(String code) {
        return projectMapper.convertToDto(projectRepository.findByProjectCode(code));
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> projects = projectRepository.findAll(Sort.by("projectCode"));
        return projects.stream().map(obj -> projectMapper.convertToDto(obj)).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO save(ProjectDTO dto) {
        dto.setProjectStatus(Status.OPEN);
        Project project= projectMapper.convertToEntity(dto);
        //userDTO doesn't have id, and it uses username as a reference. I find user by username
        project.setAssignedManager(userRepository.findByUserName(dto.getAssignedManager().getUserName()));
        projectRepository.save(project);
        return projectMapper.convertToDto(projectRepository.findByProjectCode(dto.getProjectCode()));
    }

    @Override
    public ProjectDTO update(ProjectDTO dto) {
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        Project converted = projectMapper.convertToEntity(dto);
        converted.setId(project.getId());
        //userDTO doesn't have id, and it uses username as a reference. I find user by username
        converted.setAssignedManager(userRepository.findByUserName(dto.getAssignedManager().getUserName()));
        converted.setProjectStatus(project.getProjectStatus());
        projectRepository.save(converted);
        return projectMapper.convertToDto(projectRepository.findByProjectCode(dto.getProjectCode()));
    }

    @Override
    public void delete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setIsDeleted(true);

        // we'll add task delete
        taskService.deleteByProject(projectMapper.convertToDto(project));

        project.setProjectCode(project.getProjectCode() + "-" + project.getId());
        projectRepository.save(project);

    }

    @Override
    public void complete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO manager = userService.findByUserName(username);

     //   UserDTO manager = userService.findByUserName("john@cybertek.com");
        User user = userMapper.convertToEntity(manager);
        user.setId(userRepository.findByUserName(manager.getUserName()).getId());
        List<Project> list = projectRepository.findAllByAssignedManager(user);

        /* Benim yaptığım çözüm :
        for (Project project : list){
           // System.out.println(projectRepository.countAllByProjectIdAndTaskStatusComplete(project.getId()).toString());
            project.setCompleteTaskCounts(projectRepository.countAllByProjectIdAndTaskStatusComplete(project.getId()));
            project.setInCompleteTaskCounts(projectRepository.countAllByProjectIdAndTaskStatusNotComplete(project.getId()));
        }
        return list.stream().map(projectMapper::convertToDto).collect(Collectors.toList());
        */

        return list.stream().map(project -> {
            ProjectDTO obj = projectMapper.convertToDto(project);
            obj.setInCompleteTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));
            return obj;
        }).collect(Collectors.toList());

    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {

        return projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream().map(project -> projectMapper.convertToDto(project))
                .collect(Collectors.toList());
    }
}
