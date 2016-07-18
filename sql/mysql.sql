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
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;