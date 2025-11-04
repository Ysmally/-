package com.example.word.model;

import jakarta.persistence.*;

@Entity
public class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 200)
    private String text;

    @Column(length = 1000)
    private String meaning;



}
