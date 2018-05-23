package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.fractionals.FractionalPoint.Fractional;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Split;
import com.robinhowlett.handycapper.domain.tables.records.RacesRecord;
import com.robinhowlett.handycapper.domain.tables.records.SplitsRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.Splits.SPLITS;

@Service
@Transactional
public class RaceSplitsService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getRaceSplitRecordsByRaceId(Integer raceId) {
        return dsl.select().
                from(SPLITS)
                .where(SPLITS.RACE_ID.eq(raceId))
                .fetch();
    }

    public Split getRaceSplitEntity(Record raceSplitsRecord) {
        Integer point = raceSplitsRecord.getValue(SPLITS.POINT, Integer.class);
        String text = raceSplitsRecord.getValue(SPLITS.TEXT, String.class);
        String compact = raceSplitsRecord.getValue(SPLITS.COMPACT, String.class);
        Integer feet = raceSplitsRecord.getValue(SPLITS.FEET, Integer.class);
        String time = raceSplitsRecord.getValue(SPLITS.TIME, String.class);
        Long millis = raceSplitsRecord.getValue(SPLITS.MILLIS, Long.class);

        Fractional fromFractional = null;
        Integer fromPoint = raceSplitsRecord.getValue(SPLITS.FROM_POINT, Integer.class);
        if (fromPoint != null) {
            String fromText = raceSplitsRecord.getValue(SPLITS.FROM_TEXT, String.class);
            String fromCompact = raceSplitsRecord.getValue(SPLITS.FROM_COMPACT, String.class);
            Integer fromFeet = raceSplitsRecord.getValue(SPLITS.FROM_FEET, Integer.class);
            String fromTime = raceSplitsRecord.getValue(SPLITS.FROM_TIME, String.class);
            Long fromMillis = raceSplitsRecord.getValue(SPLITS.FROM_MILLIS, Long.class);
            fromFractional = new Fractional(fromPoint, fromText, fromCompact, fromFeet, fromTime,
                    fromMillis);
        }

        Fractional toFractional = null;
        Integer toPoint = raceSplitsRecord.getValue(SPLITS.TO_POINT, Integer.class);
        if (toPoint != null) {
            String toText = raceSplitsRecord.getValue(SPLITS.TO_TEXT, String.class);
            String toCompact = raceSplitsRecord.getValue(SPLITS.TO_COMPACT, String.class);
            Integer toFeet = raceSplitsRecord.getValue(SPLITS.TO_FEET, Integer.class);
            String toTime = raceSplitsRecord.getValue(SPLITS.TO_TIME, String.class);
            Long toMillis = raceSplitsRecord.getValue(SPLITS.TO_MILLIS, Long.class);
            toFractional = new Fractional(toPoint, toText, toCompact, toFeet, toTime, toMillis);
        }

        return new Split(point, text, compact, feet, time, millis, fromFractional, toFractional);
    }

    public SplitsRecord createSplit(RacesRecord racesRecord,
            Split split) {
        if (split == null) {
            // TODO
            throw new RuntimeException("null split");
        }

        InsertSetMoreStep<SplitsRecord> moreStep = dsl.insertInto(SPLITS)
                .set(SPLITS.RACE_ID, racesRecord.getId());

        split(split, moreStep);

        SplitsRecord splitsRecord = moreStep.returning(SPLITS.ID)
                .fetchOne();

        return splitsRecord;
    }

    private InsertSetMoreStep<SplitsRecord> split(Split split,
            InsertSetMoreStep<SplitsRecord> moreStep) {
        moreStep.set(SPLITS.POINT, split.getPoint());
        moreStep.set(SPLITS.TEXT, split.getText());
        moreStep.set(SPLITS.COMPACT, split.getCompact());
        moreStep.set(SPLITS.FEET, split.getFeet());
        moreStep.set(SPLITS.FURLONGS, split.getFurlongs());
        moreStep.set(SPLITS.TIME, split.getTime());
        moreStep.set(SPLITS.MILLIS, split.getMillis());

        Fractional from = split.getFrom();
        if (from != null) {
            moreStep.set(SPLITS.FROM_POINT, from.getPoint());
            moreStep.set(SPLITS.FROM_TEXT, from.getText());
            moreStep.set(SPLITS.FROM_COMPACT, from.getCompact());
            moreStep.set(SPLITS.FROM_FEET, from.getFeet());
            moreStep.set(SPLITS.FROM_FURLONGS, from.getFurlongs());
            moreStep.set(SPLITS.FROM_TIME, from.getTime());
            moreStep.set(SPLITS.FROM_MILLIS, from.getMillis());
        }

        Fractional to = split.getTo();
        if (to != null) {
            moreStep.set(SPLITS.TO_POINT, to.getPoint());
            moreStep.set(SPLITS.TO_TEXT, to.getText());
            moreStep.set(SPLITS.TO_COMPACT, to.getCompact());
            moreStep.set(SPLITS.TO_FEET, to.getFeet());
            moreStep.set(SPLITS.TO_FURLONGS, to.getFurlongs());
            moreStep.set(SPLITS.TO_TIME, to.getTime());
            moreStep.set(SPLITS.TO_MILLIS, to.getMillis());
        }
        return moreStep;
    }
}
