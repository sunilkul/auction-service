-- ============================================================================
-- EPL Auction - Master MySQL Schema
-- ============================================================================
-- This file is the source-of-truth schema used by local container setup.
-- Runtime expects MySQL 8.x and database name: epl_auction.
--
-- Notes:
-- 1) Keep table/column names aligned with JPA @Table/@Column mappings.
-- 2) Player description column is `description` (legacy name was `speciality`).
-- 3) This script creates objects only when missing; safe for repeat local setup.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS epl_auction;
USE epl_auction;

-- --------------------------------------------------------------------------
-- Table: tblPlayerSkill
-- Stores skill categories used by players (BATSMAN/BOWLER/ALL_ROUNDER/WILDCARD)
-- --------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tblPlayerSkill (
  id INT AUTO_INCREMENT NOT NULL,
  skillName VARCHAR(100) NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------------
-- Table: tblTournament
-- Holds tournament seasons. Exactly one active tournament is recommended.
-- --------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tblTournament (
  id INT AUTO_INCREMENT NOT NULL,
  tournamentName VARCHAR(500) NOT NULL,
  logo VARCHAR(1000) NOT NULL,
  tournamentYear VARCHAR(100) NOT NULL,
  startDate DATE NOT NULL,
  endDate DATE NOT NULL,
  isActive INT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------------
-- Table: tblPlayer
-- Core player registry used by auction and pool APIs.
-- --------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tblPlayer (
  id INT AUTO_INCREMENT NOT NULL,
  playerName VARCHAR(255) NOT NULL,
  skillId INT NOT NULL,
  photo VARCHAR(3000) NULL,
  description VARCHAR(255) NULL,
  basePrice INT NOT NULL,
  playerStatus VARCHAR(20) NOT NULL,
  playerStats VARCHAR(4000) NOT NULL,
  isNewPlayer INT NOT NULL,
  tournamentId INT NOT NULL,
  isConsiderInAuction INT NOT NULL,
  groupCode VARCHAR(10) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------------
-- Table: tblPlayerBasePrice
-- Reference prices per tournament.
-- --------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tblPlayerBasePrice (
  id INT AUTO_INCREMENT NOT NULL,
  playerBasePrice INT NOT NULL,
  tournamentId INT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------------
-- Table: tblTeam
-- Team metadata and purse amounts for a tournament.
-- --------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tblTeam (
  id INT AUTO_INCREMENT NOT NULL,
  teamName VARCHAR(500) NOT NULL,
  logo VARCHAR(3000) NOT NULL,
  purse INT NOT NULL,
  remainingPurse INT NULL,
  poc1 VARCHAR(500) NOT NULL,
  poc2 VARCHAR(500) NOT NULL,
  tournamentId INT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------------
-- Table: tblTeamPlayer
-- Sold-player mapping table (final auction outcomes).
-- --------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tblTeamPlayer (
  id INT AUTO_INCREMENT NOT NULL,
  teamId INT NOT NULL,
  playerId INT NOT NULL,
  soldPrice INT NOT NULL,
  soldAt DATETIME(6) NOT NULL,
  tournamentId INT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------------
-- Foreign Keys (created only if absent)
-- --------------------------------------------------------------------------

SET @fk_player_skill_exists := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
	AND TABLE_NAME = 'tblPlayer'
	AND CONSTRAINT_NAME = 'fk_player_skill'
);
SET @fk_sql := IF(
  @fk_player_skill_exists = 0,
  'ALTER TABLE tblPlayer ADD CONSTRAINT fk_player_skill FOREIGN KEY (skillId) REFERENCES tblPlayerSkill (id)',
  'SELECT 1'
);
PREPARE stmt FROM @fk_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk_player_tournament_exists := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
	AND TABLE_NAME = 'tblPlayer'
	AND CONSTRAINT_NAME = 'fk_player_tournament'
);
SET @fk_sql := IF(
  @fk_player_tournament_exists = 0,
  'ALTER TABLE tblPlayer ADD CONSTRAINT fk_player_tournament FOREIGN KEY (tournamentId) REFERENCES tblTournament (id)',
  'SELECT 1'
);
PREPARE stmt FROM @fk_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk_playerbaseprice_tournament_exists := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
	AND TABLE_NAME = 'tblPlayerBasePrice'
	AND CONSTRAINT_NAME = 'fk_playerbaseprice_tournament'
);
SET @fk_sql := IF(
  @fk_playerbaseprice_tournament_exists = 0,
  'ALTER TABLE tblPlayerBasePrice ADD CONSTRAINT fk_playerbaseprice_tournament FOREIGN KEY (tournamentId) REFERENCES tblTournament (id)',
  'SELECT 1'
);
PREPARE stmt FROM @fk_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk_team_tournament_exists := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
	AND TABLE_NAME = 'tblTeam'
	AND CONSTRAINT_NAME = 'fk_team_tournament'
);
SET @fk_sql := IF(
  @fk_team_tournament_exists = 0,
  'ALTER TABLE tblTeam ADD CONSTRAINT fk_team_tournament FOREIGN KEY (tournamentId) REFERENCES tblTournament (id)',
  'SELECT 1'
);
PREPARE stmt FROM @fk_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk_teamplayer_player_exists := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
	AND TABLE_NAME = 'tblTeamPlayer'
	AND CONSTRAINT_NAME = 'fk_teamplayer_player'
);
SET @fk_sql := IF(
  @fk_teamplayer_player_exists = 0,
  'ALTER TABLE tblTeamPlayer ADD CONSTRAINT fk_teamplayer_player FOREIGN KEY (playerId) REFERENCES tblPlayer (id)',
  'SELECT 1'
);
PREPARE stmt FROM @fk_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk_teamplayer_team_exists := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
	AND TABLE_NAME = 'tblTeamPlayer'
	AND CONSTRAINT_NAME = 'fk_teamplayer_team'
);
SET @fk_sql := IF(
  @fk_teamplayer_team_exists = 0,
  'ALTER TABLE tblTeamPlayer ADD CONSTRAINT fk_teamplayer_team FOREIGN KEY (teamId) REFERENCES tblTeam (id)',
  'SELECT 1'
);
PREPARE stmt FROM @fk_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk_teamplayer_tournament_exists := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
	AND TABLE_NAME = 'tblTeamPlayer'
	AND CONSTRAINT_NAME = 'fk_teamplayer_tournament'
);
SET @fk_sql := IF(
  @fk_teamplayer_tournament_exists = 0,
  'ALTER TABLE tblTeamPlayer ADD CONSTRAINT fk_teamplayer_tournament FOREIGN KEY (tournamentId) REFERENCES tblTournament (id)',
  'SELECT 1'
);
PREPARE stmt FROM @fk_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- --------------------------------------------------------------------------
-- Indexes for frequently used filters (pool + auction queries)
-- --------------------------------------------------------------------------
SET @idx_player_skill_group_status_exists := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'tblPlayer'
    AND INDEX_NAME = 'idx_player_skill_group_status'
);
SET @idx_sql := IF(
  @idx_player_skill_group_status_exists = 0,
  'CREATE INDEX idx_player_skill_group_status ON tblPlayer (skillId, groupCode, playerStatus)',
  'SELECT 1'
);
PREPARE stmt FROM @idx_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_player_tournament_consider_exists := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'tblPlayer'
    AND INDEX_NAME = 'idx_player_tournament_consider'
);
SET @idx_sql := IF(
  @idx_player_tournament_consider_exists = 0,
  'CREATE INDEX idx_player_tournament_consider ON tblPlayer (tournamentId, isConsiderInAuction)',
  'SELECT 1'
);
PREPARE stmt FROM @idx_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_teamplayer_player_soldat_exists := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'tblTeamPlayer'
    AND INDEX_NAME = 'idx_teamplayer_player_soldat'
);
SET @idx_sql := IF(
  @idx_teamplayer_player_soldat_exists = 0,
  'CREATE INDEX idx_teamplayer_player_soldat ON tblTeamPlayer (playerId, soldAt)',
  'SELECT 1'
);
PREPARE stmt FROM @idx_sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- --------------------------------------------------------------------------
-- Legacy migration note
-- If an older DB still has tblPlayer.speciality, rename it once:
--   ALTER TABLE tblPlayer CHANGE COLUMN speciality description VARCHAR(255) NULL;
-- --------------------------------------------------------------------------
