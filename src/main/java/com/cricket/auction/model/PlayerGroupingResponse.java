package com.cricket.auction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerGroupingResponse {

	private List<SkillGroup> skillGroups;
	private String summary;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SkillGroup {
		private String skill;
		private int totalPlayers;
		private List<PlayerGroup> groups;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PlayerGroup {
		private int groupNumber;
		private String groupLabel;
		private List<ScoredPlayer> players;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ScoredPlayer {
		private Integer playerId;
		private String playerName;
		private Double pScore;
	}
}
