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
),
(
    'Rakesh Ghonmode',
    1,
    'https://static.cdn.epam.com/avatar/5a156e8e688656e62aaef66eaa2ce37c.jpg',
    'High Strike-Rate Finisher (SR 194.44)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":22,"runs":385,"strikeRate":194.44,"Wickets":19,"economy":9.29}',
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
    'Devidas Rakte',
    1,
    'https://static.cdn.epam.com/avatar/3e159a4dd5e39d492381b373c91a495b.jpg',
    'Wildcard / New Entry',
    4000,
    'NOT_ASSIGNED',
    '{"matches":3,"runs":23,"strikeRate":153.33,"Wickets":3,"economy":11.83}',
    0,
    1,
    0,
    'BAT-1'
),
(
    'Sharad Chavan',
    1,
    'https://static.cdn.epam.com/avatar/e98e38eeeb8e580c79637943ab545884.jpg',
    'Utility Top-Order Batter',
    4000,
    'NOT_ASSIGNED',
    '{"matches":72,"runs":426,"strikeRate":129.09,"Wickets":31,"economy":13.06}',
    0,
    1,
    0,
    'BAT-1'
),
(
    'ABHISHEK SHARMA',
    1,
    'https://static.cdn.epam.com/avatar/7961304fb4a916232c4381d1d4c212fd.jpg',
    'Middle-Order Batter',
    4000,
    'NOT_ASSIGNED',
    '{"matches":57,"runs":556,"strikeRate":117.05,"Wickets":22,"economy":12.02}',
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

