package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.fractionals.FractionalPoint.Fractional;
import com.robinhowlett.handycapper.domain.tables.records.FractionalsRecord;
import com.robinhowlett.handycapper.domain.tables.records.RacesRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.Fractionals.FRACTIONALS;

@Service
@Transactional
public class RaceFractionalsService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getRaceFractionalRecordsByRaceId(Integer raceId) {
        return dsl.select().
                from(FRACTIONALS)
                .where(FRACTIONALS.RACE_ID.eq(raceId))
                .fetch();
    }

    public Fractional getRaceFractionalEntity(Record raceFractionalRecord) {
        Integer point = raceFractionalRecord.getValue(FRACTIONALS.POINT, Integer.class);
        String text = raceFractionalRecord.getValue(FRACTIONALS.TEXT, String.class);
        String compact = raceFractionalRecord.getValue(FRACTIONALS.COMPACT, String.class);
        Integer feet = raceFractionalRecord.getValue(FRACTIONALS.FEET, Integer.class);
        String time = raceFractionalRecord.getValue(FRACTIONALS.TIME, String.class);
        Long millis = raceFractionalRecord.getValue(FRACTIONALS.MILLIS, Long.class);
        return new Fractional(point, text, compact, feet, time, millis);
    }

    public FractionalsRecord createFractional(RacesRecord racesRecord,
            Fractional fractional) {
        if (fractional == null) {
            // TODO
            throw new RuntimeException("null race fractional");
        }

        InsertSetMoreStep<FractionalsRecord> moreStep = dsl.insertInto(FRACTIONALS)
                .set(FRACTIONALS.RACE_ID, racesRecord.getId());

        fractional(fractional, moreStep);

        FractionalsRecord indivFractionalsRecord = moreStep.returning(FRACTIONALS.ID)
                .fetchOne();

        return indivFractionalsRecord;
    }

    private InsertSetMoreStep<FractionalsRecord> fractional(Fractional fractional,
            InsertSetMoreStep<FractionalsRecord> moreStep) {
        moreStep.set(FRACTIONALS.POINT, fractional.getPoint());
        moreStep.set(FRACTIONALS.TEXT, fractional.getText());
        moreStep.set(FRACTIONALS.COMPACT, fractional.getCompact());
        moreStep.set(FRACTIONALS.FEET, fractional.getFeet());
        moreStep.set(FRACTIONALS.FURLONGS, fractional.getFurlongs());
        moreStep.set(FRACTIONALS.TIME, fractional.getTime());
        moreStep.set(FRACTIONALS.MILLIS, fractional.getMillis());
        return moreStep;
    }
}
