# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
./gradlew build

# Run (starts on port 8282)
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.cricket.auction.AuctionApplicationTests"
```

## Database Setup

**Option A — Docker Compose (recommended):**
```bash
docker compose up -d   # starts MySQL 8.0 on port 3306, auto-loads 00-MY-SQL-TABLE.sql as schema
```

**Option B — local MySQL:**
```bash
mysql -u root -p < 00-MY-SQL-TABLE.sql
```

DB credentials are in `src/main/resources/application.properties`:
- URL: `jdbc:mysql://localhost:3306/epl_auction`
- Default user/password: `root` / `Root@123`

`spring.jpa.hibernate.ddl-auto=none` — Hibernate will never auto-create or alter tables. All schema changes must be applied manually against MySQL.

Hibernate uses `PhysicalNamingStrategyStandardImpl`, so JPA entity field names must match MySQL column names exactly (no automatic camelCase → snake_case conversion).

## API & Swagger

- Base URL: `http://localhost:8282`
- Swagger UI: `http://localhost:8282/swagger-ui.html`
- All endpoints are open (CSRF disabled, no auth required, CORS wildcard)

## Architecture

**Package layout:** `com.cricket.auction`
- `controller/` — REST layer (Spring MVC)
- `service/` — business logic
- `repository/` — Spring Data JPA repositories with native SQL queries
- `entity/` — JPA-mapped DB tables
- `model/` — DTOs and projections (not persisted)
- `config/` — `SecurityConfig` (CORS + disable CSRF/auth)

**Controllers and their responsibilities:**
| Controller | Path | Purpose |
|---|---|---|
| `AuctionController` | `/api` | Teams; `NOT_ASSIGNED` players; bid submission; reset auction; last-sold; next-pool-players |
| `GroupController` | `/api/groups` | Pool management (list pools, pool status, pool players, next active pool) |
| `PlayerController` | `/api/players` | `/all-players` — all players regardless of status |
| `TeamController` | `/api/teams` | Non-auction team lookup |
| `SkillController` | `/api/skills` | Skill lookup |

Note: `GET /api/players` (AuctionController) returns only `NOT_ASSIGNED` players. `GET /api/players/all-players` (PlayerController) returns every player in any status.

**AuctionController endpoints:**
| Method | Path | Notes |
|---|---|---|
| `GET` | `/api/teams` | All teams for active tournament |
| `GET` | `/api/players` | `NOT_ASSIGNED` players only |
| `GET` | `/api/players/last-sold` | Last N sold players; optional `?count=` param (default 5) |
| `POST` | `/api/players/auction` | Bid submission — body: `PlayerAuctionRequest` |
| `POST` | `/api/players/reset-auction` | Reset a player back to `NOT_ASSIGNED`; query param `?playerId=` |
| `GET` | `/api/auction/next-pool-players` | Next active pool players; query params `?skillId=&tournamentId=` |

**`PlayerAuctionRequest` validation (used by `POST /api/players/auction`):**
- `playerId` — `@NotNull`
- `teamId` — `@NotNull`
- `soldPrice` — `@NotNull`, `@Min(0)`
- `status` — `@NotBlank`; accepted values: `SOLD`, `UNSOLD`, `ASSIGNED`

**Exception handling:**
`GlobalExceptionHandler` (`@RestControllerAdvice`) maps domain exceptions to HTTP status codes:
| Exception | HTTP status |
|---|---|
| `PlayerNotFoundException` | 404 |
| `TeamNotFoundException` | 404 |
| `InsufficientPurseException` | 422 |
| `IllegalStateException` | 400 |

All error responses have the shape `{"error": "<message>"}`.

**Key domain concepts:**

