package com.cydeo.implementation;

import com.cydeo.dto.RoleDTO;
import com.cydeo.entity.Role;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.util.MapperUtil;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {


    private RoleRepository roleRepository;
    private MapperUtil mapperUtil;

    @Override
    public List<RoleDTO> listAllRoles() {
        List<Role> list = roleRepository.findAll();
        System.out.println("list.toString() = " + list.get(0).getDescription());
        // convert to DTO and return it
        return list.stream().map(obj -> mapperUtil.convert(obj, new RoleDTO())).collect(Collectors.toList());
      //  return list.stream().map(obj -> {return roleMapper.convertToDto(Optional.ofNullable(obj));}).collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(Long id) throws TicketingProjectException {
        Role role = roleRepository.findById(id).orElseThrow(() -> new TicketingProjectException("Role does not exists"));
        return mapperUtil.convert(role, new RoleDTO());
    }
}
