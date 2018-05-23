package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.Breeder;
import com.robinhowlett.chartparser.charts.pdf.Horse;
import com.robinhowlett.chartparser.charts.pdf.Starter;
import com.robinhowlett.handycapper.domain.tables.records.BreedingRecord;
import com.robinhowlett.handycapper.domain.tables.records.StartersRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

import static com.robinhowlett.handycapper.domain.tables.Breeding.BREEDING;

@Service
@Transactional
public class BreedingService {

    @Autowired
    private DSLContext dsl;

    public Record getBreedingRecordsByStarterId(Integer starterId) {
        return dsl.select().
                from(BREEDING)
                .where(BREEDING.STARTER_ID.eq(starterId))
                .fetchOne();
    }

    public Horse getBreedingEntity(Record breedingRecord, Horse horse) {
        String color = breedingRecord.getValue(BREEDING.COLOR, String.class);
        String sex = breedingRecord.getValue(BREEDING.SEX, String.class);
        String sire = breedingRecord.getValue(BREEDING.SIRE, String.class);
        String dam = breedingRecord.getValue(BREEDING.DAM, String.class);
        String damSire = breedingRecord.getValue(BREEDING.DAM_SIRE, String.class);
        Date foalingDate = breedingRecord.getValue(BREEDING.FOALING_DATE, Date.class);
        String foalingLocation = breedingRecord.getValue(BREEDING.FOALING_LOCATION, String.class);
        String breeder = breedingRecord.getValue(BREEDING.BREEDER, String.class);

        horse.setColor(color);
        horse.setSex(sex);
        horse.setFoalingLocation(foalingLocation);

        if (foalingDate != null) {
            horse.setFoalingDate(foalingDate.toLocalDate());
        }
        if (sire != null) {
            horse.setSire(new Horse(sire));
        }
        if (dam != null) {
            horse.setDam(new Horse(dam));
        }
        if (damSire != null) {
            horse.setDamSire(new Horse(damSire));
        }
        if (breeder != null) {
            horse.setBreeder(new Breeder(breeder));
        }

        return horse;
    }

    public BreedingRecord createBreeding(StartersRecord startersRecord, Starter winner) {
        if (winner == null) {
            // TODO
            throw new RuntimeException("null winner");
        }

        Horse horse = winner.getHorse();
        if (horse != null) {
            Horse sire = horse.getSire();
            Horse dam = horse.getDam();
            Horse damSire = horse.getDamSire();

            // only save if lineage is documented
            if (sire != null && dam != null && damSire != null) {
                InsertSetMoreStep<BreedingRecord> moreStep = dsl.insertInto(BREEDING)
                        .set(BREEDING.STARTER_ID, startersRecord.getId());
                winner(winner, moreStep);


                BreedingRecord breedingRecord = moreStep.returning(BREEDING.ID).fetchOne();
                return breedingRecord;
            }
        }

        return null;
    }

    private InsertSetMoreStep<BreedingRecord> winner(Starter winner,
            InsertSetMoreStep<BreedingRecord> moreStep) {
        Horse horse = winner.getHorse();
        if (horse != null) {
            Horse sire = horse.getSire();
            Horse dam = horse.getDam();
            Horse damSire = horse.getDamSire();

            // only save if lineage is documented
            if (sire != null && dam != null && damSire != null) {
                moreStep.set(BREEDING.HORSE, horse.getName());
                moreStep.set(BREEDING.COLOR, horse.getColor());
                moreStep.set(BREEDING.SEX, horse.getSex());
                moreStep.set(BREEDING.SIRE, sire.getName());
                moreStep.set(BREEDING.DAM, dam.getName());
                moreStep.set(BREEDING.DAM_SIRE, damSire.getName());

                LocalDate foalingDate = horse.getFoalingDate();
                if (foalingDate != null) {
                    moreStep.set(BREEDING.FOALING_DATE, Date.valueOf(foalingDate));
                }

                moreStep.set(BREEDING.FOALING_LOCATION, horse.getFoalingLocation());

                Breeder breeder = horse.getBreeder();
                if (breeder != null) {
                    moreStep.set(BREEDING.BREEDER, breeder.getName());
                }
            }
        }

        return moreStep;
    }
}
