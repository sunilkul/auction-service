-- ============================================================================
-- Production Data Load Script
-- ============================================================================
-- WARNING:
--   This script loads data only.
--   Run sql-scripts/production/01-SQL_CLEANUP_AUCTION_DATA.sql first if you need a full reset.
--   Run sql-scripts/production/02-SQL_SEED_REFERENCE_MASTER_DATA.sql before this script.
-- Scope:
--   Loads the provided Batsmen production dataset into tblPlayer.
--
-- Notes:
--   1) playerStats is saved in the agreed JSON structure: matches/runs/strikeRate/Wickets/economy.
--   2) playerStatus is initialized as NOT_ASSIGNED for auction readiness.
--   3) isConsiderInAuction is set to 0 as expected by current repository filters.
-- ============================================================================

USE epl_auction;

-- --------------------------------------------------------------------------
-- Step 1: Load production players (Batsmen)
-- --------------------------------------------------------------------------
INSERT INTO tblPlayer (
    playerName,
    skillId,
    photo,
    description,
    basePrice,
    playerStatus,
    playerStats,
    isNewPlayer,
    tournamentId,
    isConsiderInAuction,
    groupCode
) VALUES
(
    'Abhishek Patil',
    1,
    'https://static.cdn.epam.com/avatar/775e192fef9908eb3a29e6c5dab6b268.jpg',
    'Top-tier Explosive Batter (SR 176.80)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":64,"runs":861,"strikeRate":176.80,"Wickets":11,"economy":13.96}',
    0,
    1,
    0,
    'BAT-1'
),
(
    'Mahesh Gurjal',
    1,
    'https://static.cdn.epam.com/avatar/09dff20c9109e4191bf745b95014a64c.jpg',
    'Highly Experienced Anchor (2500+ Runs)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":224,"runs":2596,"strikeRate":142.87,"Wickets":0,"economy":0.00}',
    0,
    1,
    0,
    'BAT-1'
),
(
    'Madhur Maheshwari',
    1,
    'https://static.cdn.epam.com/avatar/5f719f63eb5736f75e129339f7dd477e.jpg',
    'Explosive All-Rounder (SR 200.73)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":28,"runs":550,"strikeRate":200.73,"Wickets":22,"economy":7.12}',
    0,
    1,
    0,
    'BAT-1'
),
(
    'Aashutosh Ajay Sharma',
    1,
    'https://static.cdn.epam.com/avatar/ef02a0d47beb8d1d8c0a45b5d9fe63bf.jpg',
    'Proven Specialist Batter (100 Matches)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":100,"runs":922,"strikeRate":124.09,"Wickets":8,"economy":11.40}',
    0,
    1,
    0,
    'BAT-1'
),
(
    'Vinayak Raut',
    1,
    'https://static.cdn.epam.com/avatar/f299065f4e011d9c21d93fc38cbfcff9.jpg',
    'Power Hitter (Monster SR 281.82)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":13,"runs":403,"strikeRate":281.82,"Wickets":8,"economy":9.40}',
    0,
    1,
    0,
    'BAT-1'
),
(
    'Balaji Dongare',
    1,
    'https://static.cdn.epam.com/avatar/bc70ddc532d8c17b39f759b33d618042.jpg',
    'Explosive scorer, consistent batting presence',
    4000,
    'NOT_ASSIGNED',
    '{"matches":27,"runs":230,"strikeRate":149.35,"Wickets":0,"economy":0.00}',
    0,
    1,
    0,
    'BAT-1'
),
(
    'Maroti Sorgekar',
    1,
    'https://static.cdn.epam.com/avatar/71a187337fb4ee6ff3b8afe1a294cefb.jpg',
    'Handy scorer and useful wicket-taker',
    4000,
    'NOT_ASSIGNED',
    '{"matches":18,"runs":175,"strikeRate":132.58,"Wickets":4,"economy":12.00}',
    0,
    1,
    0,
    'BAT-1'
),
(
    'Ashish Ranjan',
    1,
    'https://static.cdn.epam.com/avatar/badc344332e792497b99bc7683eb5e52.jpg',
    'Balanced Middle-Order Batter',
    4000,
    'NOT_ASSIGNED',
    '{"matches":42,"runs":542,"strikeRate":120.44,"Wickets":15,"economy":7.64}',
    0,
    1,
    0,
    'BAT-1'
),
(
    'Ganesh Kanade',
    1,
    'https://static.cdn.epam.com/avatar/193493f0db53853dfb10e6ab9a3ea988.jpg',
    'Wildcard / New Entry',
    4000,
    'NOT_ASSIGNED',
    '{"matches":6,"runs":115,"strikeRate":176.00,"Wickets":2,"economy":10.38}',
    0,
    1,
    0,
    'BAT-1'
),(
    'Akshay Salwatkar',
    1,
    'https://static.cdn.epam.com/avatar/f46ea04be9f6b177e6c30ffa983e23d1.jpg',
    'Aggressive batter with solid strike-rate',
    4000,
    'NOT_ASSIGNED',
    '{"matches":16,"runs":119,"strikeRate":136.78,"Wickets":0,"economy":0.00}',
    0,
    1,
    0,
    'BAT-1'
);

-- --------------------------------------------------------------------------
-- Step 2: Verification
-- --------------------------------------------------------------------------
SELECT id, playerName, skillId, description, basePrice, groupCode
FROM tblPlayer
WHERE skillId = 1
ORDER BY id;

SELECT skillId, groupCode, COUNT(*) AS player_count
FROM tblPlayer
GROUP BY skillId, groupCode
ORDER BY skillId, groupCode;

