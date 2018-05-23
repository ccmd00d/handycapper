CREATE TABLE handycapper.races
(
  id                  bigserial not null PRIMARY KEY,
  date                date not null,
  track               varchar(3) not null,
  track_canonical     varchar(3) not null,
  track_state         varchar(3),
  track_country       varchar(3) not null,
  track_name          varchar(80) not null,
  number              smallint,
  breed               varchar(15),
  type                varchar(75),
  code                varchar(3),
  race_name           varchar(100),
  grade               smallint,
  black_type          varchar(45),
  conditions          text,
  min_claim           bigint,
  max_claim           bigint,
  restrictions        varchar(45),
  min_age             smallint,
  max_age             smallint,
  age_code            varchar(15),
  sexes               smallint,
  sexes_code          varchar(45),
  female_only         boolean,
  state_bred          boolean,
  distance_text       varchar(100),
  distance_compact    varchar(15),
  feet                smallint,
  furlongs            decimal(5,2),
  exact               boolean,
  run_up              smallint,
  temp_rail           smallint,
  surface             varchar(45),
  course              varchar(45),
  track_condition     varchar(25),
  scheduled_surface   varchar(45),
  scheduled_course    varchar(45),
  off_turf            boolean,
  format              varchar(5),
  track_record_holder varchar(45),
  track_record_time   varchar(15),
  track_record_millis bigint,
  track_record_date   date,
  purse               bigint,
  purse_text          varchar(25),
  available_money     varchar(100),
  purse_enhancements  varchar(200),
  value_of_race       varchar(500),
  weather             varchar(25),
  wind_speed          smallint,
  wind_direction      varchar(15),
  post_time           varchar(15),
  start_comments      varchar(100),
  timer               varchar(15),
  dead_heat           boolean,
  number_of_runners   smallint,
  final_time          varchar(15),
  final_millis        bigint,
  total_wps_pool      bigint,
  footnotes           text
);
CREATE UNIQUE INDEX idx_24645_primary ON handycapper.races (id);
CREATE UNIQUE INDEX idx_24645_idx_races_date_track_number ON handycapper.races (date, track, number);
CREATE INDEX idx_24645_idx_races_surface ON handycapper.races (surface);
CREATE INDEX idx_24645_idx_races_type ON handycapper.races (type);
CREATE INDEX idx_24645_idx_races_female_only ON handycapper.races (female_only);
CREATE INDEX idx_24645_idx_races_exact ON handycapper.races (exact);
CREATE INDEX idx_24645_idx_races_sexes_code ON handycapper.races (sexes_code);
CREATE INDEX idx_24645_idx_races_code ON handycapper.races (code);
CREATE INDEX idx_24645_idx_races_total_wps_pool ON handycapper.races (total_wps_pool);
CREATE INDEX idx_24645_idx_races_breed ON handycapper.races (breed);
CREATE INDEX idx_24645_idx_races_dead_heat ON handycapper.races (dead_heat);
CREATE INDEX idx_24645_idx_races_post_time ON handycapper.races (post_time);
CREATE INDEX idx_24645_idx_races_track_condition ON handycapper.races (track_condition);
CREATE INDEX idx_24645_idx_races_purse ON handycapper.races (purse);
CREATE INDEX idx_24645_idx_races_number_of_runners ON handycapper.races (number_of_runners);
CREATE INDEX idx_24645_idx_races_course ON handycapper.races (course);
CREATE INDEX idx_24645_idx_races_final_millis ON handycapper.races (final_millis);
CREATE INDEX idx_24645_idx_races_format ON handycapper.races (format);
CREATE INDEX idx_24645_idx_races_restrictions ON handycapper.races (restrictions);
CREATE INDEX idx_24645_idx_races_age_code ON handycapper.races (age_code);
CREATE INDEX idx_24645_idx_races_sexes ON handycapper.races (sexes);
CREATE INDEX idx_24645_idx_races_distance_compact ON handycapper.races (distance_compact);
CREATE INDEX idx_24645_idx_races_track_canonical ON handycapper.races (track_canonical);
CREATE INDEX idx_24645_idx_races_track_state ON handycapper.races (track_state);
CREATE INDEX idx_24645_idx_races_off_turf ON handycapper.races (off_turf);
CREATE INDEX idx_24645_idx_races_feet ON handycapper.races (feet);
CREATE INDEX idx_24645_idx_races_grade ON handycapper.races (grade);
CREATE INDEX idx_24645_idx_races_black_type ON handycapper.races (black_type);
CREATE INDEX idx_24645_idx_races_state_bred ON handycapper.races (state_bred);
CREATE INDEX idx_24645_idx_races_furlongs ON handycapper.races (furlongs);
CREATE INDEX idx_24645_idx_races_track_record_millis ON handycapper.races (track_record_millis);
CREATE INDEX idx_24645_idx_races_track_country ON handycapper.races (track_country);

