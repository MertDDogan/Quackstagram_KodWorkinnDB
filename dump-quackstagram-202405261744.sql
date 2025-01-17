-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: quackstagram
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `picture_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `comment_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `idx_picture_id` (`picture_id`),
  KEY `idx_comment_user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,'Mystar_2','YourUsername','Hey Mate'),(2,'Mystar_1','Unknown','12'),(3,'Lorin_1','Unknown','Cute'),(4,'Lorin_1','Unknown','Whatsap Broo'),(5,'Zara_2','Unknown','my Comment'),(6,'Zara_1','mert','Hello Africa Yahu'),(7,'Mystar_2','mert','awesome'),(8,'Lorin_2','mert','Great Picture Lorin'),(9,'Xylo_3','Xylo','Ramadan Kareem'),(10,'cdog4_2','Lorin','Amazin Bromir'),(11,'Zara_2','Lorin','Amazing !!!'),(12,'Xylo_3','mert','Happy Ramadan'),(13,'Lorin_1','mert','Hey'),(14,'Zara_1','mert','Thumbs up'),(15,'Lorin_1','mert','heyo'),(16,'mert_4','mert','amazing !!!'),(17,'mert_1','Barrack','Amazin picture Mert 1!');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `content_popularity`
--

DROP TABLE IF EXISTS `content_popularity`;
/*!50001 DROP VIEW IF EXISTS `content_popularity`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `content_popularity` AS SELECT 
 1 AS `picture_name`,
 1 AS `likes_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `current_user`
--

