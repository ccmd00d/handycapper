package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.Breeder;
import com.robinhowlett.chartparser.charts.pdf.ClaimedHorse;
import com.robinhowlett.chartparser.charts.pdf.ClaimingPrice;
import com.robinhowlett.chartparser.charts.pdf.Horse;
import com.robinhowlett.chartparser.charts.pdf.Jockey;
import com.robinhowlett.chartparser.charts.pdf.Owner;
import com.robinhowlett.chartparser.charts.pdf.Rating;
import com.robinhowlett.chartparser.charts.pdf.Starter;
import com.robinhowlett.chartparser.charts.pdf.Starter.Claim;
import com.robinhowlett.chartparser.charts.pdf.Trainer;
import com.robinhowlett.chartparser.charts.pdf.running_line.LastRaced;
import com.robinhowlett.chartparser.charts.pdf.running_line.LastRaced.LastRacePerformance;
import com.robinhowlett.chartparser.charts.pdf.running_line.MedicationEquipment;
import com.robinhowlett.chartparser.charts.pdf.running_line.MedicationEquipment.Equipment;
import com.robinhowlett.chartparser.charts.pdf.running_line.MedicationEquipment.Medication;
import com.robinhowlett.chartparser.charts.pdf.running_line.Weight;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff.Place;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff.Show;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff.Win;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff.WinPlaceShow;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall;
import com.robinhowlett.chartparser.tracks.Track;
import com.robinhowlett.chartparser.tracks.TrackService;
import com.robinhowlett.handycapper.domain.tables.records.RacesRecord;
import com.robinhowlett.handycapper.domain.tables.records.StartersRecord;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.robinhowlett.chartparser.fractionals.FractionalPoint.Fractional;
import static com.robinhowlett.chartparser.fractionals.FractionalPoint.Split;
import static com.robinhowlett.handycapper.domain.tables.Breeding.BREEDING;
import static com.robinhowlett.handycapper.domain.tables.Starters.STARTERS;

