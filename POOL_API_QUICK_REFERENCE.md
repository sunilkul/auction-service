# Quick Reference: Pool-Based Auction APIs

## Summary Table

| Endpoint | Method | Purpose | Returns |
|----------|--------|---------|---------|
| `/api/groups/pools` | GET | Get all pools for a skill | List of PoolStatus |
| `/api/groups/pools/next` | GET | Get next active pool | Single PoolStatus |
| `/api/groups/pools/{poolCode}/players` | GET | Get players in pool | List of PlayerResponse |
| `/api/auction/next-pool-players` | GET | Get next pool players | List of PlayerResponse |
| `/api/groups/pools/{poolCode}/status` | GET | Get pool statistics | PoolStatus |
| `/api/groups/pools/{poolCode}/complete` | GET | Check if pool complete | Boolean |
| `/api/players/auction` | POST | Auction a player | Updated Player entity |

## Request Parameters

```
skillId (required)     : Skill ID (1=Batsman, 2=Bowler, etc.)
tournamentId (required): Tournament ID
poolCode (required)    : Pool code in path (P1, P2, etc.)
```

## Response Models

### PoolStatus
```json
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

### PlayerResponse
```json
{
  "id": 1,
  "name": "Virat Kohli",
  "photo": "url...",
  "basePrice": 100000,
  "stats": { "batting_avg": 50, "runs": 5000 },
  "status": "NOT_ASSIGNED",
  "skillId": 1,
  "skillName": "Batsman",
  "soldPrice": null,
  "teamId": null,
  "teamName": null,
  "isNewPlayer": 0,
  "groupCode": "P1"
}
```

## Usage Scenarios

### Scenario 1: Start Auction
```bash
# Step 1: Get next pool for Skill 1
curl "http://localhost:8080/api/groups/pools/next?skillId=1&tournamentId=1"

# Step 2: Get all players in that pool
curl "http://localhost:8080/api/groups/pools/P1/players?skillId=1"

# Step 3: Start auctioning players
curl -X POST "http://localhost:8080/api/players/auction" \
  -H "Content-Type: application/json" \
  -d '{"playerId":1,"teamId":2,"soldPrice":150000,"status":"SOLD"}'
```

### Scenario 2: Check Progress
```bash
# Check current pool status
curl "http://localhost:8080/api/groups/pools/P1/status?skillId=1"

# Check if pool is complete
curl "http://localhost:8080/api/groups/pools/P1/complete?skillId=1"
```

### Scenario 3: Move to Next Pool
```bash
# Get next active pool (returns P2 if P1 complete, null if all complete)
curl "http://localhost:8080/api/groups/pools/next?skillId=1&tournamentId=1"
```

### Scenario 4: Get Players from Next Pool
```bash
# Directly get next pool players without two calls
curl "http://localhost:8080/api/auction/next-pool-players?skillId=1&tournamentId=1"
```

## Database Queries

### Check Pool Structure
```sql
-- Get all pools for a skill
SELECT DISTINCT groupCode, skillId, COUNT(*) as player_count
FROM tblPlayer
WHERE skillId = 1 AND isConsiderInAuction = 0
GROUP BY groupCode, skillId
ORDER BY groupCode;

-- Get pool progress
SELECT 
  groupCode,
  COUNT(*) as total,
  SUM(CASE WHEN playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END) as completed,
  SUM(CASE WHEN playerStatus = 'NOT_ASSIGNED' THEN 1 ELSE 0 END) as remaining
FROM tblPlayer
WHERE skillId = 1 AND groupCode = 'P1'
GROUP BY groupCode;

-- Check if pool complete
SELECT 
  CASE 
    WHEN COUNT(*) = SUM(CASE WHEN playerStatus IN ('SOLD', 'UNSOLD') THEN 1 ELSE 0 END)
    THEN 'Complete'
    ELSE 'Incomplete'
  END as pool_status
FROM tblPlayer
WHERE skillId = 1 AND groupCode = 'P1';
```

## Code Integration

### Java Usage
```java
@Autowired
private AuctionPoolService poolService;

// Get next pool
PoolStatus nextPool = poolService.getNextActivePool(1, 1);
System.out.println("Next pool: " + nextPool.getPoolCode());

// Get players
List<PlayerResponse> players = poolService.getPoolPlayers(1, "P1");
players.forEach(p -> System.out.println(p.getName()));

// Check completion
if (poolService.isPoolComplete(1, "P1")) {
    // Move to next pool
    PoolStatus nextPool = poolService.getNextActivePool(1, 1);
}
```

## Error Handling

| Error | Cause | Solution |
|-------|-------|----------|
| Skill not found | Invalid skillId | Verify skillId exists in tblPlayerSkill |
| No players returned | groupCode doesn't exist | Check data: `SELECT DISTINCT groupCode FROM tblPlayer WHERE skillId=?` |
| Pool shows 0 players | isConsiderInAuction=1 | Update: `UPDATE tblPlayer SET isConsiderInAuction=0 WHERE groupCode IN ('P1','P2',...)'` |
| Pool not progressing | Players still NOT_ASSIGNED | Ensure all players auctioned before moving |

## Files Modified

1. **PlayerRepository.java** - Added 5 new query methods
2. **AuctionPoolService.java** - New service class (7 public methods)
3. **PoolStatus.java** - New model class
4. **GroupController.java** - Added 6 pool endpoints
5. **AuctionController.java** - Added 1 pool endpoint

## Backward Compatibility

✅ All existing endpoints still work:
- `GET /api/players` - Returns all players
- `POST /api/players/auction` - Updates player status
- `GET /api/teams` - Returns teams
- `GET /api/groups` - Returns legacy groups

No breaking changes. Old code paths unaffected.
