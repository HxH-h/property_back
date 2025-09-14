package com.propertysystem.Dao.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    String uuid;
    String username;
    String password;
    String email;
    boolean role;
    String create;
}