@Service
@Transactional
public class StarterService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private TrackService trackService;

    @Autowired
    private MedsService medsService;

    @Autowired
    private EquipService equipService;

    @Autowired
    private PointsOfCallService pointsOfCallService;

    @Autowired
    private IndividualFractionalsService indivFractionalsService;

    @Autowired
    private IndividualSplitsService indivSplitsService;

    @Autowired
    private IndividualRatingsService indivRatingsService;

    @Autowired
    private BreedingService breedingService;

    @Autowired
    private WinPlaceShowPayoffsService wpsPayoffsService;

    public Result<Record> getStarterRecordsByRaceId(int raceId) {
        return dsl.select().
                from(STARTERS)
                .where(STARTERS.RACE_ID.eq(raceId))
                .fetch();
    }

    public Starter getStarterEntity(Record starterRecord) {
        Integer starterId = starterRecord.getValue(STARTERS.ID, Integer.class);

        // pointsOfCall
        List<PointOfCall> pointsOfCall = new ArrayList<>();
        Result<Record> pocRecords = pointsOfCallService.getPointOfCallRecordsByStarterId(starterId);
        if (pocRecords != null) {
            for (Record pocRecord : pocRecords) {
                PointOfCall pointOfCall = pointsOfCallService.getPointOfCallEntity(pocRecord);
                pointsOfCall.add(pointOfCall);
            }
        }

        // indiv fractionals
        List<Fractional> indivFractionals = new ArrayList<>();
        Result<Record> indivFractionalRecords =
                indivFractionalsService.getIndivFractionalRecordsByStarterId(starterId);
        if (indivFractionalRecords != null) {
            for (Record indivFractionalRecord : indivFractionalRecords) {
                Fractional indivFractional =
                        indivFractionalsService.getIndivFractionalEntity(indivFractionalRecord);
                indivFractionals.add(indivFractional);
            }
        }

        // indiv splits
        List<Split> indivSplits = new ArrayList<>();
        Result<Record> indivSplitsRecords =
                indivSplitsService.getIndivSplitRecordsByStarterId(starterId);
        if (indivSplitsRecords != null) {
            for (Record indivSplitsRecord : indivSplitsRecords) {
                Split indivSplit = indivSplitsService.getIndivSplitEntity(indivSplitsRecord);
                indivSplits.add(indivSplit);
            }
        }

        // indiv ratings
        List<Rating> indivRatings = new ArrayList<>();
        Result<Record> indivRatingsRecords =
                indivRatingsService.getIndivRatingRecordsByStarterId(starterId);
        if (indivRatingsRecords != null) {
            for (Record indivRatingRecord : indivRatingsRecords) {
                Rating indivRating = indivRatingsService.getIndivRatingEntity(indivRatingRecord);
                indivRatings.add(indivRating);
            }
        }

        // last raced
        LastRaced lastRaced = null;
        Date lastRacedDate = starterRecord.getValue(STARTERS.LAST_RACED_DATE, Date.class);
        if (lastRacedDate != null) {
            Integer lastRacedDaysSince =
                    starterRecord.getValue(STARTERS.LAST_RACED_DAYS_SINCE, Integer.class);
            Integer lastRacedNumber =
                    starterRecord.getValue(STARTERS.LAST_RACED_NUMBER, Integer.class);
            String lastRacedTrack = starterRecord.getValue(STARTERS.LAST_RACED_TRACK, String.class);
            Track track = trackService.getTrack(lastRacedTrack).get();
            Integer lastRacedPosition = starterRecord.getValue(STARTERS.LAST_RACED_POSITION,
                    Integer.class);
            LastRacePerformance lastRacePerformance = new LastRacePerformance(lastRacedNumber,
                    track, lastRacedPosition);
            lastRaced = new LastRaced(lastRacedDate.toLocalDate(), lastRacedDaysSince,
                    lastRacePerformance);
        }

        // program and entry
        String program = starterRecord.getValue(STARTERS.PROGRAM, String.class);
        String entryProgram = starterRecord.getValue(STARTERS.ENTRY_PROGRAM, String.class);
        boolean entry = starterRecord.getValue(STARTERS.ENTRY, Boolean.class);

        // horse and breeding
        String horseName = starterRecord.getValue(STARTERS.HORSE, String.class);
        Horse horse = new Horse(horseName);
        Record breedingRecord = breedingService.getBreedingRecordsByStarterId(starterId);
        if (breedingRecord != null) {
            horse = breedingService.getBreedingEntity(breedingRecord, horse);
        }

        // win, place, and show payoffs
        WinPlaceShowPayoff wpsPayoff = getWinPlaceShowPayoffEntity(starterId, program, horse);

        // jockey
        String jockeyFirst = starterRecord.getValue(STARTERS.JOCKEY_FIRST, String.class);
        String jockeyLast = starterRecord.getValue(STARTERS.JOCKEY_LAST, String.class);
        Jockey jockey = new Jockey(jockeyFirst, jockeyLast);

        // weight
        Integer weightCarried = starterRecord.getValue(STARTERS.WEIGHT, Integer.class);
        Integer jockeyAllowance = starterRecord.getValue(STARTERS.JOCKEY_ALLOWANCE, Integer.class);
        Weight weight = new Weight(weightCarried, jockeyAllowance);

        // medication and equipment
        String medEquipText = starterRecord.getValue(STARTERS.MEDICATION_EQUIPMENT, String.class);
        List<Medication> meds = new ArrayList<>();
        Result<Record> medsRecords = medsService.getMedsRecordsByStarterId(starterId);
        if (medsRecords != null) {
            for (Record medRecord : medsRecords) {
                Medication medication = medsService.getMedicationEntity(medRecord);
                meds.add(medication);
            }
        }
        List<Equipment> equipment = new ArrayList<>();
        Result<Record> equipRecords = equipService.getEquipRecordsByStarterId(starterId);
        if (equipRecords != null) {
            for (Record equipRecord : equipRecords) {
                Equipment equip = equipService.getEquipmentEntity(equipRecord);
                equipment.add(equip);
            }
        }
        MedicationEquipment medEquip =
                new MedicationEquipment(medEquipText, meds, equipment);

        // post position
        Integer pp = starterRecord.getValue(STARTERS.PP, Integer.class);

        // comments
        String comments = starterRecord.getValue(STARTERS.COMMENTS, String.class);

        // odds
        Double odds = starterRecord.getValue(STARTERS.ODDS, Double.class);
        Boolean favorite = starterRecord.getValue(STARTERS.FAVORITE, Boolean.class);
        Integer choice = starterRecord.getValue(STARTERS.CHOICE, Integer.class);

        // trainer
        String trainerFirst = starterRecord.getValue(STARTERS.TRAINER_FIRST, String.class);
        String trainerLast = starterRecord.getValue(STARTERS.TRAINER_LAST, String.class);
        Trainer trainer = new Trainer(program, trainerFirst, trainerLast);

        // owner
        String ownerName = starterRecord.getValue(STARTERS.OWNER, String.class);
        Owner owner = new Owner(program, ownerName);

        // claim
        Claim claim = null;
        Integer claimPrice = starterRecord.getValue(STARTERS.CLAIM_PRICE, Integer.class);
        if (claimPrice != null) {
            ClaimedHorse claimedHorse = null;
            boolean claimed = starterRecord.getValue(STARTERS.CLAIMED, Boolean.class);
            if (claimed) {
                String newTrainer = starterRecord.getValue(STARTERS.NEW_TRAINER_NAME, String.class);
                String newOwner = starterRecord.getValue(STARTERS.NEW_OWNER_NAME, String.class);
                claimedHorse = new ClaimedHorse(horse, newTrainer, newOwner);
            }
            claim = new Claim(new ClaimingPrice(program, horse, claimPrice), claimedHorse);
        }

        // positions
        Integer finishPosition = starterRecord.getValue(STARTERS.FINISH_POSITION, Integer.class);
        Integer officialPosition = starterRecord.getValue(STARTERS.OFFICIAL_POSITION,
                Integer.class);
        Boolean positionDeadHeat = starterRecord.getValue(STARTERS.POSITION_DEAD_HEAT,
                Boolean.class);
        Integer wageringPosition = starterRecord.getValue(STARTERS.WAGERING_POSITION,
                Integer.class);

        // winner
        boolean winner = starterRecord.getValue(STARTERS.WINNER, Boolean.class);

        // disqualified
        boolean disqualified = starterRecord.getValue(STARTERS.DISQUALIFIED, Boolean.class);

        return new Starter(lastRaced, program, entryProgram, entry, horse, jockey, weight, medEquip,
                pp, odds, favorite, comments, pointsOfCall, finishPosition, officialPosition,
                positionDeadHeat, wageringPosition, trainer, owner, claim, winner, disqualified,
                wpsPayoff, indivRatings, indivFractionals, indivSplits, choice);
    }

    public WinPlaceShowPayoff getWinPlaceShowPayoffEntity(Integer starterId, String program,
            Horse horse) {
        Win win = null;
        Place place = null;
        Show show = null;
        Result<Record> wpsPayoffRecords =
                wpsPayoffsService.getWPSPayoffsByStarterId(starterId);
        if (wpsPayoffRecords != null) {
            for (Record wpsPayoffRecord : wpsPayoffRecords) {
                WinPlaceShow wps = wpsPayoffsService.getWPSEntity(wpsPayoffRecord);
                if (wps instanceof Win) {
                    win = (Win) wps;
                } else if (wps instanceof Place) {
                    place = (Place) wps;
                } else if (wps instanceof Show) {
                    show = (Show) wps;
                }
            }
        }

        WinPlaceShowPayoff wpsPayoff = null;
        if (win != null || place != null || show != null) {
            wpsPayoff = new WinPlaceShowPayoff(program, horse, win, place, show);
        }
        return wpsPayoff;
    }

    public StartersRecord createStarter(RacesRecord racesRecord, Starter starter) {
        if (starter == null) {
            // TODO
            throw new RuntimeException("null starter");
        }

        InsertSetMoreStep<StartersRecord> moreStep = dsl.insertInto(STARTERS)
                .set(STARTERS.RACE_ID, racesRecord.getId());

        // last raced
        setLastRaced(starter, moreStep);

        // program number
        moreStep.set(STARTERS.PROGRAM, starter.getProgram());
        moreStep.set(STARTERS.ENTRY, starter.isEntry());
        moreStep.set(STARTERS.ENTRY_PROGRAM, starter.getEntryProgram());

        horse(starter, moreStep);
        jockey(starter, moreStep);
        trainer(starter, moreStep);
        owner(starter, moreStep);
        weight(starter, moreStep);
        medicationEquipment(starter, moreStep);
        claim(starter, moreStep);

        moreStep.set(STARTERS.PP, starter.getPostPosition());
        moreStep.set(STARTERS.FINISH_POSITION, starter.getFinishPosition());
        moreStep.set(STARTERS.OFFICIAL_POSITION, starter.getOfficialPosition());
        moreStep.set(STARTERS.POSITION_DEAD_HEAT, starter.isPositionDeadHeat());
        moreStep.set(STARTERS.WAGERING_POSITION, starter.getWageringPosition());
        moreStep.set(STARTERS.WINNER, starter.isWinner());
        moreStep.set(STARTERS.DISQUALIFIED, starter.isDisqualified());

        // odds
        moreStep.set(STARTERS.ODDS, starter.getOdds());
        moreStep.set(STARTERS.CHOICE, starter.getChoice());
        moreStep.set(STARTERS.FAVORITE, starter.isFavorite());

        moreStep.set(STARTERS.COMMENTS, starter.getComments());

        // save starter
        StartersRecord startersRecord = moreStep.returning(STARTERS.ID).fetchOne();

        // save starter meds and equipment
        medicationEquipment(starter, startersRecord);

        // save points of call
        pointsOfCall(starter, startersRecord);

        // save individual fractionals
        fractionals(starter, startersRecord);

        // save individual splits
        splits(starter, startersRecord);

        // save individual ratings
        ratings(starter, startersRecord);

        // save winners' breeding
        breeding(starter, startersRecord);

        // save wps and exotics
        winPlaceShow(starter, startersRecord);

        return startersRecord;
    }

    private void ratings(Starter starter, StartersRecord startersRecord) {
        List<Rating> ratings = starter.getRatings();
        if (ratings != null) {
            for (Rating rating : ratings) {
                indivRatingsService.createRating(startersRecord, rating);
            }
        }
    }

    private void splits(Starter starter, StartersRecord startersRecord) {
        List<Split> splits = starter.getSplits();
        if (splits != null) {
            for (Split split : splits) {
                indivSplitsService.createSplit(startersRecord, split);
            }
        }
    }

    private void fractionals(Starter starter, StartersRecord startersRecord) {
        List<Fractional> fractionals = starter.getFractionals();
        if (fractionals != null) {
            for (Fractional fractional : fractionals) {
                indivFractionalsService.createFractional(startersRecord, fractional);
            }
        }
    }

    private void pointsOfCall(Starter starter, StartersRecord startersRecord) {
        List<PointOfCall> pointsOfCall = starter.getPointsOfCall();
        if (pointsOfCall != null) {
            for (PointOfCall pointOfCall : pointsOfCall) {
                pointsOfCallService.createPointOfCall(startersRecord, pointOfCall);
            }
        }
    }

    private void medicationEquipment(Starter starter, StartersRecord startersRecord) {
        MedicationEquipment medicationEquipment = starter.getMedicationEquipment();
        if (medicationEquipment != null) {
            // medications
            List<Medication> medications = medicationEquipment.getMedications();
            if (medications != null) {
                for (Medication medication : medications) {
                    medsService.createMedication(startersRecord, medication);
                }
            }

            // equipment
            List<Equipment> equipment = medicationEquipment.getEquipment();
            if (equipment != null) {
                for (Equipment equip : equipment) {
                    equipService.createEquipment(startersRecord, equip);
                }
            }
        }
    }

    private InsertSetMoreStep<StartersRecord> setLastRaced(Starter starter,
            InsertSetMoreStep<StartersRecord> moreStep) {
        LastRaced lastRaced = starter.getLastRaced();
        if (lastRaced != null) {
            LastRacePerformance lastRacePerformance = lastRaced.getLastRacePerformance();
            // last race date
            if (lastRaced.getRaceDate() != null) {
                moreStep = moreStep.set(STARTERS.LAST_RACED_DATE,
                        Date.valueOf(lastRaced.getRaceDate()));
            }

            // last raced days since
            moreStep = moreStep.set(STARTERS.LAST_RACED_DAYS_SINCE, lastRaced.getDaysSince());

            if (lastRacePerformance != null) {
                Track lastRacedTrack = lastRacePerformance.getTrack();
                if (lastRacedTrack != null) {
                    // last raced track details
                    moreStep = moreStep.set(STARTERS.LAST_RACED_TRACK, lastRacedTrack.getCode())
                            .set(STARTERS.LAST_RACED_TRACK_CANONICAL, lastRacedTrack.getCanonical())
                            .set(STARTERS.LAST_RACED_TRACK_COUNTRY, lastRacedTrack.getCountry())
                            .set(STARTERS.LAST_RACED_TRACK_STATE, lastRacedTrack.getState())
                            .set(STARTERS.LAST_RACED_TRACK_NAME, lastRacedTrack.getName());
                }

                // last raced race number
                moreStep = moreStep.set(STARTERS.LAST_RACED_NUMBER,
                        lastRacePerformance.getRaceNumber());
            }

            // last raced official finishing position
            moreStep = moreStep.set(STARTERS.LAST_RACED_POSITION,
                    lastRacePerformance.getOfficialPosition());
        }
        return moreStep;
    }

    private InsertSetMoreStep<StartersRecord> horse(Starter starter,
            InsertSetMoreStep<StartersRecord> moreStep) {
        Horse horse = starter.getHorse();
        if (horse != null) {
            moreStep.set(STARTERS.HORSE, horse.getName());
        }
        return moreStep;
    }

    private InsertSetMoreStep<StartersRecord> jockey(Starter starter,
            InsertSetMoreStep<StartersRecord> moreStep) {
        Jockey jockey = starter.getJockey();
        if (jockey != null) {
            moreStep.set(STARTERS.JOCKEY_FIRST, jockey.getFirstName());
            moreStep.set(STARTERS.JOCKEY_LAST, jockey.getLastName());
        }
        return moreStep;
    }

    private InsertSetMoreStep<StartersRecord> trainer(Starter starter,
            InsertSetMoreStep<StartersRecord> moreStep) {
        Trainer trainer = starter.getTrainer();
        if (trainer != null) {
            moreStep.set(STARTERS.TRAINER_FIRST, trainer.getFirstName());
            moreStep.set(STARTERS.TRAINER_LAST, trainer.getLastName());
        }
        return moreStep;
    }

    private InsertSetMoreStep<StartersRecord> owner(Starter starter,
            InsertSetMoreStep<StartersRecord> moreStep) {
        Owner owner = starter.getOwner();
        if (owner != null) {
            moreStep.set(STARTERS.OWNER, owner.getName());
        }
        return moreStep;
    }

    private InsertSetMoreStep<StartersRecord> weight(Starter starter,
            InsertSetMoreStep<StartersRecord> moreStep) {
        Weight weight = starter.getWeight();
        if (weight != null) {
            moreStep.set(STARTERS.WEIGHT, weight.getWeightCarried());
            moreStep.set(STARTERS.JOCKEY_ALLOWANCE, weight.getJockeyAllowance());
        }

        return moreStep;
    }

    private InsertSetMoreStep<StartersRecord> medicationEquipment(Starter starter,
            InsertSetMoreStep<StartersRecord> moreStep) {
        MedicationEquipment medicationEquipment = starter.getMedicationEquipment();
        if (medicationEquipment != null) {
            moreStep.set(STARTERS.MEDICATION_EQUIPMENT, medicationEquipment.getText());
        }
        return moreStep;
    }

    private InsertSetMoreStep<StartersRecord> claim(Starter starter,
            InsertSetMoreStep<StartersRecord> moreStep) {
        Claim claim = starter.getClaim();
        if (claim != null) {
            moreStep.set(STARTERS.CLAIM_PRICE, claim.getPrice());
            moreStep.set(STARTERS.CLAIMED, claim.isClaimed());
            moreStep.set(STARTERS.NEW_TRAINER_NAME, claim.getNewTrainerName());
            moreStep.set(STARTERS.NEW_OWNER_NAME, claim.getNewOwnerName());
        }
        return moreStep;
    }

    private void winPlaceShow(Starter starter, StartersRecord startersRecord) {
        WinPlaceShowPayoff winPlaceShowPayoff = starter.getWinPlaceShowPayoff();
        if (winPlaceShowPayoff != null) {
            wpsPayoffsService.createWinPlaceShowPayoff(startersRecord, winPlaceShowPayoff);
        }
    }

    private void breeding(Starter starter, StartersRecord startersRecord) {
        if (starter != null && starter.isWinner()) {
            breedingService.createBreeding(startersRecord, starter);
        }
    }

    public List<Horse> findByHorseName(Condition condition) {
        List<Horse> horses = new ArrayList<>();

        Field<String> maleOrFemaleCaseField = DSL.decode()
                .when(BREEDING.SEX.in("Gelding", "Horse", "Colt", "Ridgling"), "M")
                .when(BREEDING.SEX.in("Filly", "Mare"), "F")
                .otherwise((String) null).as(BREEDING.SEX);

        Result<Record9<String, String, String, String, String, String, Date, String, String>>
                horseRecords = dsl.select(STARTERS.HORSE, BREEDING.COLOR,
                maleOrFemaleCaseField,
                BREEDING.SIRE, BREEDING.DAM, BREEDING.DAM_SIRE, BREEDING.FOALING_DATE,
                BREEDING.FOALING_LOCATION, BREEDING.BREEDER)
                .from(STARTERS).leftJoin(BREEDING).on(STARTERS.HORSE.eq(BREEDING.HORSE))
                .where(condition)
                .groupBy(STARTERS.HORSE, BREEDING.COLOR, maleOrFemaleCaseField, BREEDING.SIRE,
                        BREEDING.DAM, BREEDING.DAM_SIRE, BREEDING.FOALING_DATE,
                        BREEDING.FOALING_LOCATION, BREEDING.BREEDER)
                .orderBy(STARTERS.HORSE)
                .limit(200)
                .fetch();

        if (horseRecords != null) {
            horses = horseRecords.stream()
                    .map(this::getHorseEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return horses;
    }

    Horse getHorseEntity(Record horseRecord) {
        String horseName = horseRecord.get(STARTERS.HORSE, String.class);
        String color = horseRecord.get(BREEDING.COLOR, String.class);
        String sex = horseRecord.get(BREEDING.SEX, String.class);
        String sire = horseRecord.get(BREEDING.SIRE, String.class);
        String dam = horseRecord.get(BREEDING.DAM, String.class);
        String damSire = horseRecord.get(BREEDING.DAM_SIRE, String.class);
        Date foalingDate = horseRecord.get(BREEDING.FOALING_DATE, Date.class);
        String foalingLocation = horseRecord.get(BREEDING.FOALING_LOCATION, String.class);
        String breeder = horseRecord.get(BREEDING.BREEDER, String.class);

        return new Horse(horseName, color, sex,
                (sire != null ? new Horse(sire) : null),
                (dam != null ? new Horse(dam) : null),
                (damSire != null ? new Horse(damSire) : null),
                (foalingDate != null ? foalingDate.toLocalDate() : null),
                foalingLocation,
                (breeder != null ? new Breeder(breeder) : null));
    }
}
