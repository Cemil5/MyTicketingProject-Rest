package com.cydeo.implementation;

import com.cydeo.dto.RoleDTO;
import com.cydeo.entity.Role;
import com.cydeo.mapper.RoleMapper;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {


    private RoleRepository roleRepository;
    private RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper){
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<RoleDTO> listAllRoles() {
        List<Role> list = roleRepository.findAll();
        System.out.println("list.toString() = " + list.get(0).getDescription());
        // convert to DTO and return it
        return list.stream().map(obj -> {return roleMapper.convertToDto(obj);}).collect(Collectors.toList());
      //  return list.stream().map(obj -> {return roleMapper.convertToDto(Optional.ofNullable(obj));}).collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(Long id) {
        Role role = roleRepository.findById(id).get();
        return roleMapper.convertToDto(role);
    }
}
