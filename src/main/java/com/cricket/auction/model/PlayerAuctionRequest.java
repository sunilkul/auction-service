package com.cricket.auction.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerAuctionRequest {
    // Getters and setters
    @NotNull(message = "Player ID is required")
    private Integer playerId;

    @NotNull(message = "Team ID is required")
    private Integer teamId;

    @NotNull(message = "Sold price is required")
    @Min(value = 0, message = "Sold price must be non-negative")
    private Integer soldPrice;

    @NotBlank(message = "Status is required")
    private String status;

}

