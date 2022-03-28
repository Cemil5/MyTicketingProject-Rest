package com.cydeo.controller;

import com.cydeo.annotation.DefaultExceptionMessage;
import com.cydeo.dto.TaskDTO;
import com.cydeo.entity.ResponseWrapper;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import com.cydeo.utils.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Controller
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/task")
@Tag(name = "Task Controller", description = "Task API")
public class TaskController {

    ProjectService projectService;
    UserService userService;
    TaskService taskService;

    @GetMapping
    @Operation(summary = "Read all tasks")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAll(){
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved all tasks", taskService.findAll()));
    }

    @GetMapping("/project-manager")
    @Operation(summary = "Read all tasks by project manager")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllByManager() throws TicketingProjectException {
        List<TaskDTO> taskList = taskService.listAllTasksByProjectManager();
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved all tasks by manager", taskList));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Read task by id")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @PreAuthorize("hasAnyAuthority('Manager', 'Employee')")
    public ResponseEntity<ResponseWrapper> readById(@PathVariable Long id) throws TicketingProjectException {
        TaskDTO dto = taskService.findById(id);
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved all tasks by manager", dto));
    }







    // from MVC:
   /*
    ProjectService projectService;
    UserService userService;
    TaskService taskService;

    @GetMapping("/create")
    public String createTask(Model model){
        model.addAttribute("task", new TaskDTO());
        model.addAttribute("projects", projectService.listAllNonCompletedProjects());
        model.addAttribute("employees", userService.listAllByRole("employee"));
        model.addAttribute("tasks", taskService.findAll());
        return "/task/create";
    }

    @PostMapping("/create")
    public String insertTask(Model model, TaskDTO task){
        taskService.save(task);
        return "redirect:/task/create";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id){
        taskService.deleteById(id);
        return "redirect:/task/create";
    }

    @GetMapping("update/{id}")
    public String editTask(@PathVariable Long id, Model model){
        model.addAttribute("task", taskService.findById(id));
        model.addAttribute("projects", projectService.listAllNonCompletedProjects());
        model.addAttribute("employees", userService.listAllByRole("employee"));
        model.addAttribute("tasks", taskService.findAll());
        return "/task/update";
    }

    @PostMapping("update/{id}")
    public String updateTask(@PathVariable Long id, TaskDTO task){
        task.setId(id);
        taskService.update(task);
        return "redirect:/task/create";
    }

    @GetMapping("pending-task")
    public String pendingTask(Model model){
        List<TaskDTO> tasks = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);
        model.addAttribute("tasks", tasks);
        return "/task/pending-tasks";
    }

    @GetMapping("/pending-task-edit/{id}")
    public String editPendingTask(@PathVariable Long id, Model model){
        TaskDTO dto = taskService.findById(id);
        List<TaskDTO> dtos = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);
        model.addAttribute("task", dto);
        model.addAttribute("tasks", dtos);
        model.addAttribute("statuses", Status.values());
        model.addAttribute("users",userService.listAllByRole("employee"));
        model.addAttribute("projects",projectService.listAllNonCompletedProjects());
        return "/task/task-update";
    }

    @PostMapping("/pending-task-edit/{id}")
    public String updatePendingTask(@PathVariable Long id, TaskDTO dto){
        dto.setId(id);
        taskService.updateTaskStatus(dto);
        return "redirect:/task/pending-task";
    }

    @GetMapping("/archive")
    public String archive(Model model){
        List<TaskDTO> tasks = taskService.listAllTasksByStatusIs(Status.COMPLETE);
        model.addAttribute("tasks", tasks);
        return "/task/archive";
    }*/
}
