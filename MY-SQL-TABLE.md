-- Create the database if it doesn't already exist and select it
CREATE DATABASE IF NOT EXISTS epl_auction;
USE epl_auction;

-- -----------------------------------------------------
-- Table: tblPlayerSkill
-- -----------------------------------------------------
CREATE TABLE tblPlayerSkill (
id INT AUTO_INCREMENT NOT NULL,
skillName VARCHAR(100) NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: tblTournament
-- -----------------------------------------------------
CREATE TABLE tblTournament (
id INT AUTO_INCREMENT NOT NULL,
tournamentName VARCHAR(500) NOT NULL,
logo VARCHAR(1000) NOT NULL,
tournamentYear VARCHAR(100) NOT NULL,
startDate DATE NOT NULL,
endDate DATE NOT NULL,
isActive INT NOT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: tblPlayer
-- -----------------------------------------------------
CREATE TABLE tblPlayer (
id INT AUTO_INCREMENT NOT NULL,
playerName VARCHAR(255) NOT NULL,
skillId INT NOT NULL,
photo VARCHAR(3000) NULL,
basePrice INT NOT NULL,
playerStatus VARCHAR(20) NOT NULL,
playerStats VARCHAR(4000) NOT NULL,
isNewPlayer INT NOT NULL,
tournamentId INT NOT NULL,
isConsiderInAuction INT NOT NULL,
groupCode VARCHAR(10) NOT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: tblPlayerBasePrice
-- -----------------------------------------------------
CREATE TABLE tblPlayerBasePrice (
id INT AUTO_INCREMENT NOT NULL,
playerBasePrice INT NOT NULL,
tournamentId INT NOT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: tblTeam
-- -----------------------------------------------------
CREATE TABLE tblTeam (
id INT AUTO_INCREMENT NOT NULL,
teamName VARCHAR(500) NOT NULL,
logo VARCHAR(3000) NOT NULL,
purse INT NOT NULL,
remainingPurse INT NULL,
poc1 VARCHAR(500) NOT NULL,
poc2 VARCHAR(500) NOT NULL,
tournamentId INT NOT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: tblTeamPlayer
-- -----------------------------------------------------
CREATE TABLE tblTeamPlayer (
id INT AUTO_INCREMENT NOT NULL,
teamId INT NOT NULL,
playerId INT NOT NULL,
soldPrice INT NOT NULL,
soldAt DATETIME(6) NOT NULL,
tournamentId INT NOT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- -----------------------------------------------------
-- Foreign Key Constraints
-- -----------------------------------------------------

-- tblPlayer Foreign Keys
ALTER TABLE tblPlayer
ADD CONSTRAINT fk_player_skill FOREIGN KEY (skillId) REFERENCES tblPlayerSkill (id),
ADD CONSTRAINT fk_player_tournament FOREIGN KEY (tournamentId) REFERENCES tblTournament (id);

-- tblPlayerBasePrice Foreign Keys
ALTER TABLE tblPlayerBasePrice
ADD CONSTRAINT fk_playerbaseprice_tournament FOREIGN KEY (tournamentId) REFERENCES tblTournament (id);

-- tblTeam Foreign Keys
ALTER TABLE tblTeam
ADD CONSTRAINT fk_team_tournament FOREIGN KEY (tournamentId) REFERENCES tblTournament (id);

-- tblTeamPlayer Foreign Keys
ALTER TABLE tblTeamPlayer
ADD CONSTRAINT fk_teamplayer_player FOREIGN KEY (playerId) REFERENCES tblPlayer (id),
ADD CONSTRAINT fk_teamplayer_team FOREIGN KEY (teamId) REFERENCES tblTeam (id),
ADD CONSTRAINT fk_teamplayer_tournament FOREIGN KEY (tournamentId) REFERENCES tblTournament (id);