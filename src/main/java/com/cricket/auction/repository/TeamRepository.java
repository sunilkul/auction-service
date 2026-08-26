package com.cricket.auction.repository;

import com.cricket.auction.entity.Team;
import com.cricket.auction.model.TeamResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    @Query(value = """
            SELECT t.id,t.teamName AS name, t.logo,t.purse,t.remainingPurse,t.poc1,t.poc2, t.tournamentId FROM tblTeam t
            JOIN tblTournament tr ON t.tournamentId = tr.id
            WHERE tr.isActive = 1""", nativeQuery = true)
    List<TeamResponse> fetchTeamResponse();

    @Query(value = """
            SELECT t.* FROM tblTeam t
            WHERE t.id NOT IN(
            SELECT DISTINCT tp.teamId FROM tblTeamPlayer tp\s
            JOIN tblPlayer p ON tp.playerId = p.id
            JOIN tblTournament tr1 ON tp.tournamentId = tr1.id
            WHERE p.groupCode = :groupCode AND tr1.isActive = 1
            AND p.skillId = :skillId AND isConsiderInAuction = 0)\s""", nativeQuery = true)
    List<TeamResponse> fetchNonAuctionTeamResponse(@Param("skillId") Integer skillId, @Param("groupCode") String groupCode);
}