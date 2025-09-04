package com.cricket.auction.repository;

import com.cricket.auction.entity.Team;
import com.cricket.auction.model.TeamResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    @Query(value = "SELECT t.id,t.teamName AS name, t.logo,t.purse,t.remainingPurse,t.poc1,t.poc2 FROM tblTeam t", nativeQuery = true)
    public List<TeamResponse> fetchTeamResponse();
}