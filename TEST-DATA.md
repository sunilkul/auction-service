-- -----------------------------
-- Insert seed data
-- -----------------------------

-- Skills
INSERT INTO tblPlayerSkill (skillName) VALUES
('BATSMAN'),
('BOWLER'),
('ALL_ROUNDER');

-- Tournament
INSERT INTO tblTournament (tournamentName, logo, tournamentYear, startDate, endDate, isActive)
VALUES ('Maharashtra Premier Auction', '', '2026', '2026-09-01', '2026-09-30', 1);

-- Player base price reference entries (generic categories)
INSERT INTO tblPlayerBasePrice (playerBasePrice, tournamentId) VALUES
(1000, 1),
(5000, 1),
(10000, 1);

-- Teams (10 teams named using Maharashtra forts) - each with purse 50000
INSERT INTO tblTeam (teamName, logo, purse, remainingPurse, poc1, poc2, tournamentId) VALUES
('Raigad Fort', '', 50000, 50000, 'Rohit Pawar', 'Sachin Ghadge', 1),
('Sinhagad Fort', '', 50000, 50000, 'Ajay More', 'Vijay Kulkarni', 1),
('Pratapgad Fort', '', 50000, 50000, 'Sagar Patil', 'Manoj Deshmukh', 1),
('Shivneri Fort', '', 50000, 50000, 'Sandeep Jadhav', 'Kunal Shinde', 1),
('Lohagad Fort', '', 50000, 50000, 'Rajat Taksande', 'Deepak Bhoyar', 1),
('Daulatabad Fort', '', 50000, 50000, 'Nilesh Wagh', 'Pradeep Kadam', 1),
('Torna Fort', '', 50000, 50000, 'Rahul Bhosale', 'Milind More', 1),
('Rajgad Fort', '', 50000, 50000, 'Vikram Desai', 'Sujit Rane', 1),
('Janjira Fort', '', 50000, 50000, 'Amit Kendre', 'Ramesh Shinde', 1),
('Vijaydurg Fort', '', 50000, 50000, 'Nikhil Patil', 'Harish Gawande', 1);

-- Players: 3 skills * 2 groups * 5 players = 30 players
-- Group codes: BAT_G1, BAT_G2, BWL_G1, BWL_G2, ALL_G1, ALL_G2
-- All playerStatus set to "UNSLOD" per request
-- Only IND, AUS, END players used

-- BATSMAN Group 1
INSERT INTO tblPlayer (playerName, skillId, photo, basePrice, playerStatus, playerStats, isNewPlayer, tournamentId, isConsiderInAuction, groupCode) VALUES
('Virat Kohli (IND)', 1, '', 15000, 'UNSLOD', '', 0, 1, 1, 'BAT_G1'),
('Rohit Sharma (IND)', 1, '', 14000, 'UNSLOD', '', 0, 1, 1, 'BAT_G1'),
('Steve Smith (AUS)', 1, '', 13000, 'UNSLOD', '', 0, 1, 1, 'BAT_G1'),
('Joe Root (END)', 1, '', 12000, 'UNSLOD', '', 0, 1, 1, 'BAT_G1'),
('David Warner (AUS)', 1, '', 12500, 'UNSLOD', '', 0, 1, 1, 'BAT_G1');

-- BATSMAN Group 2
INSERT INTO tblPlayer (playerName, skillId, photo, basePrice, playerStatus, playerStats, isNewPlayer, tournamentId, isConsiderInAuction, groupCode) VALUES
('KL Rahul (IND)', 1, '', 11000, 'UNSLOD', '', 0, 1, 1, 'BAT_G2'),
('Shikhar Dhawan (IND)', 1, '', 9000, 'UNSLOD', '', 0, 1, 1, 'BAT_G2'),
('Marnus Labuschagne (AUS)', 1, '', 8000, 'UNSLOD', '', 0, 1, 1, 'BAT_G2'),
('Jos Buttler (END)', 1, '', 11500, 'UNSLOD', '', 0, 1, 1, 'BAT_G2'),
('Jonny Bairstow (END)', 1, '', 9500, 'UNSLOD', '', 0, 1, 1, 'BAT_G2');

