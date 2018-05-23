SET SCHEMA `handycapper`;

CREATE TABLE `races` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `track` varchar(3) NOT NULL,
  `track_canonical` varchar(3) NOT NULL,
  `track_state` varchar(3) DEFAULT NULL,
  `track_country` varchar(3) NOT NULL,
  `track_name` varchar(80) NOT NULL,
  `number` tinyint(2) DEFAULT NULL,
  `breed` varchar(15) DEFAULT NULL,
  `type` varchar(75) DEFAULT NULL,
  `code` varchar(3) DEFAULT NULL,
  `race_name` varchar(100) DEFAULT NULL,
  `grade` tinyint(1) DEFAULT NULL,
  `black_type` varchar(45) DEFAULT NULL,
  `conditions` text,
  `min_claim` int(10) DEFAULT NULL,
  `max_claim` int(10) DEFAULT NULL,
  `restrictions` varchar(45) DEFAULT NULL,
  `min_age` smallint(6) DEFAULT NULL,
  `max_age` smallint(6) DEFAULT NULL,
  `age_code` varchar(15) DEFAULT NULL,
  `sexes` tinyint(1) DEFAULT NULL,
  `sexes_code` varchar(45) DEFAULT NULL,
  `female_only` tinyint(1) DEFAULT NULL,
  `state_bred` tinyint(1) DEFAULT NULL,
  `distance_text` varchar(100) DEFAULT NULL,
  `distance_compact` varchar(15) DEFAULT NULL,
  `feet` smallint(6) DEFAULT NULL,
  `furlongs` decimal(5,2) DEFAULT NULL,
  `exact` tinyint(1) DEFAULT NULL,
  `run_up` smallint(6) DEFAULT NULL,
  `temp_rail` smallint(6) DEFAULT NULL,
  `surface` varchar(45) DEFAULT NULL,
  `course` varchar(45) DEFAULT NULL,
  `track_condition` varchar(25) DEFAULT NULL,
  `scheduled_surface` varchar(45) DEFAULT NULL,
  `scheduled_course` varchar(45) DEFAULT NULL,
  `off_turf` tinyint(1) DEFAULT NULL,
  `format` varchar(5) DEFAULT NULL,
  `track_record_holder` varchar(45) DEFAULT NULL,
  `track_record_time` varchar(15) DEFAULT NULL,
  `track_record_millis` bigint(11) DEFAULT NULL,
  `track_record_date` date DEFAULT NULL,
  `purse` int(11) DEFAULT NULL,
  `purse_text` varchar(25) DEFAULT NULL,
  `available_money` varchar(100) DEFAULT NULL,
  `purse_enhancements` varchar(200) DEFAULT NULL,
  `value_of_race` varchar(500) DEFAULT NULL,
  `weather` varchar(25) DEFAULT NULL,
  `wind_speed` smallint(3) DEFAULT NULL,
  `wind_direction` varchar(15) DEFAULT NULL,
  `post_time` varchar(15) DEFAULT NULL,
  `start_comments` varchar(100) DEFAULT NULL,
  `timer` varchar(15) DEFAULT NULL,
  `dead_heat` tinyint(1) DEFAULT NULL,
  `number_of_runners` tinyint(2) DEFAULT NULL,
  `final_time` varchar(15) DEFAULT NULL,
  `final_millis` bigint(11) DEFAULT NULL,
  `total_wps_pool` int(20) DEFAULT NULL,
  `footnotes` text,
  PRIMARY KEY (`id`),
  CONSTRAINT `idx_RACES_date_track_number` UNIQUE (`date`,`track`,`number`),
);
CREATE INDEX `idx_RACES_track_canonical` ON `races` (`track_canonical`);
CREATE INDEX `idx_RACES_track_state` ON `races` (`track_state`);
CREATE INDEX `idx_RACES_track_country` ON `races` (`track_country`);
CREATE INDEX `idx_RACES_breed` ON `races` (`breed`);
CREATE INDEX `idx_RACES_type` ON `races` (`type`);
CREATE INDEX `idx_RACES_code` ON `races` (`code`);
CREATE INDEX `idx_RACES_grade` ON `races` (`grade`);
CREATE INDEX `idx_RACES_black_type` ON `races` (`black_type`);
CREATE INDEX `idx_RACES_restrictions` ON `races` (`restrictions`);
CREATE INDEX `idx_RACES_age_code` ON `races` (`age_code`);
CREATE INDEX `idx_RACES_sexes_code` ON `races` (`sexes_code`);
CREATE INDEX `idx_RACES_sexes` ON `races` (`sexes`);
CREATE INDEX `idx_RACES_female_only` ON `races` (`female_only`);
CREATE INDEX `idx_RACES_state_bred` ON `races` (`state_bred`);
CREATE INDEX `idx_RACES_distance_compact` ON `races` (`distance_compact`);
CREATE INDEX `idx_RACES_feet` ON `races` (`feet`);
CREATE INDEX `idx_RACES_furlongs` ON `races` (`furlongs`);
CREATE INDEX `idx_RACES_exact` ON `races` (`exact`);
CREATE INDEX `idx_RACES_surface` ON `races` (`surface`);
CREATE INDEX `idx_RACES_course` ON `races` (`course`);
CREATE INDEX `idx_RACES_track_condition` ON `races` (`track_condition`);
CREATE INDEX `idx_RACES_off_turf` ON `races` (`off_turf`);
CREATE INDEX `idx_RACES_format` ON `races` (`format`);
CREATE INDEX `idx_RACES_track_record_millis` ON `races` (`track_record_millis`);
CREATE INDEX `idx_RACES_purse` ON `races` (`purse`);
CREATE INDEX `idx_RACES_number_of_runners` ON `races` (`number_of_runners`);
CREATE INDEX `idx_RACES_total_wps_pool` ON `races` (`total_wps_pool`);
CREATE INDEX `idx_RACES_dead_heat` ON `races` (`dead_heat`);
CREATE INDEX `idx_RACES_post_time` ON `races` (`post_time`);
CREATE INDEX `idx_RACES_final_millis` ON `races` (`final_millis`);

