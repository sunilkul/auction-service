package com.cricket.auction.service;

import com.cricket.auction.entity.Player;
import com.cricket.auction.entity.Team;
import com.cricket.auction.entity.TeamPlayer;
import com.cricket.auction.repository.PlayerRepository;
import com.cricket.auction.repository.TeamPlayerRepository;
import com.cricket.auction.repository.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuctionService {
    @Autowired
    private TeamRepository teamRepo;
    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private TeamPlayerRepository teamPlayerRepo;

    @Transactional
    public Player updatePlayerStatus(Integer playerId, Integer teamId, Integer soldPrice, String status) {
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if ("SOLD".equalsIgnoreCase(status)) {
            if (team.getRemainingPurse() < soldPrice)
            {throw new RuntimeException("Insufficient purse");}
            else{
                TeamPlayer teamPlayer = TeamPlayer.builder()
                        .playerId(playerId)
                        .teamId(teamId)
                        .soldPrice(soldPrice)
                        .soldAt(LocalDateTime.now())
                        .build();
                player.setPlayerStatus(Player.Status.SOLD);
                team.setRemainingPurse(team.getPurse() - soldPrice);
                teamRepo.save(team);
                teamPlayerRepo.save(teamPlayer);
            }
        } else {
            player.setPlayerStatus(Player.Status.UNSOLD);
        }
        return playerRepo.save(player);
    }
}