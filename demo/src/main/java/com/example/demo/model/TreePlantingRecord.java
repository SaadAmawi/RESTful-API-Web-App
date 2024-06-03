package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name="Trees")
public class TreePlantingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//    @Column(nullable = false)
//    private String username;

    @Column(nullable = true)
    private LocalDate plantingDate;

    @Column(nullable = false)
    private String gpsLocation;

    @Column(nullable = false)
    private String treeSpecies;

    @Column(name = "file_hash", nullable = false, unique = true)
    private String fileHash;

    @Lob
    @Column(name = "image_file", nullable = false, columnDefinition="BLOB",unique = true)
    private byte[] photo;
}

