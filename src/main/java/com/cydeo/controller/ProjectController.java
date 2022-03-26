package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.service.ProjectService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    //    @Autowired
//    TaskService taskService;

//    @Autowired
//    ProjectManager projectManager;

    @GetMapping("/create")
    public String createProject(Model model){
        model.addAttribute("project", new ProjectDTO());
        model.addAttribute("projects", projectService.listAllProjects());
        model.addAttribute("managers", userService.listAllByRole("manager"));
        return "/project/create";
    }

    @PostMapping("/create")
    public String insertProject(ProjectDTO projectDTO){
        projectService.save(projectDTO);
        return "redirect:/project/create";
    }

    @GetMapping("/delete/{projectCode}")
    public String deleteProject(@PathVariable String projectCode){
        projectService.delete(projectCode);
        return "redirect:/project/create";
    }

    @GetMapping("/update/{projectCode}")
    public String editProject(@PathVariable String projectCode, Model model){
        model.addAttribute("project", projectService.getByProjectCode(projectCode));
        model.addAttribute("projects", projectService.listAllProjects());
        model.addAttribute("managers", userService.listAllByRole("manager"));
        return "/project/update";
    }

    @PostMapping("/update/{projectCode}")
    public String updateProject(@PathVariable String projectCode, ProjectDTO projectDTO){
        projectService.update(projectDTO);
        return "redirect:/project/create";
    }

    @GetMapping("/manager")
    public String getProjectByManager(Model model){

        model.addAttribute("projects",  projectService.listAllProjectDetails());
        return "/manager/project-status";
    }

    @GetMapping("/manager/complete/{projectCode}")
    public String completeProject(@PathVariable String projectCode){
        projectService.complete(projectCode);
        return "redirect:/project/manager";
    }


/*  // I put this method to manager package
    List<ProjectDTO> getCountedListOfProjectDTO(UserDTO manager){
        return projectService.findAll()
                .stream().filter(project -> project.getAssignedManager().equals(manager)).
                map(project -> {
                    List<TaskDTO> taskList = taskService.findTaskByManager(manager);
                    int completeCount = (int) taskList.stream().
                            filter(t->t.getProject().equals(project) && t.getTaskStatus() == Status.COMPLETE).count();
                    int inCompleteCount = (int) taskList.stream().
                            filter(t->t.getProject().equals(project) && t.getTaskStatus() != Status.COMPLETE).count();
                    project.setCompleteTaskCounts(completeCount);
                    project.setInCompleteTaskCounts(inCompleteCount);
                    return project;
                }).collect(Collectors.toList());
    }
*/

}
