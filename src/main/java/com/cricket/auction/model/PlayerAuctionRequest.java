package com.cricket.auction.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PlayerAuctionRequest {
    @NotNull(message = "Player ID is required")
    private Integer playerId;

    @NotNull(message = "Team ID is required")
    private Integer teamId;

    @NotNull(message = "Sold price is required")
    @Min(value = 0, message = "Sold price must be non-negative")
    private Integer soldPrice;

    @NotBlank(message = "Status is required")
    private String status;

    // Getters and setters
    public Integer getPlayerId() { return playerId; }
    public void setPlayerId(Integer playerId) { this.playerId = playerId; }
    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public Integer getSoldPrice() { return soldPrice; }
    public void setSoldPrice(Integer soldPrice) { this.soldPrice = soldPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

