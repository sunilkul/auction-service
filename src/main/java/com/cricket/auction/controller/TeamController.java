package com.cricket.auction.controller;

import com.cricket.auction.model.TeamResponse;
import com.cricket.auction.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/non-auction")
    public ResponseEntity<TeamResponse> getNonAuctionTeams(@RequestParam(name = "skillId") Integer skillId,
                                                           @RequestParam(name = "groupCode") String groupCode) {
        return ResponseEntity.ok().body(teamService.getNonAuctionTeams(skillId, groupCode).get(0));
    }

}
