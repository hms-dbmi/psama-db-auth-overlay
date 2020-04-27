use auth;
INSERT INTO application VALUES (0x__APPLICATION_UUID__,'PIC-SURE multiple data access API',0x01,'PICSURE','','/picsureui');
alter table `privilege` ADD COLUMN (queryScope VARCHAR(8192));
alter table `privilege` ADD COLUMN (queryTemplate VARCHAR(8192));
INSERT INTO `privilege` VALUES 
(0x7044061AF65B425F86CE73A1BF7F4402,'PIC-SURE Auth super admin for managing roles/privileges/application/connections','SUPER_ADMIN',NULL,'[]',NULL),
(0xAD08212E096F414CBA8D1BAE09415DAB,'PIC-SURE Auth admin for managing users.','ADMIN',NULL,'[]',NULL);
INSERT INTO `privilege` VALUES (0xF65B425F867044061ACE73A1BF7F4402,'User who cann run any PIC-SURE Query','PIC_SURE_ANY_QUERY',0x__APPLICATION_UUID__,'[]',NULL);
INSERT INTO `role` VALUES (0x002DC366B0D8420F998F885D0ED797FD,'PIC-SURE Top Admin','PIC-SURE Auth Micro App Top admin including Admin and super Admin, can manage roles and privileges directly');
INSERT INTO `role` VALUES (0x8F885D0ED797FD002DC366B0D8420F99,'Admin','Normal admin users, can manage other users including assignment of roles and privileges');
INSERT INTO `role` VALUES (0x797FD002DC366B0D8420F998F885D0ED,'PIC-SURE User','Normal user, can run any query including data export.');
INSERT INTO `connection` VALUES (0x97FD002DC366B0D8420F998F885D0ED7, 'Google', 'google-oauth2','google-oauth2|','[{"label":"Email", "id":"email"}]');
INSERT INTO `connection` VALUES (0x97FB0D8420F998F885D0ED7D002DC366, 'BCH', 'ldap-connector','ad|ldap-connector|','[{"label":"BCH Email","id":"email"}]');
INSERT INTO `connection` VALUES (0x97FD0F998F88002DC366B0D8425D0ED7, 'CCHMC', 'cchmc','samlp|cchmc|','[{"label":"CCHMC Email", "id":"email"}]');
INSERT INTO `connection` VALUES (0x9766B0D8420F998F88FD002DC35D0ED7, 'CHOP', 'CHOP','samlp|CHOP|','[{"label":"CHOP Email", "id":"email"}]');
INSERT INTO `connection` VALUES (0x976B0D8420F998F88FD002DC365D0ED7, 'PITT', 'pitt-edu','samlp|','[{"label":"PITT Email", "id":"email"}]');
INSERT INTO `connection` VALUES (0x9B0D8420F7FD002DC366998F885D0ED7, 'WUSTL', 'wustl','samlp|wustl|','[{"label":"Washington University in St. Louis Email", "id":"email"}]');
INSERT INTO `role_privilege` VALUES 
(0x002DC366B0D8420F998F885D0ED797FD,0x7044061AF65B425F86CE73A1BF7F4402),
(0x002DC366B0D8420F998F885D0ED797FD,0xAD08212E096F414CBA8D1BAE09415DAB),
(0x8F885D0ED797FD002DC366B0D8420F99,0xAD08212E096F414CBA8D1BAE09415DAB),
(0x797FD002DC366B0D8420F998F885D0ED,0xF65B425F867044061ACE73A1BF7F4402);
INSERT INTO `userMetadataMapping` VALUES 
(unhex('17FD002DC366B0D8420F998F885D0ED7'), '$.email', unhex('97FD002DC366B0D8420F998F885D0ED7'), '$.email'),
(unhex('17FB0D8420F998F885D0ED7D002DC366'), '$.email', unhex('97FB0D8420F998F885D0ED7D002DC366'), '$.email'),
(unhex('17FD0F998F88002DC366B0D8425D0ED7'), '$.email', unhex('97FD0F998F88002DC366B0D8425D0ED7'), '$.email'),
(unhex('1766B0D8420F998F88FD002DC35D0ED7'), '$.email', unhex('9766B0D8420F998F88FD002DC35D0ED7'), '$.email'),
(unhex('176B0D8420F998F88FD002DC365D0ED7'), '$.email', unhex('976B0D8420F998F88FD002DC365D0ED7'), '$.email'),
(unhex('1B0D8420F7FD002DC366998F885D0ED7'), '$.email', unhex('9B0D8420F7FD002DC366998F885D0ED7'), '$.email');
