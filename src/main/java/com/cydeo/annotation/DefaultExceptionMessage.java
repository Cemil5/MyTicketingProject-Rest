package com.cydeo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // annotation at method level
@Retention(RetentionPolicy.RUNTIME) // it will be active during runtime
public @interface DefaultExceptionMessage {

    String defaultMessage() default "";

}
