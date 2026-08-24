package com.cricket.auction.entity;

import jakarta.persistence.*;

@Entity
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@Table(name = "tblTeam")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String teamName;
    private String logo;
    private Integer purse;
    private Integer remainingPurse;
    private String poc1;
    private String poc2;
    private Integer tournamentId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "teamId")
    private java.util.List<TeamPlayer> players;

}