package com.cydeo.implementation;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.entity.common.UserPrincipal;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private UserService userService;
    private MapperUtil mapperUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = userService.findByUserName(username);
        // if don't have this and user not exits, our app crash
        if (user == null){
            throw new UsernameNotFoundException("This username does not exist");
        }
//        return new UserPrincipal(user);   // from MVC security
//        return new org.springframework.security.core.userdetails.User(foundUser.getUsername(), foundUser.getPassword(),
//                listAuthorities(foundUser));  // from API security module
        return new org.springframework.security.core.userdetails.User(user.getId().toString(), user.getPassWord(), listAuthorities(user));
    }

    public User loadUser(String value){
        UserDTO dto = userService.findByUserName(value);
        return mapperUtil.convert(dto, new User());
    }

    // we added this because we don't use UserPrincipal for api security
    private Collection<? extends GrantedAuthority> listAuthorities(UserDTO user){
        List<GrantedAuthority> authorityList = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getDescription());
        authorityList.add(authority);
        return authorityList;
    }


}
