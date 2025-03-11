package com.example.oneid.service.impl;


import com.example.oneid.config.KeyGenerationUtil;
import com.example.oneid.model.User;
import com.example.oneid.repository.UserRepository;
import com.example.oneid.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    private EntityManager entityManager;
    @Override
    public String createUser(String name, String surname, String embg, String username, MultipartFile idCard) {
        KeyPair keyPair = null;
        String privateKeyEncoded;
        String publicKeyEncoded;
        try {
            keyPair = KeyGenerationUtil.generateKeyPair();
            privateKeyEncoded=KeyGenerationUtil.privateKeyToPem(keyPair.getPrivate());
            publicKeyEncoded=KeyGenerationUtil.publicKeyToPem(keyPair.getPublic());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        User user= null;
        try {
            user = new User(name,surname,embg,publicKeyEncoded,username, idCard.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.userRepository.save(user);
        return privateKeyEncoded;
    }
    public boolean authenticateUserFromLogin(byte[] encryptedData, String randomString, String publicKey) {
        try {

            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            PublicKey publicKey1=keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance("RSASSA-PSS");
            signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
            signature.initVerify(publicKey1);
            signature.update(randomString.getBytes(StandardCharsets.UTF_8));

            boolean verified=signature.verify(encryptedData);

            return verified;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void removeUser(User user) {
        userRepository.deleteByUsername(user.getUsername());
    }

    @Override
    public Optional<User> findByUsername(String username) {
        User user=userRepository.findByUsername(username).orElse(null);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmbg(String embg) {
        User user=userRepository.findByEmbg(embg).orElse(null);
        return Optional.ofNullable(user);
    }

    public boolean authenticateUser(byte[] faceScan, byte[] faceScan1) {
        try {

            File storedFaceFile = saveImage(faceScan, "stored_face");
            File providedFaceFile = saveImage(faceScan1, "provided_face");
            String pythonPath = "src/main/resources/scripts/face-comparison.py";
            String pythonExe="C:\\Users\\Lenovo\\anaconda3\\python.exe";
            ProcessBuilder pb = new ProcessBuilder(pythonExe, pythonPath,storedFaceFile.getAbsolutePath() , providedFaceFile.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process process = pb.start();


            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String temp="";
            while ((line = reader.readLine()) != null) {
                temp=line;
                System.out.println(line);
            }
            int exitCode=process.waitFor();
            System.out.println("Python script for face comparison exited with exit code " + exitCode);
            storedFaceFile.delete();
            providedFaceFile.delete();

            if(temp.equals("Faces match!")){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private File saveImage(byte[] imageBytes, String filename) throws IOException {
        Path path = Files.createTempFile(filename, ".png");
        Files.write(path, imageBytes, StandardOpenOption.CREATE);
        return path.toFile();
    }


}
