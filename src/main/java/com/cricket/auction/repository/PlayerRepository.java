package com.cricket.auction.repository;

import com.cricket.auction.entity.Player;
import com.cricket.auction.model.PlayerGroupMaster;
import com.cricket.auction.model.PlayerResponseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    @Query(value = """
            SELECT p.id,p.playerName as name,p.photo,p.basePrice,p.playerStats AS stats,
            p.playerStatus as status,p.skillId,ps.skillName,tp.soldPrice,t.id AS teamId,t.teamName, p.isNewPlayer
            FROM tblPlayer p
            JOIN tblTournament tr ON p.tournamentId = tr.id
            LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
            LEFT JOIN tblTeamPlayer tp ON p.id = tp.playerId
            LEFT JOIN tblTeam t ON t.id = tp.teamId
            WHERE tr.isActive = 1\s
            """, nativeQuery = true)
    List<PlayerResponseProjection> fetchPlayersInfo();


    @Query(value = "SELECT DISTINCT pg.skillId, pg.groupCode " +
            "FROM tblPlayer pg " +
            "JOIN tblTournament t ON pg.tournamentId = t.id " +
            "WHERE t.isActive = 1 AND pg.isConsiderInAuction = 0", nativeQuery = true)
    List<PlayerGroupMaster> fetchPlayerGroups();


    @Query(value = """
            SELECT p.id,p.playerName as name,p.photo,p.basePrice,p.playerStats AS stats,
            p.playerStatus as status,p.skillId,ps.skillName,tp.soldPrice,t.id AS teamId,t.teamName, p.isNewPlayer, p.groupCode,p.isConsiderInAuction
            FROM tblPlayer p
            JOIN tblTournament tr ON p.tournamentId = tr.id
            LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
            LEFT JOIN tblTeamPlayer tp ON p.id = tp.playerId
            LEFT JOIN tblTeam t ON t.id = tp.teamId
            WHERE tr.isActive = 1 AND p.isConsiderInAuction = 0 AND p.playerStatus = 'NOT_ASSIGNED'
            """, nativeQuery = true)
    List<PlayerResponseProjection> fetchNonAuctionPlayersInfo();

    @Query(value = "SELECT DISTINCT p.groupCode FROM tblPlayer p " +
            "JOIN tblTournament tr ON p.tournamentId = tr.id " +
            "WHERE p.skillId = ?1 AND p.tournamentId = ?2 " +
            "AND tr.isActive = 1 AND p.isConsiderInAuction = 0 " +
            "ORDER BY p.groupCode", nativeQuery = true)
    List<String> getPoolsBySkill(Integer skillId, Integer tournamentId);

    @Query(value = """
            SELECT p.id,p.playerName as name,p.photo,p.basePrice,p.playerStats as stats,
            p.playerStatus as status,p.skillId,ps.skillName,tp.soldPrice,t.id as teamId,t.teamName,
            p.isNewPlayer, p.groupCode
            FROM tblPlayer p
            JOIN tblTournament tr ON p.tournamentId = tr.id
            LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
            LEFT JOIN tblTeamPlayer tp ON p.id = tp.playerId
            LEFT JOIN tblTeam t ON t.id = tp.teamId
            WHERE tr.isActive = 1 AND p.skillId = ?1 AND p.groupCode = ?2 \
            AND p.isConsiderInAuction = 0 AND p.playerStatus = 'NOT_ASSIGNED' \
            ORDER BY p.id""", nativeQuery = true)
    List<PlayerResponseProjection> getPoolPlayers(Integer skillId, String poolCode);

    @Query(value = "SELECT COUNT(*) FROM tblPlayer " +
            "WHERE skillId = ?1 AND groupCode = ?2 " +
            "AND playerStatus IN ('SOLD', 'UNSOLD')", nativeQuery = true)
    Integer getPoolCompleteCount(Integer skillId, String poolCode);

    @Query(value = "SELECT COUNT(*) FROM tblPlayer " +
            "WHERE skillId = ?1 AND groupCode = ?2", nativeQuery = true)
    Integer getPoolTotalCount(Integer skillId, String poolCode);

    @Query(value = """
            SELECT p.id,p.playerName as name,p.photo,p.basePrice,p.playerStats as stats,
            p.playerStatus as status,p.skillId,ps.skillName,tp.soldPrice,t.id as teamId,t.teamName,
            p.isNewPlayer, p.groupCode
            FROM tblPlayer p
            JOIN tblTournament tr ON p.tournamentId = tr.id
            LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
            LEFT JOIN tblTeamPlayer tp ON p.id = tp.playerId
            LEFT JOIN tblTeam t ON t.id = tp.teamId
            WHERE tr.isActive = 1 AND p.skillId = ?1 AND p.tournamentId = ?2 \
            AND p.isConsiderInAuction = 0 AND p.playerStatus = 'NOT_ASSIGNED' \
            ORDER BY p.groupCode , p.id""", nativeQuery = true)
    List<PlayerResponseProjection> getNextPoolForSkill(Integer skillId, Integer tournamentId);

    @Query(value = """
            SELECT p.id,p.playerName as name,p.photo,p.basePrice,p.playerStats AS stats,
            p.playerStatus as status,p.skillId,ps.skillName,tp.soldPrice,t.id AS teamId,t.teamName,
            p.isNewPlayer,p.tournamentId,p.isConsiderInAuction,p.groupCode
            FROM tblTeamPlayer tp
            JOIN tblPlayer p ON p.id = tp.playerId
            JOIN tblTournament tr ON p.tournamentId = tr.id
            LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
            LEFT JOIN tblTeam t ON t.id = tp.teamId
            WHERE tr.isActive = 1 AND p.playerStatus = 'SOLD'
            ORDER BY tp.soldAt DESC, tp.id DESC
            LIMIT 3
            """, nativeQuery = true)
    List<PlayerResponseProjection> fetchLastThreeSoldPlayers();

}