CREATE TABLE handycapper.starters
(
  id                         bigserial not null PRIMARY KEY,
  race_id                    bigint not null REFERENCES handycapper.races ON DELETE CASCADE,
  last_raced_date            date,
  last_raced_days_since      smallint,
  last_raced_track           varchar(3),
  last_raced_track_canonical varchar(3),
  last_raced_track_state     varchar(3),
  last_raced_track_country   varchar(3),
  last_raced_track_name      varchar(80),
  last_raced_number          smallint,
  last_raced_position        smallint,
  program                    varchar(15),
  entry                      boolean,
  entry_program              varchar(15),
  horse                      varchar(45) not null,
  jockey_first               varchar(100),
  jockey_last                varchar(100),
  trainer_first              varchar(100),
  trainer_last               varchar(100),
  owner                      varchar(100),
  weight                     smallint,
  jockey_allowance           smallint,
  medication_equipment       varchar(15),
  claim_price                bigint,
  claimed                    boolean,
  new_trainer_name           varchar(100),
  new_owner_name             varchar(100),
  pp                         smallint,
  finish_position            smallint,
  official_position          smallint,
  position_dead_heat         boolean,
  wagering_position          smallint,
  winner                     boolean,
  disqualified               boolean,
  odds                       decimal(10,2),
  choice                     smallint,
  favorite                   boolean,
  comments                   varchar(100)
);
CREATE UNIQUE INDEX idx_24672_primary ON handycapper.starters (id);
CREATE INDEX idx_24672_idx_starters_claimed ON handycapper.starters (claimed);
CREATE INDEX idx_24672_idx_starters_last_raced_days_since ON handycapper.starters (last_raced_days_since);
CREATE INDEX idx_24672_idx_starters_pp ON handycapper.starters (pp);
CREATE INDEX idx_24672_idx_starters_jockey_first_jockey_last ON handycapper.starters (jockey_last, jockey_first);
CREATE INDEX idx_24672_idx_starters_choice ON handycapper.starters (choice);
CREATE INDEX idx_24672_idx_starters_odds ON handycapper.starters (odds);
CREATE INDEX idx_24672_idx_starters_last_raced_track_canonical ON handycapper.starters (last_raced_track_canonical);
CREATE INDEX idx_24672_idx_starters_claim_price ON handycapper.starters (claim_price);
CREATE INDEX idx_24672_idx_starters_finish_position ON handycapper.starters (finish_position);
CREATE INDEX idx_24672_idx_starters_wagering_position ON handycapper.starters (wagering_position);
CREATE INDEX idx_24672_idx_starters_official_position ON handycapper.starters (official_position);
CREATE INDEX idx_24672_idx_starters_position_dead_heat ON handycapper.starters (position_dead_heat);
CREATE INDEX idx_24672_idx_starters_entry ON handycapper.starters (entry);
CREATE INDEX idx_24672_idx_starters_last_raced_position ON handycapper.starters (last_raced_position);
CREATE INDEX idx_24672_idx_starters_trainer_first_trainer_last ON handycapper.starters (trainer_last, trainer_first);
CREATE INDEX idx_24672_idx_starters_last_raced_track ON handycapper.starters (last_raced_track);
CREATE INDEX idx_24672_idx_starters_weight ON handycapper.starters (weight);
CREATE INDEX idx_24672_idx_starters_last_raced_date ON handycapper.starters (last_raced_date);
CREATE INDEX idx_24672_idx_starters_favorite ON handycapper.starters (favorite);
CREATE INDEX idx_24672_idx_starters_last_raced_track_state ON handycapper.starters (last_raced_track_state);
CREATE INDEX idx_24672_race_id_fk_idx ON handycapper.starters (race_id);
CREATE INDEX idx_24672_idx_starters_horse ON handycapper.starters (horse);

