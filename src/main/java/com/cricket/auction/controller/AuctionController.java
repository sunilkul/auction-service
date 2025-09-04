package com.cricket.auction.controller;

import com.cricket.auction.entity.Player;
import com.cricket.auction.entity.Team;
import com.cricket.auction.model.PlayerAuctionRequest;
import com.cricket.auction.model.PlayerResponse;
import com.cricket.auction.model.PlayerResponseProjection;
import com.cricket.auction.model.TeamResponse;
import com.cricket.auction.repository.PlayerRepository;
import com.cricket.auction.repository.TeamRepository;
import com.cricket.auction.service.AuctionService;
import com.cricket.auction.service.PlayerService;
import com.cricket.auction.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Auction API", description = "Endpoints for cricket auction operations")
public class AuctionController {
    @Autowired
    private TeamRepository teamRepo;
    @Autowired private PlayerRepository playerRepo;
    @Autowired private AuctionService auctionService;
    @Autowired private PlayerService playerService;
    @Autowired private TeamService teamService;

    @GetMapping("/teams")
    @Operation(summary = "Get all teams", description = "Returns a list of all teams participating in the auction.")
    public List<TeamResponse> getAllTeams() {
        return teamService.getTeams();
    }

    @GetMapping("/players")
    @Operation(summary = "Get all players", description = "Returns a list of all players available for auction.")
    public List<PlayerResponse> getAllPlayers() {
        return playerService.getPlayersInfo();
    }

    @PostMapping("/players/auction")
    @Operation(summary = "Update player auction status", description = "Updates the auction status of a player (SOLD/UNSOLD) and assigns them to a team.")
    public Player updatePlayerAuction(@Valid @RequestBody PlayerAuctionRequest request) {
        return auctionService.updatePlayerStatus(
            request.getPlayerId(),
            request.getTeamId(),
            request.getSoldPrice(),
            request.getStatus()
        );
    }
}