-- ============================================================================
-- Pool-Based Auction System - SQL Reference & Testing
-- ============================================================================

-- ============================================================================
-- SECTION 1: Setup & Data Verification
-- ============================================================================

-- Check existing skill data
SELECT * FROM tblPlayerSkill;
-- Expected: id, skillName (Batsman, Bowler, etc.)

-- Check existing players with groupCode
SELECT 
  id, 
  playerName, 
  skillId, 
  groupCode, 
  playerStatus, 
  isConsiderInAuction
FROM tblPlayer
LIMIT 20;

-- Check data readiness for pools
SELECT 
  skillId,
  groupCode,
  COUNT(*) as player_count
FROM tblPlayer
WHERE isConsiderInAuction = 0
GROUP BY skillId, groupCode
ORDER BY skillId, groupCode;

-- ============================================================================
-- SECTION 2: Pool Queries (Mirrors Repository Methods)
-- ============================================================================

-- Query 1: Get all pools for a skill
-- Repository: getPoolsBySkill(Integer skillId, Integer tournamentId)
SELECT DISTINCT p.groupCode
FROM tblPlayer p
JOIN tblTournament tr ON p.tournamentId = tr.id
WHERE p.skillId = 1 
  AND p.tournamentId = 1
  AND tr.isActive = 1
  AND p.isConsiderInAuction = 0
ORDER BY p.groupCode ASC;

-- Query 2: Get all players in a specific pool
-- Repository: getPoolPlayers(Integer skillId, String poolCode)
SELECT 
  p.id,
  p.playerName as name,
  p.photo,
  p.basePrice,
  p.playerStats as stats,
  p.playerStatus as status,
  p.skillId,
  ps.skillName,
  tp.soldPrice,
  t.id as teamId,
  t.teamName,
  p.isNewPlayer,
  p.groupCode
FROM tblPlayer p
JOIN tblTournament tr ON p.tournamentId = tr.id
LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
LEFT JOIN tblTeamPlayer tp ON p.id = tp.playerId
LEFT JOIN tblTeam t ON t.id = tp.teamId
WHERE tr.isActive = 1
  AND p.skillId = 1
  AND p.groupCode = 'P1'
  AND p.isConsiderInAuction = 0
  AND p.playerStatus = 'NOT_ASSIGNED'
ORDER BY p.id ASC;

-- Query 3: Count completed players in a pool (SOLD/UNSOLD)
-- Repository: getPoolCompleteCount(Integer skillId, String poolCode)
SELECT COUNT(*) as completed_count
FROM tblPlayer
WHERE skillId = 1
  AND groupCode = 'P1'
  AND playerStatus IN ('SOLD', 'UNSOLD');

-- Query 4: Count total players in a pool
-- Repository: getPoolTotalCount(Integer skillId, String poolCode)
SELECT COUNT(*) as total_count
FROM tblPlayer
WHERE skillId = 1
  AND groupCode = 'P1';

-- Query 5: Get detailed pool statistics (Combined)
SELECT 
  p.skillId,
  ps.skillName,
  p.groupCode as poolCode,
  COUNT(*) as total,
  SUM(CASE WHEN p.playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END) as sold,
  COUNT(*) - SUM(CASE WHEN p.playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END) as remaining,
  CASE 
    WHEN COUNT(*) = SUM(CASE WHEN p.playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END)
    THEN 1
    ELSE 0
  END as isComplete
FROM tblPlayer p
JOIN tblTournament tr ON p.tournamentId = tr.id
LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
WHERE tr.isActive = 1
  AND p.skillId = 1
  AND p.groupCode = 'P1'
  AND p.isConsiderInAuction = 0
GROUP BY p.skillId, ps.skillName, p.groupCode;

-- ============================================================================
-- SECTION 3: Pool Status Checks
-- ============================================================================

-- Get ALL pools for a skill with complete status
SELECT 
  p.skillId,
  ps.skillName,
  p.groupCode as poolCode,
  COUNT(*) as total,
  SUM(CASE WHEN p.playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END) as sold,
  COUNT(*) - SUM(CASE WHEN p.playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END) as remaining,
  CASE 
    WHEN COUNT(*) = SUM(CASE WHEN p.playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END)
    THEN 1
    ELSE 0
  END as isComplete
FROM tblPlayer p
JOIN tblTournament tr ON p.tournamentId = tr.id
LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
WHERE tr.isActive = 1
  AND p.skillId = 1
  AND p.tournamentId = 1
  AND p.isConsiderInAuction = 0
GROUP BY p.skillId, ps.skillName, p.groupCode
ORDER BY p.groupCode ASC;

-- Get NEXT incomplete pool for a skill
SELECT TOP 1
  p.skillId,
  ps.skillName,
  p.groupCode as poolCode,
  COUNT(*) as total,
  SUM(CASE WHEN p.playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END) as sold,
  COUNT(*) - SUM(CASE WHEN p.playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END) as remaining
FROM tblPlayer p
JOIN tblTournament tr ON p.tournamentId = tr.id
LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
WHERE tr.isActive = 1
  AND p.skillId = 1
  AND p.tournamentId = 1
  AND p.isConsiderInAuction = 0
GROUP BY p.skillId, ps.skillName, p.groupCode
HAVING COUNT(*) > SUM(CASE WHEN p.playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END)
ORDER BY p.groupCode ASC;

-- ============================================================================
-- SECTION 4: Pool Progression Tracking
-- ============================================================================

