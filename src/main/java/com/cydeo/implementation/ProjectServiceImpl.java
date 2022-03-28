package com.cydeo.implementation;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.mapper.MapperUtil;
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
    TaskService taskService;
    UserService userService;
//    UserMapper userMapper;
//    ProjectMapper projectMapper;
    MapperUtil mapperUtil;
    UserRepository userRepository;

    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project = projectRepository.findByProjectCode(code);
        return mapperUtil.convert(project, new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> projects = projectRepository.findAll(Sort.by("projectCode"));
       // return projects.stream().map(obj -> projectMapper.convertToDto(obj)).collect(Collectors.toList());
        return projects.stream().map(obj -> mapperUtil.convert(obj, new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO save(ProjectDTO dto) throws TicketingProjectException {
        Project foundedProject = projectRepository.findByProjectCode(dto.getProjectCode());
        if (foundedProject!= null){
            throw new TicketingProjectException("Project with this code already exists");
        }

        dto.setProjectStatus(Status.OPEN);
        Project project= mapperUtil.convert(dto, new Project());
        //userDTO doesn't have id, and it uses username as a reference. I find user by username
        // project.setAssignedManager(userRepository.findByUserName(dto.getAssignedManager().getUserName()));
        Project saved = projectRepository.save(project);

        return mapperUtil.convert(saved, new ProjectDTO());
    }

    @Override
    public ProjectDTO update(ProjectDTO dto) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        if (project == null){
            throw new TicketingProjectException("Project with this code does not exist");
        }
        Project converted = mapperUtil.convert(project, new Project());
        converted.setId(project.getId());
        //userDTO doesn't have id, and it uses username as a reference. I find user by username
        //converted.setAssignedManager(userRepository.findByUserName(dto.getAssignedManager().getUserName()));
        //converted.setProjectStatus(project.getProjectStatus());
        Project saved = projectRepository.save(converted);
        return mapperUtil.convert(saved, new ProjectDTO());
    }

    @Override
    public void delete(String code) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(code);
        if (project == null){
            throw new TicketingProjectException("Project with this code does not exist");
        }

        // we added task delete to delete related tasks before project delete,
        // otherwise it throws exception and tasks without project are meaningless
        taskService.deleteByProject(mapperUtil.convert(project, new ProjectDTO()));

        project.setIsDeleted(true);
        project.setProjectCode(project.getProjectCode() + "-" + project.getId());
        projectRepository.save(project);
    }

    @Override
    public ProjectDTO complete(String code) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(code);
        if (project == null){
            throw new TicketingProjectException("Project with this code does not exist");
        }
        project.setProjectStatus(Status.COMPLETE);
        Project completed = projectRepository.save(project);
        return mapperUtil.convert(completed, new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() throws TicketingProjectException {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentId = Long.parseLong(id);

        User user = userRepository.findById(currentId).orElseThrow(()-> new TicketingProjectException("This user doesn't exist"));
        List<Project> list = projectRepository.findAllByAssignedManager(user);

        if (list.size() == 0)
            throw new TicketingProjectException("This manager does not have any project assigned");

        return list.stream().map(project -> {
            ProjectDTO obj = mapperUtil.convert(project, new ProjectDTO());
            obj.setInCompleteTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));
            return obj;
        }).collect(Collectors.toList());

        // MVC çözümü :
      /*
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO manager = userService.findByUserName(username);

     //   UserDTO manager = userService.findByUserName("john@cybertek.com");
        User user = mapperUtil.convert(manager, new User());
        user.setId(userRepository.findByUserName(manager.getUserName()).getId());
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(project -> {
            ProjectDTO obj = mapperUtil.convert(project, new ProjectDTO());
            obj.setInCompleteTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));
            return obj;
        }).collect(Collectors.toList());
       */
        /* Benim yaptığım çözüm :
        for (Project project : list){
           // System.out.println(projectRepository.countAllByProjectIdAndTaskStatusComplete(project.getId()).toString());
            project.setCompleteTaskCounts(projectRepository.countAllByProjectIdAndTaskStatusComplete(project.getId()));
            project.setInCompleteTaskCounts(projectRepository.countAllByProjectIdAndTaskStatusNotComplete(project.getId()));
        }
        return list.stream().map(projectMapper::convertToDto).collect(Collectors.toList());
        */
    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(obj ->mapperUtil.convert(obj,new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {

        return projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream().map(project -> mapperUtil.convert(project, new ProjectDTO()))
                .collect(Collectors.toList());
    }
}
