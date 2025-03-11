package com.example.oneid.web;
import com.example.oneid.model.User;
import com.example.oneid.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class VerificationController{

    private final UserService userService;

    public VerificationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/verifyLogin")
    public ResponseEntity<Map<String, Object>> verifyLogin(@RequestBody Map<String, String> requestData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String username = requestData.get("username");
        String encryptedData = requestData.get("encryptedData");
        String randomString = requestData.get("randomString");
        User user = userService.findByUsername(username).orElse(null);

        byte[] signature = Base64.getDecoder().decode(encryptedData);
        if (user == null) {
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);

        } else {
            String publicKey = user.getPublicKey();
            if (userService.authenticateUserFromLogin(signature, randomString, publicKey)) {
                System.out.println("success");
                session.setAttribute("username",username);
                response.put("success", username);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }
    }
}
