/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50725
Source Host           : localhost:3306
Source Database       : netdisk

Target Server Type    : MYSQL
Target Server Version : 50725
File Encoding         : 65001

Date: 2019-12-28 17:53:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for transfer
-- ----------------------------
DROP TABLE IF EXISTS `transfer`;
CREATE TABLE `transfer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `filename` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `fileLength` int(11) DEFAULT NULL,
  `filepath` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `time` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `operating` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='这是传输记录表';

-- ----------------------------
-- Records of transfer
-- ----------------------------
INSERT INTO `transfer` VALUES ('27', '刘北山', '2017117153魏琳网络编程技术作业2.docx', '15322', 'C:\\Users\\Yee\\Downloads\\2017117153魏琳网络编程技术作业2.docx', '2019-11-12 11:47:42', '下载');
INSERT INTO `transfer` VALUES ('28', '刘北山', 'jetbrains-agent.jar', '1385083', 'C:\\Users\\Yee\\Downloads\\jetbrains-agent.jar', '2019-12-20 11:47:52', '下载');
INSERT INTO `transfer` VALUES ('29', '刘北山', '计网试卷复习资料.zip', '3565723', '', '2019-12-25 11:48:09', '上传');
INSERT INTO `transfer` VALUES ('30', '刘北山', '._Head2.gif', '172', 'C:\\Users\\Yee\\Desktop\\__MACOSX\\NetDisk案例\\resources\\imgs\\headimages\\._Head2.gif', '2019-12-27 12:12:57', '上传');
INSERT INTO `transfer` VALUES ('31', '刘北山', '12.jar', '983911', 'C:\\Users\\Yee\\Pictures\\12.jar', '2019-12-27 12:54:52', '上传');
INSERT INTO `transfer` VALUES ('32', '刘北山', 'btn_qz_open.png', '1368', 'C:\\Users\\Yee\\Desktop\\btn_qz_open.png', '2019-12-28 13:04:24', '下载');
INSERT INTO `transfer` VALUES ('33', '刘北山', '._resources', '172', 'C:\\Users\\Yee\\Desktop\\__MACOSX\\NetDisk案例\\._resources', '2019-12-28 13:06:38', '上传');
INSERT INTO `transfer` VALUES ('34', '刘北山', 'PacketTracer6.exe', '40438784', 'D:\\Cisco Packet Tracer 6.0\\bin\\PacketTracer6.exe', '2019-12-28 17:48:14', '上传');
INSERT INTO `transfer` VALUES ('35', '刘北山', 'pools.info', '12', 'D:\\eclipse\\p2\\pools.info', '2019-12-28 17:48:40', '上传');
INSERT INTO `transfer` VALUES ('36', '刘北山', 'profile-v9.32-yoo190131143813@2x.png', '34312', 'C:\\Users\\Yee\\Desktop\\profile-v9.32-yoo190131143813@2x.png', '2019-12-28 17:49:02', '上传');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `USERNAME` varchar(20) NOT NULL,
  `PASSWORD` varchar(20) NOT NULL,
  `SEX` char(1) DEFAULT NULL,
  `AGE` varchar(30) DEFAULT NULL,
  `TEL` varchar(12) DEFAULT NULL,
  `EMAIL` varchar(20) DEFAULT NULL,
  `images` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='存储网盘用户信息的表';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('123', '123', '女', '2019-12-22', '', '', 'head/head8.jpg');
INSERT INTO `user` VALUES ('刘北山', '123', '女', '2019-12-22', '1232343454', '12312323', 'head/head8.jpg');
INSERT INTO `user` VALUES ('千玺', '123', '男', '2019-12-10', '123124', '1243423', 'head/head4.jpg');
INSERT INTO `user` VALUES ('小北', '123', '男', '2000-12-28', '137873495', '12324345', 'head/head1.jpg');
INSERT INTO `user` VALUES ('小小北', '123', '男', '2019-12-09', '12324345', '143432435', 'head/head7.jpg');
INSERT INTO `user` VALUES ('小易', '123', '男', '2019-12-03', '123243', '14344', 'head/head9.jpg');
