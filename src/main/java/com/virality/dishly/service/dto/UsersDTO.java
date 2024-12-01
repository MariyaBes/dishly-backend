package com.virality.dishly.service.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class UsersDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    private String role;

    private String verificationStatus;
    private String status;
}
