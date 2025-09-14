package com.propertysystem.Controller.Exception;


import com.propertysystem.Controller.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(CusException.class)
    @ResponseBody
    public Result cusExpHandler(CusException e) {
        log.info(e.getMessage());
        return new Result(e.getCode());
    }
}
