package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.fractionals.FractionalPoint.Fractional;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Split;
import com.robinhowlett.handycapper.domain.tables.records.IndivSplitsRecord;
import com.robinhowlett.handycapper.domain.tables.records.StartersRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.IndivSplits.INDIV_SPLITS;
import static com.robinhowlett.handycapper.domain.tables.Splits.SPLITS;

@Service
@Transactional
public class IndividualSplitsService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getIndivSplitRecordsByStarterId(Integer starterId) {
        return dsl.select().
                from(INDIV_SPLITS)
                .where(INDIV_SPLITS.STARTER_ID.eq(starterId))
                .fetch();
    }

    public Split getIndivSplitEntity(Record indivSplitsRecord) {
        Integer point = indivSplitsRecord.getValue(SPLITS.POINT, Integer.class);
        String text = indivSplitsRecord.getValue(SPLITS.TEXT, String.class);
        String compact = indivSplitsRecord.getValue(SPLITS.COMPACT, String.class);
        Integer feet = indivSplitsRecord.getValue(SPLITS.FEET, Integer.class);
        String time = indivSplitsRecord.getValue(SPLITS.TIME, String.class);
        Long millis = indivSplitsRecord.getValue(SPLITS.MILLIS, Long.class);

        Fractional fromFractional = null;
        Integer fromPoint = indivSplitsRecord.getValue(SPLITS.FROM_POINT, Integer.class);
        if (fromPoint != null) {
            String fromText = indivSplitsRecord.getValue(SPLITS.FROM_TEXT, String.class);
            String fromCompact = indivSplitsRecord.getValue(SPLITS.FROM_COMPACT, String.class);
            Integer fromFeet = indivSplitsRecord.getValue(SPLITS.FROM_FEET, Integer.class);
            String fromTime = indivSplitsRecord.getValue(SPLITS.FROM_TIME, String.class);
            Long fromMillis = indivSplitsRecord.getValue(SPLITS.FROM_MILLIS, Long.class);
            fromFractional = new Fractional(fromPoint, fromText, fromCompact, fromFeet, fromTime,
                    fromMillis);
        }

        Fractional toFractional = null;
        Integer toPoint = indivSplitsRecord.getValue(SPLITS.TO_POINT, Integer.class);
        if (toPoint != null) {
            String toText = indivSplitsRecord.getValue(SPLITS.TO_TEXT, String.class);
            String toCompact = indivSplitsRecord.getValue(SPLITS.TO_COMPACT, String.class);
            Integer toFeet = indivSplitsRecord.getValue(SPLITS.TO_FEET, Integer.class);
            String toTime = indivSplitsRecord.getValue(SPLITS.TO_TIME, String.class);
            Long toMillis = indivSplitsRecord.getValue(SPLITS.TO_MILLIS, Long.class);
            toFractional = new Fractional(toPoint, toText, toCompact, toFeet, toTime, toMillis);
        }

        return new Split(point, text, compact, feet, time, millis, fromFractional, toFractional);
    }

    public IndivSplitsRecord createSplit(StartersRecord startersRecord, Split split) {
        if (split == null) {
            // TODO
            throw new RuntimeException("null split");
        }

        InsertSetMoreStep<IndivSplitsRecord> moreStep = dsl.insertInto(INDIV_SPLITS)
                .set(INDIV_SPLITS.STARTER_ID, startersRecord.getId());

        split(split, moreStep);

        IndivSplitsRecord indivSplitsRecord = moreStep.returning(INDIV_SPLITS.ID)
                .fetchOne();

        return indivSplitsRecord;
    }

    private InsertSetMoreStep<IndivSplitsRecord> split(Split split,
            InsertSetMoreStep<IndivSplitsRecord> moreStep) {
        moreStep.set(INDIV_SPLITS.POINT, split.getPoint());
        moreStep.set(INDIV_SPLITS.TEXT, split.getText());
        moreStep.set(INDIV_SPLITS.COMPACT, split.getCompact());
        moreStep.set(INDIV_SPLITS.FEET, split.getFeet());
        moreStep.set(INDIV_SPLITS.FURLONGS, split.getFurlongs());
        moreStep.set(INDIV_SPLITS.TIME, split.getTime());
        moreStep.set(INDIV_SPLITS.MILLIS, split.getMillis());

        Fractional from = split.getFrom();
        if (from != null) {
            moreStep.set(INDIV_SPLITS.FROM_POINT, from.getPoint());
            moreStep.set(INDIV_SPLITS.FROM_TEXT, from.getText());
            moreStep.set(INDIV_SPLITS.FROM_COMPACT, from.getCompact());
            moreStep.set(INDIV_SPLITS.FROM_FEET, from.getFeet());
            moreStep.set(INDIV_SPLITS.FROM_FURLONGS, from.getFurlongs());
            moreStep.set(INDIV_SPLITS.FROM_TIME, from.getTime());
            moreStep.set(INDIV_SPLITS.FROM_MILLIS, from.getMillis());
        }

        Fractional to = split.getTo();
        if (to != null) {
            moreStep.set(INDIV_SPLITS.TO_POINT, to.getPoint());
            moreStep.set(INDIV_SPLITS.TO_TEXT, to.getText());
            moreStep.set(INDIV_SPLITS.TO_COMPACT, to.getCompact());
            moreStep.set(INDIV_SPLITS.TO_FEET, to.getFeet());
            moreStep.set(INDIV_SPLITS.TO_FURLONGS, to.getFurlongs());
            moreStep.set(INDIV_SPLITS.TO_TIME, to.getTime());
            moreStep.set(INDIV_SPLITS.TO_MILLIS, to.getMillis());
        }
        return moreStep;
    }
}
