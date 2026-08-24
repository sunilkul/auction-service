# Pool-Based Auction System Implementation

## Overview
This document describes the implementation of a pool-based auction system using `groupCode` as a pool identifier within each skill category. Players are grouped by both **Skill** and **Pool** (groupCode), allowing multiple independent auction pools per skill.

## Database Schema (No Changes Required!)
The system reuses existing fields:
- **Player.skillId** - Skill category (e.g., Batsman, Bowler)
- **Player.groupCode** - Pool identifier within that skill (e.g., P1, P2, P3)

### Example Data Structure
```
Batsman (skillId=1)
├── Pool P1 (groupCode='P1') → 5 players
├── Pool P2 (groupCode='P2') → 5 players
└── Pool P3 (groupCode='P3') → 5 players

Bowler (skillId=2)
├── Pool P1 (groupCode='P1') → 4 players
└── Pool P2 (groupCode='P2') → 4 players
```

## Architecture Changes

### 1. New Repository Queries (PlayerRepository)
Added methods to query pools:

```java
// Get all pool codes for a skill
List<String> getPoolsBySkill(Integer skillId, Integer tournamentId);

// Get all players from a specific pool
List<PlayerResponseProjection> getPoolPlayers(Integer skillId, String poolCode);

// Count completed players in a pool (SOLD/UNSOLD)
Integer getPoolCompleteCount(Integer skillId, String poolCode);

// Count total players in a pool
Integer getPoolTotalCount(Integer skillId, String poolCode);

// Get all players ordered by pool and ID for sequential auction
List<PlayerResponseProjection> getNextPoolForSkill(Integer skillId, Integer tournamentId);
```

### 2. New Service: AuctionPoolService
Manages pool-level operations:

```java
@Service
public class AuctionPoolService {
    // Get all pools for a skill with status
    List<PoolStatus> getPoolsBySkill(Integer skillId, Integer tournamentId);
    
    // Get next incomplete pool
    PoolStatus getNextActivePool(Integer skillId, Integer tournamentId);
    
    // Get players from a specific pool
    List<PlayerResponse> getPoolPlayers(Integer skillId, String poolCode);
    
    // Get players from next active pool
    List<PlayerResponse> getNextPoolPlayers(Integer skillId, Integer tournamentId);
    
    // Get detailed pool status
    PoolStatus getPoolStatus(Integer skillId, String poolCode);
    
    // Check if all players in pool are sold/unsold
    Boolean isPoolComplete(Integer skillId, String poolCode);
}
```

### 3. New Model: PoolStatus
Represents the status of a pool:

```java
@Data
public class PoolStatus {
    private Integer skillId;           // Skill ID
    private String skillName;          // Skill Name
    private String poolCode;           // Pool identifier (P1, P2, etc.)
    private Integer total;             // Total players in pool
    private Integer sold;              // Players marked as SOLD/UNSOLD
    private Integer remaining;         // Unsold players yet to auction
    private Boolean isComplete;        // true if all players in pool are finalized
}
```

## API Endpoints

### Pool Management Endpoints

#### 1. Get All Pools for a Skill
```
GET /api/groups/pools?skillId=1&tournamentId=1
Response:
[
  {
    "skillId": 1,
    "skillName": "Batsman",
    "poolCode": "P1",
    "total": 5,
    "sold": 2,
    "remaining": 3,
    "isComplete": false
  },
  {
    "skillId": 1,
    "skillName": "Batsman",
    "poolCode": "P2",
    "total": 5,
    "sold": 0,
    "remaining": 5,
    "isComplete": false
  }
]
```

#### 2. Get Next Active Pool
```
GET /api/groups/pools/next?skillId=1&tournamentId=1
Response:
{
  "skillId": 1,
  "skillName": "Batsman",
  "poolCode": "P1",
  "total": 5,
  "sold": 2,
  "remaining": 3,
  "isComplete": false
}
```

#### 3. Get Players in a Specific Pool
```
GET /api/groups/pools/P1/players?skillId=1
Response:
[
  {
    "id": 1,
    "name": "Virat Kohli",
    "photo": "url",
    "basePrice": 100000,
    "stats": { ... },
    "status": "NOT_ASSIGNED",
    "skillId": 1,
    "skillName": "Batsman",
    "groupCode": "P1",
    ...
  },
  ...
]
```

#### 4. Get Next Pool's Players
```
GET /api/auction/next-pool-players?skillId=1&tournamentId=1
Response:
[
  { player objects from next incomplete pool }
]
```

#### 5. Get Pool Status
```
GET /api/groups/pools/P1/status?skillId=1
Response:
{
  "skillId": 1,
  "skillName": "Batsman",
  "poolCode": "P1",
  "total": 5,
  "sold": 2,
  "remaining": 3,
  "isComplete": false
}
```

#### 6. Check if Pool is Complete
```
GET /api/groups/pools/P1/complete?skillId=1
Response: true/false
```

## Auction Flow

### Frontend/Client Usage