-- Track auction progress across all pools for a skill
SELECT 
  p.groupCode as poolCode,
  COUNT(*) as totalPlayers,
  SUM(CASE WHEN p.playerStatus = 'SOLD' THEN 1 ELSE 0 END) as soldCount,
  SUM(CASE WHEN p.playerStatus = 'UNSOLD' THEN 1 ELSE 0 END) as unsoldCount,
  SUM(CASE WHEN p.playerStatus = 'NOT_ASSIGNED' THEN 1 ELSE 0 END) as notAssignedCount,
  CASE 
    WHEN COUNT(*) = SUM(CASE WHEN p.playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END)
    THEN 'COMPLETE'
    ELSE 'IN_PROGRESS'
  END as poolStatus
FROM tblPlayer p
WHERE p.skillId = 1
  AND p.isConsiderInAuction = 0
GROUP BY p.groupCode
ORDER BY p.groupCode ASC;

-- ============================================================================
-- SECTION 5: Testing Scenarios
-- ============================================================================

-- SCENARIO 1: View all players in P1 pool waiting for auction
SELECT 
  p.id,
  p.playerName,
  p.basePrice,
  ps.skillName,
  p.groupCode
FROM tblPlayer p
LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
WHERE p.skillId = 1
  AND p.groupCode = 'P1'
  AND p.playerStatus = 'NOT_ASSIGNED'
  AND p.isConsiderInAuction = 0
ORDER BY p.id;

-- SCENARIO 2: Check P1 pool progress
SELECT 
  COUNT(*) as totalInP1,
  SUM(CASE WHEN playerStatus = 'SOLD' THEN 1 ELSE 0 END) as soldsCount,
  SUM(CASE WHEN playerStatus = 'UNSOLD' THEN 1 ELSE 0 END) as unsoldCount,
  SUM(CASE WHEN playerStatus = 'NOT_ASSIGNED' THEN 1 ELSE 0 END) as remainingCount
FROM tblPlayer
WHERE skillId = 1 AND groupCode = 'P1';

-- SCENARIO 3: Verify P1 is complete (ready for P2)
SELECT 
  CASE 
    WHEN COUNT(*) = SUM(CASE WHEN playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END)
    THEN 'Pool P1 is COMPLETE - Move to P2'
    ELSE 'Pool P1 still has ' + CAST(COUNT(*) - SUM(CASE WHEN playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END) AS VARCHAR) + ' remaining'
  END as poolStatus
FROM tblPlayer
WHERE skillId = 1 AND groupCode = 'P1';

-- SCENARIO 4: Check which pools have remaining players
SELECT 
  groupCode,
  COUNT(*) - SUM(CASE WHEN playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END) as remainingPlayers,
  STRING_AGG(playerName, ', ') as playerNames
FROM tblPlayer
WHERE skillId = 1
  AND playerStatus = 'NOT_ASSIGNED'
  AND isConsiderInAuction = 0
GROUP BY groupCode
ORDER BY groupCode;

-- SCENARIO 5: Compare parallel skills (each has independent pools)
SELECT 
  p.skillId,
  ps.skillName,
  p.groupCode,
  COUNT(*) as total,
  SUM(CASE WHEN p.playerStatus = 'NOT_ASSIGNED' THEN 1 ELSE 0 END) as remaining
FROM tblPlayer p
LEFT JOIN tblPlayerSkill ps ON p.skillId = ps.id
WHERE p.skillId IN (1, 2)
  AND p.isConsiderInAuction = 0
GROUP BY p.skillId, ps.skillName, p.groupCode
ORDER BY p.skillId, p.groupCode;

-- ============================================================================
-- SECTION 6: Data Validation
-- ============================================================================

-- Validate pool structure
-- All pools must have groupCode starting with 'P'
SELECT 
  id,
  playerName,
  skillId,
  groupCode
FROM tblPlayer
WHERE groupCode NOT LIKE 'P%'
  AND isConsiderInAuction = 0;
-- Expected: ZERO rows

-- Validate all pool players have skillId
SELECT COUNT(*) as missingSkillId
FROM tblPlayer
WHERE skillId IS NULL AND isConsiderInAuction = 0;
-- Expected: 0 rows

-- Validate no orphaned pools
SELECT DISTINCT groupCode
FROM tblPlayer
WHERE isConsiderInAuction = 0
ORDER BY groupCode;

-- ============================================================================
-- SECTION 7: Sample Data Setup (for testing)
-- ============================================================================

-- IF NEEDED: Assign groupCode to existing players
-- UPDATE tblPlayer
-- SET groupCode = 'P' + CAST(ROW_NUMBER() OVER (PARTITION BY skillId ORDER BY id) / 5 + 1 AS VARCHAR)
-- WHERE isConsiderInAuction = 0;

-- IF NEEDED: Reset all pool players to NOT_ASSIGNED
-- UPDATE tblPlayer
-- SET playerStatus = 'NOT_ASSIGNED'
-- WHERE isConsiderInAuction = 0
--   AND groupCode LIKE 'P%';

-- ============================================================================
-- SECTION 8: Performance Notes
-- ============================================================================

-- For better performance, consider adding indexes:
-- CREATE INDEX idx_player_skill_group ON tblPlayer(skillId, groupCode, playerStatus);
-- CREATE INDEX idx_player_tournament_consider ON tblPlayer(tournamentId, isConsiderInAuction);

-- ============================================================================