DROP TABLE IF EXISTS `current_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `current_user` (
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `current_user`
--

LOCK TABLES `current_user` WRITE;
/*!40000 ALTER TABLE `current_user` DISABLE KEYS */;
INSERT INTO `current_user` VALUES ('kenan');
/*!40000 ALTER TABLE `current_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow`
--

DROP TABLE IF EXISTS `follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow` (
  `follower_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `followed_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  KEY `idx_follower_user` (`follower_user`),
  KEY `idx_followed_user` (`followed_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow`
--

LOCK TABLES `follow` WRITE;
/*!40000 ALTER TABLE `follow` DISABLE KEYS */;
INSERT INTO `follow` VALUES ('Xylo','Lorin'),('Zara','Lorin'),('Mystar','Lorin'),('Mystar','Zara'),('Lorin','Mystar'),('Lorin','Zara'),('mert','Lorin'),('mert','cdog1'),('mert','Xylo'),('cdog1','cdog2'),('cdog1','cdog3'),('cdog1','cdog4'),('cdog1','mert'),('Alp','Lorin'),('mert','Zara'),('Zara','mert'),('mert','Mystar'),('mert','mert'),('Barrack','mert'),('Donald','Lorin'),('Barrack','Barrack'),('mert','Barrack');
/*!40000 ALTER TABLE `follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `likes` (
  `like_id` int NOT NULL AUTO_INCREMENT,
  `liked_user` varchar(50) DEFAULT NULL,
  `liking_user` varchar(50) DEFAULT NULL,
  `liked_picture` varchar(50) DEFAULT NULL,
  `date_time` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`like_id`),
  KEY `idx_liked_picture` (`liked_picture`),
  KEY `idx_liking_user` (`liking_user`),
  KEY `idx_liked_user` (`liked_user`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `likes`
--

LOCK TABLES `likes` WRITE;
/*!40000 ALTER TABLE `likes` DISABLE KEYS */;
INSERT INTO `likes` VALUES (1,'Lorin','Zara','Lorin_1','17/12/2023 19:29'),(2,'Lorin','mert','Lorin_1','08/03/2024 10:18'),(3,'Lorin','mert','Lorin_1','08/03/2024 10:18'),(4,'cdog1','cdog2','cdog1_1','19/03/2024 21:18');
/*!40000 ALTER TABLE `likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `picture`
--

DROP TABLE IF EXISTS `picture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `picture` (
  `picture_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `image_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `caption` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `timestamp` varchar(50) DEFAULT NULL,
  `likes_count` int DEFAULT NULL,
  PRIMARY KEY (`picture_name`),
  KEY `idx_picture_user_name` (`user_name`),
  KEY `idx_picture_likes_count` (`likes_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `picture`
--

LOCK TABLES `picture` WRITE;
/*!40000 ALTER TABLE `picture` DISABLE KEYS */;
INSERT INTO `picture` VALUES ('Alp_1','Alp','img/uploaded/',' Enter a caption','2024-03-27 19:35:18',0),('Barrack_1','Barrack','img/uploaded/','Enter a caption','2024-05-26 09:21:09',0),('Barrack_2','Barrack','img/uploaded/','','2024-05-26 09:35:44',0),('Barrack_3','Barrack','img/uploaded/','New Image I love it','2024-05-26 09:37:39',0),('Donald_1','Donald','img/uploaded/','','2024-05-26 09:56:50',0),('kenan_1','kenan','img/uploaded/','Abi selam','2024-05-26 14:17:50',0),('Lorin_1','Lorin','img/uploaded/',' In the cookie jar my hand was not.','2023-12-17 19:07:43',7),('Lorin_2','Lorin','img/uploaded/',' Meditate I must.','2023-12-17 19:09:35',0),('mert_1','mert','img/uploaded/',' Enter a caption','2024-03-08 13:51:21',0),('mert_2','mert','img/uploaded/',' AI','2024-03-27 00:37:48',0),('mert_3','mert','img/uploaded/','Food factory','2024-05-26 08:47:00',0),('mert_4','mert','img/uploaded/','Enter a caption','2024-05-26 08:50:03',0),('mert_5','mert','img/uploaded/','HEYOOO','2024-05-26 14:07:39',0),('Mystar_1','Mystar','img/uploaded/',' Cookies gone? ','2023-12-17 19:26:50',0),('Mystar_2','Mystar','img/uploaded/',' In my soup a fly is.','2023-12-17 19:27:24',0),('Xylo_1','Xylo','img/uploaded/',' My tea strong as Force is.','2023-12-17 19:22:40',0),('Xylo_2','Xylo','img/uploaded/',' Jedi mind trick failed.','2023-12-17 19:23:14',0),('Xylo_3','Xylo','img/uploaded/',' Enter a caption','2024-03-18 19:44:39',0),('Zara_1','Zara','img/uploaded/',' Lost my map I have. Oops.','2023-12-17 19:24:31',0),('Zara_2','Zara','img/uploaded/',' Yoga with Yoda','2023-12-17 19:25:03',0);
/*!40000 ALTER TABLE `picture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `system_analytics`
--

DROP TABLE IF EXISTS `system_analytics`;
/*!50001 DROP VIEW IF EXISTS `system_analytics`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `system_analytics` AS SELECT 
 1 AS `user_name`,
 1 AS `comment_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `bio` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `posts_count` int DEFAULT NULL,
  `followers_count` int DEFAULT NULL,
  `following_count` int DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Lorin','Password','For copyright reasons  I am not Grogu',2,6,2),(2,'Xylo','Password','Fierce warrior not solo',3,1,1),(3,'Zara','Password','Humanoid robot much like the rest',2,3,2),(4,'Mystar','Password','Xylo and I are not the same!',2,2,2),(5,'mert','mert','mert',4,4,6),(6,'Ata','Ata','Aysu 19.01.2024',0,0,0),(7,'caglar','caglar','MyBio Caglar 123',0,0,0),(8,'cdog1','cdog1','Bio of cdog1',0,1,0),(9,'cdog2','cdog2','Bio of Cdog2',0,1,0),(10,'cdog3','cdog3','Bio og Cdog3',0,1,0),(11,'cdog4','cdog4','Bio of Cdog 4',0,1,0),(12,'Mero','Mero','Meor',0,0,0),(13,'Alp','Alp','Aydin',0,0,0),(14,'Ersan','Password','Ersam Kuneri 1988',0,0,0),(15,'Barrack','Obama','The president',0,0,0),(16,'Donald','Trump','No bio I have bro',0,0,0),(17,'kenan','yildiz','Bio',1,0,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `user_behavior`
--

DROP TABLE IF EXISTS `user_behavior`;
/*!50001 DROP VIEW IF EXISTS `user_behavior`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `user_behavior` AS SELECT 
 1 AS `user_name`,
 1 AS `post_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping routines for database 'quackstagram'
--

--
-- Final view structure for view `content_popularity`
--

/*!50001 DROP VIEW IF EXISTS `content_popularity`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `content_popularity` AS select `picture`.`picture_name` AS `picture_name`,`picture`.`likes_count` AS `likes_count` from `picture` order by `picture`.`likes_count` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `system_analytics`
--

/*!50001 DROP VIEW IF EXISTS `system_analytics`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `system_analytics` AS select `comment`.`user_name` AS `user_name`,count(`comment`.`comment_id`) AS `comment_count` from `comment` group by `comment`.`user_name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_behavior`
--

/*!50001 DROP VIEW IF EXISTS `user_behavior`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_behavior` AS select `picture`.`user_name` AS `user_name`,count(`picture`.`picture_name`) AS `post_count` from `picture` group by `picture`.`user_name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-26 17:44:37
