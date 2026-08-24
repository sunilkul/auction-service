package com.cricket.auction.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@Getter
@Setter
public class TeamResponse {
    private Integer id;
    private String name;
    private String logo;
    private Integer purse;
    private Integer remainingPurse;
    private String poc1;
    private String poc2;
    private Integer tournamentId;
}
