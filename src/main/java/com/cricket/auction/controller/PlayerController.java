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

    @GetMapping("/non-auctioned")
    public ResponseEntity<List<PlayerResponse>> getNonAuctionedPlayers() {
        List<PlayerResponse> players = playerService.getNonAuctionedPlayers();
        return ResponseEntity.ok(players);
    }
}
