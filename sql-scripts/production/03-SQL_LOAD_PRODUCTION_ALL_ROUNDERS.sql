-- ============================================================================
-- Production Data Load Script
-- ============================================================================
-- WARNING:
--   This script loads data only.
--   Run sql-scripts/production/01-SQL_CLEANUP_AUCTION_DATA.sql first if you need a full reset.
--   Run sql-scripts/production/02-SQL_SEED_REFERENCE_MASTER_DATA.sql before this script.
-- Scope:
--   Loads the provided All Rounders production dataset into tblPlayer.
--
-- Notes:
--   1) playerStats is saved in the agreed JSON structure: matches/runs/strikeRate/Wickets/economy.
--   2) playerStatus is initialized as NOT_ASSIGNED for auction readiness.
--   3) isConsiderInAuction is set to 0 as expected by current repository filters.
-- ============================================================================

USE epl_auction;

-- --------------------------------------------------------------------------
-- Step 1: Load production players (All Rounders)
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
    'Satish Parmeshwar',
    3,
    'https://static.cdn.epam.com/avatar/c6657bd1e759279f4bd9bf3a9cb47fbf.jpg',
    'Premium All-Rounder (Huge Experience)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":357,"runs":3399,"strikeRate":147.02,"Wickets":304,"economy":8.56}',
    0,
    1,
    0,
    'AR-1'
),
(
    'Abhishek Gawande',
    3,
    'https://static.cdn.epam.com/avatar/0e22e624911d782def6846d49a5f0102.jpg',
    'Elite Bowling All-Rounder (Eco 7.28)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":49,"runs":504,"strikeRate":167.44,"Wickets":50,"economy":7.28}',
    0,
    1,
    0,
    'AR-1'
),
(
    'Madhur Maheshwari',
    3,
    'https://static.cdn.epam.com/avatar/5f719f63eb5736f75e129339f7dd477e.jpg',
    'Explosive All-Rounder (SR 200.73)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":28,"runs":550,"strikeRate":200.73,"Wickets":22,"economy":7.12}',
    0,
    1,
    0,
    'AR-1'
),
(
    'Rohit Baghel',
    3,
    'https://static.cdn.epam.com/avatar/1d1e3abd57070aac4801b6d79e4a5d23.jpg',
    'Bowling All-Rounder (Eco 7.30)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":49,"runs":304,"strikeRate":169.83,"Wickets":45,"economy":7.30}',
    0,
    1,
    0,
    'AR-1'
),
(
    'Prathyu',
    3,
    'https://static.cdn.epam.com/avatar/520cb534f5afc17609dc888ad276dcfd.jpg',
    'Pure Batting All-Rounder (1500+ Runs)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":74,"runs":1514,"strikeRate":151.10,"Wickets":20,"economy":8.22}',
    0,
    1,
    0,
    'AR-1'
),
(
    'Sunil Kulkarni',
    3,
    'https://static.cdn.epam.com/avatar/c44f13946a6630c646d073dd6488ec1e.jpg',
    'Balanced All-Rounder',
    4000,
    'NOT_ASSIGNED',
    '{"matches":82,"runs":781,"strikeRate":137.26,"Wickets":64,"economy":8.65}',
    0,
    1,
    0,
    'AR-1'
),
(
    'Pravin Chaudhari',
    3,
    'https://static.cdn.epam.com/avatar/2a6fca47c3386287a7f81abfb681334e.jpg',
    'Power-hitting All-Rounder',
    4000,
    'NOT_ASSIGNED',
    '{"matches":59,"runs":1075,"strikeRate":158.79,"Wickets":31,"economy":9.30}',
    0,
    1,
    0,
    'AR-1'
),
(
    'Sandip Katore',
    3,
    'https://static.cdn.epam.com/avatar/b1345d8fc51da0a9c021f624b664771c.jpg',
    'High SR Finisher (SR 176.41)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":89,"runs":1062,"strikeRate":176.41,"Wickets":37,"economy":10.25}',
    0,
    1,
    0,
    'AR-1'
),
(
    'Ankush Regundwar',
    3,
    'https://static.cdn.epam.com/avatar/5fc4c6592bca2d9bb8468833e8785d29.jpg',
    'Experienced Bowling All-Rounder (100+ Wkts)',
    4000,
    'NOT_ASSIGNED',
    '{"matches":154,"runs":660,"strikeRate":114.98,"Wickets":101,"economy":8.87}',
    0,
    1,
    0,
    'AR-1'
),
(
    'Harshit Saran',
    3,
    'https://static.cdn.epam.com/avatar/609b8b2796c1fe056f91d07fce79acc2.jpg',
    'Utility All-Rounder',
    4000,
    'NOT_ASSIGNED',
    '{"matches":54,"runs":636,"strikeRate":162.24,"Wickets":30,"economy":9.85}',
    0,
    1,
    0,
    'AR-1'
);

-- --------------------------------------------------------------------------
-- Step 2: Verification
-- --------------------------------------------------------------------------
SELECT id, playerName, skillId, description, basePrice, groupCode
FROM tblPlayer
ORDER BY id;

SELECT skillId, groupCode, COUNT(*) AS player_count
FROM tblPlayer
GROUP BY skillId, groupCode
ORDER BY skillId, groupCode;

