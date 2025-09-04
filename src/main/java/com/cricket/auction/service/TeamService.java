package com.cricket.auction.service;

import com.cricket.auction.entity.Team;
import com.cricket.auction.model.TeamResponse;
import com.cricket.auction.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public List<TeamResponse> getTeams() {
        return teamRepository.fetchTeamResponse();
    }
}