CREATE TABLE `starters` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `race_id` int(11) NOT NULL,
  `last_raced_date` date DEFAULT NULL,
  `last_raced_days_since` smallint(5) DEFAULT NULL,
  `last_raced_track` varchar(3) DEFAULT NULL,
  `last_raced_track_canonical` varchar(3) DEFAULT NULL,
  `last_raced_track_state` varchar(3) DEFAULT NULL,
  `last_raced_track_country` varchar(3) DEFAULT NULL,
  `last_raced_track_name` varchar(80) DEFAULT NULL,
  `last_raced_number` tinyint(2) DEFAULT NULL,
  `last_raced_position` tinyint(2) DEFAULT NULL,
  `program` varchar(15) DEFAULT NULL,
  `entry` tinyint(1) DEFAULT NULL,
  `entry_program` varchar(15) DEFAULT NULL,
  `horse` varchar(45) NOT NULL,
  `jockey_first` varchar(100) DEFAULT NULL,
  `jockey_last` varchar(100) DEFAULT NULL,
  `trainer_first` varchar(100) DEFAULT NULL,
  `trainer_last` varchar(100) DEFAULT NULL,
  `owner` varchar(100) DEFAULT NULL,
  `weight` smallint(3) DEFAULT NULL,
  `jockey_allowance` smallint(2) DEFAULT NULL,
  `medication_equipment` varchar(15) DEFAULT NULL,
  `claim_price` int(10) DEFAULT NULL,
  `claimed` tinyint(1) DEFAULT NULL,
  `new_trainer_name` varchar(100) DEFAULT NULL,
  `new_owner_name` varchar(100) DEFAULT NULL,
  `pp` tinyint(2) DEFAULT NULL,
  `finish_position` tinyint(2) DEFAULT NULL,
  `official_position` tinyint(2) DEFAULT NULL,
  `position_dead_heat` tinyint(1) DEFAULT NULL,
  `wagering_position` tinyint(2) DEFAULT NULL,
  `winner` tinyint(1) DEFAULT NULL,
  `disqualified` tinyint(1) DEFAULT NULL,
  `odds` decimal(10,2) DEFAULT NULL,
  `choice` tinyint(2) DEFAULT NULL,
  `favorite` tinyint(1) DEFAULT NULL,
  `comments` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `race_starter` FOREIGN KEY (`race_id`) REFERENCES `races` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `race_id_fk_idx` ON `starters` (`race_id`);
