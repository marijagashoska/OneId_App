package com.example.oneid.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String username;
    private String embg;
    @Column(length = 4096)
    private String publicKey;
    @Lob
    private byte[] faceScan;


    public User(String name, String surname,String embg,String publicKey,String username,byte[] faceScan) {
        this.name = name;
        this.surname = surname;
        this.embg=embg;
        this.publicKey=publicKey;
        this.username=username;
        this.faceScan=faceScan;
    }


}
