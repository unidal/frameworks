CREATE TABLE `config` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID' ,
  `category` varchar(64) NOT NULL COMMENT 'Category',
  `name` varchar(64) NOT NULL COMMENT 'Name',
  `description` varchar(256) NOT NULL COMMENT 'Description',
  `status` int NOT NULL COMMENT 'Status, 1:active, 2:inactive',
  `details` blob NOT NULL COMMENT 'Details',
  `creation_date` datetime NOT NULL COMMENT 'Creation Date',
  `last_modified_date` datetime NOT NULL COMMENT 'Last Modified Date',
  PRIMARY KEY (`id`),
  UNIQUE KEY `category_name` (`category`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 KEY_BLOCK_SIZE=8 ROW_FORMAT=COMPRESSED COMMENT='Config';
