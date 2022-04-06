package com.cydeo.converter;

import com.cydeo.dto.TaskDTO;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoConverter implements Converter<String, TaskDTO>
{

    @Autowired
    TaskService taskService;

    @Override
    public TaskDTO convert(String source) {
        Long id = Long.parseLong(source);
        try {
            return taskService.findById(id);
        } catch (TicketingProjectException e) {
            e.printStackTrace();
        }
        return null;
    }


}
