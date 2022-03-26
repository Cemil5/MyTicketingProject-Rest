package com.cydeo.repository;

import com.cydeo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByDescription(String description);

    List<Role> findAllByIdAfter(Long id);
    void removeAllByIdAfter(Long id);
}
