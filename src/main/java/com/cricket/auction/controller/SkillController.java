package com.cricket.auction.controller;

import com.cricket.auction.entity.PlayerSkill;
import com.cricket.auction.repository.PlayerSkillRepository;
import com.cricket.auction.service.PlayerSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skill API", description = "Endpoints for player skills")
public class SkillController {

    @Autowired
    private PlayerSkillService playerSkillService;

    @GetMapping
    @Operation(summary = "Get all skills", description = "Returns a list of all player skills.")
    public List<PlayerSkill> getSkills() {
        return playerSkillService.getPlayerSkills();
    }
}
