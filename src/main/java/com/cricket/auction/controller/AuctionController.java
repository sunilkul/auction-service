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
import com.cricket.auction.service.AuctionPoolService;
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
    @Autowired private AuctionPoolService auctionPoolService;

    @GetMapping("/teams")
    @Operation(summary = "Get all teams", description = "Returns a list of all teams participating in the auction.")
    public List<TeamResponse> getAllTeams() {
        return teamService.getTeams();
    }

    @GetMapping("/players")
    @Operation(summary = "Get all players", description = "Returns a list of all players available for auction.")
    public List<PlayerResponse> getAllPlayers() {

        //return playerService.getPlayersInfo();
        return playerService.getNonAuctionedPlayers();
    }

    @PostMapping("/players/auction")
    @Operation(summary = "Update player auction status", description = "Updates the auction status of a player (SOLD/UNSOLD) and assigns them to a team. This endpoint works across all pools.")
    public Player updatePlayerAuction(@Valid @RequestBody PlayerAuctionRequest request) {
        return auctionService.updatePlayerStatus(
            request.getPlayerId(),
            request.getTeamId(),
            request.getSoldPrice(),
            request.getStatus()
        );
    }

    @GetMapping("/auction/next-pool-players")
    @Operation(summary = "Get next pool players for skill", description = "Returns players from the next active pool for a skill. Use this to automatically get the next pool to auction.")
    public List<PlayerResponse> getNextPoolPlayers(
            @RequestParam @Parameter(description = "Skill ID") Integer skillId,
            @RequestParam @Parameter(description = "Tournament ID") Integer tournamentId) {
        return auctionPoolService.getNextPoolPlayers(skillId, tournamentId);
    }
}