CREATE TABLE handycapper.breeding
(
  id               bigserial not null PRIMARY KEY,
  starter_id       bigint not null REFERENCES handycapper.starters ON DELETE CASCADE,
  horse            varchar(45) not null,
  color            varchar(25),
  sex              varchar(15),
  sire             varchar(45),
  dam              varchar(45),
  dam_sire         varchar(45),
  foaling_date     date,
  foaling_location varchar(100),
  breeder          varchar(100)
);
CREATE UNIQUE INDEX idx_24585_primary ON handycapper.breeding (id);
CREATE INDEX idx_24585_idx_breeding_dam ON handycapper.breeding (dam);
CREATE INDEX idx_24585_idx_breeding_horse ON handycapper.breeding (horse);
CREATE INDEX idx_24585_idx_breeding_dam_sire ON handycapper.breeding (dam_sire);
CREATE INDEX idx_24585_idx_breeding_sire ON handycapper.breeding (sire);
CREATE INDEX idx_24585_starter_breeding_idx ON handycapper.breeding (starter_id);

CREATE TABLE handycapper.cancelled
(
  id              bigserial not null PRIMARY KEY,
  reason          varchar(45),
  date            date not null,
  track           varchar(3) not null,
  track_canonical varchar(3) not null,
  track_state     varchar(3),
  track_country   varchar(3) not null,
  track_name      varchar(80) not null,
  number          smallint
);
CREATE UNIQUE INDEX idx_24591_primary ON handycapper.cancelled (id);
CREATE UNIQUE INDEX idx_24591_idx_cancelled_date_track_number ON handycapper.cancelled (date, track, number);

CREATE TABLE handycapper.equip
(
  id         bigserial not null PRIMARY KEY,
  starter_id bigint not null REFERENCES handycapper.starters ON DELETE CASCADE,
  code       char(1),
  text       varchar(25)
);
CREATE UNIQUE INDEX idx_24597_primary ON handycapper.equip (id);
CREATE INDEX idx_24597_starter_idx ON handycapper.equip (starter_id);
CREATE INDEX idx_24597_idx_equip_code ON handycapper.equip (code);

CREATE TABLE handycapper.exotics
(
  id              bigserial not null PRIMARY KEY,
  race_id         bigint not null REFERENCES handycapper.races ON DELETE CASCADE,
  unit            decimal(5,2),
  name            varchar(100),
  winning_numbers varchar(100),
  payoff          decimal(12,2),
  odds            decimal(10,2),
  number_correct  varchar(100),
  pool            decimal(12,2),
  carryover       decimal(12,2)
);
CREATE UNIQUE INDEX idx_24603_primary ON handycapper.exotics (id);
CREATE INDEX idx_24603_idx_exotics_payoff ON handycapper.exotics (payoff);
CREATE INDEX idx_24603_race_exotics_idx ON handycapper.exotics (race_id);
CREATE INDEX idx_24603_idx_exotics_odds ON handycapper.exotics (odds);
CREATE INDEX idx_24603_idx_exotics_name ON handycapper.exotics (name);
CREATE INDEX idx_24603_idx_exotics_carryover ON handycapper.exotics (carryover);
CREATE INDEX idx_24603_idx_exotics_pool ON handycapper.exotics (pool);

