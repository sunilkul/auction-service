-- ============================================================================
-- Reference/Master Data Seed Script
-- ============================================================================
-- Purpose:
--   Seeds base master data required before loading production players.
-- Run order:
--   1) SQL_CLEANUP_AUCTION_DATA.sql (optional/full reset)
--   2) SQL_SEED_REFERENCE_MASTER_DATA.sql
--   3) 03-SQL_LOAD_PRODUCTION_ALL_ROUNDERS.sql
-- ============================================================================

USE epl_auction;

INSERT INTO tblPlayerSkill (id, skillName) VALUES
                                               (1, 'BATSMAN'),
                                               (2, 'BOWLER'),
                                               (3, 'ALL_ROUNDER'),
                                               (4, 'WILDCARD');

INSERT INTO tblTournament (
    id,
    tournamentName,
    logo,
    tournamentYear,
    startDate,
    endDate,
    isActive
) VALUES (
             1,
             'EPAM Premier League',
             'epl-logo.png',
             '2026',
             '2026-09-01',
             '2026-09-30',
             1
         );

INSERT INTO tblPlayerBasePrice (playerBasePrice, tournamentId) VALUES
                                                                   (2000, 1),
                                                                   (5000, 1),
                                                                   (10000, 1),
                                                                   (15000, 1);

INSERT INTO tblTeam (teamName, logo, purse, remainingPurse, poc1, poc2, tournamentId) VALUES
                                                                                          ('EPAM Purandar', 'http://localhost:3000/team-logo/EPL_8/EPAM_PURANDAR.png', 120000, 120000, 'Sunny Virmani', 'Sushant Shinde', 1),
                                                                                          ('EPAM Lohagad', 'http://localhost:3000/team-logo/EPL_8/EPAM_LOHGAD.png', 120000, 120000, 'Pradip Thite', 'Mayuresh Behere', 1),
                                                                                          ('EPAM Devgiri', 'http://localhost:3000/team-logo/EPL_8/EPAM_DEVGIRI.png', 120000, 120000, 'Jitendra Gosavi', 'Rushikesh Ahire', 1),
                                                                                          ('EPAM Sinhagad', 'http://localhost:3000/team-logo/EPL_8/EPAM_SINHAGAD.png', 120000, 120000, 'Jincy Bharill', 'Vivek Chaturvedi', 1),
                                                                                          ('EPAM Panhala', 'http://localhost:3000/team-logo/EPL_8/EPAM_PANHALA.png', 120000, 120000, 'Vikas Tiwari', 'Tejas Mudekar', 1),
                                                                                          ('EPAM Torna', 'http://localhost:3000/team-logo/EPL_8/EPAM_TORNA.png', 120000, 120000, 'Mayur Ingle', 'Ritesh Patel', 1),
                                                                                          ('EPAM Raigad', 'http://localhost:3000/team-logo/EPL_8/EPAM_RAIGAD.png', 120000, 120000, 'Nandkumar Mname', 'Manoj Sathe', 1),
                                                                                          ('EPAM Shivneri', 'http://localhost:3000/team-logo/EPL_8/EPAM_SHIVNERI.png', 120000, 120000, 'Sopan Shelar', 'Tushar Kinhikar', 1),
                                                                                          ('EPAM Harihar', 'http://localhost:3000/team-logo/EPL_8/EPAM_HARIHAR.png', 120000, 120000, 'Vishal Patwardhan', 'Suraj Yadav', 1),
                                                                                          ('EPAM Kondhana', 'http://localhost:3000/team-logo/EPL_8/EPAM_KONDHANA.png', 120000, 120000, 'Krishna Agrawal', 'Aditya Bansod', 1);

-- Optional verification
SELECT
    (SELECT COUNT(*) FROM tblPlayerSkill) AS skills_count,
    (SELECT COUNT(*) FROM tblTournament) AS tournaments_count,
    (SELECT COUNT(*) FROM tblPlayerBasePrice) AS base_prices_count,
    (SELECT COUNT(*) FROM tblTeam) AS teams_count;

