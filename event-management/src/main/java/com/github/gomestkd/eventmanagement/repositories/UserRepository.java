package com.github.gomestkd.eventmanagement.repositories;

import com.github.gomestkd.eventmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username =:username" )
    User findByUsername(@Param("username") String username);
}
