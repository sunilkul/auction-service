package com.cricket.auction.model;

import com.cricket.auction.entity.Player;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PlayerResponse {
    private Integer id;
    private String name;
    private String photo;
    private Integer basePrice;
    private Map<String,Object> stats;
    private String status; // SOLD, UNSOLD
    private Integer skillId;
    private String skillName;
    private Integer soldPrice;
    private Integer teamId;
    private String teamName;
    private Integer isNewPlayer;
}
