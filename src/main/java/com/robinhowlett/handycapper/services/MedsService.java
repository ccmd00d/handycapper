package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.running_line.MedicationEquipment.Medication;
import com.robinhowlett.chartparser.exceptions.ChartParserException;
import com.robinhowlett.handycapper.domain.tables.records.MedsRecord;
import com.robinhowlett.handycapper.domain.tables.records.StartersRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.Meds.MEDS;

@Service
@Transactional
public class MedsService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getMedsRecordsByStarterId(Integer starterId) {
        return dsl.select().
                from(MEDS)
                .where(MEDS.STARTER_ID.eq(starterId))
                .fetch();
    }

    public Medication getMedicationEntity(Record medRecord) {
        Character code = medRecord.getValue(MEDS.CODE, Character.class);
        Medication medication = null;
        try {
            medication = Medication.lookup(code);
        } catch (ChartParserException e) {
            // shouldn't happen
        }
        return medication;
    }

    public MedsRecord createMedication(StartersRecord startersRecord, Medication medication) {
        if (medication == null) {
            // TODO
            throw new RuntimeException("null med");
        }

        InsertSetMoreStep<MedsRecord> moreStep = dsl.insertInto(MEDS)
                .set(MEDS.STARTER_ID, startersRecord.getId());

        medication(medication, moreStep);

        MedsRecord medsRecord = moreStep.returning(MEDS.ID).fetchOne();

        return medsRecord;
    }

    private InsertSetMoreStep<MedsRecord> medication(Medication medication,
            InsertSetMoreStep<MedsRecord> moreStep) {
        moreStep.set(MEDS.CODE, String.valueOf(medication.getCode()));
        moreStep.set(MEDS.TEXT, medication.getText());
        return moreStep;
    }
}
