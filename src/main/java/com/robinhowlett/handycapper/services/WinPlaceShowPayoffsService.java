package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff.Place;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff.Show;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff.Win;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff.WinPlaceShow;
import com.robinhowlett.handycapper.domain.tables.records.StartersRecord;
import com.robinhowlett.handycapper.domain.tables.records.WpsRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.Wps.WPS;

@Service
@Transactional
public class WinPlaceShowPayoffsService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getWPSPayoffsByStarterId(Integer starterId) {
        return dsl.select().
                from(WPS)
                .where(WPS.STARTER_ID.eq(starterId))
                .fetch();
    }

    public WinPlaceShow getWPSEntity(Record wpsPayoffRecord) {
        String type = wpsPayoffRecord.getValue(WPS.TYPE, String.class);
        Double payoff = wpsPayoffRecord.getValue(WPS.PAYOFF, Double.class);

        switch (type) {
            case "Win":
                return new Win(payoff);
            case "Place":
                return new Place(payoff);
            case "Show":
                return new Show(payoff);
        }

        return null;
    }

    public void createWinPlaceShowPayoff(StartersRecord startersRecord,
            WinPlaceShowPayoff wpsPayoff) {
        if (wpsPayoff == null) {
            // TODO
            throw new RuntimeException("null wpsPayoff");
        }

        wpsPayoff(wpsPayoff.getWin(), startersRecord);
        wpsPayoff(wpsPayoff.getPlace(), startersRecord);
        wpsPayoff(wpsPayoff.getShow(), startersRecord);
    }

    private WpsRecord wpsPayoff(WinPlaceShow wager, StartersRecord startersRecord) {
        if (wager != null) {
            InsertSetMoreStep<WpsRecord> moreStep = dsl.insertInto(WPS)
                    .set(WPS.STARTER_ID, startersRecord.getId());

            moreStep.set(WPS.TYPE, wager.getType());
            moreStep.set(WPS.UNIT, wager.getUnit());
            moreStep.set(WPS.PAYOFF, wager.getPayoff());
            moreStep.set(WPS.ODDS, wager.getOdds());

            WpsRecord wpsPayoffsRecord = moreStep.returning(WPS.ID).fetchOne();
            return wpsPayoffsRecord;
        }

        return null;
    }
}
