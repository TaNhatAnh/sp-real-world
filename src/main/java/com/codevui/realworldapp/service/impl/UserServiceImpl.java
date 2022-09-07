package com.codevui.realworldapp.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.codevui.realworldapp.entity.User;
import com.codevui.realworldapp.exception.custom.CustomBadRequestException;
import com.codevui.realworldapp.exception.custom.CustomNotFoundException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codevui.realworldapp.model.CustomError;
import com.codevui.realworldapp.model.profile.dto.ProfileDTOResponse;
import com.codevui.realworldapp.model.user.dto.UserDTOCreate;
import com.codevui.realworldapp.model.user.dto.UserDTOLoginRequest;
import com.codevui.realworldapp.model.user.dto.UserDTOResponse;
import com.codevui.realworldapp.model.user.mapper.UserMapper;
import com.codevui.realworldapp.repository.UserRepository;
import com.codevui.realworldapp.service.UserService;
import com.codevui.realworldapp.util.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Map<String, UserDTOResponse> authenticate(Map<String, UserDTOLoginRequest> userLoginRequestMap)
            throws CustomBadRequestException {
        UserDTOLoginRequest userDTOLoginRequest = userLoginRequestMap.get("user");

        Optional<User> userOptional = userRepository.findByEmail(userDTOLoginRequest.getEmail());

        boolean isAuthen = false;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(userDTOLoginRequest.getPassword(), user.getPassword())) {
                isAuthen = true;
            }
        }
        if (!isAuthen) {
            throw new CustomBadRequestException(
                    CustomError.builder().code("400").message("User name and password incorrect").build());
        }

        return buildDTOResponse(userOptional.get());
    }

    @Override
    public Map<String, UserDTOResponse> registerUser(Map<String, UserDTOCreate> userDTOCreateMap) {
        UserDTOCreate userDTOCreate = userDTOCreateMap.get("user");
        User user = UserMapper.toUser(userDTOCreate);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return buildDTOResponse(user);
    }

    private Map<String, UserDTOResponse> buildDTOResponse(User user) {
        Map<String, UserDTOResponse> wrapper = new HashMap<>();
        UserDTOResponse userDTOResponse = UserMapper.toUserDTOResponse(user);
        userDTOResponse.setToken(jwtTokenUtil.generateToken(user, 24 * 60 * 60));
        wrapper.put("user", userDTOResponse);
        return wrapper;
    }

    @Override
    public Map<String, UserDTOResponse> getCurrentUser() throws CustomNotFoundException {
        User userLoggedin = getUserLoggedin();
        if(userLoggedin!=null){
            return buildDTOResponse(userLoggedin);
        }
        throw new CustomNotFoundException(CustomError.builder().code("404").message("user not exist").build());
    }

    @Override
    public Map<String, ProfileDTOResponse> getProfile(String username) throws CustomNotFoundException {
        User userLoggedin = getUserLoggedin();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new CustomNotFoundException(CustomError.builder().code("404").message("User not found").build());
        }
        User user = userOptional.get();
        Set<User> followers = user.getFollowers();
        boolean isFollowing = false;
        for (User u : followers) {
            if(u.getId() == userLoggedin.getId()){
                isFollowing =true;
                break;
            }
        }
        return buildProfileResponse(userOptional.get(), isFollowing);
    }

    public User getUserLoggedin(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userRepository.findByEmail(email).get();
            return user;
        }
        return null;
    }

    private Map<String, ProfileDTOResponse> buildProfileResponse(User user, boolean isFollowing) {
        Map<String, ProfileDTOResponse> wrapper = new HashMap<>();
        ProfileDTOResponse profileDTOResponse = ProfileDTOResponse.builder().username(user.getUsername())
                .bio(user.getBio()).image(user.getImage()).following(isFollowing).build();
        wrapper.put("profile", profileDTOResponse);
        return wrapper;
    }

    @Override
    public Map<String, ProfileDTOResponse> followUser(String username) throws CustomNotFoundException {
        User userLoggedin = getUserLoggedin();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new CustomNotFoundException(CustomError.builder().code("404").message("User not found").build());
        }
        User user = userOptional.get();
        Set<User> followers = user.getFollowers();
        boolean isFollowing = false;
        for (User u : followers) {
            if(u.getId() == userLoggedin.getId()){
                isFollowing =true;
                break;
            }
        }
        if(!isFollowing){
            isFollowing = true;
            user.getFollowers().add(userLoggedin);
            user=userRepository.save(user);
            isFollowing = true;
        }
        return buildProfileResponse(userOptional.get(), isFollowing);
    }

    @Override
    public Map<String, ProfileDTOResponse> unfollowUser(String username) throws CustomNotFoundException {
        User userLoggedin = getUserLoggedin();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new CustomNotFoundException(CustomError.builder().code("404").message("User not found").build());
        }
        User user = userOptional.get();
        Set<User> followers = user.getFollowers();
        boolean isFollowing = false;
        for (User u : followers) {
            if(u.getId() == userLoggedin.getId()){
                isFollowing =true;
                break;
            }
        }
        if(isFollowing){
            isFollowing = true;
            user.getFollowers().remove(userLoggedin);
            user=userRepository.save(user);
            isFollowing = false;
        }
        return buildProfileResponse(userOptional.get(), isFollowing);
    }
    
}
