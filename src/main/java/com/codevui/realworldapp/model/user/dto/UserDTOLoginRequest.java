package com.codevui.realworldapp.model.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDTOLoginRequest {
    private String email;
    private String password;
}
