# ⚡ Auction Service (Cricket Auction)

A lightweight Spring Boot service for managing cricket player auctions (teams, players, pools).

🎯 Key highlights
- Java + Spring Boot
- MySQL-backed (see MY-SQL-TABLE.md)
- Port: 8282 (configurable in src/main/resources/application.properties)

Prerequisites
- Java 11+ (or as required by gradle build)
- MySQL server
- Gradle (wrapper included)

Quickstart
1. Start MySQL and create the schema/tables:

   Open a terminal in the repo root and run (Windows/Unix):

   - Option A: run SQL file directly
     mysql -u root -p < "MY-SQL-TABLE.md"

   - Option B: import inside MySQL client
     mysql -u root -p
     mysql> SOURCE C:/path/to/repo/MY-SQL-TABLE.md;

   Note: The SQL script creates database `epl_auction` and all tables.

2. Load test data (optional):

   mysql -u root -p epl_auction < "src/TEST-DATA.md"

3. Configure DB credentials
   - Default datasource in src/main/resources/application.properties uses:
     jdbc:mysql://localhost:3306/epl_auction
     username: root
     password: (set in your local file)
   - Update the file or provide environment variables as needed.

4. Run the service (Windows):
   gradlew.bat bootRun

   Or (Unix/mac):
   ./gradlew bootRun

5. API will be available at: http://localhost:8282

API Endpoints (summary)

Base: http://localhost:8282

- GET /api/teams
  - Description: Get all teams
  - Example: curl http://localhost:8282/api/teams

- GET /api/players
  - Description: Get all players (non-auctioned by default in current code)
  - Example: curl http://localhost:8282/api/players

- POST /api/players/auction
  - Description: Update player auction status and assign to a team
  - Body: JSON -> { "playerId": <id>, "teamId": <id>, "soldPrice": <price>, "status": "SOLD" }
  - Example: curl -X POST -H "Content-Type: application/json" -d @payload.json http://localhost:8282/api/players/auction

- GET /api/auction/next-pool-players?skillId=<skillId>&tournamentId=<tournamentId>
  - Description: Returns players from the next active pool for a skill

Skill endpoints
- GET /api/skills
  - Description: Get all skills

Team helper
- GET /api/teams/non-auction?skillId=<skillId>&groupCode=<groupCode>
  - Description: Returns non-auction teams (expects skillId and groupCode query params)

Player helper
- GET /api/players/non-auctioned
  - Description: Returns players not yet auctioned

Group / Pool management (Pool Management API)
- GET /api/groups
  - Get all groups

- GET /api/groups/pools?skillId=<skillId>&tournamentId=<tournamentId>
  - Get all pools for a skill

- GET /api/groups/pools/next?skillId=<skillId>&tournamentId=<tournamentId>
  - Get next active pool for a skill

- GET /api/groups/pools/{poolCode}/players?skillId=<skillId>
  - Get players in a specific pool

- GET /api/groups/pools/next/players?skillId=<skillId>&tournamentId=<tournamentId>
  - Get players for the next active pool

- GET /api/groups/pools/{poolCode}/status?skillId=<skillId>
  - Get pool status (total/sold/remaining)

- GET /api/groups/pools/{poolCode}/complete?skillId=<skillId>
  - Check if pool is complete (sold/unsold)

Notes & Tips
- application.properties already points to MySQL on localhost:3306 and server.port=8282.
- If MY-SQL-TABLE.md has .md extension and your tools expect .sql, rename it to .sql or run import via the mysql client using SOURCE.
- For debugging enable spring.jpa.show-sql=true in application.properties (already set).

Useful commands
- Build: gradlew.bat build
- Run tests: gradlew.bat test

Contact
- Repo maintainer: Sunil Kulkarni

License
- (Add license here)

Happy auctioning! 🏏🚀
