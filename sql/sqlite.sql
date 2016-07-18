-- --------------------------------------------------------

--
-- Table structure for table `parkours`
--

CREATE TABLE IF NOT EXISTS `%prefix%parkours` (
  `ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `name` varchar(20) NOT NULL,
  `owner` varchar(36) NOT NULL,
  `createdTimestamp` bigint(20) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `playerscore`
--

CREATE TABLE IF NOT EXISTS `%prefix%playerscore` (
  `ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `UUID` varchar(36) NOT NULL,
  `parkourId` int(11) NOT NULL,
  `date` bigint(20) NOT NULL,
  `timeTook` bigint(20) NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table `points`
--

CREATE TABLE IF NOT EXISTS `%prefix%points` (
  `ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `parkour_id` int(11) NOT NULL,
  `point_index` int(11) NOT NULL,
  `location` varchar(200) NOT NULL
);

--
-- Table structure for table `parkour_leaderboards`
--

CREATE TABLE IF NOT EXISTS `%prefix%parkour_leaderboards` (
  `ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `parkourId` int(11) NOT NULL,
  `location` varchar(150) NOT NULL,
  `players_count` int(11) NOT NULL,
  `page` int(11) NOT NULL
);
