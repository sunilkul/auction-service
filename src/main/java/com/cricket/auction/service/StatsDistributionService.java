package com.cricket.auction.service;

import com.cricket.auction.model.PlayerGroupingResponse;
import com.cricket.auction.model.PlayerResponseProjection;
import com.cricket.auction.repository.PlayerRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class StatsDistributionService {

    private static final int GROUP_SIZE = 10;

    private final PlayerRepository playerRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StatsDistributionService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayerGroupingResponse groupPooledPlayers() {
        List<PlayerResponseProjection> pooledPlayers = playerRepository.fetchPooledPlayers();

        if (pooledPlayers.isEmpty()) {
            return createEmptyResponse();
        }

        List<PlayerResponseProjection> eligiblePlayers = filterEligiblePlayers(pooledPlayers);
        Map<String, List<PlayerResponseProjection>> playersBySkill = groupBySkill(eligiblePlayers);

        List<PlayerGroupingResponse.SkillGroup> skillGroups = buildSkillGroups(playersBySkill);

        return createResponse(eligiblePlayers.size(), skillGroups, pooledPlayers.size() - eligiblePlayers.size());
    }

    private PlayerGroupingResponse createEmptyResponse() {
        PlayerGroupingResponse emptyResponse = new PlayerGroupingResponse();
        emptyResponse.setSkillGroups(List.of());
        emptyResponse.setSummary("No POOLED players found to group.");
        return emptyResponse;
    }

    private List<PlayerResponseProjection> filterEligiblePlayers(List<PlayerResponseProjection> players) {
        return players.stream()
                .filter(p -> !"Wildcard".equals(normalizeSkill(p.getSkillName())))
                .toList();
    }

    private Map<String, List<PlayerResponseProjection>> groupBySkill(List<PlayerResponseProjection> players) {
        Map<String, List<PlayerResponseProjection>> skillGroups = new LinkedHashMap<>();
        skillGroups.put("All-Rounder", new ArrayList<>());
        skillGroups.put("Batsman", new ArrayList<>());
        skillGroups.put("Bowler", new ArrayList<>());

        for (PlayerResponseProjection player : players) {
            skillGroups.computeIfAbsent(normalizeSkill(player.getSkillName()), k -> new ArrayList<>()).add(player);
        }

        skillGroups.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        return skillGroups;
    }

    private List<PlayerGroupingResponse.SkillGroup> buildSkillGroups(Map<String, List<PlayerResponseProjection>> playersBySkill) {
        List<PlayerGroupingResponse.SkillGroup> skillGroups = new ArrayList<>();

        for (Map.Entry<String, List<PlayerResponseProjection>> entry : playersBySkill.entrySet()) {
            String skill = entry.getKey();
            List<PlayerResponseProjection> players = entry.getValue();
            try {
                Map<Integer, Double> scoreByPlayerId = scorePlayers(players);
                skillGroups.add(buildSkillGroup(skill, players, scoreByPlayerId));
            } catch (Exception e) {
                throw new RuntimeException(
                        String.format("Ranking failed for skill group '%s': %s", skill, e.getMessage()), e);
            }
        }

        return skillGroups;
    }

    private PlayerGroupingResponse createResponse(int eligiblePlayerCount, List<PlayerGroupingResponse.SkillGroup> skillGroups, int wildcardCount) {
        int totalGroups = skillGroups.stream().mapToInt(sg -> sg.getGroups().size()).sum();

        PlayerGroupingResponse response = new PlayerGroupingResponse();
        response.setSkillGroups(skillGroups);
        response.setSummary(String.format(
                "Ranked and grouped %d POOLED players into %d skill-differentiated groups of up to %d using deterministic stats-based scoring. " +
                        "%d Wildcard player(s) excluded.",
                eligiblePlayerCount, totalGroups, GROUP_SIZE, wildcardCount));
        return response;
    }

    private String normalizeSkill(String rawSkill) {
        if (rawSkill == null) return "Unknown";
        return switch (rawSkill.trim().toUpperCase()) {
            case "BATSMAN", "BAT"                         -> "Batsman";
            case "BOWLER", "BOWL"                         -> "Bowler";
            case "ALL-ROUNDER", "ALLROUNDER", "ALL ROUNDER",
                 "ALL_ROUNDER", "ALLROUND"                -> "All-Rounder";
            case "WILDCARD", "WILD CARD", "WILD"          -> "Wildcard";
            default                                       -> rawSkill.trim();
        };
    }

    private Map<Integer, Double> scorePlayers(List<PlayerResponseProjection> players) {
        return players.stream()
                .map(p -> new PlayerScore(p.getId(), p.getName(), calculatePlayerScore(parseStats(p.getStats()))))
                .collect(HashMap::new, (map, playerScore) -> map.put(playerScore.playerId(), playerScore.score()), HashMap::putAll);
    }

    private double calculatePlayerScore(Map<String, Object> stats) {
        double matches = toDouble(stats.getOrDefault("matches", 0));
        double runs = toDouble(stats.getOrDefault("runs", 0));
        double strikeRate = toDouble(stats.getOrDefault("strikeRate", 0));
        double wickets = toDouble(stats.getOrDefault("Wickets", stats.getOrDefault("wickets", 0)));
        double economy = toDouble(stats.getOrDefault("economy", 0));

        double battingImpactScore = (runs / Math.max(matches, 1.0)) * (strikeRate / 100.0);
        double wicketImpactScore = wickets * Math.max(0.0, 10.0 - (economy / 2.0));
        return battingImpactScore + wicketImpactScore;
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    private double roundScore(double score) {
        return Math.round(score * 100.0) / 100.0;
    }

    private PlayerGroupingResponse.SkillGroup buildSkillGroup(
            String skill, List<PlayerResponseProjection> players, Map<Integer, Double> scoreByPlayerId) {

        List<PlayerResponseProjection> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort(Comparator
                .comparingDouble((PlayerResponseProjection p) -> scoreByPlayerId.getOrDefault(p.getId(), 0.0))
                .reversed()
                .thenComparing(p -> p.getName() == null ? "" : p.getName())
                .thenComparing(PlayerResponseProjection::getId));

        List<PlayerGroupingResponse.PlayerGroup> groups = new ArrayList<>();
        int groupNumber = 1;
        for (int i = 0; i < sortedPlayers.size(); i += GROUP_SIZE) {
            List<PlayerResponseProjection> slice = sortedPlayers.subList(i, Math.min(i + GROUP_SIZE, sortedPlayers.size()));

            List<PlayerGroupingResponse.ScoredPlayer> scoredPlayers = slice.stream().map(p ->
                    new PlayerGroupingResponse.ScoredPlayer(p.getId(), p.getName(),
                            roundScore(scoreByPlayerId.getOrDefault(p.getId(), 0.0)))
            ).toList();

            groups.add(new PlayerGroupingResponse.PlayerGroup(groupNumber, skill + " - Group " + groupNumber, scoredPlayers));
            groupNumber++;
        }

        return new PlayerGroupingResponse.SkillGroup(skill, players.size(), groups);
    }

    private Map<String, Object> parseStats(String statsJson) {
        if (statsJson == null || statsJson.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(statsJson, new TypeReference<>() {});
        } catch (Exception e) {
            log.debug("Unable to parse player stats payload, defaulting to empty stats", e);
            return Map.of();
        }
    }

    private record PlayerScore(Integer playerId, String name, double score) {
    }
}