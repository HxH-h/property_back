package com.propertysystem.Controller.Pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
public class RegisterDTO {
    private String username;
    private String password;
    private String email;
    private String code;
    private boolean role;
}
