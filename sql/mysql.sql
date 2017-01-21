-- --------------------------------------------------------

--
-- Table structure for table `settings`
--

CREATE TABLE IF NOT EXISTS `%prefix%settings` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `setting_key` varchar(26) NOT NULL,
  `value` varchar(300) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `setting_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `parkours`
--

CREATE TABLE IF NOT EXISTS `%prefix%parkours` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `owner` varchar(36) NOT NULL,
  `createdTimestamp` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `playerscore`
--

CREATE TABLE IF NOT EXISTS `%prefix%playerscore` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UUID` varchar(36) NOT NULL,
  `parkourId` int(11) NOT NULL,
  `date` bigint(20) NOT NULL,
  `timeTook` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `points`
--

CREATE TABLE IF NOT EXISTS `%prefix%points` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `parkour_id` int(11) NOT NULL,
  `point_index` int(11) NOT NULL,
  `location` varchar(200) NOT NULL,
  `pointMode` varchar(15) NOT NULL,
  `distance` double NOT NULL
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `parkour_leaderboards`
--

CREATE TABLE IF NOT EXISTS `%prefix%parkour_leaderboards` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `parkourId` int(11) NOT NULL,
  `location` varchar(150) NOT NULL,
  `players_count` int(11) NOT NULL,
  `page` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `point_effects`
--

CREATE TABLE IF NOT EXISTS `%prefix%point_effects` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `pointId` int(11) NOT NULL,
  `type` varchar(30) NOT NULL,
  `duration` int(11) NOT NULL,
  `amplifier` smallint(6) NOT NULL,
  `particles` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
