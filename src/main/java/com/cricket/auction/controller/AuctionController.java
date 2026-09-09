package com.cricket.auction.controller;

import com.cricket.auction.entity.Player;
import com.cricket.auction.model.BulkAssignRequest;
import com.cricket.auction.model.PlayerAuctionRequest;
import com.cricket.auction.model.PlayerResponse;
import com.cricket.auction.model.PlayerGroupingResponse;
import com.cricket.auction.model.TeamResponse;
import com.cricket.auction.service.AuctionPoolService;
import com.cricket.auction.service.AuctionService;
import com.cricket.auction.service.PlayerService;
import com.cricket.auction.service.StatsDistributionService;
import com.cricket.auction.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Auction API", description = "Endpoints for cricket auction operations")
public class AuctionController {

    private final AuctionService auctionService;
    private final PlayerService playerService;
    private final TeamService teamService;
    private final AuctionPoolService auctionPoolService;
    private final StatsDistributionService distributionService;

    @Autowired
    public AuctionController(AuctionService auctionService, PlayerService playerService, TeamService teamService,
                             AuctionPoolService auctionPoolService, StatsDistributionService distributionService) {
        this.auctionService = auctionService;
        this.playerService = playerService;
        this.teamService = teamService;
        this.auctionPoolService = auctionPoolService;
        this.distributionService = distributionService;
    }

    @GetMapping("/teams")
    @Operation(summary = "Get all teams", description = "Returns a list of all teams participating in the auction.")
    public List<TeamResponse> getAllTeams() {
        return teamService.getTeams();
    }

    @GetMapping("/players")
    @Operation(summary = "Get all players", description = "Returns a list of all players available for auction.")
    public List<PlayerResponse> getAllPlayers() {
        return playerService.getNonAuctionedPlayers();
    }

    @GetMapping("/players/last-sold")
    @Operation(summary = "Get last sold players", description = "Returns details of the most recent sold players from the active tournament. Provide an optional count to limit the number of records; omit it to fetch all sold players.")
    public List<PlayerResponse> getLastSoldPlayers(@RequestParam(required = false, defaultValue = "5") @Parameter(description = "Number of records to fetch") Integer count) {
        return playerService.getLastSoldPlayers(count);
    }

    @PostMapping("/players/auction")
    @Operation(summary = "Update player auction status", description = "Updates the auction status of a player (SOLD/UNSOLD) and assigns them to a team. This endpoint works across all pools.")
    public Player updatePlayerAuction(@Valid @RequestBody PlayerAuctionRequest request) {
        return auctionService.updatePlayerStatus(request.getPlayerId(), request.getTeamId(), request.getSoldPrice(), request.getStatus());
    }

    @PostMapping("/players/reset-auction")
    @Operation(summary = "Reset player auction status", description = "Resets the auction status of a player, making them available for auction again.")
    public Player resetPlayerAuction(@RequestParam @Parameter(description = "Player ID") Integer playerId) {
        return auctionService.resetPlayerAuction(playerId);
    }

    @GetMapping("/auction/next-pool-players")
    @Operation(summary = "Get next pool players for skill", description = "Returns players from the next active pool for a skill. Use this to automatically get the next pool to auction.")
    public List<PlayerResponse> getNextPoolPlayers(@RequestParam @Parameter(description = "Skill ID") Integer skillId, @RequestParam @Parameter(description = "Tournament ID") Integer tournamentId) {
        return auctionPoolService.getNextPoolPlayers(skillId, tournamentId);
    }

    @PostMapping("/players/bulk-assign")
    @Operation(summary = "Bulk assign players to teams", description = "Assigns multiple players to teams in a single atomic operation. All assignments use ASSIGNED status (no purse deduction). If any assignment fails, all changes are rolled back.")
    public ResponseEntity<Map<String, Object>> bulkAssignPlayers(@Valid @RequestBody List<BulkAssignRequest> assignments) {
        auctionService.bulkAssignPlayers(assignments);
        return ResponseEntity.ok(Map.of("assigned", assignments.size()));
    }

    @PostMapping("/players/update-status")
    @Operation(summary = "Update assigned player status", description = "Moves a player from ASSIGNED to POOLED and reverts assignment side effects by removing the latest team mapping entry.")
    public Player unassignPlayer(@RequestParam @Parameter(description = "Player ID") Integer playerId) {
        return auctionService.unassignPlayer(playerId);
    }

    @GetMapping("/auction/distribute-pooled-players")
    @Operation(
            summary = "Stats-based pooled player grouping",
            description = "Ranks all POOLED players by their stats within each skill category (Batsman, Bowler, All-Rounder), then groups similarly ranked players into groups of up to 10."
    )
    public ResponseEntity<PlayerGroupingResponse> distributePooledPlayers() {
        return ResponseEntity.ok(distributionService.groupPooledPlayers());
    }

}