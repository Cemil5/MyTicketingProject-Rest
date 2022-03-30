package com.cydeo.implementation;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.util.MapperUtil;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.repository.TaskRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.AccessControlException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;
    private MapperUtil mapperUtil;
    private PasswordEncoder passwordEncoder;


    @Override
    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll(Sort.by("firstName"));
        // convert to dto
        return list.stream().map(obj -> {return mapperUtil.convert(obj, new UserDTO());}).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) throws AccessDeniedException {
        User user = userRepository.findByUserName(username);
        checkForAuthorities(user);
        return mapperUtil.convert(user, new UserDTO());
    }

    @Override
    public UserDTO save(UserDTO dto) throws TicketingProjectException {
        User foundedUser = userRepository.findByUserName(dto.getUserName());
        if(foundedUser!=null){
            throw new TicketingProjectException("This username already exists!");
        }

        // dto.setEnabled(true);   // isEnabled variable becomes true. checks sign up confirmations by email

        User user = mapperUtil.convert(dto, new User());
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));
        User saved = userRepository.save(user);
        return mapperUtil.convert(saved, new UserDTO());
    }

    @Override
    public UserDTO update(UserDTO dto) throws TicketingProjectException, AccessDeniedException {
        User user = userRepository.findByUserName(dto.getUserName());

        checkForAuthorities(user);
        if(user==null){
            throw new TicketingProjectException("This username does not exist!");
        }
        if (!user.getEnabled()){
            throw new TicketingProjectException("User is not confirmed. Please, update after confirmation");
        }

        User convertedUser = mapperUtil.convert(dto, new User());
        convertedUser.setId(user.getId());
        convertedUser.setPassWord(passwordEncoder.encode(dto.getPassWord()));
        userRepository.save(convertedUser);
        return findByUserName(dto.getUserName());
    }

    @Override
    public void delete(String username) throws TicketingProjectException {
        User user = userRepository.findByUserName(username);
        if(user==null){
            throw new TicketingProjectException("This username does not exist!");
        }
      deleteByUsername(username);
    }

    // hard delete is not preferred, and we use soft delete below
    @Override
    public void deleteByUsername(String username) throws TicketingProjectException {
        User user = userRepository.findByUserName(username);

        if(user == null) throw new TicketingProjectException("User does not exist");

        if (!checkIfUserCanBeDeleted(user)) {
            throw new TicketingProjectException("User can not be deleted. It is linked by a project or task or only admin");
        }
        user.setUserName(user.getUserName()+ "-"+user.getId());
        user.setIsDeleted(true);
        userRepository.save(user);

    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(role);
        return users.stream().map(obj -> {return mapperUtil.convert(obj, new UserDTO());}).collect(Collectors.toList());
    }

    @Override
    public Boolean checkIfUserCanBeDeleted(User user) {
        switch (user.getRole().getDescription().toLowerCase(Locale.ROOT)){
            case "manager":
           //     Project project = projectRepository.findByProjectCode("P004");
                List<Project> projects = projectRepository.findAllByAssignedManager(user);
                return projects.size() == 0;
            case "employee":
                List<Task> tasks = taskRepository.findAllByAssignedEmployee(user);
                return tasks.size() == 0;
            default:
                List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(user.getRole().getDescription());
                return users.size() > 1;
        }
    }

    @Override
    public UserDTO confirm(User user) {
        user.setEnabled(true);
        User confirmedUser = userRepository.save(user);
        return mapperUtil.convert(confirmedUser, new UserDTO());
    }

    private void checkForAuthorities(User user) throws AccessDeniedException {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // !authentication.getName().equals("anonymousUser") : if you are not log in the system
        if (authentication != null && !authentication.getName().equals("anonymousUser")){
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            // if logged user's id and user's id written in inputbox does not match:
            if (!(authentication.getName().equals(user.getId().toString())) || roles.contains("Admin")){
                throw new AccessDeniedException("Access is denied");
            }
        }
    }
}
