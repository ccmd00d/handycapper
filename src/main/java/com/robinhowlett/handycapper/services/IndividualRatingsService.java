package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.Rating;
import com.robinhowlett.handycapper.domain.tables.records.IndivRatingsRecord;
import com.robinhowlett.handycapper.domain.tables.records.StartersRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.IndivRatings.INDIV_RATINGS;
import static com.robinhowlett.handycapper.domain.tables.Ratings.RATINGS;

@Service
@Transactional
public class IndividualRatingsService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getIndivRatingRecordsByStarterId(Integer starterId) {
        return dsl.select().
                from(INDIV_RATINGS)
                .where(INDIV_RATINGS.STARTER_ID.eq(starterId))
                .fetch();
    }

    public Rating getIndivRatingEntity(Record indivRatingRecord) {
        String name = indivRatingRecord.getValue(RATINGS.NAME, String.class);
        String text = indivRatingRecord.getValue(RATINGS.TEXT, String.class);
        Double value = indivRatingRecord.getValue(RATINGS.VALUE, Double.class);
        String extra = indivRatingRecord.getValue(RATINGS.EXTRA, String.class);
        return new Rating(name, text, value, extra);
    }

    public IndivRatingsRecord createRating(StartersRecord startersRecord, Rating rating) {
        if (rating == null) {
            // TODO
            throw new RuntimeException("null rating");
        }

        InsertSetMoreStep<IndivRatingsRecord> moreStep = dsl.insertInto(INDIV_RATINGS)
                .set(INDIV_RATINGS.STARTER_ID, startersRecord.getId());

        rating(rating, moreStep);

        IndivRatingsRecord indivRatingsRecord = moreStep.returning(INDIV_RATINGS.ID)
                .fetchOne();

        return indivRatingsRecord;
    }

    private InsertSetMoreStep<IndivRatingsRecord> rating(Rating rating,
            InsertSetMoreStep<IndivRatingsRecord> moreStep) {
        moreStep.set(INDIV_RATINGS.NAME, rating.getName());
        moreStep.set(INDIV_RATINGS.TEXT, rating.getText());
        moreStep.set(INDIV_RATINGS.VALUE, rating.getValue());
        moreStep.set(INDIV_RATINGS.EXTRA, rating.getExtra());
        return moreStep;
    }
}
