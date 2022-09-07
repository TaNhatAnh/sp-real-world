package com.codevui.realworldapp.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codevui.realworldapp.exception.custom.CustomNotFoundException;
import com.codevui.realworldapp.model.profile.dto.ProfileDTOResponse;
import com.codevui.realworldapp.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles/{username}")
public class ProfileController {
    private final UserService userService;

    @GetMapping("")
    public Map<String, ProfileDTOResponse> getProfile(@PathVariable String username) throws CustomNotFoundException{
     return userService.getProfile(username);  
    }

    @PostMapping("/follow")
    public Map<String, ProfileDTOResponse> followUser(@PathVariable String username) throws CustomNotFoundException{
     return userService.followUser(username);  
    }

    @DeleteMapping("/follow")
    public Map<String, ProfileDTOResponse> unfollowUser(@PathVariable String username) throws CustomNotFoundException{
     return userService.unfollowUser(username);  
    }
}
