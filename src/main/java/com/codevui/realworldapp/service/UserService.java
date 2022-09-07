package com.codevui.realworldapp.service;

import java.util.Map;

import com.codevui.realworldapp.exception.custom.CustomBadRequestException;
import com.codevui.realworldapp.exception.custom.CustomNotFoundException;
import com.codevui.realworldapp.model.profile.dto.ProfileDTOResponse;
import com.codevui.realworldapp.model.user.dto.UserDTOCreate;
import com.codevui.realworldapp.model.user.dto.UserDTOLoginRequest;
import com.codevui.realworldapp.model.user.dto.UserDTOResponse;

public interface UserService {

    public Map<String, UserDTOResponse> authenticate(Map<String, UserDTOLoginRequest> userLoginRequestMap) throws CustomBadRequestException;

    public Map<String, UserDTOResponse> registerUser(Map<String, UserDTOCreate> userDTOCreateMap);

    public Map<String, UserDTOResponse> getCurrentUser() throws CustomNotFoundException;

    public Map<String, ProfileDTOResponse> getProfile(String username) throws CustomNotFoundException;

    public Map<String, ProfileDTOResponse> followUser(String username) throws CustomNotFoundException;

    public Map<String, ProfileDTOResponse> unfollowUser(String username) throws CustomNotFoundException;

    
}
