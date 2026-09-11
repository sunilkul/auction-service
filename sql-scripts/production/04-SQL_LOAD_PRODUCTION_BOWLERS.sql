-- ============================================================================
-- Production Data Load Script
-- ============================================================================
-- WARNING:
--   This script loads data only.
--   Run sql-scripts/production/01-SQL_CLEANUP_AUCTION_DATA.sql first if you need a full reset.
--   Run sql-scripts/production/02-SQL_SEED_REFERENCE_MASTER_DATA.sql before this script.
-- Scope:
--   Loads the provided Bowlers production dataset into tblPlayer.
--
-- Notes:
--   1) playerStats is saved in the agreed JSON structure: matches/runs/strikeRate/Wickets/economy.
--   2) playerStatus is initialized as NOT_ASSIGNED for auction readiness.
--   3) isConsiderInAuction is set to 0 as expected by current repository filters.
-- ============================================================================

USE epl_auction;

-- --------------------------------------------------------------------------
-- Step 1: Load production players (Bowlers)
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
    'Shiv Jirwankar',
    2,
    'https://static.cdn.epam.com/avatar/b01a8df8c781c1a109bc695425ea144b.jpg',
    'Leading Wicket-Taker and Strike Bowler (77 Wkts)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":73,"runs":312,"strikeRate":98.73,"Wickets":77,"economy":8.47}',
    0,
    1,
    0,
    'BW-1'
),
(
    'Niranjan Ingole',
    2,
    'https://static.cdn.epam.com/avatar/9b25240a8a481b8ad47c65c1fa82eaee.jpg',
    'Highly Economical Strike Option (Eco 7.27, 20 Wkts)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":17,"runs":26,"strikeRate":68.42,"Wickets":20,"economy":7.27}',
    0,
    1,
    0,
    'BW-1'
),
(
    'Omkar Bhujbal',
    2,
    'https://static.cdn.epam.com/avatar/f8f561f66ef6aef15e02b32c26342b17.jpg',
    'Lockdown Economy Specialist (Eco 6.70)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":28,"runs":32,"strikeRate":60.38,"Wickets":16,"economy":6.70}',
    0,
    1,
    0,
    'BW-1'
),
(
    'Rakesh Ghonmode',
    2,
    'https://static.cdn.epam.com/avatar/5a156e8e688656e62aaef66eaa2ce37c.jpg',
    'Top-tier Wicket-Taker (19 Wkts, Eco 9.29)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":22,"runs":385,"strikeRate":194.44,"Wickets":19,"economy":9.29}',
    0,
    1,
    0,
    'BW-1'
)
,
(
    'Onkar Joshi',
    2,
    'https://static.cdn.epam.com/avatar/651eeb32709418a718e1c900c0c0df2a.jpg',
    'Wildcard / New Entry',
    4000,
    'NOT_ASSIGNED',
    '{"matches":14,"runs":5,"strikeRate":45.45,"Wickets":9,"economy":9.46}',
    0,
    1,
    0,
    'BW-1'
),
(
    'Rohit Baghel',
    2,
    'https://static.cdn.epam.com/avatar/1d1e3abd57070aac4801b6d79e4a5d23.jpg',
    'Dynamic all-rounder with strong batting and bowling',
    4000,
    'NOT_ASSIGNED',
    '{"matches":49,"runs":304,"strikeRate":169.83,"Wickets":45,"economy":7.30}',
    0,
    1,
    0,
    'BW-1'
),
(
    'Sudhir Dhondje',
    2,
    'https://static.cdn.epam.com/avatar/1f982076320531979ac0fe427ad3975e.jpg',
    'Wildcard / New Entry',
    4000,
    'NOT_ASSIGNED',
    '{"matches":27,"runs":68,"strikeRate":133.33,"Wickets":17,"economy":10.21}',
    0,
    1,
    0,
    'BW-1'
),
(
    'Sumant Dharkar',
    2,
    'https://static.cdn.epam.com/avatar/13b2eb69be55dcda6eedea722afd61cf.jpg',
    'Experienced Bowler (28 Wkts)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":53,"runs":289,"strikeRate":99.66,"Wickets":28,"economy":10.06}',
    0,
    1,
    0,
    'BW-1'
),
(
    'Ujjwal Kumar',
    2,
    'https://static.cdn.epam.com/avatar/d542a38785a6e2011498193fc588f31b.jpg',
    'Utility Support Bowler (68 Matches)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":68,"runs":537,"strikeRate":110.72,"Wickets":15,"economy":9.11}',
    0,
    1,
    0,
    'BW-1'
),
(
    'Dhananjay Bathe',
    2,
    'https://static.cdn.epam.com/avatar/9c62e80e61664443b09e264ba3425868.jpg',
    'Budget Support Bowler',
    4000,
    'NOT_ASSIGNED',
    '{"matches":18,"runs":98,"strikeRate":113.95,"Wickets":14,"economy":8.97}',
    0,
    1,
    0,
    'BW-1'
);

-- --------------------------------------------------------------------------
-- Step 2: Verification
-- --------------------------------------------------------------------------
SELECT id, playerName, skillId, description, basePrice, groupCode
FROM tblPlayer
WHERE skillId = 2
ORDER BY id;

SELECT skillId, groupCode, COUNT(*) AS player_count
FROM tblPlayer
GROUP BY skillId, groupCode
ORDER BY skillId, groupCode;

