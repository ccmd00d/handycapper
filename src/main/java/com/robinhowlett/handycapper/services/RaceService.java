package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.Breed;
import com.robinhowlett.chartparser.charts.pdf.Cancellation;
import com.robinhowlett.chartparser.charts.pdf.DistanceSurfaceTrackRecord;
import com.robinhowlett.chartparser.charts.pdf.DistanceSurfaceTrackRecord.RaceDistance;
import com.robinhowlett.chartparser.charts.pdf.DistanceSurfaceTrackRecord.TrackRecord;
import com.robinhowlett.chartparser.charts.pdf.Horse;
import com.robinhowlett.chartparser.charts.pdf.PostTimeStartCommentsTimer;
import com.robinhowlett.chartparser.charts.pdf.Purse;
import com.robinhowlett.chartparser.charts.pdf.Purse.EnhancementType;
import com.robinhowlett.chartparser.charts.pdf.Purse.PurseEnhancement;
import com.robinhowlett.chartparser.charts.pdf.RaceConditions;
import com.robinhowlett.chartparser.charts.pdf.RaceConditions.ClaimingPriceRange;
import com.robinhowlett.chartparser.charts.pdf.RaceRestrictions;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.charts.pdf.RaceResult.Weather;
import com.robinhowlett.chartparser.charts.pdf.RaceTypeNameBlackTypeBreed;
import com.robinhowlett.chartparser.charts.pdf.Rating;
import com.robinhowlett.chartparser.charts.pdf.Scratch;
import com.robinhowlett.chartparser.charts.pdf.Starter;
import com.robinhowlett.chartparser.charts.pdf.WindSpeedDirection;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.ExoticPayoffPool;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Fractional;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Split;
import com.robinhowlett.chartparser.tracks.Track;
import com.robinhowlett.chartparser.tracks.TrackService;
import com.robinhowlett.handycapper.components.AgeSlider;
import com.robinhowlett.handycapper.domain.tables.records.RacesRecord;
import com.robinhowlett.handycapper.dtos.SearchParams;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.robinhowlett.handycapper.domain.tables.Races.RACES;
import static com.robinhowlett.handycapper.domain.tables.Starters.STARTERS;

