# Auction Service

Spring Boot REST service for managing cricket player auctions — teams, players, pools, and bid outcomes.

## Prerequisites

- Java 21
- MySQL 8.x (or Docker)
- Gradle (wrapper included — no local install needed)

## Quickstart

### Option A — Docker Compose (recommended)

```bash
docker compose up -d
```

Starts MySQL 8.0 on port 3306 and auto-loads the schema. Then seed data and run the app:

```bash
# Seed reference master data (skills, tournament, teams, base prices)
mysql -u root -pRoot@123 epl_auction < sql-scripts/production/02-SQL_SEED_REFERENCE_MASTER_DATA.sql

# Load players (run all four, or pick the skills you need)
mysql -u root -pRoot@123 epl_auction < sql-scripts/production/03-SQL_LOAD_PRODUCTION_ALL_ROUNDERS.sql
mysql -u root -pRoot@123 epl_auction < sql-scripts/production/04-SQL_LOAD_PRODUCTION_BOWLERS.sql
mysql -u root -pRoot@123 epl_auction < sql-scripts/production/05-SQL_LOAD_PRODUCTION_BATSMEN.sql
mysql -u root -pRoot@123 epl_auction < sql-scripts/production/06-SQL_LOAD_PRODUCTION_WILDCARDS.sql

./gradlew bootRun
```

### Option B — Local MySQL

```bash
# Create schema
mysql -u root -p < sql-scripts/production/00-MY-SQL-TABLE.sql

# Seed data (same commands as above)
mysql -u root -pRoot@123 epl_auction < sql-scripts/production/02-SQL_SEED_REFERENCE_MASTER_DATA.sql
...

./gradlew bootRun
```

### Development / test data

Use the self-contained dev seed instead of the production scripts:

```bash
mysql -u root -pRoot@123 epl_auction < sql-scripts/development/01-SQL_DEV_TEST_DATA.sql
```

This truncates all tables and loads 12 fictional players across all four skill categories with pre-seeded auction outcomes (SOLD / UNSOLD / ASSIGNED) for end-to-end testing.

## Configuration

`src/main/resources/application.properties`:

| Property | Default |
|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/epl_auction` |
| `spring.datasource.username` | `root` |
| `spring.datasource.password` | `Root@123` |
| `server.port` | `8282` |
| `spring.jpa.show-sql` | `true` |

`spring.jpa.hibernate.ddl-auto=none` — Hibernate never auto-creates or alters tables. All schema changes must be applied manually.

## Build & Run

```bash
./gradlew build       # compile + test
./gradlew bootRun     # start on port 8282
./gradlew test        # run tests only
```

## API Reference

Base URL: `http://localhost:8282`  
Swagger UI: `http://localhost:8282/swagger-ui.html`

All endpoints are open (no auth, CORS wildcard).

---

### Auction (`/api`)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/teams` | All teams for the active tournament |
| `GET` | `/api/players` | Players with status `NOT_ASSIGNED` (available for auction) |
| `GET` | `/api/players/last-sold` | Last N sold players. Optional `?count=` (default `5`) |
| `POST` | `/api/players/auction` | Submit a bid — see request body below |
| `POST` | `/api/players/reset-auction?playerId={id}` | Reset a player back to `NOT_ASSIGNED` |
| `GET` | `/api/auction/next-pool-players?skillId={id}&tournamentId={id}` | Players in the next active pool for a skill |

**POST `/api/players/auction` — request body**

```json
{
  "playerId": 1,
  "teamId": 2,
  "soldPrice": 8500,
  "status": "SOLD"
}
```

`status` accepted values: `SOLD`, `UNSOLD`, `ASSIGNED`

- `SOLD` — writes a `tblTeamPlayer` record and deducts `soldPrice` from the team's `remainingPurse`.
- `ASSIGNED` — writes a `tblTeamPlayer` record but does **not** deduct purse.
- `UNSOLD` — updates player status only; no team record created.

---

### Players (`/api/players`)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/players/all-players` | All players regardless of status |

---

### Teams (`/api/teams`)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/teams/non-auction?skillId={id}&groupCode={code}` | First non-auction team for a given skill and pool |

---

### Groups / Pools (`/api/groups`)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/groups` | All pool groups |
| `GET` | `/api/groups/pools?skillId={id}&tournamentId={id}` | All pools for a skill |
| `GET` | `/api/groups/pools/next?skillId={id}&tournamentId={id}` | Next active pool (first pool with `NOT_ASSIGNED` players) |
| `GET` | `/api/groups/pools/{poolCode}/players?skillId={id}` | Players in a specific pool |
| `GET` | `/api/groups/pools/next/players?skillId={id}&tournamentId={id}` | Players in the next active pool |
| `GET` | `/api/groups/pools/{poolCode}/status?skillId={id}` | Pool counts: total / sold / remaining |
| `GET` | `/api/groups/pools/{poolCode}/complete?skillId={id}` | `true` if all players in the pool are SOLD or UNSOLD |

---

### Skills (`/api/skills`)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/skills` | All skill categories (BATSMAN, BOWLER, ALL_ROUNDER, WILDCARD) |

---

## Error Responses

All errors return `{"error": "<message>"}` with the appropriate HTTP status:

| Scenario | Status |
|---|---|
| Player not found | 404 |
| Team not found | 404 |
| Team has insufficient purse | 422 |
| Invalid state (e.g. resetting a `NOT_ASSIGNED` player) | 400 |

## SQL Scripts

```
sql-scripts/
├── production/
│   ├── 00-MY-SQL-TABLE.sql                       # Master schema
│   ├── 01-SQL_CLEANUP_AUCTION_DATA.sql            # Truncate all tables
│   ├── 02-SQL_SEED_REFERENCE_MASTER_DATA.sql      # Skills, tournament, teams, base prices
│   ├── 03-SQL_LOAD_PRODUCTION_ALL_ROUNDERS.sql
│   ├── 04-SQL_LOAD_PRODUCTION_BOWLERS.sql
│   ├── 05-SQL_LOAD_PRODUCTION_BATSMEN.sql
│   └── 06-SQL_LOAD_PRODUCTION_WILDCARDS.sql
└── development/
    └── 01-SQL_DEV_TEST_DATA.sql                   # Self-contained dev seed
```
