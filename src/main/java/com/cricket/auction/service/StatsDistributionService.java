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
            PlayerGroupingResponse empty = new PlayerGroupingResponse();
            empty.setSkillGroups(List.of());
            empty.setSummary("No POOLED players found to group.");
            return empty;
        }

        List<PlayerResponseProjection> eligiblePlayers = pooledPlayers.stream()
                .filter(p -> !"Wildcard".equals(normalizeSkill(p.getSkillName())))
                .toList();

        Map<String, List<PlayerResponseProjection>> bySkill = groupBySkill(eligiblePlayers);

        List<PlayerGroupingResponse.SkillGroup> skillGroups = new ArrayList<>();
        for (Map.Entry<String, List<PlayerResponseProjection>> entry : bySkill.entrySet()) {
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

        int totalGroups = skillGroups.stream().mapToInt(sg -> sg.getGroups().size()).sum();
        int wildcardCount = pooledPlayers.size() - eligiblePlayers.size();

        PlayerGroupingResponse response = new PlayerGroupingResponse();
        response.setSkillGroups(skillGroups);
        response.setSummary(String.format(
                "Ranked and grouped %d POOLED players into %d skill-differentiated groups of up to %d using deterministic stats-based scoring. " +
                        "%d Wildcard player(s) excluded.",
                eligiblePlayers.size(), totalGroups, GROUP_SIZE, wildcardCount));
        return response;
    }

    private Map<String, List<PlayerResponseProjection>> groupBySkill(List<PlayerResponseProjection> players) {
        Map<String, List<PlayerResponseProjection>> result = new LinkedHashMap<>();
        result.put("All-Rounder", new ArrayList<>());
        result.put("Batsman", new ArrayList<>());
        result.put("Bowler", new ArrayList<>());

        for (PlayerResponseProjection p : players) {
            result.computeIfAbsent(normalizeSkill(p.getSkillName()), k -> new ArrayList<>()).add(p);
        }

        result.entrySet().removeIf(e -> e.getValue().isEmpty());
        return result;
    }

    private String normalizeSkill(String raw) {
        if (raw == null) return "Unknown";
        return switch (raw.trim().toUpperCase()) {
            case "BATSMAN", "BAT"                         -> "Batsman";
            case "BOWLER", "BOWL"                         -> "Bowler";
            case "ALL-ROUNDER", "ALLROUNDER", "ALL ROUNDER",
                 "ALL_ROUNDER", "ALLROUND"                -> "All-Rounder";
            case "WILDCARD", "WILD CARD", "WILD"          -> "Wildcard";
            default                                       -> raw.trim();
        };
    }

    private Map<Integer, Double> scorePlayers(List<PlayerResponseProjection> players) {
        return players.stream()
                .map(p -> new PlayerScore(p.getId(), p.getName(), calculatePlayerScore(parseStats(p.getStats()))))
                .collect(HashMap::new, (m, p) -> m.put(p.playerId(), p.score()), HashMap::putAll);
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

        List<PlayerResponseProjection> sorted = new ArrayList<>(players);
        sorted.sort(Comparator
                .comparingDouble((PlayerResponseProjection p) -> scoreByPlayerId.getOrDefault(p.getId(), 0.0))
                .reversed()
                .thenComparing(p -> p.getName() == null ? "" : p.getName())
                .thenComparing(PlayerResponseProjection::getId));

        List<PlayerGroupingResponse.PlayerGroup> groups = new ArrayList<>();
        int groupNumber = 1;
        for (int i = 0; i < sorted.size(); i += GROUP_SIZE) {
            List<PlayerResponseProjection> slice = sorted.subList(i, Math.min(i + GROUP_SIZE, sorted.size()));

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

