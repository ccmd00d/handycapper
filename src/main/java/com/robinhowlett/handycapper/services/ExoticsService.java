package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.ExoticPayoffPool;
import com.robinhowlett.handycapper.domain.tables.records.ExoticsRecord;
import com.robinhowlett.handycapper.domain.tables.records.RacesRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.Exotics.EXOTICS;

@Service
@Transactional
public class ExoticsService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getExoticPayoffPoolRecordsByRaceId(Integer raceId) {
        return dsl.select().
                from(EXOTICS)
                .where(EXOTICS.RACE_ID.eq(raceId))
                .fetch();
    }

    public ExoticPayoffPool getExoticPayoffPoolEntity(Record exoticPayoffPoolRecord) {
        Double unit = exoticPayoffPoolRecord.getValue(EXOTICS.UNIT, Double.class);
        String name = exoticPayoffPoolRecord.getValue(EXOTICS.NAME, String.class);
        String winningNumbers =
                exoticPayoffPoolRecord.getValue(EXOTICS.WINNING_NUMBERS, String.class);
        Integer numberCorrect =
                exoticPayoffPoolRecord.getValue(EXOTICS.NUMBER_CORRECT, Integer.class);
        Double payoff = exoticPayoffPoolRecord.getValue(EXOTICS.PAYOFF, Double.class);
        Double pool = exoticPayoffPoolRecord.getValue(EXOTICS.POOL, Double.class);
        Double carryover = exoticPayoffPoolRecord.getValue(EXOTICS.CARRYOVER, Double.class);
        return new ExoticPayoffPool(unit, payoff, name, winningNumbers, numberCorrect, pool,
                carryover);
    }

    public ExoticsRecord createExoticPayoffPool(RacesRecord racesRecord,
            ExoticPayoffPool exoticPayoffPool) {
        if (exoticPayoffPool == null) {
            // TODO
            throw new RuntimeException("null exoticPayoffPool");
        }

        InsertSetMoreStep<ExoticsRecord> moreStep = dsl.insertInto(EXOTICS)
                .set(EXOTICS.RACE_ID, racesRecord.getId());

        exoticPayoffPool(exoticPayoffPool, moreStep);

        ExoticsRecord exoticsRecord = moreStep.returning(EXOTICS.ID)
                .fetchOne();

        return exoticsRecord;
    }

    private InsertSetMoreStep<ExoticsRecord> exoticPayoffPool(
            ExoticPayoffPool exoticPayoffPool, InsertSetMoreStep<ExoticsRecord> moreStep) {
        moreStep.set(EXOTICS.UNIT, exoticPayoffPool.getUnit());
        moreStep.set(EXOTICS.NAME, exoticPayoffPool.getName());
        moreStep.set(EXOTICS.WINNING_NUMBERS, exoticPayoffPool.getWinningNumbers());
        moreStep.set(EXOTICS.NUMBER_CORRECT, exoticPayoffPool.getNumberCorrect());
        moreStep.set(EXOTICS.PAYOFF, exoticPayoffPool.getPayoff());
        moreStep.set(EXOTICS.ODDS, exoticPayoffPool.getOdds());
        moreStep.set(EXOTICS.POOL, exoticPayoffPool.getPool());
        moreStep.set(EXOTICS.CARRYOVER, exoticPayoffPool.getCarryover());
        return moreStep;
    }
}
