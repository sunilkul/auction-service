package com.cricket.auction.service;

import com.cricket.auction.entity.PlayerSkill;
import com.cricket.auction.repository.PlayerSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerSkillService {
    @Autowired
    private PlayerSkillRepository playerSkillRepository;

    public List<PlayerSkill> getPlayerSkills() {
        System.out.println(playerSkillRepository.findAll().toString());
        return playerSkillRepository.findAll();
    }
}
