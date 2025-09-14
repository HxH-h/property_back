package com.propertysystem.Controller.Pojo;

import lombok.Data;

@Data
public class UserVO {
    String uuid;
    String username;
    String email;
    boolean role;
    String avatar;
    String create_time;

}
