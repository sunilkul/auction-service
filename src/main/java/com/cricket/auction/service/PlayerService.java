package com.cricket.auction.service;

import com.cricket.auction.model.PlayerResponse;
import com.cricket.auction.model.PlayerResponseProjection;
import com.cricket.auction.repository.PlayerRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private PlayerRepository playerRepository;


    public List<PlayerResponse> generatePlayerResponse(List<PlayerResponseProjection> projections) {
        ObjectMapper objectMapper = new ObjectMapper();
        return projections.stream().map(p -> {
            String statsStr = p.getStats();
            Map<String, Object> statsMap = null;
            if (statsStr != null) {
                try {
                    statsMap = objectMapper.readValue(statsStr, new TypeReference<>() {});
                } catch (Exception e) {
                    logger.debug("Unable to parse player stats JSON for player {}", p.getId(), e);
                }
            }
            return new PlayerResponse(
                    p.getId(),
                    p.getName(),
                    p.getPhoto(),
                    p.getDescription(),
                    p.getBasePrice(),
                    statsMap,
                    p.getStatus(),
                    p.getSkillId(),
                    p.getSkillName(),
                    p.getSoldPrice(),
                    p.getTeamId(),
                    p.getTeamName(),
                    p.getIsNewPlayer(),
                    p.getTournamentId(),
                    p.getIsConsiderInAuction(),
                    p.getGroupCode()
            );
        }).collect(Collectors.toList());
    }

    public List<PlayerResponse> getPlayersInfo() {
        logger.info("Fetching players info from repository");
        List<PlayerResponseProjection> projections = playerRepository.fetchPlayersInfo();
        return generatePlayerResponse(projections);
    }

    public List<PlayerResponse> getNonAuctionedPlayers() {
        List<PlayerResponseProjection> projections = playerRepository.fetchNonAuctionPlayersInfo();
        return generatePlayerResponse(projections);
    }

    public List<PlayerResponse> getLastSoldPlayers(Integer count) {
        List<PlayerResponseProjection> projections = count == null
                ? playerRepository.fetchAllLastSoldPlayers()
                : playerRepository.fetchLastSoldPlayers(count);
        return generatePlayerResponse(projections);
    }
}
