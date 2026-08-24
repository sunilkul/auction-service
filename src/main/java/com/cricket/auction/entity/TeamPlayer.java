package com.cricket.auction.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@Table(name = "tblTeamPlayer")
public class TeamPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name ="teamId")
    private Integer teamId;

    @Column(name = "playerId")
    private Integer playerId;

    private Integer soldPrice;

    private LocalDateTime soldAt;

    private Integer tournamentId;

}
