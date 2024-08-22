package com.instaTest.BackEnd_InstaTest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String user_id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "password_confirm", nullable = false)
    private String passwordConfirm;

    @Column(name = "e_mail", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image")
    private String profileImage;

    private String role;


}