CREATE TABLE handycapper.fractionals
(
  id       bigserial not null PRIMARY KEY,
  race_id  bigint not null REFERENCES handycapper.races ON DELETE CASCADE,
  point    smallint not null,
  text     varchar(15) not null,
  compact  varchar(15) not null,
  feet     smallint not null,
  furlongs decimal(5,2) not null,
  time     varchar(15),
  millis   bigint
);
CREATE UNIQUE INDEX idx_24609_primary ON handycapper.fractionals (id);
CREATE INDEX idx_24609_idx_fractionals_feet ON handycapper.fractionals (feet);
CREATE INDEX idx_24609_idx_fractionals_millis ON handycapper.fractionals (millis);
CREATE INDEX idx_24609_idx_fractionals_compact ON handycapper.fractionals (compact);
CREATE INDEX idx_24609_idx_fractionals_furlongs ON handycapper.fractionals (furlongs);
CREATE INDEX idx_24609_idx_fractionals_point ON handycapper.fractionals (point);
CREATE INDEX idx_24609_race_fractionals_idx ON handycapper.fractionals (race_id);

CREATE TABLE handycapper.indiv_fractionals
(
  id         bigserial not null PRIMARY KEY,
  starter_id bigint not null REFERENCES handycapper.starters ON DELETE CASCADE,
  point      smallint not null,
  text       varchar(15) not null,
  compact    varchar(15) not null,
  feet       smallint not null,
  furlongs   decimal(5,2) not null,
  time       varchar(15),
  millis     bigint
);
CREATE UNIQUE INDEX idx_24615_primary ON handycapper.indiv_fractionals (id);
CREATE INDEX idx_24615_idx_indiv_fractionals_furlongs ON handycapper.indiv_fractionals (furlongs);
CREATE INDEX idx_24615_idx_indiv_fractionals_millis ON handycapper.indiv_fractionals (millis);
CREATE INDEX idx_24615_idx_indiv_fractionals_feet ON handycapper.indiv_fractionals (feet);
CREATE INDEX idx_24615_idx_indiv_fractionals_compact ON handycapper.indiv_fractionals (compact);
CREATE INDEX idx_24615_idx_indiv_fractionals_point ON handycapper.indiv_fractionals (point);
CREATE INDEX idx_24615_starter_fractionals_idx ON handycapper.indiv_fractionals (starter_id);

CREATE TABLE handycapper.indiv_ratings
(
  id         bigserial not null PRIMARY KEY,
  starter_id bigint not null REFERENCES handycapper.starters ON DELETE CASCADE,
  name       varchar(100),
  text       varchar(100),
  value      double precision,
  extra      varchar(200)
);
CREATE UNIQUE INDEX idx_24621_primary ON handycapper.indiv_ratings (id);
CREATE INDEX idx_24621_starter_rating_idx ON handycapper.indiv_ratings (starter_id);

