package com.cricket.auction.repository;

import com.cricket.auction.entity.Player;
import com.cricket.auction.model.PlayerResponse;
import com.cricket.auction.model.PlayerResponseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    @Query(value = "SELECT p.id,p.playerName as name,p.photo,p.basePrice,p.playerStats AS stats,\n" +
            "p.playerStatus as status,p.skillId,ps.skillName,tp.soldPrice,t.id AS teamId,t.teamName, p.isNewPlayer\n" +
            "FROM tblPlayer p\n" +
            "LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id\n" +
            "LEFT JOIN tblTeamPlayer tp ON p.id = tp.playerId\n" +
            "LEFT JOIN tblTeam t ON t.id = tp.teamId", nativeQuery = true)
    public List<PlayerResponseProjection> fetchPlayersInfo();
}