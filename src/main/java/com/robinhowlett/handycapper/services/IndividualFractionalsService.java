package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.fractionals.FractionalPoint.Fractional;
import com.robinhowlett.handycapper.domain.tables.records.IndivFractionalsRecord;
import com.robinhowlett.handycapper.domain.tables.records.StartersRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.Fractionals.FRACTIONALS;
import static com.robinhowlett.handycapper.domain.tables.IndivFractionals.INDIV_FRACTIONALS;

@Service
@Transactional
public class IndividualFractionalsService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getIndivFractionalRecordsByStarterId(Integer starterId) {
        return dsl.select().
                from(INDIV_FRACTIONALS)
                .where(INDIV_FRACTIONALS.STARTER_ID.eq(starterId))
                .fetch();
    }

    public Fractional getIndivFractionalEntity(Record indivFractionalRecord) {
        Integer point = indivFractionalRecord.getValue(FRACTIONALS.POINT, Integer.class);
        String text = indivFractionalRecord.getValue(FRACTIONALS.TEXT, String.class);
        String compact = indivFractionalRecord.getValue(FRACTIONALS.COMPACT, String.class);
        Integer feet = indivFractionalRecord.getValue(FRACTIONALS.FEET, Integer.class);
        String time = indivFractionalRecord.getValue(FRACTIONALS.TIME, String.class);
        Long millis = indivFractionalRecord.getValue(FRACTIONALS.MILLIS, Long.class);
        return new Fractional(point, text, compact, feet, time, millis);
    }

    public IndivFractionalsRecord createFractional(StartersRecord startersRecord,
            Fractional fractional) {
        if (fractional == null) {
            // TODO
            throw new RuntimeException("null fractional");
        }

        InsertSetMoreStep<IndivFractionalsRecord> moreStep = dsl.insertInto(INDIV_FRACTIONALS)
                .set(INDIV_FRACTIONALS.STARTER_ID, startersRecord.getId());

        fractional(fractional, moreStep);

        IndivFractionalsRecord indivFractionalsRecord = moreStep.returning(INDIV_FRACTIONALS.ID)
                .fetchOne();

        return indivFractionalsRecord;
    }

    private InsertSetMoreStep<IndivFractionalsRecord> fractional(Fractional fractional,
            InsertSetMoreStep<IndivFractionalsRecord> moreStep) {
        moreStep.set(INDIV_FRACTIONALS.POINT, fractional.getPoint());
        moreStep.set(INDIV_FRACTIONALS.TEXT, fractional.getText());
        moreStep.set(INDIV_FRACTIONALS.COMPACT, fractional.getCompact());
        moreStep.set(INDIV_FRACTIONALS.FEET, fractional.getFeet());
        moreStep.set(INDIV_FRACTIONALS.FURLONGS, fractional.getFurlongs());
        moreStep.set(INDIV_FRACTIONALS.TIME, fractional.getTime());
        moreStep.set(INDIV_FRACTIONALS.MILLIS, fractional.getMillis());
        return moreStep;
    }
}