CREATE TABLE handycapper.indiv_splits
(
  id            bigserial not null PRIMARY KEY,
  starter_id    bigint not null REFERENCES handycapper.starters ON DELETE CASCADE,
  point         smallint not null,
  text          varchar(25) not null,
  compact       varchar(35) not null,
  feet          smallint not null,
  furlongs      decimal(5,2) not null,
  time          varchar(15),
  millis        bigint,
  from_point    smallint,
  from_text     varchar(15),
  from_compact  varchar(15),
  from_feet     smallint,
  from_furlongs decimal(5,2),
  from_time     varchar(15),
  from_millis   bigint,
  to_point      smallint,
  to_text       varchar(15),
  to_compact    varchar(15),
  to_feet       smallint,
  to_furlongs   decimal(5,2),
  to_time       varchar(15),
  to_millis     bigint
);
CREATE UNIQUE INDEX idx_24627_primary ON handycapper.indiv_splits (id);
CREATE INDEX idx_24627_idx_indiv_splits_to_furlongs ON handycapper.indiv_splits (to_furlongs);
CREATE INDEX idx_24627_idx_indiv_splits_millis ON handycapper.indiv_splits (millis);
CREATE INDEX idx_24627_starter_splits_idx ON handycapper.indiv_splits (starter_id);
CREATE INDEX idx_24627_idx_indiv_splits_from_compact ON handycapper.indiv_splits (from_compact);
CREATE INDEX idx_24627_idx_indiv_splits_to_millis ON handycapper.indiv_splits (to_millis);
CREATE INDEX idx_24627_idx_indiv_splits_to_point ON handycapper.indiv_splits (to_point);
CREATE INDEX idx_24627_idx_indiv_splits_to_feet ON handycapper.indiv_splits (to_feet);
CREATE INDEX idx_24627_idx_indiv_splits_feet ON handycapper.indiv_splits (feet);
CREATE INDEX idx_24627_idx_indiv_splits_point ON handycapper.indiv_splits (point);
CREATE INDEX idx_24627_idx_indiv_splits_from_millis ON handycapper.indiv_splits (from_millis);
CREATE INDEX idx_24627_idx_indiv_splits_from_furlongs ON handycapper.indiv_splits (from_furlongs);
CREATE INDEX idx_24627_idx_indiv_splits_from_point ON handycapper.indiv_splits (from_point);
CREATE INDEX idx_24627_idx_indiv_splits_furlongs ON handycapper.indiv_splits (furlongs);
CREATE INDEX idx_24627_idx_indiv_splits_to_compact ON handycapper.indiv_splits (to_compact);
CREATE INDEX idx_24627_idx_indiv_splits_from_feet ON handycapper.indiv_splits (from_feet);
CREATE INDEX idx_24627_idx_indiv_splits_compact ON handycapper.indiv_splits (compact);

CREATE TABLE handycapper.meds
(
  id         bigserial not null PRIMARY KEY,
  starter_id bigint not null REFERENCES handycapper.starters ON DELETE CASCADE,
  code       char(1),
  text       varchar(25)
);
CREATE UNIQUE INDEX idx_24633_primary ON handycapper.meds (id);
CREATE INDEX idx_24633_idx_meds_code ON handycapper.meds (code);
CREATE INDEX idx_24633_starter_fk_idx ON handycapper.meds (starter_id);

CREATE TABLE handycapper.points_of_call
(
  id               bigserial not null PRIMARY KEY,
  starter_id       bigint not null REFERENCES handycapper.starters ON DELETE CASCADE,
  point            smallint not null,
  text             varchar(15) not null,
  compact          varchar(15) not null,
  feet             smallint,
  furlongs         decimal(5,2),
  position         smallint,
  len_ahead_text   varchar(15),
  len_ahead        decimal(5,2),
  tot_len_bhd_text varchar(15),
  tot_len_bhd      decimal(5,2),
  wide         smallint
);
CREATE UNIQUE INDEX idx_24639_primary ON handycapper.points_of_call (id);
CREATE INDEX idx_24639_idx_points_of_call_tot_len_bhd ON handycapper.points_of_call (tot_len_bhd);
CREATE INDEX idx_24639_idx_points_of_call_position ON handycapper.points_of_call (position);
CREATE INDEX idx_24639_idx_points_of_call_point ON handycapper.points_of_call (point);
CREATE INDEX idx_24639_idx_points_of_call_feet ON handycapper.points_of_call (feet);
CREATE INDEX idx_24639_idx_points_of_call_furlongs ON handycapper.points_of_call (furlongs);
CREATE INDEX idx_24639_idx_points_of_call_compact ON handycapper.points_of_call (compact);
CREATE INDEX idx_24639_idx_points_of_call_len_ahead ON handycapper.points_of_call (len_ahead);
CREATE INDEX idx_24639_starter_points_of_call_idx ON handycapper.points_of_call (starter_id);

CREATE TABLE handycapper.ratings
(
  id      bigserial not null PRIMARY KEY,
  race_id bigint not null REFERENCES handycapper.races ON DELETE CASCADE,
  name    varchar(100),
  text    varchar(100),
  value   double precision,
  extra   varchar(200)
);
CREATE UNIQUE INDEX idx_24654_primary ON handycapper.ratings (id);
CREATE INDEX idx_24654_race_rating_idx ON handycapper.ratings (race_id);

