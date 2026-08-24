package com.cricket.auction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoolStatus {
    private Integer skillId;
    private String skillName;
    private String poolCode;
    private Integer total;
    private Integer sold;
    private Integer remaining;
    private Boolean isComplete;
}
