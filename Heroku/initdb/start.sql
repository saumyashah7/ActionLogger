use DATABASE_NAME;

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

CREATE EVENT `DELETE_TOKENS` ON SCHEDULE EVERY 5 SECOND STARTS NOW() ON COMPLETION NOT PRESERVE ENABLE DO DELETE FROM tokens WHERE timestamp < NOW();
