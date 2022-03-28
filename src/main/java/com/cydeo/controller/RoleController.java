package com.cydeo.controller;

import com.cydeo.annotation.DefaultExceptionMessage;
import com.cydeo.dto.RoleDTO;
import com.cydeo.entity.ResponseWrapper;
import com.cydeo.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
@AllArgsConstructor
@Tag(name = "Role Controller", description = "Role API")
public class RoleController {

    private RoleService roleService;

    @GetMapping
    @Operation(summary = "read all roles")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again later")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> readAll(){
        List<RoleDTO> list = roleService.listAllRoles();
        return ResponseEntity.ok(new ResponseWrapper("Retrieved all roles successfully", list));
    }

}
