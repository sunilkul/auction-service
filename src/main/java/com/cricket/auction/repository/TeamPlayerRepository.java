package com.cricket.auction.repository;

import com.cricket.auction.entity.TeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, Integer> {
    Optional<TeamPlayer> findTopByPlayerIdOrderByIdDesc(Integer playerId);
}