- **Tournament**: Only the row with `isActive = 1` is in scope. All player/team queries join `tblTournament` and filter on this flag.
- **Player status**: `SOLD`, `UNSOLD`, `NOT_ASSIGNED` (available for auction), `ASSIGNED` (assigned without bidding).
- **`isConsiderInAuction`**: Counter-intuitive — value `0` means the player **is** included in the auction pool. Value `1` means excluded. All queries filter `isConsiderInAuction = 0`.
- **Pool / groupCode**: Players are pre-assigned to pools (e.g., `P1`, `P2`) via the `groupCode` column on `tblPlayer`. Pools are per-skill. `AuctionPoolService` iterates `groupCode` values alphabetically and returns the first pool that still has `NOT_ASSIGNED` players.
- **Bid flow**: `POST /api/players/auction` calls `AuctionService.updatePlayerStatus`.
- `SOLD` — writes `tblTeamPlayer`, deducts `soldPrice` from `tblTeam.remainingPurse`.
- `ASSIGNED` — writes `tblTeamPlayer` but does **not** deduct purse (direct assignment without bidding).
- `UNSOLD` — updates player status only; no `tblTeamPlayer` record.

**Reset flow**: `POST /api/players/reset-auction?playerId=X` calls `AuctionService.resetPlayerAuction`. Deletes the latest `tblTeamPlayer` record, restores purse if status was `SOLD`, and sets player back to `NOT_ASSIGNED`. Only works on `SOLD`, `UNSOLD`, or `ASSIGNED` players.

**`PlayerResponseMapper`** (`model/mapper/`): a `@Component` that centralises projection→DTO mapping (JSON parse of `playerStats` string → `Map<String, Object>`). It is wired as a bean but `PlayerService` and `AuctionPoolService` each still carry a private `generatePlayerResponse` method with identical logic. If you change how `playerStats` JSON is parsed, update all three places (mapper + both services) until the duplication is removed.

**Data model:**

```
tblTournament (id, isActive, ...)
tblPlayerSkill (id, skillName)
tblPlayer (id, playerName, skillId→tblPlayerSkill, tournamentId→tblTournament,
           playerStatus, groupCode, isNewPlayer, isConsiderInAuction, playerStats[JSON string], ...)
tblTeam (id, teamName, purse, remainingPurse, tournamentId→tblTournament, ...)
tblTeamPlayer (id, teamId→tblTeam, playerId→tblPlayer, soldPrice, soldAt, tournamentId)
```

`PlayerResponseProjection` is a Spring Data projection interface used to map native query results. `PlayerResponse` is the DTO returned to callers; `playerStats` is stored as a JSON string in the DB and deserialized to `Map<String, Object>` in `AuctionPoolService.generatePlayerResponse`.

## SQL Scripts

All SQL lives under `sql-scripts/`:

```
sql-scripts/
├── production/
│   ├── 00-MY-SQL-TABLE.sql          # Master schema (source of truth; also auto-loaded by Docker Compose)
│   ├── 01-SQL_CLEANUP_AUCTION_DATA.sql   # Truncates all tables — run before a fresh load
│   ├── 02-SQL_SEED_REFERENCE_MASTER_DATA.sql  # Skills, tournament, base prices, teams
│   ├── 03-SQL_LOAD_PRODUCTION_ALL_ROUNDERS.sql
│   ├── 04-SQL_LOAD_PRODUCTION_BOWLERS.sql
│   ├── 05-SQL_LOAD_PRODUCTION_BATSMEN.sql
│   └── 06-SQL_LOAD_PRODUCTION_WILDCARDS.sql
└── development/
    └── 01-SQL_DEV_TEST_DATA.sql     # Self-contained dev seed: cleanup → reference data → 12 fictional
                                     # players (3 per skill, 2 pools each) → pre-seeded SOLD/UNSOLD/ASSIGNED
```

The `docker compose up -d` command auto-loads `sql-scripts/production/00-MY-SQL-TABLE.sql` as the init script (see `compose.yml`).

## Dependencies

- Spring Boot 3.5.5, Java 21
- Spring Data JPA + MySQL (`mysql-connector-j`)
- Spring Security (configured to permit all)
- springdoc-openapi 2.2.0 (Swagger UI)
- Lombok 1.18.34
