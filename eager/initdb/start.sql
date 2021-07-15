CREATE USER IF NOT EXISTS 'eageruser'@'localhost';
ALTER USER 'eageruser'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';

CREATE DATABASE IF NOT EXISTS eager;
GRANT ALL PRIVILEGES ON eager.* to 'eageruser'@'localhost';

use eager;

DROP TABLE IF EXISTS tokens;
CREATE TABLE `tokens` (
   `userid` int(11) DEFAULT NULL,
   `token` varchar(45) NOT NULL,
   `timestamp` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`token`)
 ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS usage_metric;
CREATE TABLE `usage_metric` (
   `userid` int(11) NOT NULL,
   `application` varchar(45) NOT NULL,
   `metric` varchar(45) NOT NULL DEFAULT 'usage',
   `usage_data` int(11) NOT NULL,
   PRIMARY KEY (`userid`,`application`,`metric`)
 ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS user;
CREATE TABLE `user` (
   `userid` int(11) NOT NULL AUTO_INCREMENT,
   `machineaddress` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`userid`)
 ) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS gaapps;
CREATE TABLE `gaapps` (
`application` varchar(50) NOT NULL,
`url` varchar(200) DEFAULT NULL,
`userid` int(11) DEFAULT NULL,
 PRIMARY KEY (`application`)
 ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE DEFINER=`root`@`localhost` EVENT `DELETE_TOKENS` ON SCHEDULE EVERY 5 SECOND STARTS NOW() ON COMPLETION NOT PRESERVE ENABLE DO DELETE FROM tokens WHERE timestamp < NOW();
