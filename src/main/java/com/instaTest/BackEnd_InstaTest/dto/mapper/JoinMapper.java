package com.instaTest.BackEnd_InstaTest.dto.mapper;

import com.instaTest.BackEnd_InstaTest.dto.JoinDto;
//import org.hibernate.mapping.Join;
import com.instaTest.BackEnd_InstaTest.entity.User;

import jakarta.persistence.Column;

public class JoinMapper {

    public static JoinDto mapToJoinDto(User user) {
        return new JoinDto(
                user.getId(),
                user.getUser_id(),
                user.getUsername(),
                user.getPassword(),
                user.getPasswordConfirm(),
                user.getEmail(),
                user.getProfileImage()
        );
    }

    public static User mapToJoin(JoinDto joinDto) {
        User user = new User();
        user.setId(joinDto.getId());
        user.setUser_id(joinDto.getUser_id());
        user.setUsername(joinDto.getUserName());
        user.setPassword(joinDto.getPassword());
        user.setPasswordConfirm(joinDto.getPasswordConfirm());
        user.setEmail(joinDto.getEmail());
        user.setProfileImage(joinDto.getProfileImage());
        return user;
    }
}
