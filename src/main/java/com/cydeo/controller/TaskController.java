package com.cydeo.controller;

import com.cydeo.dto.TaskDTO;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import com.cydeo.utils.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/task")
public class TaskController {

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
    }
}