CREATE TABLE handycapper.scratches
(
  id      bigserial not null PRIMARY KEY,
  race_id bigint not null REFERENCES handycapper.races ON DELETE CASCADE,
  horse   varchar(45) not null,
  reason  varchar(45)
);
CREATE UNIQUE INDEX idx_24660_primary ON handycapper.scratches (id);
CREATE INDEX idx_24660_race_scratches_idx ON handycapper.scratches (race_id);
CREATE INDEX idx_24660_idx_scratches_horse ON handycapper.scratches (horse);

CREATE TABLE handycapper.splits
(
  id            bigserial not null PRIMARY KEY,
  race_id       bigint not null REFERENCES handycapper.races ON DELETE CASCADE,
  point         smallint not null,
  text          varchar(25) not null,
  compact       varchar(35) not null,
  feet          smallint not null,
  furlongs      decimal(5,2) not null,
  time          varchar(15),
  millis        bigint,
  from_point    smallint,
  from_text     varchar(15),
  from_compact  varchar(15),
  from_feet     smallint,
  from_furlongs decimal(5,2),
  from_time     varchar(15),
  from_millis   bigint,
  to_point      smallint,
  to_text       varchar(15),
  to_compact    varchar(15),
  to_feet       smallint,
  to_furlongs   decimal(5,2),
  to_time       varchar(15),
  to_millis     bigint
);
CREATE UNIQUE INDEX idx_24666_primary ON handycapper.splits (id);
CREATE INDEX idx_24666_idx_splits_to_point ON handycapper.splits (to_point);
CREATE INDEX idx_24666_idx_splits_millis ON handycapper.splits (millis);
CREATE INDEX idx_24666_idx_splits_to_furlongs ON handycapper.splits (to_furlongs);
CREATE INDEX idx_24666_idx_splits_to_millis ON handycapper.splits (to_millis);
CREATE INDEX idx_24666_idx_splits_from_point ON handycapper.splits (from_point);
CREATE INDEX idx_24666_idx_splits_from_millis ON handycapper.splits (from_millis);
CREATE INDEX idx_24666_idx_splits_to_compact ON handycapper.splits (to_compact);
CREATE INDEX idx_24666_idx_splits_compact ON handycapper.splits (compact);
CREATE INDEX idx_24666_idx_splits_to_feet ON handycapper.splits (to_feet);
CREATE INDEX idx_24666_idx_splits_from_furlongs ON handycapper.splits (from_furlongs);
CREATE INDEX idx_24666_idx_splits_furlongs ON handycapper.splits (furlongs);
CREATE INDEX idx_24666_idx_splits_point ON handycapper.splits (point);
CREATE INDEX idx_24666_race_splits_idx ON handycapper.splits (race_id);
CREATE INDEX idx_24666_idx_splits_from_feet ON handycapper.splits (from_feet);
CREATE INDEX idx_24666_idx_splits_from_compact ON handycapper.splits (from_compact);
CREATE INDEX idx_24666_idx_splits_feet ON handycapper.splits (feet);

CREATE TABLE handycapper.wps
(
  id         bigserial not null PRIMARY KEY,
  starter_id bigint not null REFERENCES handycapper.starters ON DELETE CASCADE,
  type       varchar(10),
  unit       decimal(5,2),
  payoff     decimal(12,2),
  odds       decimal(10,2)
);
CREATE UNIQUE INDEX idx_24681_primary ON handycapper.wps (id);
CREATE INDEX idx_24681_idx_wps_type ON handycapper.wps (type);
CREATE INDEX idx_24681_starter_wps_idx ON handycapper.wps (starter_id);
CREATE INDEX idx_24681_idx_wps_odds ON handycapper.wps (odds);
CREATE INDEX idx_24681_idx_wps_payoff ON handycapper.wps (payoff);