CREATE INDEX `idx_STARTERS_last_raced_date` ON `starters` (`last_raced_date`);
CREATE INDEX `idx_STARTERS_last_raced_days_since` ON `starters` (`last_raced_days_since`);
CREATE INDEX `idx_STARTERS_last_raced_track` ON `starters` (`last_raced_track`);
CREATE INDEX `idx_STARTERS_last_raced_track_canonical` ON `starters` (`last_raced_track_canonical`);
CREATE INDEX `idx_STARTERS_last_raced_track_state` ON `starters` (`last_raced_track_state`);
CREATE INDEX `idx_STARTERS_last_raced_position` ON `starters` (`last_raced_position`);
CREATE INDEX `idx_STARTERS_entry` ON `starters` (`entry`);
CREATE INDEX `idx_STARTERS_horse` ON `starters` (`horse`);
CREATE INDEX `idx_STARTERS_jockey_first_jockey_last` ON `starters` (`jockey_last`, `jockey_first`);
CREATE INDEX `idx_STARTERS_trainer_first_trainer_last` ON `starters` (`trainer_last`, `trainer_first`);
CREATE INDEX `idx_STARTERS_weight` ON `starters` (`weight`);
CREATE INDEX `idx_STARTERS_claim_price` ON `starters` (`claim_price`);
CREATE INDEX `idx_STARTERS_claimed` ON `starters` (`claimed`);
CREATE INDEX `idx_STARTERS_pp` ON `starters` (`pp`);
CREATE INDEX `idx_STARTERS_finish_position` ON `starters` (`finish_position`);
CREATE INDEX `idx_STARTERS_official_position` ON `starters` (`official_position`);
CREATE INDEX `idx_STARTERS_position_dead_heat` ON `starters` (`position_dead_heat`);
CREATE INDEX `idx_STARTERS_wagering_position` ON `starters` (`wagering_position`);
CREATE INDEX `idx_STARTERS_odds` ON `starters` (`odds`);
CREATE INDEX `idx_STARTERS_choice` ON `starters` (`choice`);
CREATE INDEX `idx_STARTERS_favorite` ON `starters` (`favorite`);

CREATE TABLE `breeding` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `starter_id` int(11) NOT NULL,
  `horse` varchar(45) NOT NULL,
  `color` varchar(25) DEFAULT NULL,
  `sex` varchar(15) DEFAULT NULL,
  `sire` varchar(45) DEFAULT NULL,
  `dam` varchar(45) DEFAULT NULL,
  `dam_sire` varchar(45) DEFAULT NULL,
  `foaling_date` date DEFAULT NULL,
  `foaling_location` varchar(100) DEFAULT NULL,
  `breeder` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `starter_breeding` FOREIGN KEY (`starter_id`) REFERENCES `starters` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `starter_breeding_idx` ON `breeding`(`starter_id`);
CREATE INDEX `idx_BREEDING_horse` ON `breeding`(`horse`);
CREATE INDEX `idx_BREEDING_sire` ON `breeding`(`sire`);
CREATE INDEX `idx_BREEDING_dam` ON `breeding`(`dam`);
CREATE INDEX `idx_BREEDING_dam_sire` ON `breeding`(`dam_sire`);

