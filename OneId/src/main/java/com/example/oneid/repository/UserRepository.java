package com.example.oneid.repository;

import com.example.oneid.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.embg = :embg")
    Optional<User> findByEmbg(String embg);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.username = :username")
    void deleteByUsername(String username);
}