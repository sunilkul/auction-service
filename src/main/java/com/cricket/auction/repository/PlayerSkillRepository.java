package com.cricket.auction.repository;

import com.cricket.auction.entity.PlayerSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerSkillRepository extends JpaRepository<PlayerSkill , Integer> {
}
