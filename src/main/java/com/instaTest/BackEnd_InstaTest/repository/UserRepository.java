package com.instaTest.BackEnd_InstaTest.repository;

import com.instaTest.BackEnd_InstaTest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);  //username 중복 여부를 검사하는 쿼리 메서드

    // 사용자 정보 조회
    User findByUsername(String username);
}