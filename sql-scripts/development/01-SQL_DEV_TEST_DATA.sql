-- ============================================================================
-- DEV Test Data Script
-- ============================================================================
-- Purpose:
--   Seeds a lightweight development dataset for local testing.
--   Covers all four skill categories, multiple pool groups, and pre-seeded
--   auction outcomes (SOLD / UNSOLD / NOT_ASSIGNED) so every app code path
--   can be exercised without running a full auction.
--
-- Prerequisites:
--   Schema must already exist. Run the following first if starting from scratch:
--     sql-scripts/production/00-MY-SQL-TABLE.sql
--
-- Run order (this file is self-contained):
--   1) Cleanup  — truncates all auction tables
--   2) Skills   — 4 skill categories
--   3) Tournament — single active DEV tournament
--   4) Base prices — 4 price tiers
--   5) Teams    — 4 teams (trimmed from production's 10)
--   6) Players  — 3 per skill × 4 skills = 12 players across two pools each
--   7) Sold outcomes — 2 pre-sold players with tblTeamPlayer records
--   8) Verification queries
--
-- Notes:
--   isConsiderInAuction = 0  → included in auction pool (counter-intuitive; matches prod)
--   isConsiderInAuction = 1  → excluded from auction pool
--   groupCode pattern: AR-1/AR-2, BW-1/BW-2, BAT-1/BAT-2, WC-1/WC-2
-- ============================================================================

USE epl_auction;

-- ============================================================================
-- STEP 1: Cleanup
-- ============================================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE tblTeamPlayer;
TRUNCATE TABLE tblTeam;
TRUNCATE TABLE tblPlayerBasePrice;
TRUNCATE TABLE tblPlayer;
TRUNCATE TABLE tblPlayerSkill;
TRUNCATE TABLE tblTournament;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- STEP 2: Skill categories
-- ============================================================================
INSERT INTO tblPlayerSkill (id, skillName) VALUES
    (1, 'BATSMAN'),
    (2, 'BOWLER'),
    (3, 'ALL_ROUNDER'),
    (4, 'WILDCARD');

-- ============================================================================
-- STEP 3: Tournament  (DEV — active)
-- ============================================================================
INSERT INTO tblTournament (
    id, tournamentName, logo, tournamentYear, startDate, endDate, isActive
) VALUES (
    1,
    'EPL Local Dev Season',
    'epl-logo.png',
    '2026',
    '2026-09-01',
    '2026-09-30',
    1
);

-- ============================================================================
-- STEP 4: Base prices
-- ============================================================================
INSERT INTO tblPlayerBasePrice (playerBasePrice, tournamentId) VALUES
    (2000, 1),
    (5000, 1),
    (10000, 1),
    (15000, 1);

-- ============================================================================
-- STEP 5: Teams  (4 teams, purse 120 000 each)
-- ============================================================================
INSERT INTO tblTeam (teamName, logo, purse, remainingPurse, poc1, poc2, tournamentId) VALUES
    ('EPAM Purandar', 'http://localhost:3000/team-logo/EPL_8/EPAM_PURANDAR.png', 120000, 120000, 'Sunny Virmani',    'Sushant Shinde',  1),
    ('EPAM Lohagad',  'http://localhost:3000/team-logo/EPL_8/EPAM_LOHGAD.png',   120000, 120000, 'Pradip Thite',     'Mayuresh Behere', 1),
    ('EPAM Devgiri',  'http://localhost:3000/team-logo/EPL_8/EPAM_DEVGIRI.png',  120000, 120000, 'Jitendra Gosavi',  'Rushikesh Ahire', 1),
    ('EPAM Sinhagad', 'http://localhost:3000/team-logo/EPL_8/EPAM_SINHAGAD.png', 120000, 120000, 'Jincy Bharill',    'Vivek Chaturvedi',1);

-- ============================================================================
-- STEP 6: Players
-- ============================================================================
-- Each skill has 3 players spread across 2 group codes (2 in pool-1, 1 in pool-2)
-- so pool-progression logic (AuctionPoolService) can be tested end-to-end.
-- All start as NOT_ASSIGNED; statuses are patched in Step 7.

