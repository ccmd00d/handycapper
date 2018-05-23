package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.Cancellation;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.tracks.Track;
import com.robinhowlett.chartparser.tracks.TrackService;
import com.robinhowlett.handycapper.domain.tables.records.CancelledRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.robinhowlett.handycapper.domain.tables.Cancelled.CANCELLED;

@Service
@Transactional
public class CancelledRaceService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private TrackService trackService;

    public List<RaceResult> findByTrackAndDate(String trackCode, LocalDate date) {
        List<RaceResult> cancelledRaces = new ArrayList<>();

        Result<Record> cancelledRaceRecords = dsl.select().
                from(CANCELLED)
                .where(CANCELLED.TRACK.eq(trackCode)
                        .and(CANCELLED.DATE.eq(Date.valueOf(date))))
                .orderBy(CANCELLED.NUMBER.asc())
                .fetch();

        if (cancelledRaceRecords != null) {
            cancelledRaces = cancelledRaceRecords.stream()
                    .map(this::getCancelledRaceEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return cancelledRaces;
    }

    public RaceResult findByTrackAndDateAndNumber(String trackCode, LocalDate date,
            Integer raceNumber) {
        Record cancelledRaceRecord = dsl.select().
                from(CANCELLED)
                .where(CANCELLED.TRACK.eq(trackCode).and(
                        CANCELLED.DATE.eq(Date.valueOf(date)).and(
                                CANCELLED.NUMBER.eq(raceNumber)
                        )
                ))
                .fetchOne();

        if (cancelledRaceRecord != null) {
            RaceResult raceResult = getCancelledRaceEntity(cancelledRaceRecord);
            return raceResult;
        }

        return null;
    }

    private RaceResult getCancelledRaceEntity(Record cancelledRaceRecord) {
        // race date
        Date date = cancelledRaceRecord.getValue(CANCELLED.DATE, Date.class);

        // track
        String trackCode = cancelledRaceRecord.getValue(CANCELLED.TRACK, String.class);
        Track track = trackService.getTrack(trackCode).get();

        // race number
        Integer raceNumber = cancelledRaceRecord.getValue(CANCELLED.NUMBER, Integer.class);

        // cancellation reason
        String reason = cancelledRaceRecord.getValue(CANCELLED.REASON, String.class);

        return new RaceResult(new Cancellation(reason), date.toLocalDate(), track, raceNumber);
    }

    public CancelledRecord cancellation(RaceResult raceResult) {
        InsertSetStep<CancelledRecord> step = dsl.insertInto(CANCELLED);

        Cancellation cancellation = raceResult.getCancellation();
        if (cancellation == null) {
            // TODO
            throw new RuntimeException("bad");
        }
        InsertSetMoreStep<CancelledRecord> moreStep =
                step.set(CANCELLED.REASON, cancellation.getReason());

        // rate date
        raceDate(raceResult, moreStep);

        // track
        track(raceResult, moreStep);

        // race number
        moreStep.set(CANCELLED.NUMBER, raceResult.getRaceNumber());

        // save cancelled race
        CancelledRecord cancelledRecord = moreStep.returning(CANCELLED.ID).fetchOne();

        return cancelledRecord;
    }

    private InsertSetMoreStep<CancelledRecord> raceDate(RaceResult raceResult,
            InsertSetMoreStep<CancelledRecord> moreStep) {
        LocalDate raceDate = raceResult.getRaceDate();
        if (raceDate == null) {
            // TODO
            throw new RuntimeException("bad");
        }

        return moreStep.set(CANCELLED.DATE, Date.valueOf(raceDate));
    }

    private InsertSetMoreStep<CancelledRecord> track(RaceResult raceResult,
            InsertSetMoreStep<CancelledRecord> moreStep) {
        Track track = raceResult.getTrack();
        if (track != null) {
            moreStep = moreStep.set(CANCELLED.TRACK, track.getCode())
                    .set(CANCELLED.TRACK_CANONICAL, track.getCanonical())
                    .set(CANCELLED.TRACK_COUNTRY, track.getCountry())
                    .set(CANCELLED.TRACK_STATE, track.getState())
                    .set(CANCELLED.TRACK_NAME, track.getName());
        }
        return moreStep;
    }
}
