package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition;
import com.robinhowlett.handycapper.domain.tables.records.PointsOfCallRecord;
import com.robinhowlett.handycapper.domain.tables.records.StartersRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall
        .RelativePosition.LengthsAhead;
import static com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall
        .RelativePosition.TotalLengthsBehind;
import static com.robinhowlett.handycapper.domain.tables.PointsOfCall.POINTS_OF_CALL;

@Service
@Transactional
public class PointsOfCallService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getPointOfCallRecordsByStarterId(Integer starterId) {
        return dsl.select().
                from(POINTS_OF_CALL)
                .where(POINTS_OF_CALL.STARTER_ID.eq(starterId))
                .fetch();
    }

    public PointOfCall getPointOfCallEntity(Record pocRecord) {
        Integer point = pocRecord.getValue(POINTS_OF_CALL.POINT, Integer.class);
        String text = pocRecord.getValue(POINTS_OF_CALL.TEXT, String.class);
        String compact = pocRecord.getValue(POINTS_OF_CALL.COMPACT, String.class);
        Integer feet = pocRecord.getValue(POINTS_OF_CALL.FEET, Integer.class);

        RelativePosition relativePosition = null;
        Integer position = pocRecord.getValue(POINTS_OF_CALL.POSITION, Integer.class);
        if (position != null) {
            LengthsAhead lengthsAhead = null;
            Double lengthsAheadValue = pocRecord.getValue(POINTS_OF_CALL.LEN_AHEAD, Double.class);
            if (lengthsAheadValue != null) {
                String lengthsAheadText = pocRecord.getValue(POINTS_OF_CALL.LEN_AHEAD_TEXT,
                        String.class);
                lengthsAhead = new LengthsAhead(lengthsAheadText, lengthsAheadValue);
            }

            TotalLengthsBehind totalLengthsBehind = null;
            Double totalLengthsBehindValue = pocRecord.getValue(POINTS_OF_CALL.TOT_LEN_BHD,
                    Double.class);
            if (totalLengthsBehindValue != null) {
                String totalLengthsBehindText = pocRecord.getValue(POINTS_OF_CALL
                        .TOT_LEN_BHD_TEXT, String.class);
                totalLengthsBehind = new TotalLengthsBehind(totalLengthsBehindText,
                        totalLengthsBehindValue);
            }

            relativePosition = new RelativePosition(position, lengthsAhead, totalLengthsBehind);
        }

        return new PointOfCall(point, text, compact, feet, relativePosition);
    }

    public PointsOfCallRecord createPointOfCall(StartersRecord startersRecord,
            PointOfCall pointOfCall) {
        if (pointOfCall == null) {
            // TODO
            throw new RuntimeException("null point of call");
        }

        InsertSetMoreStep<PointsOfCallRecord> moreStep = dsl.insertInto(POINTS_OF_CALL)
                .set(POINTS_OF_CALL.STARTER_ID, startersRecord.getId());

        pointOfCall(pointOfCall, moreStep);

        PointsOfCallRecord pointsOfCallRecord = moreStep.returning(POINTS_OF_CALL.ID).fetchOne();

        return pointsOfCallRecord;
    }

    private InsertSetMoreStep<PointsOfCallRecord> pointOfCall(PointOfCall pointOfCall,
            InsertSetMoreStep<PointsOfCallRecord> moreStep) {
        moreStep.set(POINTS_OF_CALL.POINT, pointOfCall.getPoint());
        moreStep.set(POINTS_OF_CALL.TEXT, pointOfCall.getText());
        moreStep.set(POINTS_OF_CALL.COMPACT, pointOfCall.getCompact());
        moreStep.set(POINTS_OF_CALL.FEET, pointOfCall.getFeet());
        moreStep.set(POINTS_OF_CALL.FURLONGS, pointOfCall.getFurlongs());

        RelativePosition relativePosition = pointOfCall.getRelativePosition();
        if (relativePosition != null) {
            moreStep.set(POINTS_OF_CALL.POSITION, relativePosition.getPosition());

            LengthsAhead lengthsAhead = relativePosition.getLengthsAhead();
            if (lengthsAhead != null) {
                moreStep.set(POINTS_OF_CALL.LEN_AHEAD_TEXT, lengthsAhead.getText());
                moreStep.set(POINTS_OF_CALL.LEN_AHEAD, lengthsAhead.getLengths());
            }

            TotalLengthsBehind totalLengthsBehind = relativePosition.getTotalLengthsBehind();
            if (totalLengthsBehind != null) {
                moreStep.set(POINTS_OF_CALL.TOT_LEN_BHD_TEXT, totalLengthsBehind.getText());
                moreStep.set(POINTS_OF_CALL.TOT_LEN_BHD, totalLengthsBehind.getLengths());
            }

            Integer wide = relativePosition.getWide();
            if (wide != null) {
                moreStep.set(POINTS_OF_CALL.WIDE, wide);
            }
        }

        return moreStep;
    }
}