INSERT INTO tblPlayer (
    playerName, skillId, photo, description,
    basePrice, playerStatus, playerStats,
    isNewPlayer, tournamentId, isConsiderInAuction, groupCode
) VALUES

-- -----------------------------------------------------------------------
-- ALL ROUNDERS  (skillId = 3)
-- -----------------------------------------------------------------------
(
    'Dev Alpha', 3,
    NULL,
    'Test All-Rounder A (Pool 1)',
    4000, 'NOT_ASSIGNED',
    '{"matches":357,"runs":3399,"strikeRate":147.02,"Wickets":304,"economy":8.56}',
    0, 1, 0, 'AR-1'
),
(
    'Dev Beta', 3,
    NULL,
    'Test All-Rounder B (Pool 1)',
    4000, 'NOT_ASSIGNED',
    '{"matches":49,"runs":504,"strikeRate":167.44,"Wickets":50,"economy":7.28}',
    0, 1, 0, 'AR-1'
),
(
    'Dev Gamma', 3,
    NULL,
    'Test All-Rounder C (Pool 2)',
    4000, 'NOT_ASSIGNED',
    '{"matches":28,"runs":550,"strikeRate":200.73,"Wickets":22,"economy":7.12}',
    0, 1, 0, 'AR-2'
),

-- -----------------------------------------------------------------------
-- BOWLERS  (skillId = 2)
-- -----------------------------------------------------------------------
(
    'Dev Delta', 2,
    NULL,
    'Test Bowler A (Pool 1)',
    4000, 'NOT_ASSIGNED',
    '{"matches":73,"runs":312,"strikeRate":98.73,"Wickets":77,"economy":8.47}',
    0, 1, 0, 'BW-1'
),
(
    'Dev Epsilon', 2,
    NULL,
    'Test Bowler B (Pool 1)',
    4000, 'NOT_ASSIGNED',
    '{"matches":17,"runs":26,"strikeRate":68.42,"Wickets":20,"economy":7.27}',
    0, 1, 0, 'BW-1'
),
(
    'Dev Zeta', 2,
    NULL,
    'Test Bowler C (Pool 2)',
    4000, 'NOT_ASSIGNED',
    '{"matches":28,"runs":32,"strikeRate":60.38,"Wickets":16,"economy":6.70}',
    0, 1, 0, 'BW-2'
),

-- -----------------------------------------------------------------------
-- BATSMEN  (skillId = 1)
-- -----------------------------------------------------------------------
(
    'Dev Eta', 1,
    NULL,
    'Test Batsman A (Pool 1)',
    4000, 'NOT_ASSIGNED',
    '{"matches":64,"runs":861,"strikeRate":176.80,"Wickets":11,"economy":13.96}',
    0, 1, 0, 'BAT-1'
),
(
    'Dev Theta', 1,
    NULL,
    'Test Batsman B (Pool 1)',
    4000, 'NOT_ASSIGNED',
    '{"matches":224,"runs":2596,"strikeRate":142.87,"Wickets":0,"economy":0.00}',
    0, 1, 0, 'BAT-1'
),
(
    'Dev Iota', 1,
    NULL,
    'Test Batsman C (Pool 2)',
    4000, 'NOT_ASSIGNED',
    '{"matches":22,"runs":385,"strikeRate":194.44,"Wickets":19,"economy":9.29}',
    0, 1, 0, 'BAT-2'
),

