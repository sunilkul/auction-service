package com.cricket.auction.service;

import com.cricket.auction.entity.PlayerSkill;
import com.cricket.auction.model.PoolStatus;
import com.cricket.auction.model.PlayerResponse;
import com.cricket.auction.model.PlayerResponseProjection;
import com.cricket.auction.repository.PlayerRepository;
import com.cricket.auction.repository.PlayerSkillRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionPoolService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerSkillRepository playerSkillRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get all pools available for a specific skill
     */
    public List<PoolStatus> getPoolsBySkill(Integer skillId, Integer tournamentId) {
        Optional<PlayerSkill> skillOpt = playerSkillRepository.findById(skillId);
        if (skillOpt.isEmpty()) {
            throw new RuntimeException("Skill not found with ID: " + skillId);
        }

        String skillName = skillOpt.get().getSkillName();
        List<String> poolCodes = playerRepository.getPoolsBySkill(skillId, tournamentId);

        return poolCodes.stream().map(poolCode -> {
            Integer total = playerRepository.getPoolTotalCount(skillId, poolCode);
            Integer complete = playerRepository.getPoolCompleteCount(skillId, poolCode);
            Integer remaining = total - complete;

            return PoolStatus.builder()
                    .skillId(skillId)
                    .skillName(skillName)
                    .poolCode(poolCode)
                    .total(total)
                    .sold(complete)
                    .remaining(remaining)
                    .isComplete(remaining == 0)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * Get the next active pool for a skill (first pool with remaining players)
     */
    public PoolStatus getNextActivePool(Integer skillId, Integer tournamentId) {
        Optional<PlayerSkill> skillOpt = playerSkillRepository.findById(skillId);
        if (skillOpt.isEmpty()) {
            throw new RuntimeException("Skill not found with ID: " + skillId);
        }

        String skillName = skillOpt.get().getSkillName();
        List<String> poolCodes = playerRepository.getPoolsBySkill(skillId, tournamentId);

        for (String poolCode : poolCodes) {
            Integer total = playerRepository.getPoolTotalCount(skillId, poolCode);
            Integer complete = playerRepository.getPoolCompleteCount(skillId, poolCode);
            Integer remaining = total - complete;

            // Return first incomplete pool
            if (remaining > 0) {
                return PoolStatus.builder()
                        .skillId(skillId)
                        .skillName(skillName)
                        .poolCode(poolCode)
                        .total(total)
                        .sold(complete)
                        .remaining(remaining)
                        .isComplete(false)
                        .build();
            }
        }

        // All pools are complete
        return PoolStatus.builder()
                .skillId(skillId)
                .skillName(skillName)
                .poolCode(null)
                .total(0)
                .sold(0)
                .remaining(0)
                .isComplete(true)
                .build();
    }

    /**
     * Get all players in a specific pool with skill and pool code
     */
    public List<PlayerResponse> getPoolPlayers(Integer skillId, String poolCode) {
        List<PlayerResponseProjection> projections = playerRepository.getPoolPlayers(skillId, poolCode);
        return generatePlayerResponse(projections);
    }

    /**
     * Get players from the next active pool for a skill
     */
    public List<PlayerResponse> getNextPoolPlayers(Integer skillId, Integer tournamentId) {
        PoolStatus nextPool = getNextActivePool(skillId, tournamentId);

        if (nextPool.getPoolCode() == null || nextPool.getIsComplete()) {
            return Collections.emptyList();
        }

        return getPoolPlayers(skillId, nextPool.getPoolCode());
    }

    /**
     * Get status of a specific pool
     */
    public PoolStatus getPoolStatus(Integer skillId, String poolCode) {
        Optional<PlayerSkill> skillOpt = playerSkillRepository.findById(skillId);
        if (skillOpt.isEmpty()) {
            throw new RuntimeException("Skill not found with ID: " + skillId);
        }

        String skillName = skillOpt.get().getSkillName();
        Integer total = playerRepository.getPoolTotalCount(skillId, poolCode);
        Integer complete = playerRepository.getPoolCompleteCount(skillId, poolCode);
        Integer remaining = total - complete;

        return PoolStatus.builder()
                .skillId(skillId)
                .skillName(skillName)
                .poolCode(poolCode)
                .total(total)
                .sold(complete)
                .remaining(remaining)
                .isComplete(remaining == 0)
                .build();
    }

    /**
     * Check if pool is complete (all players sold or unsold)
     */
    public Boolean isPoolComplete(Integer skillId, String poolCode) {
        Integer total = playerRepository.getPoolTotalCount(skillId, poolCode);
        Integer complete = playerRepository.getPoolCompleteCount(skillId, poolCode);
        return total.equals(complete);
    }

    /**
     * Convert projections to player responses with stats parsing
     */
    private List<PlayerResponse> generatePlayerResponse(List<PlayerResponseProjection> projections) {
        return projections.stream().map(p -> {
            Object statsObj = p.getStats();
            Map<String, Object> statsMap = null;
            if (statsObj instanceof String statsStr) {
                try {
                    statsMap = objectMapper.readValue(statsStr, Map.class);
                } catch (Exception e) {
                    statsMap = null;
                }
            } else if (statsObj instanceof Map) {
                statsMap = (Map<String, Object>) statsObj;
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
}