1. **Start Auction for a Skill:**
   ```
   GET /api/groups/pools/next?skillId=1&tournamentId=1
   ```
   → Get the first incomplete pool (P1)

2. **Display Players:**
   ```
   GET /api/groups/pools/P1/players?skillId=1
   ```
   → Display all players from P1

3. **Auction Players (using existing endpoint):**
   ```
   POST /api/players/auction
   {
     "playerId": 1,
     "teamId": 2,
     "soldPrice": 150000,
     "status": "SOLD"
   }
   ```
   → This works for any pool; existing logic unchanged

4. **Check Pool Progress:**
   ```
   GET /api/groups/pools/P1/status?skillId=1
   ```
   → Shows remaining players

5. **Move to Next Pool (when current complete):**
   ```
   GET /api/groups/pools/next?skillId=1&tournamentId=1
   ```
   → Automatically returns P2 once P1 is complete

## Existing Functionality Preservation

✅ **All existing endpoints remain unchanged:**
- `GET /api/players` - Still returns all players
- `POST /api/players/auction` - Still marks players as SOLD/UNSOLD
- `GET /api/teams` - Still returns teams
- `GET /api/groups` - Still returns legacy group info

✅ **No database migrations required** - Uses existing `groupCode` and `skillId` fields

✅ **Backward compatible** - Old code paths still work without modification

## Implementation Details

### Player Status Flow (Per Pool)
```
NOT_ASSIGNED (player in pool, waiting to auction)
    ↓
    ├→ SOLD (auctioned to team, paid price ≥ basePrice)
    │
    └→ UNSOLD (rejected, no team wanted at any price)
         ↓
    (Pool moves to next only when ALL players are SOLD or UNSOLD)
```

### Pool Completion Logic
A pool is considered **complete** when:
```
ALL players in pool have status = SOLD OR UNSOLD
AND
total players count = (SOLD count + UNSOLD count)
```

### Auto-Sequencing
The system automatically determines the next pool by:
1. Getting all pools for a skill in order (P1, P2, P3...)
2. For each pool, checking if `remaining > 0`
3. Returning the first pool with remaining players

## Usage Examples

### Example 1: Multi-Pool Auction Flow
```java
// Auction coordinator starts with Batsman category
GET /api/groups/pools/next?skillId=1&tournamentId=1
// Response: Pool P1 with 5 players

// Display P1 players and start auctioning
GET /api/groups/pools/P1/players?skillId=1
// Auction each player using POST /api/players/auction

// Check progress
GET /api/groups/pools/P1/status?skillId=1
// Shows: remaining=1 (still 1 player to go)

// After all players in P1 are auctioned
GET /api/groups/pools/next?skillId=1&tournamentId=1
// Response: Pool P2 (automatic progression)
```

### Example 2: Parallel Skills
```
Skill1 (Batsman): P1 → P2 → P3 (independent)
Skill2 (Bowler):  P1 → P2 (independent)

Each skill's pools auction independently!
Skill1-P1 does NOT affect Skill2-P1
```

### Example 3: Pool Statistics
```java
// Get all pools overview
GET /api/groups/pools?skillId=1&tournamentId=1
// Shows: P1 (60% complete), P2 (0% complete), P3 (not started)

// Decide which pool to auction next based on coordinator preference
```

## Migration Notes for Existing Data

If you have existing data with `groupCode` already populated:

1. **No schema changes needed** - System works with current structure
2. **Ensure groupCode follows format:** P1, P2, P3, etc. (per skill)
3. **Set isConsiderInAuction = 0** for players meant for pool auction
4. **Verify skillId is populated** for all players

## Testing Checklist

- [ ] Verify `getPoolsBySkill` returns pools in order
- [ ] Verify `getNextActivePool` skips complete pools
- [ ] Verify `getPoolPlayers` returns only NOT_ASSIGNED players
- [ ] Verify existing auction endpoint still works
- [ ] Verify pool status updates after player auction
- [ ] Verify pool complete detection works
- [ ] Test parallel skills don't interfere
- [ ] Test with multiple tournaments

## Benefits of This Approach

✅ **No Database Changes** - Reuses existing fields  
✅ **Backward Compatible** - All existing code works  
✅ **Flexible** - Unlimited pools per skill  
✅ **Auto-Sequencing** - Automatic pool progression  
✅ **Clean Separation** - Each skill has independent pools  
✅ **Scalable** - Works with any number of skills/pools  
✅ **Simple to Understand** - Pool = groupCode per skill  

## Troubleshooting

**Issue:** Pool shows 0 players
- Check `isConsiderInAuction = 0` for those players
- Verify `playerStatus = 'NOT_ASSIGNED'`
- Verify `groupCode` matches exactly

**Issue:** Pool not transitioning to next
- Ensure all players in current pool have status SOLD or UNSOLD
- Check pool complete detection: `remaining should be 0`

**Issue:** Next pool returns all pools
- Call `/api/groups/pools/next` not `/api/groups/pools`
- Ensure you're passing correct skillId and tournamentId
