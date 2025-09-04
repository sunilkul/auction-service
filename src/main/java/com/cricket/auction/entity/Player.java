package com.cricket.auction.entity;


import jakarta.persistence.*;

@Entity
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@Table(name = "tblPlayer")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String playerName;
    private Integer skillId;
    private String photo;
    private Integer basePrice;
    @Enumerated(EnumType.STRING)
    private Status playerStatus; // SOLD, UNSOLD

    // Example stats as JSON string (or use a separate table for normalized stats)
    @Column(columnDefinition = "nvarchar(4000)")
    private String playerStats;
    // getters/setters
    public enum Status { SOLD, UNSOLD }
}