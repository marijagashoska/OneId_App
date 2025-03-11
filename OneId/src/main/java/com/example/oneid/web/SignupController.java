package com.example.oneid.web;

import com.example.oneid.model.User;
import com.example.oneid.service.UserService;
import com.example.oneid.service.impl.OCRService;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Map;

@Controller
@SessionAttributes("privateKey")
public class SignupController {

    private final OCRService ocrService;
    private final UserService userService;

    public SignupController(OCRService ocrService, UserService userService) {
        this.ocrService = ocrService;
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String showSignUpForm() {
        return "signup";
    }
    @PostMapping("/signup")
    public String handleSignup(@RequestParam("fileInput") MultipartFile idCard, @RequestParam("faceImageBase64") String faceScan, @RequestParam(name = "username") String username,@RequestParam(name = "name") String name, @RequestParam(name = "surname") String surname, @RequestParam(name = "embg") String embg, Model model,HttpSession session) {
      try {
          if(!(userService.findByUsername(username).isEmpty()) || !(userService.findByEmbg(embg).isEmpty())){
              model.addAttribute("existingUser", "User already exists.");
              return "signup";
          }
          name=name.toUpperCase();
          surname=surname.toUpperCase();
          JSONObject ocrResult = ocrService.processImage(idCard,name,surname,embg);
          String faceBase64 = ocrResult.optString("face", null);
          String alternativeValue;
          if(faceBase64 == null || faceBase64.isEmpty()){
              for (String key : ocrResult.keySet()) {
                  if (!key.equals("face")) {  // Ignore "face" and take another key's value
                      alternativeValue = ocrResult.optString(key);
                      model.addAttribute("alertMessage", alternativeValue);
                      return "signup";
                  }
              }
          }

          faceScan=faceScan.split(",")[1];
          byte[] idFaceScanBytes = Base64.getDecoder().decode(faceBase64);
          byte[] scannedFaceBytes = Base64.getDecoder().decode(faceScan);

          boolean isAuthenticated = userService.authenticateUser(idFaceScanBytes,scannedFaceBytes);
          if (isAuthenticated) {
              String privateKeyEncoded=userService.createUser(name,surname,embg,username,idCard);
              session.setAttribute("privateKey", privateKeyEncoded);
              session.setAttribute("username",username);
              return "redirect:/keyPage";
          } else {
              model.addAttribute("error", "Invalid username, password, or face scan.");
              return "signup";
          }
        } catch (Exception e) {
            model.addAttribute("ocrText", "Error processing image.");
            return "signup";
        }
    }
    @GetMapping("/keyPage")
    public String showKeyPage(@SessionAttribute(name = "privateKey", required = false) String privateKey) {
        if (privateKey == null) {
            return "redirect:/signup";
        }
        return "keyPage";
    }
    @PostMapping("/clear-session")
    public String clearSession(@RequestBody Map<String, Object> payload, SessionStatus sessionStatus, HttpSession session) {
        boolean checked = (boolean) payload.get("checked");
        String username = (String) payload.get("username");
        System.out.println(checked);

        if(!checked){
            User user=userService.findByUsername(username).orElse(null);
            if(user!=null){
                userService.removeUser(user);
            }
        }
        session.removeAttribute("privateKey");
        session.invalidate();
        sessionStatus.setComplete();
        return "redirect:/login";
    }

}