-- -----------------------------------------------------------------------
-- WILDCARDS  (skillId = 4)
-- -----------------------------------------------------------------------
(
    'Dev Kappa', 4,
    NULL,
    'Test Wildcard A (Pool 1)',
    4000, 'NOT_ASSIGNED',
    '{"matches":27,"runs":230,"strikeRate":149.35,"Wickets":0,"economy":0.00}',
    0, 1, 0, 'WC-1'
),
(
    'Dev Lambda', 4,
    NULL,
    'Test Wildcard B (Pool 1)',
    4000, 'NOT_ASSIGNED',
    '{"matches":20,"runs":219,"strikeRate":133.54,"Wickets":1,"economy":11.29}',
    0, 1, 0, 'WC-1'
),
(
    'Dev Mu', 4,
    NULL,
    'Test Wildcard C (Pool 2)',
    4000, 'NOT_ASSIGNED',
    '{"matches":27,"runs":175,"strikeRate":107.36,"Wickets":10,"economy":9.35}',
    0, 1, 0, 'WC-2'
);

-- ============================================================================
-- STEP 7: Pre-seed auction outcomes
-- ============================================================================
-- Dev Alpha  (AR-1) → SOLD to EPAM Purandar at 8500
-- Dev Delta  (BW-1) → UNSOLD
-- Dev Eta    (BAT-1) → ASSIGNED to EPAM Lohagad at base price
-- All others remain NOT_ASSIGNED for live auction testing.

-- Resolve IDs dynamically so the script is safe to re-run after truncation.
SET @alpha_id    := (SELECT id FROM tblPlayer WHERE playerName = 'Dev Alpha'    AND tournamentId = 1);
SET @delta_id    := (SELECT id FROM tblPlayer WHERE playerName = 'Dev Delta'    AND tournamentId = 1);
SET @eta_id      := (SELECT id FROM tblPlayer WHERE playerName = 'Dev Eta'      AND tournamentId = 1);
SET @purandar_id := (SELECT id FROM tblTeam   WHERE teamName   = 'EPAM Purandar' AND tournamentId = 1);
SET @lohagad_id  := (SELECT id FROM tblTeam   WHERE teamName   = 'EPAM Lohagad'  AND tournamentId = 1);

-- Mark Dev Alpha as SOLD
UPDATE tblPlayer SET playerStatus = 'SOLD'     WHERE id = @alpha_id;
-- Mark Dev Delta as UNSOLD
UPDATE tblPlayer SET playerStatus = 'UNSOLD'   WHERE id = @delta_id;
-- Mark Dev Eta as ASSIGNED
UPDATE tblPlayer SET playerStatus = 'ASSIGNED' WHERE id = @eta_id;

-- tblTeamPlayer record for SOLD player (deduct purse)
INSERT INTO tblTeamPlayer (teamId, playerId, soldPrice, soldAt, tournamentId)
VALUES (@purandar_id, @alpha_id, 8500, NOW(6), 1);
UPDATE tblTeam SET remainingPurse = remainingPurse - 8500 WHERE id = @purandar_id;

-- tblTeamPlayer record for ASSIGNED player (no purse deduction)
INSERT INTO tblTeamPlayer (teamId, playerId, soldPrice, soldAt, tournamentId)
VALUES (@lohagad_id, @eta_id, 4000, NOW(6), 1);

-- ============================================================================
-- STEP 8: Verification
-- ============================================================================
SELECT 'Skills'       AS entity, COUNT(*) AS cnt FROM tblPlayerSkill
UNION ALL
SELECT 'Tournament',               COUNT(*)       FROM tblTournament
UNION ALL
SELECT 'BasePrices',               COUNT(*)       FROM tblPlayerBasePrice
UNION ALL
SELECT 'Teams',                    COUNT(*)       FROM tblTeam
UNION ALL
SELECT 'Players (total)',          COUNT(*)       FROM tblPlayer
UNION ALL
SELECT 'TeamPlayer records',       COUNT(*)       FROM tblTeamPlayer;

SELECT playerName, skillId, groupCode, playerStatus, isConsiderInAuction
FROM tblPlayer
ORDER BY skillId, groupCode, id;

SELECT t.teamName, t.purse, t.remainingPurse,
       p.playerName, tp.soldPrice
FROM tblTeamPlayer tp
JOIN tblTeam   t ON t.id = tp.teamId
JOIN tblPlayer p ON p.id = tp.playerId
ORDER BY tp.soldAt;
