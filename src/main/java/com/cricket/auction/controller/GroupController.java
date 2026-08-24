package com.cricket.auction.controller;

import com.cricket.auction.model.PlayerGroupMaster;
import com.cricket.auction.model.PoolStatus;
import com.cricket.auction.model.PlayerResponse;
import com.cricket.auction.service.GroupService;
import com.cricket.auction.service.AuctionPoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Pool Management API", description = "Endpoints for managing player pools by skill and category")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private AuctionPoolService auctionPoolService;

    @GetMapping
    @Operation(summary = "Get all groups", description = "Returns all player groups (legacy endpoint)")
    public ResponseEntity<List<PlayerGroupMaster>> getAllGroups() {
        List<PlayerGroupMaster> groups = groupService.getGroupInfo();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/pools")
    @Operation(summary = "Get all pools for a skill", description = "Returns all pools with their status for a specific skill category")
    public ResponseEntity<List<PoolStatus>> getPoolsBySkill(
            @RequestParam @Parameter(description = "Skill ID") Integer skillId,
            @RequestParam @Parameter(description = "Tournament ID") Integer tournamentId) {
        List<PoolStatus> pools = auctionPoolService.getPoolsBySkill(skillId, tournamentId);
        return ResponseEntity.ok(pools);
    }

    @GetMapping("/pools/next")
    @Operation(summary = "Get next active pool", description = "Returns the next pool with unsold players for the given skill")
    public ResponseEntity<PoolStatus> getNextActivePool(
            @RequestParam @Parameter(description = "Skill ID") Integer skillId,
            @RequestParam @Parameter(description = "Tournament ID") Integer tournamentId) {
        PoolStatus nextPool = auctionPoolService.getNextActivePool(skillId, tournamentId);
        return ResponseEntity.ok(nextPool);
    }

    @GetMapping("/pools/{poolCode}/players")
    @Operation(summary = "Get players in a pool", description = "Returns all unsold players from a specific pool")
    public ResponseEntity<List<PlayerResponse>> getPoolPlayers(
            @RequestParam @Parameter(description = "Skill ID") Integer skillId,
            @PathVariable @Parameter(description = "Pool Code (e.g., P1, P2)") String poolCode) {
        List<PlayerResponse> players = auctionPoolService.getPoolPlayers(skillId, poolCode);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/pools/next/players")
    @Operation(summary = "Get next pool players", description = "Returns all players from the next active pool for a skill")
    public ResponseEntity<List<PlayerResponse>> getNextPoolPlayers(
            @RequestParam @Parameter(description = "Skill ID") Integer skillId,
            @RequestParam @Parameter(description = "Tournament ID") Integer tournamentId) {
        List<PlayerResponse> players = auctionPoolService.getNextPoolPlayers(skillId, tournamentId);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/pools/{poolCode}/status")
    @Operation(summary = "Get pool status", description = "Returns the status of a specific pool (total, sold, remaining)")
    public ResponseEntity<PoolStatus> getPoolStatus(
            @RequestParam @Parameter(description = "Skill ID") Integer skillId,
            @PathVariable @Parameter(description = "Pool Code (e.g., P1, P2)") String poolCode) {
        PoolStatus status = auctionPoolService.getPoolStatus(skillId, poolCode);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/pools/{poolCode}/complete")
    @Operation(summary = "Check if pool is complete", description = "Returns true if all players in the pool are sold or marked unsold")
    public ResponseEntity<Boolean> isPoolComplete(
            @RequestParam @Parameter(description = "Skill ID") Integer skillId,
            @PathVariable @Parameter(description = "Pool Code (e.g., P1, P2)") String poolCode) {
        Boolean isComplete = auctionPoolService.isPoolComplete(skillId, poolCode);
        return ResponseEntity.ok(isComplete);
    }
}
