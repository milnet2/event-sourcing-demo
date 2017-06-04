
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `event_id` UUID NOT NULL,
  `payload_id` INTEGER NOT NULL,
  `event_timestamp` TIMESTAMP,
  `event_type` VARCHAR(16),
  `given_name` VARCHAR(16),
  `surname` VARCHAR(16),
  `email` VARCHAR(16),
  PRIMARY KEY (`event_id`)
);

DROP TABLE IF EXISTS `user_snapshot`;
CREATE TABLE `user_snapshot` (
  `snapshot_id` UUID NOT NULL,
  `payload_id` INTEGER NOT NULL,
  `snapshot_timestamp` TIMESTAMP,

  `given_name` VARCHAR(16),
  `surname` VARCHAR(16),
  `email` VARCHAR(16),
  PRIMARY KEY (`snapshot_id`)
);
