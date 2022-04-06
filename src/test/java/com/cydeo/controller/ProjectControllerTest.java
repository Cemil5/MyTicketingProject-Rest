package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.enums.Gender;
import com.cydeo.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJmaXJzdE5hbWUiOiJNaWtlIiwibGFzdE5hbWUiOiJTbWl0aCIsInN1YiI6ImFkbWluQGFkbWluLmNvbSIsImlkIjoxLCJleHAiOjE2NTE4MjA3MjYsImlhdCI6MTY0OTIyODcyNiwidXNlcm5hbWUiOiJhZG1pbkBhZG1pbi5jb20ifQ.68aEHsogQYsD7p6xYA8jvg43tPn0ee9q1eRZIrSO5XM";

    static UserDTO userDTO;
    static ProjectDTO projectDTO;


    @BeforeAll
    static void setUp(){
        userDTO = UserDTO.builder()
                .id(2L)
                .firstName("Mike")
                .lastName("Smith")
                .userName("ozzy@cydeo.com")
                .passWord("Abc1")
                .confirmPassword("Abc1")
                .role(new RoleDTO(2L, "Manager"))
                .gender(Gender.MALE)
                .build();

        projectDTO = ProjectDTO.builder()
                .projectCode("Api1")
                .projectName("Api-ozzy")
                .assignedManager(userDTO)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .projectDetail("Api Test")
                .projectStatus(Status.OPEN)
                .completeTaskCounts(0)
                .inCompleteTaskCounts(0)
                .build();
    }

    @Test
    public void givenNoToken_whenGetSecureRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/project/Api1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenToken_getAllProjects() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/project")
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
              //  .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].projectCode").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].assignedManager.userName").isNotEmpty());
    }

    @Test
    public void givenToken_createProjects() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/project")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJsonString(projectDTO)))
                .andExpect(MockMvcResultMatchers.jsonPath("projectCode").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.assignedManager.userName").isNotEmpty());
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.projectCode").isNotEmpty())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.assignedManager.userName").isNotEmpty());
    }

    protected String toJsonString(final Object obj){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}