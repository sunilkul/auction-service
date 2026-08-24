package com.cricket.auction.repository;

import com.cricket.auction.entity.Player;
import com.cricket.auction.model.PlayerGroupMaster;
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
            "JOIN tblTournament tr ON p.tournamentId = tr.id\n" +
            "LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id\n" +
            "LEFT JOIN tblTeamPlayer tp ON p.id = tp.playerId\n" +
            "LEFT JOIN tblTeam t ON t.id = tp.teamId\n" +
            "WHERE tr.isActive = 1 AND p.isConsiderInAuction = 0\n", nativeQuery = true)
    public List<PlayerResponseProjection> fetchPlayersInfo();


    @Query(value = "SELECT DISTINCT pg.skillId, pg.groupCode " +
            "FROM tblPlayer pg " +
            "JOIN tblTournament t ON pg.tournamentId = t.id " +
            "WHERE t.isActive = 1 AND pg.isConsiderInAuction = 0", nativeQuery = true)
    public List<PlayerGroupMaster> fetchPlayerGroups();


    @Query(value = "SELECT p.id,p.playerName as name,p.photo,p.basePrice,p.playerStats AS stats,\n" +
            "p.playerStatus as status,p.skillId,ps.skillName,tp.soldPrice,t.id AS teamId,t.teamName, p.isNewPlayer, p.groupCode,p.isConsiderInAuction\n" +
            "FROM tblPlayer p\n" +
            "JOIN tblTournament tr ON p.tournamentId = tr.id\n" +
            "LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id\n" +
            "LEFT JOIN tblTeamPlayer tp ON p.id = tp.playerId\n" +
            "LEFT JOIN tblTeam t ON t.id = tp.teamId\n" +
            "WHERE tr.isActive = 1 AND p.isConsiderInAuction = 0 AND p.playerStatus = 'NOT_ASSIGNED'\n", nativeQuery = true)
    public List<PlayerResponseProjection> fetchNonAuctionPlayersInfo();

    @Query(value = "SELECT DISTINCT p.groupCode FROM tblPlayer p " +
            "JOIN tblTournament tr ON p.tournamentId = tr.id " +
            "WHERE p.skillId = ?1 AND p.tournamentId = ?2 " +
            "AND tr.isActive = 1 AND p.isConsiderInAuction = 0 " +
            "ORDER BY p.groupCode ASC", nativeQuery = true)
    public List<String> getPoolsBySkill(Integer skillId, Integer tournamentId);

    @Query(value = "SELECT p.id,p.playerName as name,p.photo,p.basePrice,p.playerStats as stats,\n" +
            "p.playerStatus as status,p.skillId,ps.skillName,tp.soldPrice,t.id as teamId,t.teamName,\n" +
            "p.isNewPlayer, p.groupCode\n" +
            "FROM tblPlayer p\n" +
            "JOIN tblTournament tr ON p.tournamentId = tr.id\n" +
            "LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id\n" +
            "LEFT JOIN tblTeamPlayer tp ON p.id = tp.playerId\n" +
            "LEFT JOIN tblTeam t ON t.id = tp.teamId\n" +
            "WHERE tr.isActive = 1 AND p.skillId = ?1 AND p.groupCode = ?2 " +
            "AND p.isConsiderInAuction = 0 AND p.playerStatus = 'NOT_ASSIGNED' " +
            "ORDER BY p.id ASC", nativeQuery = true)
    public List<PlayerResponseProjection> getPoolPlayers(Integer skillId, String poolCode);

    @Query(value = "SELECT COUNT(*) FROM tblPlayer " +
            "WHERE skillId = ?1 AND groupCode = ?2 " +
            "AND playerStatus IN ('SOLD', 'UNSOLD')", nativeQuery = true)
    public Integer getPoolCompleteCount(Integer skillId, String poolCode);

    @Query(value = "SELECT COUNT(*) FROM tblPlayer " +
            "WHERE skillId = ?1 AND groupCode = ?2", nativeQuery = true)
    public Integer getPoolTotalCount(Integer skillId, String poolCode);

    @Query(value = "SELECT p.id,p.playerName as name,p.photo,p.basePrice,p.playerStats as stats,\n" +
            "p.playerStatus as status,p.skillId,ps.skillName,tp.soldPrice,t.id as teamId,t.teamName,\n" +
            "p.isNewPlayer, p.groupCode\n" +
            "FROM tblPlayer p\n" +
            "JOIN tblTournament tr ON p.tournamentId = tr.id\n" +
            "LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id\n" +
            "LEFT JOIN tblTeamPlayer tp ON p.id = tp.playerId\n" +
            "LEFT JOIN tblTeam t ON t.id = tp.teamId\n" +
            "WHERE tr.isActive = 1 AND p.skillId = ?1 AND p.tournamentId = ?2 " +
            "AND p.isConsiderInAuction = 0 AND p.playerStatus = 'NOT_ASSIGNED' " +
            "ORDER BY p.groupCode ASC, p.id ASC", nativeQuery = true)
    public List<PlayerResponseProjection> getNextPoolForSkill(Integer skillId, Integer tournamentId);

}