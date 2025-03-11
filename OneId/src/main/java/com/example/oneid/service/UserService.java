package com.example.oneid.service;

import com.example.oneid.model.User;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    String createUser(String name, String surname, String embg, String username, MultipartFile idCard);
    boolean authenticateUser(byte[] faceScan,byte[] faceScan1);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmbg(String embg);
    boolean authenticateUserFromLogin(byte[] encryptedData, String randomString, String publicKey);
    void removeUser(User user);
}
