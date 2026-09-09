package com.cricket.auction.controller;

import com.cricket.auction.model.PlayerResponse;
import com.cricket.auction.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/all-players")
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        List<PlayerResponse> players = playerService.getPlayersInfo();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/pooled")
    public ResponseEntity<List<PlayerResponse>> getPooledPlayers() {
        List<PlayerResponse> players = playerService.getPooledPlayers();
        return ResponseEntity.ok(players);
    }
}
