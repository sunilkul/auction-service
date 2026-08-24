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
    private Status playerStatus; // SOLD, UNSOLD, NOT_ASSIGNED, ASSIGNED

    // Example stats as JSON string (or use a separate table for normalized stats)
    @Column(columnDefinition = "nvarchar(4000)")
    private String playerStats;
    private Integer isNewPlayer;
    private Integer tournamentId;
    private Integer isConsiderInAuction;
    private String groupCode;
    // getters/setters
    public enum Status { SOLD, UNSOLD, NOT_ASSIGNED, ASSIGNED }
}