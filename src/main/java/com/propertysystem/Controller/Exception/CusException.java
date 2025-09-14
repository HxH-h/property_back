package com.propertysystem.Controller.Exception;

import com.propertysystem.Constant.Code;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CusException extends Exception{
    private Code code;
}

