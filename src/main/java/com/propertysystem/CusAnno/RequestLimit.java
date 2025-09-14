package com.propertysystem.CusAnno;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD , ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {
    int interval() default 1000;  // ms
    int count() default 10;
    int blockTime() default 1;    //min
}
