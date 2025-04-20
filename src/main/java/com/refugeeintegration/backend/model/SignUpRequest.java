package com.refugeeintegration.backend.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SignUpRequest {
    private String name;
    private String email;
    private LocalDate birthday;
    private String gender;
    private String occupation;
    private String interest;
    private String password;
    private String familyId;
}
