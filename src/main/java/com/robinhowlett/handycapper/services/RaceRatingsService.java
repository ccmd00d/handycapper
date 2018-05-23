package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.Rating;
import com.robinhowlett.handycapper.domain.tables.records.RacesRecord;
import com.robinhowlett.handycapper.domain.tables.records.RatingsRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.Ratings.RATINGS;

@Service
@Transactional
public class RaceRatingsService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getRaceRatingRecordsByRaceId(Integer raceId) {
        return dsl.select().
                from(RATINGS)
                .where(RATINGS.RACE_ID.eq(raceId))
                .fetch();
    }

    public Rating getRaceRatingEntity(Record raceRatingRecord) {
        String name = raceRatingRecord.getValue(RATINGS.NAME, String.class);
        String text = raceRatingRecord.getValue(RATINGS.TEXT, String.class);
        Double value = raceRatingRecord.getValue(RATINGS.VALUE, Double.class);
        String extra = raceRatingRecord.getValue(RATINGS.EXTRA, String.class);
        return new Rating(name, text, value, extra);
    }

    public RatingsRecord createRating(RacesRecord racesRecord, Rating rating) {
        if (rating == null) {
            // TODO
            throw new RuntimeException("null race rating");
        }

        InsertSetMoreStep<RatingsRecord> moreStep = dsl.insertInto(RATINGS)
                .set(RATINGS.RACE_ID, racesRecord.getId());

        rating(rating, moreStep);

        RatingsRecord ratingsRecord = moreStep.returning(RATINGS.ID)
                .fetchOne();

        return ratingsRecord;
    }

    private InsertSetMoreStep<RatingsRecord> rating(Rating rating,
            InsertSetMoreStep<RatingsRecord> moreStep) {
        moreStep.set(RATINGS.NAME, rating.getName());
        moreStep.set(RATINGS.TEXT, rating.getText());
        moreStep.set(RATINGS.VALUE, rating.getValue());
        moreStep.set(RATINGS.EXTRA, rating.getExtra());
        return moreStep;
    }
}