-- BOWLER Group 1
INSERT INTO tblPlayer (playerName, skillId, photo, basePrice, playerStatus, playerStats, isNewPlayer, tournamentId, isConsiderInAuction, groupCode) VALUES
('Jasprit Bumrah (IND)', 2, '', 16000, 'UNSLOD', '', 0, 1, 1, 'BWL_G1'),
('Mohammed Shami (IND)', 2, '', 9000, 'UNSLOD', '', 0, 1, 1, 'BWL_G1'),
('Pat Cummins (AUS)', 2, '', 15000, 'UNSLOD', '', 0, 1, 1, 'BWL_G1'),
('Mitchell Starc (AUS)', 2, '', 14000, 'UNSLOD', '', 0, 1, 1, 'BWL_G1'),
('James Anderson (END)', 2, '', 8000, 'UNSLOD', '', 0, 1, 1, 'BWL_G1');

-- BOWLER Group 2
INSERT INTO tblPlayer (playerName, skillId, photo, basePrice, playerStatus, playerStats, isNewPlayer, tournamentId, isConsiderInAuction, groupCode) VALUES
('Ravichandran Ashwin (IND)', 2, '', 10000, 'UNSLOD', '', 0, 1, 1, 'BWL_G2'),
('Nathan Lyon (AUS)', 2, '', 9500, 'UNSLOD', '', 0, 1, 1, 'BWL_G2'),
('Josh Hazlewood (AUS)', 2, '', 9000, 'UNSLOD', '', 0, 1, 1, 'BWL_G2'),
('Stuart Broad (END)', 2, '', 7500, 'UNSLOD', '', 0, 1, 1, 'BWL_G2'),
('Adil Rashid (END)', 2, '', 7000, 'UNSLOD', '', 0, 1, 1, 'BWL_G2');

-- ALL_ROUNDER Group 1
INSERT INTO tblPlayer (playerName, skillId, photo, basePrice, playerStatus, playerStats, isNewPlayer, tournamentId, isConsiderInAuction, groupCode) VALUES
('Ben Stokes (END)', 3, '', 17000, 'UNSLOD', '', 0, 1, 1, 'ALL_G1'),
('Hardik Pandya (IND)', 3, '', 12000, 'UNSLOD', '', 0, 1, 1, 'ALL_G1'),
('Glenn Maxwell (AUS)', 3, '', 11000, 'UNSLOD', '', 0, 1, 1, 'ALL_G1'),
('Marcus Stoinis (AUS)', 3, '', 9000, 'UNSLOD', '', 0, 1, 1, 'ALL_G1'),
('Sam Curran (END)', 3, '', 10000, 'UNSLOD', '', 0, 1, 1, 'ALL_G1');

-- ALL_ROUNDER Group 2
INSERT INTO tblPlayer (playerName, skillId, photo, basePrice, playerStatus, playerStats, isNewPlayer, tournamentId, isConsiderInAuction, groupCode) VALUES
('Ravindra Jadeja (IND)', 3, '', 15000, 'UNSLOD', '', 0, 1, 1, 'ALL_G2'),
('Moeen Ali (END)', 3, '', 8000, 'UNSLOD', '', 0, 1, 1, 'ALL_G2'),
('Mitchell Marsh (AUS)', 3, '', 9500, 'UNSLOD', '', 0, 1, 1, 'ALL_G2'),
('Chris Woakes (END)', 3, '', 7000, 'UNSLOD', '', 0, 1, 1, 'ALL_G2'),
('Cameron Green (AUS)', 3, '', 9000, 'UNSLOD', '', 0, 1, 1, 'ALL_G2');

-- No tblTeamPlayer inserts because players are UNSLOD (unsold). If needed later, add entries here.

-- End of seed script

UPDATE tblPlayer SET photo = 'http://localhost:3000/Cricket_Bat.jpg'
WHERE skillId = 1;

UPDATE tblPlayer SET photo = 'http://localhost:3000/Tennis_Ball.jpg'
WHERE skillId = 2;

UPDATE tblPlayer SET photo = 'http://localhost:3000/All_Rounder.jpg'
WHERE skillId = 3;

UPDATE tblPlayer SET isConsiderInAuction = 0
WHERE skillId IN(1,2,3);