@Service
@Transactional
public class RaceService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private TrackService trackService;

    @Autowired
    private CancelledRaceService cancelledRaceService;

    @Autowired
    private ScratchesService scratchesService;

    @Autowired
    private RaceFractionalsService raceFractionalsService;

    @Autowired
    private RaceSplitsService raceSplitsService;

    @Autowired
    private RaceRatingsService raceRatingsService;

    @Autowired
    private StarterService starterService;

    @Autowired
    private ExoticsService exoticsService;

    public List<RaceResult> getDescendingById() {
        List<RaceResult> raceResults = new ArrayList<>();

        Result<Record> raceRecords = dsl.select().
                from(RACES)
                .orderBy(RACES.ID.desc())
                .limit(200)
                .fetch();

        if (raceRecords != null) {
            raceResults = raceRecords.stream()
                    .map(this::getRaceResultEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return raceResults;
    }

    public List<RaceResult> findByTrackAndDate(String trackCode, LocalDate date) {
        List<RaceResult> raceResults = new ArrayList<>();

        Result<Record> raceRecords = dsl.select().
                from(RACES)
                .where(RACES.TRACK.eq(trackCode).and(RACES.DATE.eq(Date.valueOf(date))))
                .orderBy(RACES.NUMBER.asc())
                .fetch();

        if (raceRecords != null) {
            raceResults = raceRecords.stream()
                    .map(this::getRaceResultEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return raceResults;
    }

    public RaceResult findByTrackAndDateAndNumber(String trackCode, LocalDate date, Integer
            raceNumber) {
        Record raceRecord = dsl.select().
                from(RACES)
                .where(RACES.TRACK.eq(trackCode)
                        .and(RACES.DATE.eq(Date.valueOf(date)))
                        .and(RACES.NUMBER.eq(raceNumber)))
                .fetchOne();

        if (raceRecord != null) {
            RaceResult raceResult = getRaceResultEntity(raceRecord);
            return raceResult;
        }

        return null;
    }

    public List<RaceResult> findByStarterHorseName(String horseName) {
        List<RaceResult> raceResults = new ArrayList<>();

        Result<Record> raceRecords = dsl.select().
                from(RACES).join(STARTERS).on(RACES.ID.eq(STARTERS.RACE_ID))
                .where(STARTERS.HORSE.equalIgnoreCase(horseName))
                .orderBy(RACES.DATE.desc())
                .limit(200)
                .fetch();

        if (raceRecords != null) {
            raceResults = raceRecords.stream()
                    .map(this::getRaceResultEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return raceResults;
    }

    public List<RaceResult> find(SearchParams searchParams) {
        List<RaceResult> raceResults = new ArrayList<>();

        // from RACES...
        SelectJoinStep<Record> fromRacesJoinStep = dsl.select().from(RACES);

        Condition condition = null;

        // where ...
        if (searchParams != null) {
            // track
            if (searchParams.getTrack() != null) {
                Condition trackCondition = RACES.TRACK.equalIgnoreCase(
                        searchParams.getTrack().getCode());
                condition = (condition == null ? trackCondition : condition.and(trackCondition));
            }
            // race date
            if (searchParams.getDate() != null) {
                Condition dateCondition = RACES.DATE.eq(Date.valueOf(searchParams.getDate()));
                condition = (condition == null ? dateCondition : condition.and(dateCondition));
            }
            // distance range
            if (searchParams.getLowDistance() != 2.0) {
                Condition lowDistanceCondition = RACES.FURLONGS.ge(searchParams.getLowDistance());
                condition = (condition == null ? lowDistanceCondition :
                        condition.and(lowDistanceCondition));
            }
            if (searchParams.getHighDistance() != 12.0) {
                Condition highDistanceCondition = RACES.FURLONGS.le(searchParams.getHighDistance());
                condition = (condition == null ? highDistanceCondition :
                        condition.and(highDistanceCondition));
            }
            // sex
            if (searchParams.getSex() != null && !searchParams.getSex().equals("All")) {
                Condition femaleCondition = RACES.SEXES.mod(8).eq(0);
                condition = (condition == null ? femaleCondition : condition.and(femaleCondition));
            }
            // age range
            if (searchParams.getLowAge() != 1.0) {
                Condition lowAgeCondition = RACES.MIN_AGE.ge((int) searchParams.getLowAge());
                condition = (condition == null ? lowAgeCondition : condition.and(lowAgeCondition));
            }
            if (searchParams.getHighAge() != 5.0) {
                Condition highAgeCondition = RACES.MAX_AGE.le((int) searchParams.getHighAge())
                        .and(RACES.MAX_AGE.gt(AgeSlider.LOW_VALUE));
                condition = (condition == null ? highAgeCondition :
                        condition.and(highAgeCondition));

            }
            // number of runners range
            if (searchParams.getHighRunners() != 5.0) {
                Condition highRunnersCondition = RACES.NUMBER_OF_RUNNERS.le(
                        (int) searchParams.getHighRunners());
                condition = (condition == null ? highRunnersCondition :
                        condition.and(highRunnersCondition));
            }
            // surface
            if (searchParams.getSurface() != null && !searchParams.getSurface().equals("All")) {
                Condition surfaceCondition = RACES.SURFACE.eq(searchParams.getSurface());
                condition =
                        (condition == null ? surfaceCondition : condition.and(surfaceCondition));
            }
            // race types
            if (searchParams.getTypes() != null && !searchParams.getTypes().isEmpty()) {
                Condition typesCondition = RACES.TYPE.in(searchParams.getTypes());
                condition = (condition == null ? typesCondition : condition.and(typesCondition));
            }
        } else {
            condition = DSL.trueCondition();
        }

        // fetch
        Result<Record> raceRecords = fromRacesJoinStep
                .where(condition)
                .orderBy(RACES.DATE.desc(), RACES.TRACK.asc(), RACES.NUMBER.asc())
                .limit(200)
                .fetch();

        if (raceRecords != null) {
            raceResults = raceRecords.stream()
                    .map(this::getRaceResultEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return raceResults;
    }

    public RaceResult getRaceResultEntity(Record raceRecord) {
        Integer raceId = raceRecord.get(RACES.ID, Integer.class);

        // starters
        List<Starter> starters = new ArrayList<>();
        Result<Record> starterRecords =
                starterService.getStarterRecordsByRaceId(raceId);
        if (starterRecords != null) {
            for (Record starterRecord : starterRecords) {
                Starter starter = starterService.getStarterEntity(starterRecord);
                starters.add(starter);
            }
        }

        // scratches
        List<Scratch> scratches = new ArrayList<>();
        Result<Record> scratchesRecords = scratchesService.getScrachesRecordsByRaceId(raceId);
        if (scratchesRecords != null) {
            for (Record scratchRecord : scratchesRecords) {
                Scratch scratch = scratchesService.getScratchEntity(scratchRecord);
                scratches.add(scratch);
            }
        }

        // race fractionals
        List<Fractional> raceFractionals = new ArrayList<>();
        Result<Record> raceFractionalRecords =
                raceFractionalsService.getRaceFractionalRecordsByRaceId(raceId);
        if (raceFractionalRecords != null) {
            for (Record raceFractionalRecord : raceFractionalRecords) {
                Fractional raceFractional = raceFractionalsService.getRaceFractionalEntity
                        (raceFractionalRecord);
                raceFractionals.add(raceFractional);
            }
        }

        // race splits
        List<Split> raceSplits = new ArrayList<>();
        Result<Record> raceSplitsRecords = raceSplitsService.getRaceSplitRecordsByRaceId(raceId);
        if (raceSplitsRecords != null) {
            for (Record raceSplitsRecord : raceSplitsRecords) {
                Split raceSplit = raceSplitsService.getRaceSplitEntity(raceSplitsRecord);
                raceSplits.add(raceSplit);
            }
        }

        // race ratings
        List<Rating> raceRatings = new ArrayList<>();
        Result<Record> raceRatingsRecords = raceRatingsService.getRaceRatingRecordsByRaceId(raceId);
        if (raceRatingsRecords != null) {
            for (Record raceRatingRecord : raceRatingsRecords) {
                Rating raceRating = raceRatingsService.getRaceRatingEntity(raceRatingRecord);
                raceRatings.add(raceRating);
            }
        }

        // wagerPayoffPools (wps)
        List<WinPlaceShowPayoff> wpsPayoffs = new ArrayList<>();
        Integer totalWPSPool = raceRecord.getValue(RACES.TOTAL_WPS_POOL, Integer.class);
        for (Starter starter : starters) {
            WinPlaceShowPayoff wpsPayoff = starter.getWinPlaceShowPayoff();
            if (wpsPayoff != null) {
                wpsPayoffs.add(wpsPayoff);
            }
        }
        WinPlaceShowPayoffPool wpsPools = new WinPlaceShowPayoffPool(totalWPSPool, wpsPayoffs);

        // wagerPayoffPools (exotics)
        List<ExoticPayoffPool> exoticPayoffPools = new ArrayList<>();
        Result<Record> exoticPayoffPoolRecords =
                exoticsService.getExoticPayoffPoolRecordsByRaceId(raceId);
        if (exoticPayoffPoolRecords != null) {
            for (Record exoticPayoffPoolRecord : exoticPayoffPoolRecords) {
                ExoticPayoffPool exoticPayoffPool =
                        exoticsService.getExoticPayoffPoolEntity(exoticPayoffPoolRecord);
                exoticPayoffPools.add(exoticPayoffPool);
            }
        }
        WagerPayoffPools wagerPayoffPools = new WagerPayoffPools(wpsPools, exoticPayoffPools);

        // race date
        Date date = raceRecord.getValue(RACES.DATE, Date.class);

        // track
        String trackCode = raceRecord.getValue(RACES.TRACK, String.class);
        Track track = trackService.getTrack(trackCode).get();

        // race number
        Integer raceNumber = raceRecord.getValue(RACES.NUMBER, Integer.class);

        // race conditions text
        String conditions = raceRecord.getValue(RACES.CONDITIONS, String.class);

        // claiming price range
        ClaimingPriceRange claimingPriceRange = null;
        Integer minClaim = raceRecord.getValue(RACES.MIN_CLAIM, Integer.class);
        Integer maxClaim = raceRecord.getValue(RACES.MAX_CLAIM, Integer.class);
        if ((minClaim != null) && (maxClaim != null)) {
            claimingPriceRange = new ClaimingPriceRange(minClaim, maxClaim);
        }

        // race restrictions
        String code = raceRecord.getValue(RACES.RESTRICTIONS, String.class);
        Integer minAge = raceRecord.getValue(RACES.MIN_AGE, Integer.class);
        Integer maxAge = raceRecord.getValue(RACES.MAX_AGE, Integer.class);
        Integer sexes = raceRecord.getValue(RACES.SEXES, Integer.class);
        boolean stateBred = raceRecord.getValue(RACES.STATE_BRED, Boolean.class);
        RaceRestrictions raceRestrictions = new RaceRestrictions(code, minAge, maxAge, sexes,
                stateBred);

        // race type, name, grade, black type, and breed
        String type = raceRecord.getValue(RACES.TYPE, String.class);
        String name = raceRecord.getValue(RACES.RACE_NAME, String.class);
        Integer grade = raceRecord.getValue(RACES.GRADE, Integer.class);
        String blackType = raceRecord.getValue(RACES.BLACK_TYPE, String.class);
        String breedCode = raceRecord.getValue(RACES.BREED, String.class);
        Breed breed = null;
        try {
            breed = Breed.forCode(breedCode);
        } catch (Breed.NoMatchingBreedException e) {
            // shouldn't ever happen...
        }
        RaceTypeNameBlackTypeBreed raceTypeNameBlackTypeBreed =
                new RaceTypeNameBlackTypeBreed(type, name, grade, blackType, breed);

        // purse
        Integer purseValue = raceRecord.getValue(RACES.PURSE, Integer.class);
        String purseText = raceRecord.getValue(RACES.PURSE_TEXT, String.class);
        String availableMoney = raceRecord.getValue(RACES.AVAILABLE_MONEY, String.class);
        String valueOfRace = raceRecord.getValue(RACES.VALUE_OF_RACE, String.class);
        String enhancements = raceRecord.getValue(RACES.PURSE_ENHANCEMENTS, String.class);
        List<PurseEnhancement> purseEnhancements = buildPurseEnhancements(enhancements);
        Purse purse = new Purse(purseValue, purseText, availableMoney, valueOfRace,
                purseEnhancements);

        // race conditions
        RaceConditions raceConditions = new RaceConditions(conditions, claimingPriceRange,
                raceRestrictions, raceTypeNameBlackTypeBreed, purse);

        // distance, course, surface and track record
        String distanceText = raceRecord.getValue(RACES.DISTANCE_TEXT, String.class);
        String distanceCompact = raceRecord.getValue(RACES.DISTANCE_COMPACT, String.class);
        boolean exact = raceRecord.getValue(RACES.EXACT, Boolean.class);
        Integer feet = raceRecord.getValue(RACES.FEET, Integer.class);
        Integer runUp = raceRecord.getValue(RACES.RUN_UP, Integer.class);
        Integer tempRail = raceRecord.getValue(RACES.TEMP_RAIL, Integer.class);
        RaceDistance raceDistance =
                new RaceDistance(distanceText, distanceCompact, exact, feet, runUp, tempRail);

        String surface = raceRecord.getValue(RACES.SURFACE, String.class);
        String scheduledSurfce = raceRecord.getValue(RACES.SCHEDULED_SURFACE, String.class);
        String course = raceRecord.getValue(RACES.COURSE, String.class);
        String scheduledCourse = raceRecord.getValue(RACES.SCHEDULED_COURSE, String.class);
        String format = raceRecord.getValue(RACES.FORMAT, String.class);
        String trackConditon = raceRecord.getValue(RACES.TRACK_CONDITION, String.class);

        DistanceSurfaceTrackRecord distanceSurfaceTrackRecord = null;
        String holder = raceRecord.getValue(RACES.TRACK_RECORD_HOLDER, String.class);
        String trackRecordTime = raceRecord.getValue(RACES.TRACK_RECORD_TIME, String.class);
        Long trackRecordMillis = raceRecord.getValue(RACES.TRACK_RECORD_MILLIS, Long.class);
        Date trackRecordDate = raceRecord.getValue(RACES.TRACK_RECORD_DATE, Date.class);
        TrackRecord trackRecord = null;
        if (holder != null) {
            trackRecord = new TrackRecord(new Horse(holder), trackRecordTime,
                    trackRecordMillis, trackRecordDate.toLocalDate());
        }
        distanceSurfaceTrackRecord = new DistanceSurfaceTrackRecord(raceDistance, surface,
                course, scheduledSurfce, scheduledCourse, format, trackRecord, trackConditon);

        // weather
        String weatherText = raceRecord.getValue(RACES.WEATHER, String.class);
        Integer windSpeed = raceRecord.getValue(RACES.WIND_SPEED, Integer.class);
        String windDirection = raceRecord.getValue(RACES.WIND_DIRECTION, String.class);
        WindSpeedDirection windSpeedDirection = null;
        if ((windSpeed != null) && (windDirection != null)) {
            windSpeedDirection = new WindSpeedDirection(windSpeed, windDirection);
        }
        Weather weather = new Weather(weatherText, windSpeedDirection);

        // post time, start comments, and timer
        String postTime = raceRecord.getValue(RACES.POST_TIME, String.class);
        String startComments = raceRecord.getValue(RACES.START_COMMENTS, String.class);
        String timer = raceRecord.getValue(RACES.TIMER, String.class);
        PostTimeStartCommentsTimer postTimeStartCommentsTimer =
                new PostTimeStartCommentsTimer(postTime, startComments, timer);

        // dead heat
        boolean deadHeat = raceRecord.getValue(RACES.DEAD_HEAT, Boolean.class);

        // footnotes
        String footnotes = raceRecord.getValue(RACES.FOOTNOTES, String.class);

        return new RaceResult(Cancellation.notCancelled(), date.toLocalDate(), track, raceNumber,
                raceConditions, distanceSurfaceTrackRecord, weather, postTimeStartCommentsTimer,
                deadHeat, starters, scratches, raceFractionals, raceSplits,
                wagerPayoffPools, footnotes, raceRatings);
    }

    private List<PurseEnhancement> buildPurseEnhancements(String enhancements) {
        List<PurseEnhancement> purseEnhancements = new ArrayList<>();
        if (enhancements != null) {
            purseEnhancements = Arrays.stream(enhancements.split(", ")).map(s -> {
                String[] split = s.split(": ");
                EnhancementType enhancementType = EnhancementType.forChartValue(split[0]);
                return new PurseEnhancement(enhancementType, split[1]);
            }).collect(Collectors.toList());
        }
        return purseEnhancements;
    }

    public List<RacesRecord> createRaces(List<RaceResult> raceResults) {
        List<RacesRecord> raceRecords = raceResults.stream()
                .map(this::createRace)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return raceRecords;
    }

    public RacesRecord createRace(RaceResult raceResult) {
        if (raceResult == null) {
            // TODO
            throw new RuntimeException("null result");
        }

        InsertSetMoreStep<RacesRecord> moreStep;
        InsertSetStep<RacesRecord> set = dsl.insertInto(RACES);

        if (raceResult.getCancellation().isCancelled()) {
            cancelledRaceService.cancellation(raceResult);
            return null;
        }

        // race date
        moreStep = raceDate(raceResult, set);

        // track
        track(raceResult, moreStep);

        // race number
        moreStep.set(RACES.NUMBER, raceResult.getRaceNumber());

        // race conditions and purse
        conditions(raceResult, moreStep);

        // race distance, surface, track record
        distanceSurfaceTrackRecord(raceResult, moreStep);

        // weather
        weather(raceResult, moreStep);

        // post time, start comments, timer
        postTimeStartCommentsTimer(raceResult, moreStep);

        moreStep.set(RACES.DEAD_HEAT, raceResult.isDeadHeat())
                .set(RACES.NUMBER_OF_RUNNERS, raceResult.getNumberOfRunners());

        // winning performance details
        moreStep.set(RACES.FINAL_TIME, raceResult.getFinalTime())
                .set(RACES.FINAL_MILLIS, raceResult.getFinalMillis());

        // WPS total pool
        wpsTotalPool(raceResult, moreStep);

        // race footnotes
        moreStep.set(RACES.FOOTNOTES, raceResult.getFootnotes());

        // save race
        RacesRecord racesRecord = moreStep.returning().fetchOne();

        // save scratches
        scratches(raceResult, racesRecord);

        // save race fractionals
        fractionals(raceResult, racesRecord);

        // save race splits
        splits(raceResult, racesRecord);

        // save exotic payoffs and pools
        exotics(raceResult, racesRecord);

        // save race ratings
        ratings(raceResult, racesRecord);

        // save starters
        starters(raceResult, racesRecord);

        return racesRecord;
    }

    private void starters(RaceResult raceResult, RacesRecord racesRecord) {
        List<Starter> starters = raceResult.getStarters();
        if (starters != null) {
            for (Starter starter : starters) {
                starterService.createStarter(racesRecord, starter);
            }
        }
    }

    private void ratings(RaceResult raceResult, RacesRecord racesRecord) {
        List<Rating> ratings = raceResult.getRatings();
        if (ratings != null) {
            for (Rating rating : ratings) {
                raceRatingsService.createRating(racesRecord, rating);
            }
        }
    }

    private void splits(RaceResult raceResult, RacesRecord racesRecord) {
        List<Split> splits = raceResult.getSplits();
        if (splits != null) {
            for (Split split : splits) {
                raceSplitsService.createSplit(racesRecord, split);
            }
        }
    }

    private void fractionals(RaceResult raceResult, RacesRecord racesRecord) {
        List<Fractional> fractionals = raceResult.getFractionals();
        if (fractionals != null) {
            for (Fractional fractional : fractionals) {
                raceFractionalsService.createFractional(racesRecord, fractional);
            }
        }
    }

    private void scratches(RaceResult raceResult, RacesRecord racesRecord) {
        List<Scratch> scratches = raceResult.getScratches();
        if (scratches != null) {
            for (Scratch scratch : scratches) {
                scratchesService.createScratch(racesRecord, scratch);
            }
        }
    }

    private InsertSetMoreStep<RacesRecord> postTimeStartCommentsTimer(RaceResult raceResult,
            InsertSetMoreStep<RacesRecord>
                    moreStep) {
        PostTimeStartCommentsTimer postTimeStartCommentsTimer =
                raceResult.getPostTimeStartCommentsTimer();
        if (postTimeStartCommentsTimer != null) {
            moreStep.set(RACES.POST_TIME, postTimeStartCommentsTimer.getPostTime());
            moreStep.set(RACES.START_COMMENTS, postTimeStartCommentsTimer.getStartComments());
            moreStep.set(RACES.TIMER, postTimeStartCommentsTimer.getTimer());
        }
        return moreStep;
    }

    private InsertSetMoreStep<RacesRecord> track(RaceResult raceResult,
            InsertSetMoreStep<RacesRecord> moreStep) {
        Track track = raceResult.getTrack();
        if (track != null) {
            moreStep.set(RACES.TRACK, track.getCode())
                    .set(RACES.TRACK_CANONICAL, track.getCanonical())
                    .set(RACES.TRACK_COUNTRY, track.getCountry())
                    .set(RACES.TRACK_STATE, track.getState())
                    .set(RACES.TRACK_NAME, track.getName());
        }
        return moreStep;
    }

    private InsertSetMoreStep<RacesRecord> raceDate(RaceResult raceResult,
            InsertSetStep<RacesRecord> step) {
        LocalDate raceDate = raceResult.getRaceDate();
        if (raceDate == null) {
            // TODO
            throw new RuntimeException("bad");
        }

        return step.set(RACES.DATE, Date.valueOf(raceDate));
    }

    private InsertSetMoreStep<RacesRecord> conditions(RaceResult raceResult,
            InsertSetMoreStep<RacesRecord> moreStep) {
        RaceConditions conditions = raceResult.getRaceConditions();
        if (conditions != null) {
            RaceTypeNameBlackTypeBreed typeBreed = conditions.getRaceTypeNameBlackTypeBreed();
            if (typeBreed != null) {
                moreStep.set(RACES.BREED, typeBreed.getBreed().getCode());
                moreStep.set(RACES.TYPE, typeBreed.getType());
                moreStep.set(RACES.CODE, typeBreed.getCode());
                moreStep.set(RACES.RACE_NAME, typeBreed.getName());
                moreStep.set(RACES.GRADE, typeBreed.getGrade());
                moreStep.set(RACES.BLACK_TYPE, typeBreed.getBlackType());
            }

            ClaimingPriceRange claimingPriceRange = conditions.getClaimingPriceRange();
            if (claimingPriceRange != null) {
                moreStep.set(RACES.MIN_CLAIM, claimingPriceRange.getMin());
                moreStep.set(RACES.MAX_CLAIM, claimingPriceRange.getMax());
            }

            moreStep.set(RACES.CONDITIONS, conditions.getText());

            Purse purse = conditions.getPurse();
            if (purse != null) {
                moreStep.set(RACES.PURSE, purse.getValue());
                moreStep.set(RACES.PURSE_TEXT, purse.getText());
                moreStep.set(RACES.AVAILABLE_MONEY, purse.getAvailableMoney());
                moreStep.set(RACES.PURSE_ENHANCEMENTS, purse.getEnhancements());
                moreStep.set(RACES.VALUE_OF_RACE, purse.getValueOfRace());
            }

            RaceRestrictions restrictions = conditions.getRestrictions();
            if (restrictions != null) {
                moreStep.set(RACES.RESTRICTIONS, restrictions.getCode());
                moreStep.set(RACES.MIN_AGE, restrictions.getMinAge());
                moreStep.set(RACES.MAX_AGE, restrictions.getMaxAge());
                moreStep.set(RACES.AGE_CODE, restrictions.getAgeCode());
                moreStep.set(RACES.SEXES, restrictions.getSexes());
                moreStep.set(RACES.SEXES_CODE, restrictions.getSexesCode());
                moreStep.set(RACES.FEMALE_ONLY, restrictions.isFemaleOnly());
                moreStep.set(RACES.STATE_BRED, restrictions.isStateBred());
            }
        }
        return moreStep;
    }

    private InsertSetMoreStep<RacesRecord> distanceSurfaceTrackRecord(RaceResult raceResult,
            InsertSetMoreStep<RacesRecord> moreStep) {
        DistanceSurfaceTrackRecord distanceSurfaceTrackRecord =
                raceResult.getDistanceSurfaceTrackRecord();
        if (distanceSurfaceTrackRecord != null) {
            RaceDistance raceDistance = distanceSurfaceTrackRecord.getRaceDistance();
            if (raceDistance != null) {
                moreStep.set(RACES.DISTANCE_TEXT, raceDistance.getText());
                moreStep.set(RACES.DISTANCE_COMPACT, raceDistance.getCompact());
                moreStep.set(RACES.FEET, raceDistance.getFeet());
                moreStep.set(RACES.FURLONGS, raceDistance.getFurlongs());
                moreStep.set(RACES.EXACT, raceDistance.isExact());
                moreStep.set(RACES.RUN_UP, raceDistance.getRunUp());
                moreStep.set(RACES.TEMP_RAIL, raceDistance.getTempRail());
            }

            moreStep.set(RACES.SURFACE, distanceSurfaceTrackRecord.getSurface());
            moreStep.set(RACES.COURSE, distanceSurfaceTrackRecord.getCourse());
            moreStep.set(RACES.TRACK_CONDITION, distanceSurfaceTrackRecord.getTrackCondition());
            moreStep.set(RACES.OFF_TURF, distanceSurfaceTrackRecord.isOffTurf());
            moreStep.set(RACES.SCHEDULED_SURFACE, distanceSurfaceTrackRecord.getScheduledSurface());
            moreStep.set(RACES.SCHEDULED_COURSE, distanceSurfaceTrackRecord.getScheduledCourse());
            moreStep.set(RACES.FORMAT, distanceSurfaceTrackRecord.getFormat());

            TrackRecord trackRecord = distanceSurfaceTrackRecord.getTrackRecord();
            if (trackRecord != null) {
                Horse holder = trackRecord.getHolder();
                if (holder != null) {
                    moreStep.set(RACES.TRACK_RECORD_HOLDER, holder.getName());
                }
                if (trackRecord.getRaceDate() != null) {
                    moreStep.set(RACES.TRACK_RECORD_DATE, Date.valueOf(trackRecord.getRaceDate()));
                }
                moreStep.set(RACES.TRACK_RECORD_TIME, trackRecord.getTime());
                moreStep.set(RACES.TRACK_RECORD_MILLIS, trackRecord.getMillis());
            }
        }
        return moreStep;
    }

    private InsertSetMoreStep<RacesRecord> weather(RaceResult raceResult,
            InsertSetMoreStep<RacesRecord> moreStep) {
        Weather weather = raceResult.getWeather();
        if (weather != null) {
            moreStep.set(RACES.WEATHER, weather.getText());

            WindSpeedDirection windSpeedDirection = weather.getWindSpeedDirection();
            if (windSpeedDirection != null) {
                moreStep.set(RACES.WIND_SPEED, windSpeedDirection.getSpeed());
                moreStep.set(RACES.WIND_DIRECTION, windSpeedDirection.getDirection());
            }
        }
        return moreStep;
    }

    private InsertSetMoreStep<RacesRecord> wpsTotalPool(RaceResult raceResult,
            InsertSetMoreStep<RacesRecord> moreStep) {
        WagerPayoffPools wagerPayoffPools = raceResult.getWagerPayoffPools();
        if (wagerPayoffPools != null) {
            WinPlaceShowPayoffPool wpsPayoffPools = wagerPayoffPools.getWinPlaceShowPayoffPools();
            if (wpsPayoffPools != null) {
                moreStep.set(RACES.TOTAL_WPS_POOL, wpsPayoffPools.getTotalWinPlaceShowPool());
            }
        }
        return moreStep;
    }

    private void exotics(RaceResult raceResult, RacesRecord racesRecord) {
        WagerPayoffPools wagerPayoffPools = raceResult.getWagerPayoffPools();
        if (wagerPayoffPools != null) {
            // exotics
            List<ExoticPayoffPool> exoticPayoffPools = wagerPayoffPools.getExoticPayoffPools();
            if (exoticPayoffPools != null) {
                for (ExoticPayoffPool exoticPayoffPool : exoticPayoffPools) {
                    exoticsService.createExoticPayoffPool(racesRecord, exoticPayoffPool);
                }
            }
        }
    }

    public int deleteRace(String trackCode, LocalDate date, Integer raceNumber) {
        return dsl.deleteFrom(RACES)
                .where(RACES.TRACK.eq(trackCode)
                        .and(RACES.DATE.eq(Date.valueOf(date)))
                        .and(RACES.NUMBER.eq(raceNumber)))
                .execute();
    }
}
