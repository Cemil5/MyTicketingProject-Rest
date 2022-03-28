package com.cydeo.implementation;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.repository.TaskRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.TaskService;
import com.cydeo.utils.Status;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
//    private TaskMapper taskMapper;
//    private ProjectMapper projectMapper;
    private MapperUtil mapperUtil;

    @Override
    public List<TaskDTO> findAll() {
        List<Task> list = taskRepository.findAll();
        return list.stream().map(obj -> mapperUtil.convert(obj, new TaskDTO())).collect(Collectors.toList());
      //  return list.stream().map(obj -> taskMapper.convertToDto(obj)).collect(Collectors.toList());
    }

    @Override
    public TaskDTO findById(Long id) throws TicketingProjectException {
        Task task = taskRepository.findById(id).orElseThrow(()-> new TicketingProjectException("Task does not exist"));
        return mapperUtil.convert(task, new TaskDTO());
    }

    @Override
    public TaskDTO save(TaskDTO dto) {
        Task task = mapperUtil.convert(dto, new Task());
        task.setTaskStatus(Status.OPEN);
        task.setAssignedDate(LocalDate.now());
        //userDTO doesn't have id, and it uses username as a reference. I find user by username
        User user = userRepository.findByUserName(dto.getAssignedEmployee().getUserName());
        Project project = projectRepository.findByProjectCode(dto.getProject().getProjectCode());
        System.out.println(user.getUserName() + " " + user.getFirstName());
        task.setAssignedEmployee(user);
        task.setProject(project);

        taskRepository.save(task);
        return mapperUtil.convert(task, new TaskDTO());
      //  return taskMapper.convertToDto(taskRepository.findById(dto.getTaskId()).get());
    }

    @Override
    public void deleteById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            task.get().setIsDeleted(true);
            taskRepository.save(task.get());
        }
    }

    @Override
    public TaskDTO update(TaskDTO dto) {
        Task task = taskRepository.getById(dto.getId());
        Task converted = mapperUtil.convert(dto, new Task());
        converted.setId(task.getId());
        //userDTO doesn't have id, and it uses username as a reference. I find user by username
//        User user = userRepository.findByUserName(dto.getAssignedEmployee().getUserName());
//        Project project = projectRepository.findByProjectCode(dto.getProject().getProjectCode());
//        converted.setAssignedEmployee(user);
//        converted.setProject(project);
        converted.setAssignedDate(task.getAssignedDate());
        converted.setTaskStatus(task.getTaskStatus());
        Task saved = taskRepository.save(converted);
        return mapperUtil.convert(saved, new TaskDTO());
    }

    @Override
    public void updateTaskStatus(TaskDTO dto) {
        Task task = taskRepository.getById(dto.getId());
     //   Optional<Task> task1 = taskRepository.findById(dto.getId());
        task.setTaskStatus(dto.getTaskStatus());
        taskRepository.save(task);
    }

    @Override
    public Integer totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompleteTasks(projectCode);
    }

    @Override
    public Integer totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompleteTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO projectDTO) {
        List<Task> tasks = taskRepository.findAllByProject(mapperUtil.convert(projectDTO, new Project()));
        tasks.forEach(task -> deleteById(task.getId()));
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Task> tasks = taskRepository.getNonCompleteTasks(username);

      //  List<Task> tasks = taskRepository.getNonCompleteTasks("a@b.c4");
     //   return tasks.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
        return tasks.stream().map(obj -> mapperUtil.convert(obj, new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIs(Status status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Task> tasks = taskRepository.getCompletedTasks(username);
      //  List<Task> tasks = taskRepository.getCompletedTasks("a@b.c4");
        return tasks.stream().map(obj -> mapperUtil.convert(obj, new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByProjectManager() throws TicketingProjectException {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new TicketingProjectException("This user does not exist"));
        List<Task> tasks = taskRepository.findAllByProjectAssignedManager(user);
        return tasks.stream().map(obj ->mapperUtil.convert(obj,new TaskDTO())).collect(Collectors.toList());
    }


//
//    @Override
//    public List<TaskDTO> listAllTasksByEmployee() {
//        User user = userRepository.findByUserName("a@b.c4");
//        List<Task> tasks = taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee_UserName(Status.COMPLETE, user.getUserName());
//        return tasks.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
//    }
}