CREATE TABLE `cancelled` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reason` varchar(45) DEFAULT NULL,
  `date` date NOT NULL,
  `track` varchar(3) NOT NULL,
  `track_canonical` varchar(3) NOT NULL,
  `track_state` varchar(3) DEFAULT NULL,
  `track_country` varchar(3) NOT NULL,
  `track_name` varchar(80) NOT NULL,
  `number` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `idx_CANCELLED_date_track_number` UNIQUE (`date`,`track`,`number`)
);

CREATE TABLE `equip` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `starter_id` int(11) NOT NULL,
  `code` char(1) DEFAULT NULL,
  `text` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `starter_equip` FOREIGN KEY (`starter_id`) REFERENCES `starters` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `starter_idx` ON `equip`(`starter_id`);
CREATE INDEX `idx_EQUIP_code` ON `equip`(`code`);

CREATE TABLE `exotics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `race_id` int(11) NOT NULL,
  `unit` decimal(5,2) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `winning_numbers` varchar(100) DEFAULT NULL,
  `payoff` decimal(12,2) DEFAULT NULL,
  `odds` decimal(10,2) DEFAULT NULL,
  `number_correct` varchar(100) DEFAULT NULL,
  `pool` decimal(12,2) DEFAULT NULL,
  `carryover` decimal(12,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `race_exotics` FOREIGN KEY (`race_id`) REFERENCES `races` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `race_exotics_idx` ON `exotics` (`race_id`);
CREATE INDEX `idx_EXOTICS_name` ON `exotics` (`name`);
CREATE INDEX `idx_EXOTICS_pool` ON `exotics` (`pool`);
CREATE INDEX `idx_EXOTICS_carryover` ON `exotics` (`carryover`);
CREATE INDEX `idx_EXOTICS_payoff` ON `exotics` (`payoff`);
CREATE INDEX `idx_EXOTICS_odds` ON `exotics` (`odds`);

CREATE TABLE `fractionals` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `race_id` int(11) NOT NULL,
  `point` tinyint(1) NOT NULL,
  `text` varchar(15) NOT NULL,
  `compact` varchar(15) NOT NULL,
  `feet` smallint(6) NOT NULL,
  `furlongs` decimal(5,2) NOT NULL,
  `time` varchar(15) DEFAULT NULL,
  `millis` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `race_fractionals` FOREIGN KEY (`race_id`) REFERENCES `races` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `race_fractionals_idx` ON `fractionals` (`race_id`);
CREATE INDEX `idx_FRACTIONALS_point` ON `fractionals` (`point`);
CREATE INDEX `idx_FRACTIONALS_compact` ON `fractionals` (`compact`);
CREATE INDEX `idx_FRACTIONALS_feet` ON `fractionals` (`feet`);
CREATE INDEX `idx_FRACTIONALS_furlongs` ON `fractionals` (`furlongs`);
CREATE INDEX `idx_FRACTIONALS_millis` ON `fractionals` (`millis`);

CREATE TABLE `indiv_fractionals` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `starter_id` int(11) NOT NULL,
  `point` tinyint(1) NOT NULL,
  `text` varchar(15) NOT NULL,
  `compact` varchar(15) NOT NULL,
  `feet` smallint(6) NOT NULL,
  `furlongs` decimal(5,2) NOT NULL,
  `time` varchar(15) DEFAULT NULL,
  `millis` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `starter_fractionals` FOREIGN KEY (`starter_id`) REFERENCES `starters` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `starter_fractionals_idx` ON `indiv_fractionals` (`starter_id`);
CREATE INDEX `idx_INDIV_FRACTIONALS_point` ON `indiv_fractionals` (`point`);
CREATE INDEX `idx_INDIV_FRACTIONALS_compact` ON `indiv_fractionals` (`compact`);
CREATE INDEX `idx_INDIV_FRACTIONALS_feet` ON `indiv_fractionals` (`feet`);
CREATE INDEX `idx_INDIV_FRACTIONALS_furlongs` ON `indiv_fractionals` (`furlongs`);
CREATE INDEX `idx_INDIV_FRACTIONALS_millis` ON `indiv_fractionals` (`millis`);

CREATE TABLE `indiv_ratings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `starter_id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `text` varchar(100) DEFAULT NULL,
  `value` double DEFAULT NULL,
  `extra` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `starter_rating` FOREIGN KEY (`starter_id`) REFERENCES `starters` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `starter_rating_idx` ON `indiv_ratings` (`starter_id`);

CREATE TABLE `indiv_splits` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `starter_id` int(11) NOT NULL,
  `point` tinyint(1) NOT NULL,
  `text` varchar(25) NOT NULL,
  `compact` varchar(35) NOT NULL,
  `feet` smallint(6) NOT NULL,
  `furlongs` decimal(5,2) NOT NULL,
  `time` varchar(15) DEFAULT NULL,
  `millis` bigint(11) DEFAULT NULL,
  `from_point` tinyint(1) DEFAULT NULL,
  `from_text` varchar(15) DEFAULT NULL,
  `from_compact` varchar(15) DEFAULT NULL,
  `from_feet` smallint(6) DEFAULT NULL,
  `from_furlongs` decimal(5,2) DEFAULT NULL,
  `from_time` varchar(15) DEFAULT NULL,
  `from_millis` bigint(11) DEFAULT NULL,
  `to_point` tinyint(1) DEFAULT NULL,
  `to_text` varchar(15) DEFAULT NULL,
  `to_compact` varchar(15) DEFAULT NULL,
  `to_feet` smallint(6) DEFAULT NULL,
  `to_furlongs` decimal(5,2) DEFAULT NULL,
  `to_time` varchar(15) DEFAULT NULL,
  `to_millis` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `starter_splits` FOREIGN KEY (`starter_id`) REFERENCES `starters` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `starter_splits_idx` ON `indiv_splits` (`starter_id`);
CREATE INDEX `idx_INDIV_SPLITS_point` ON `indiv_splits` (`point`);
CREATE INDEX `idx_INDIV_SPLITS_compact` ON `indiv_splits` (`compact`);
CREATE INDEX `idx_INDIV_SPLITS_feet` ON `indiv_splits` (`feet`);
CREATE INDEX `idx_INDIV_SPLITS_furlongs` ON `indiv_splits` (`furlongs`);
CREATE INDEX `idx_INDIV_SPLITS_millis` ON `indiv_splits` (`millis`);
CREATE INDEX `idx_INDIV_SPLITS_from_point` ON `indiv_splits` (`from_point`);
CREATE INDEX `idx_INDIV_SPLITS_from_compact` ON `indiv_splits` (`from_compact`);
CREATE INDEX `idx_INDIV_SPLITS_from_feet` ON `indiv_splits` (`from_feet`);
CREATE INDEX `idx_INDIV_SPLITS_from_furlongs` ON `indiv_splits` (`from_furlongs`);
CREATE INDEX `idx_INDIV_SPLITS_from_millis` ON `indiv_splits` (`from_millis`);
CREATE INDEX `idx_INDIV_SPLITS_to_point` ON `indiv_splits` (`to_point`);
CREATE INDEX `idx_INDIV_SPLITS_to_compact` ON `indiv_splits` (`to_compact`);
CREATE INDEX `idx_INDIV_SPLITS_to_feet` ON `indiv_splits` (`to_feet`);
CREATE INDEX `idx_INDIV_SPLITS_to_furlongs` ON `indiv_splits` (`to_furlongs`);
CREATE INDEX `idx_INDIV_SPLITS_to_millis` ON `indiv_splits` (`to_millis`);

CREATE TABLE `meds` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `starter_id` int(11) NOT NULL,
  `code` char(1) DEFAULT NULL,
  `text` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `starter_meds` FOREIGN KEY (`starter_id`) REFERENCES `starters` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `starter_fk_idx` ON `meds` (`starter_id`);
CREATE INDEX `idx_MEDS_code` ON `meds` (`code`);

CREATE TABLE `points_of_call` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `starter_id` int(11) NOT NULL,
  `point` tinyint(1) NOT NULL,
  `text` varchar(15) NOT NULL,
  `compact` varchar(15) NOT NULL,
  `feet` smallint(6) DEFAULT NULL,
  `furlongs` decimal(5,2) DEFAULT NULL,
  `position` tinyint(2) DEFAULT NULL,
  `len_ahead_text` varchar(15) DEFAULT NULL,
  `len_ahead` decimal(5,2) DEFAULT NULL,
  `tot_len_bhd_text` varchar(15) DEFAULT NULL,
  `tot_len_bhd` decimal(5,2) DEFAULT NULL,
  `wide` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `starter_points_of_call` FOREIGN KEY (`starter_id`) REFERENCES `starters` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `starter_points_of_call_idx` ON `points_of_call` (`starter_id`);
CREATE INDEX `idx_POINTS_OF_CALL_point` ON `points_of_call` (`point`);
CREATE INDEX `idx_POINTS_OF_CALL_compact` ON `points_of_call` (`compact`);
CREATE INDEX `idx_POINTS_OF_CALL_feet` ON `points_of_call` (`feet`);
CREATE INDEX `idx_POINTS_OF_CALL_furlongs` ON `points_of_call` (`furlongs`);
CREATE INDEX `idx_POINTS_OF_CALL_position` ON `points_of_call` (`position`);
CREATE INDEX `idx_POINTS_OF_CALL_len_ahead` ON `points_of_call` (`len_ahead`);
CREATE INDEX `idx_POINTS_OF_CALL_tot_len_bhd` ON `points_of_call` (`tot_len_bhd`);

CREATE TABLE `ratings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `race_id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `text` varchar(100) DEFAULT NULL,
  `value` double DEFAULT NULL,
  `extra` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `race_rating` FOREIGN KEY (`race_id`) REFERENCES `races` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `race_rating_idx` ON `ratings` (`race_id`);

CREATE TABLE `scratches` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `race_id` int(11) NOT NULL,
  `horse` varchar(45) NOT NULL,
  `reason` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `race_scratches` FOREIGN KEY (`race_id`) REFERENCES `races` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `race_scratches_idx` ON `scratches` (`race_id`);
CREATE INDEX `idx_SCRATCHES_horse` ON `scratches` (`horse`);

CREATE TABLE `splits` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `race_id` int(11) NOT NULL,
  `point` tinyint(1) NOT NULL,
  `text` varchar(25) NOT NULL,
  `compact` varchar(35) NOT NULL,
  `feet` smallint(6) NOT NULL,
  `furlongs` decimal(5,2) NOT NULL,
  `time` varchar(15) DEFAULT NULL,
  `millis` bigint(11) DEFAULT NULL,
  `from_point` tinyint(1) DEFAULT NULL,
  `from_text` varchar(15) DEFAULT NULL,
  `from_compact` varchar(15) DEFAULT NULL,
  `from_feet` smallint(6) DEFAULT NULL,
  `from_furlongs` decimal(5,2) DEFAULT NULL,
  `from_time` varchar(15) DEFAULT NULL,
  `from_millis` bigint(11) DEFAULT NULL,
  `to_point` tinyint(1) DEFAULT NULL,
  `to_text` varchar(15) DEFAULT NULL,
  `to_compact` varchar(15) DEFAULT NULL,
  `to_feet` smallint(6) DEFAULT NULL,
  `to_furlongs` decimal(5,2) DEFAULT NULL,
  `to_time` varchar(15) DEFAULT NULL,
  `to_millis` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `race_splits` FOREIGN KEY (`race_id`) REFERENCES `races` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `race_splits_idx` ON `splits` (`race_id`);
CREATE INDEX `idx_SPLITS_point` ON `splits` (`point`);
CREATE INDEX `idx_SPLITS_compact` ON `splits` (`compact`);
CREATE INDEX `idx_SPLITS_feet` ON `splits` (`feet`);
CREATE INDEX `idx_SPLITS_furlongs` ON `splits` (`furlongs`);
CREATE INDEX `idx_SPLITS_millis` ON `splits` (`millis`);
CREATE INDEX `idx_SPLITS_from_point` ON `splits` (`from_point`);
CREATE INDEX `idx_SPLITS_from_compact` ON `splits` (`from_compact`);
CREATE INDEX `idx_SPLITS_from_feet` ON `splits` (`from_feet`);
CREATE INDEX `idx_SPLITS_from_furlongs` ON `splits` (`from_furlongs`);
CREATE INDEX `idx_SPLITS_from_millis` ON `splits` (`from_millis`);
CREATE INDEX `idx_SPLITS_to_point` ON `splits` (`to_point`);
CREATE INDEX `idx_SPLITS_to_compact` ON `splits` (`to_compact`);
CREATE INDEX `idx_SPLITS_to_feet` ON `splits` (`to_feet`);
CREATE INDEX `idx_SPLITS_to_furlongs` ON `splits` (`to_furlongs`);
CREATE INDEX `idx_SPLITS_to_millis` ON `splits` (`to_millis`);

CREATE TABLE `wps` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `starter_id` int(11) NOT NULL,
  `type` varchar(10) DEFAULT NULL,
  `unit` decimal(5,2) DEFAULT NULL,
  `payoff` decimal(12,2) DEFAULT NULL,
  `odds` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `starter_wps` FOREIGN KEY (`starter_id`) REFERENCES `starters` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
CREATE INDEX `starter_wps_idx` ON `wps` (`starter_id`);
CREATE INDEX `idx_WPS_type` ON `wps` (`type`);
CREATE INDEX `idx_WPS_payoff` ON `wps` (`payoff`);
CREATE INDEX `idx_WPS_odds` ON `wps` (`odds`);