package com.cricket.auction.entity;

import jakarta.persistence.*;

@Entity
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@Table(name = "tblPlayerSkill")
public class PlayerSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String skillName;
}
