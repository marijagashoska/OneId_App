package com.example.oneid.service.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;

@Service
public class OCRService {
    public JSONObject processImage(MultipartFile file, String name, String surname, String embg) throws Exception {
        byte[] imageBytes = file.getBytes();
        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

        JSONObject inputJson = new JSONObject();
        inputJson.put("image", imageBase64);
        inputJson.put("name", name);
        inputJson.put("surname", surname);
        inputJson.put("embg", embg);

        String pythonPath = "src/main/resources/scripts/extract_text_and_image.py";
        String pythonExe="C:\\Users\\Lenovo\\anaconda3\\python.exe";
        ProcessBuilder pb = new ProcessBuilder(pythonExe, pythonPath);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        OutputStream os = process.getOutputStream();
        os.write(inputJson.toString().getBytes());
        os.flush();
        os.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Python script for ID card extraction exited with code: " + exitCode);

        if (output.toString().trim().isEmpty()) {
            throw new Exception("Python script returned no output");
        }
        else{
            String data=output.toString();
            int jsonStart = data.indexOf('{');
            if (jsonStart != -1) {
                data = data.substring(jsonStart);
            }
            try {
                JSONObject result = new JSONObject(data);
                return result;
            } catch (Exception e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
                e.printStackTrace();
                throw new Exception("Invalid JSON received from Python");
            }

        }

    }
}
