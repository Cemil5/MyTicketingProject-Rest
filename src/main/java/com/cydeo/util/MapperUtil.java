package com.cydeo.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

// we only need this mapper not others in this package
@Component
public class MapperUtil {

    private final ModelMapper modelMapper;

    public MapperUtil(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

//    public<T>  T convertToEntity(Object objectToConverted, T convertedObject){
//        return (T) modelMapper.map(objectToConverted, convertedObject.getClass());
//    }
//
//    public <T> T convertToDTO(Object objectToConverted, T convertedObject){
//        return (T) modelMapper.map(objectToConverted, convertedObject.getClass());
//    }

    public<T>  T convert(Object objectToConverted, T convertedObject){
        return (T) modelMapper.map(objectToConverted, convertedObject.getClass());
    }

}
