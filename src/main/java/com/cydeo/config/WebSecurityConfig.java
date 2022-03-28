package com.cydeo.config;

import com.cydeo.filter.SecurityFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private SecurityFilter securityFilter;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private static final String[] permittedUrls ={
            "/authenticate",
        //    "/create-user",   only user (admin) should have this authority
            "/confirmation",
            "/api/p1/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(permittedUrls)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

    }


    // UI security part
   /* private SecurityService securityService;
    private AuthSuccessHandler authSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // request should be authorized
                .antMatchers("/", "/login", "/fragments/**", "/assets/**", "/images/**").permitAll()
               // .antMatchers("/user/**").authenticated()
                .antMatchers("/user/**").hasAuthority("Admin")
                .antMatchers("/project/**").hasAnyAuthority("Admin", "Manager")
                .antMatchers("/task/**").hasAnyAuthority("Admin", "Manager")
                .antMatchers("/task/**", "/task/pending-task","/task/pending-task/**", "/task/pending-task-edit/**", "/task/archive/**")
                    .hasAnyAuthority("Admin", "Employee", "Manager")
                .and()
                .formLogin()
                    .loginPage("/login")
                 //   .defaultSuccessUrl("/welcome")
                    .successHandler(authSuccessHandler)
                    .failureUrl("/login?error=true")
                    .permitAll()
                .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    //   .logoutSuccessUrl("/login");      // works without "You have been logout." message
                    .logoutSuccessUrl("/login?logout=true")
                .and()
                .rememberMe()
                    .tokenValiditySeconds(120) // most app don't have this, so you can stay logged in after clicking remember me
                    .key("cydeoSecret")
                    .userDetailsService(securityService);   // remembers specific user.
    }*/

    /*  @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/login",
                        "/fragments/**",
                        "/assets/**",
                        "/images/**"
                ).permitAll()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/welcome")
                    .failureUrl("/login?error=true")
                    .permitAll()
                .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login");
    }*/

    // we do this part with data generator class
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .inMemoryAuthentication()
//                .withUser("admin").password(passwordEncoder().encode("admin123")).roles("ADMIN")
//                .and()
//                .withUser("ozzy").password(passwordEncoder().encode("ozzy123")).roles("USER")
//                .and()
//                .withUser("manager").password(passwordEncoder().encode("manager123")).roles("MANAGER");
//    }


}
