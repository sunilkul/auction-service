package com.cricket.auction.service;

import com.cricket.auction.entity.Player;
import com.cricket.auction.entity.Team;
import com.cricket.auction.entity.TeamPlayer;
import com.cricket.auction.repository.PlayerRepository;
import com.cricket.auction.repository.TeamPlayerRepository;
import com.cricket.auction.repository.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuctionService {

    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;
    private final TeamPlayerRepository teamPlayerRepo;

    public AuctionService(TeamRepository teamRepo, PlayerRepository playerRepo, TeamPlayerRepository teamPlayerRepo) {
        this.teamRepo = teamRepo;
        this.playerRepo = playerRepo;
        this.teamPlayerRepo = teamPlayerRepo;
    }

    @Transactional
    public Player updatePlayerStatus(Integer playerId, Integer teamId, Integer soldPrice, String status) {
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if ("SOLD".equalsIgnoreCase(status) || "ASSIGNED".equalsIgnoreCase(status)) {
            if (team.getRemainingPurse() < soldPrice) {
                throw new RuntimeException("Insufficient purse");
            } else {
                TeamPlayer teamPlayer = TeamPlayer.builder()
                        .playerId(playerId)
                        .teamId(teamId)
                        .soldPrice(soldPrice)
                        .soldAt(LocalDateTime.now())
                        .tournamentId(player.getTournamentId())
                        .build();
                player.setPlayerStatus(Player.Status.valueOf(status));
                if ("SOLD".equalsIgnoreCase(status)) {
                    team.setRemainingPurse(team.getRemainingPurse() - soldPrice);
                }
                teamRepo.save(team);
                teamPlayerRepo.save(teamPlayer);
            }
        } else {
            player.setPlayerStatus(Player.Status.UNSOLD);
        }
        return playerRepo.save(player);
    }

    @Transactional
    public Player resetPlayerAuction(Integer playerId) {
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        Player.Status currentStatus = player.getPlayerStatus();
        if (currentStatus != Player.Status.SOLD && currentStatus != Player.Status.UNSOLD && currentStatus != Player.Status.ASSIGNED) {
            throw new RuntimeException("Only auctioned players can be reset");
        }

        teamPlayerRepo.findTopByPlayerIdOrderByIdDesc(playerId).ifPresent(teamPlayer -> {
            if (currentStatus == Player.Status.SOLD) {
                Team team = teamRepo.findById(teamPlayer.getTeamId())
                        .orElseThrow(() -> new RuntimeException("Team not found"));
                team.setRemainingPurse(team.getRemainingPurse() + teamPlayer.getSoldPrice());
                teamRepo.save(team);
            }
            teamPlayerRepo.delete(teamPlayer);
        });

        player.setPlayerStatus(Player.Status.NOT_ASSIGNED);
        return playerRepo.save(player);
    }
}