-- ============================================================================
-- Cleanup Script (DESTRUCTIVE)
-- ============================================================================
-- WARNING:
--   This script removes existing auction data by truncating all core tables.
--   Run this before a fresh production/test data load.
-- ============================================================================

USE epl_auction;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE tblTeamPlayer;
TRUNCATE TABLE tblTeam;
TRUNCATE TABLE tblPlayerBasePrice;
TRUNCATE TABLE tblPlayer;
TRUNCATE TABLE tblPlayerSkill;
TRUNCATE TABLE tblTournament;
SET FOREIGN_KEY_CHECKS = 1;

-- Optional verification
SELECT
  (SELECT COUNT(*) FROM tblTeamPlayer) AS team_player_count,
  (SELECT COUNT(*) FROM tblTeam) AS team_count,
  (SELECT COUNT(*) FROM tblPlayerBasePrice) AS base_price_count,
  (SELECT COUNT(*) FROM tblPlayer) AS player_count,
  (SELECT COUNT(*) FROM tblPlayerSkill) AS skill_count,
  (SELECT COUNT(*) FROM tblTournament) AS tournament_count;

