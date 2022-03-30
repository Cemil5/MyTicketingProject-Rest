package com.cydeo.controller;

import com.cydeo.annotation.DefaultExceptionMessage;
import com.cydeo.dto.ProjectDTO;
import com.cydeo.entity.ResponseWrapper;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.service.ProjectService;
import com.cydeo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//@Controller
@RestController
@RequestMapping("/api/v1/project")
@Tag(name = "Project Controller", description = "Project API")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read all projects")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> readAll(){
        List<ProjectDTO> list = projectService.listAllProjects();
        return ResponseEntity.ok(new ResponseWrapper("Projects are retrieved", list));
    }

    @GetMapping("/{projectcode}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read by project code")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> readByProjectCode(@PathVariable("projectcode") String projectCode){
        ProjectDTO projectDTO = projectService.getByProjectCode(projectCode);
        return ResponseEntity.ok(new ResponseWrapper("Project is retrieved", projectDTO));
    }

    @PostMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Create project")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> createProject(@RequestBody ProjectDTO projectDTO) throws TicketingProjectException {
        ProjectDTO dto = projectService.save(projectDTO);
        return ResponseEntity.ok(new ResponseWrapper("Project is created", dto));
    }

    @PutMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Update project")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO projectDTO) throws TicketingProjectException {
        ProjectDTO dto = projectService.save(projectDTO);
        return ResponseEntity.ok(new ResponseWrapper("Project is updated", dto));
    }

    @DeleteMapping("/{projectcode}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Delete project")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable String projectcode) throws TicketingProjectException {
        projectService.delete(projectcode);
        return ResponseEntity.ok(new ResponseWrapper("Project is deleted"));
    }

    @PutMapping("/complete/{projectcode}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Complete project")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> completeProject(@PathVariable String projectcode) throws TicketingProjectException {
        ProjectDTO dto = projectService.complete(projectcode);
        return ResponseEntity.ok(new ResponseWrapper("Project is completed", dto));
    }

    @GetMapping("/details")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read all projects with details")
    @PreAuthorize("hasAnyAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllByDetails() throws TicketingProjectException {
        List<ProjectDTO> list = projectService.listAllProjectDetails();
        return ResponseEntity.ok(new ResponseWrapper("Projects are retrieved with details", list));
    }


   // from MVC work
/*

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


*/
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
