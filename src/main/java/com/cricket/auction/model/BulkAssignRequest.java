package com.cricket.auction.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkAssignRequest {

    @NotNull(message = "Player ID is required")
    private Integer playerId;

    @NotNull(message = "Team ID is required")
    private Integer teamId;
}
