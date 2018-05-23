package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.Horse;
import com.robinhowlett.chartparser.charts.pdf.Scratch;
import com.robinhowlett.handycapper.domain.tables.records.RacesRecord;
import com.robinhowlett.handycapper.domain.tables.records.ScratchesRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.Scratches.SCRATCHES;

@Service
@Transactional
public class ScratchesService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getScrachesRecordsByRaceId(int raceId) {
        return dsl.select().
                from(SCRATCHES)
                .where(SCRATCHES.RACE_ID.eq(raceId))
                .fetch();

    }

    public Scratch getScratchEntity(Record scratchRecord) {
        String horseName = scratchRecord.getValue(SCRATCHES.HORSE, String.class);
        String reason = scratchRecord.getValue(SCRATCHES.REASON, String.class);
        return new Scratch(new Horse(horseName), reason);
    }

    public ScratchesRecord createScratch(RacesRecord racesRecord,
            Scratch scratch) {
        if (scratch == null) {
            // TODO
            throw new RuntimeException("null scratch");
        }

        InsertSetMoreStep<ScratchesRecord> moreStep = dsl.insertInto(SCRATCHES)
                .set(SCRATCHES.RACE_ID, racesRecord.getId());

        scratch(scratch, moreStep);

        ScratchesRecord splitsRecord = moreStep.returning(SCRATCHES.ID)
                .fetchOne();

        return splitsRecord;
    }

    private InsertSetMoreStep<ScratchesRecord> scratch(Scratch scratch,
            InsertSetMoreStep<ScratchesRecord> moreStep) {
        Horse horse = scratch.getHorse();
        if (horse != null) {
            moreStep.set(SCRATCHES.HORSE, horse.getName());
        }
        moreStep.set(SCRATCHES.REASON, scratch.getReason());
        return moreStep;
    }

}
