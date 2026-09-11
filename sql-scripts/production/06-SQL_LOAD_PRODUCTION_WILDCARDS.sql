-- ============================================================================
-- Production Data Load Script
-- ============================================================================
-- WARNING:
--   This script loads data only.
--   Run sql-scripts/production/01-SQL_CLEANUP_AUCTION_DATA.sql first if you need a full reset.
--   Run sql-scripts/production/02-SQL_SEED_REFERENCE_MASTER_DATA.sql before this script.
-- Scope:
--   Loads the provided Wildcards production dataset into tblPlayer.
--
-- Notes:
--   1) playerStats is saved in the agreed JSON structure: matches/runs/strikeRate/Wickets/economy.
--   2) playerStatus is initialized as NOT_ASSIGNED for auction readiness.
--   3) isConsiderInAuction is set to 0 as expected by current repository filters.
-- ============================================================================

USE epl_auction;

-- --------------------------------------------------------------------------
-- Step 1: Load production players (Wildcards)
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
    'Kuldip Bisen',
    4,
    'https://static.cdn.epam.com/avatar/e118cde8d45e0b5a04b41c7fb6dcbe97.jpg',
    'Reliable runs with occasional wickets',
    4000,
    'NOT_ASSIGNED',
    '{"matches":20,"runs":219,"strikeRate":133.54,"Wickets":1,"economy":11.29}',
    0,
    1,
    0,
    'WC-1'
),
(
    'Sachin Kalvikatte',
    4,
    'https://static.cdn.epam.com/avatar/e4f2de978c5da200a414cb366e27f20c.jpg',
    'A bowling all-rounder with stronger impact as a bowler.',
    4000,
    'NOT_ASSIGNED',
    '{"matches":13,"runs":87,"strikeRate":185.11,"Wickets":10,"economy":8.74}',
    0,
    1,
    0,
    'WC-1'
),
(
    'Rahul Bhagurkar',
    4,
    'https://static.cdn.epam.com/avatar/92bd4d6b9d222dc21b6583b168428458.jpg',
    'Quick scorer, limited bowling impact',
    4000,
    'NOT_ASSIGNED',
    '{"matches":9,"runs":104,"strikeRate":140.54,"Wickets":0,"economy":16.80}',
    0,
    1,
    0,
    'WC-1'
),
(
    'Ashish Biradar',
    4,
    'https://static.cdn.epam.com/avatar/ea71017fad3a695da7ff9f015291792c.jpg',
    'Strong all-round contribution with wickets',
    4000,
    'NOT_ASSIGNED',
    '{"matches":28,"runs":219,"strikeRate":106.83,"Wickets":6,"economy":11.54}',
    0,
    1,
    0,
    'WC-1'
),
(
    'Vishal Jagtap',
    4,
    'https://static.cdn.epam.com/avatar/37b06a50b36d8de7edec2454bb2dd3fc.jpg',
    'Dependable wicket-taker with steady batting',
    4000,
    'NOT_ASSIGNED',
    '{"matches":27,"runs":175,"strikeRate":107.36,"Wickets":10,"economy":9.35}',
    0,
    1,
    0,
    'WC-1'
),
(
    'Vikas Yadav',
    4,
    'https://static.cdn.epam.com/avatar/ebfad864214828b0cab38ba1e39f8477.jpg',
    'Outstanding bowler, economical and consistent',
    4000,
    'NOT_ASSIGNED',
    '{"matches":28,"runs":32,"strikeRate":60.38,"Wickets":16,"economy":6.70}',
    0,
    1,
    0,
    'WC-1'
),
(
    'Nitesh Sharma',
    4,
    'https://static.cdn.epam.com/avatar/5565189ef64903c4958c10bc23f7aba7.jpg',
    'Leading wicket-taker with steady contributions',
    4000,
    'NOT_ASSIGNED',
    '{"matches":33,"runs":76,"strikeRate":73.08,"Wickets":17,"economy":8.12}',
    0,
    1,
    0,
    'WC-1'
),
(
    'Ajinkya Chothave',
    4,
    'https://static.cdn.epam.com/avatar/0857758ce567ec7e877e92ed76274902.jpg',
    'Balanced contributor with economical bowling',
    4000,
    'NOT_ASSIGNED',
    '{"matches":13,"runs":79,"strikeRate":97.53,"Wickets":1,"economy":8.00}',
    0,
    1,
    0,
    'WC-1'
),
(
    'Aniket Ambarte',
    4,
    'https://static.cdn.epam.com/avatar/1543ed3c0466e3526ecfbc54e670cfa4.jpg',
    'Effective scorer and economical wicket-taker',
    4000,
    'NOT_ASSIGNED',
    '{"matches":24,"runs":161,"strikeRate":126.77,"Wickets":3,"economy":5.50}',
    0,
    1,
    0,
    'WC-1'
),
(
    'Satyam Tiwari',
    4,
    'https://static.cdn.epam.com/avatar/9c1eeb00e3d1644291d3bc07e808d1bc.jpg',
    'Promising bowler with useful wickets',
    4000,
    'NOT_ASSIGNED',
    '{"matches":12,"runs":17,"strikeRate":89.47,"Wickets":7,"economy":9.16}',
    0,
    1,
    0,
    'WC-1'
),(
    'Devidas Rakte',
    4,
    'https://static.cdn.epam.com/avatar/3e159a4dd5e39d492381b373c91a495b.jpg',
    'Wildcard / New Entry',
    4000,
    'NOT_ASSIGNED',
    '{"matches":3,"runs":23,"strikeRate":153.33,"Wickets":3,"economy":11.83}',
    0,
    1,
    0,
    'WC-1'
),(
    'Sharad Chavan',
    4,
    'https://static.cdn.epam.com/avatar/e98e38eeeb8e580c79637943ab545884.jpg',
    'Utility Top-Order Batter',
    4000,
    'NOT_ASSIGNED',
    '{"matches":72,"runs":426,"strikeRate":129.09,"Wickets":31,"economy":13.06}',
    0,
    1,
    0,
    'WC-1'
),
(
    'ABHISHEK SHARMA',
    4,
    'https://static.cdn.epam.com/avatar/7961304fb4a916232c4381d1d4c212fd.jpg',
    'Middle-Order Batter',
    4000,
    'NOT_ASSIGNED',
    '{"matches":57,"runs":556,"strikeRate":117.05,"Wickets":22,"economy":12.02}',
    0,
    1,
    0,
    'WC-1'
);

-- --------------------------------------------------------------------------
-- Step 2: Verification
-- --------------------------------------------------------------------------
SELECT id, playerName, skillId, description, basePrice, groupCode
FROM tblPlayer
WHERE skillId = 4
ORDER BY id;

SELECT skillId, groupCode, COUNT(*) AS player_count
FROM tblPlayer
GROUP BY skillId, groupCode
ORDER BY skillId, groupCode;

