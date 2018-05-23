package com.robinhowlett.handycapper.services;

import com.robinhowlett.chartparser.charts.pdf.running_line.MedicationEquipment.Equipment;
import com.robinhowlett.chartparser.exceptions.ChartParserException;
import com.robinhowlett.handycapper.domain.tables.records.EquipRecord;
import com.robinhowlett.handycapper.domain.tables.records.StartersRecord;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.robinhowlett.handycapper.domain.tables.Equip.EQUIP;

@Service
@Transactional
public class EquipService {

    @Autowired
    private DSLContext dsl;

    public Result<Record> getEquipRecordsByStarterId(Integer starterId) {
        return dsl.select().
                from(EQUIP)
                .where(EQUIP.STARTER_ID.eq(starterId))
                .fetch();
    }

    public Equipment getEquipmentEntity(Record equipRecord) {
        Character code = equipRecord.getValue(EQUIP.CODE, Character.class);
        Equipment equipment = null;
        try {
            equipment = Equipment.lookup(code);
        } catch (ChartParserException e) {
            // shouldn't happen
        }
        return equipment;
    }

    public EquipRecord createEquipment(StartersRecord startersRecord, Equipment equipment) {
        if (equipment == null) {
            // TODO
            throw new RuntimeException("null equip");
        }

        InsertSetMoreStep<EquipRecord> moreStep = dsl.insertInto(EQUIP)
                .set(EQUIP.STARTER_ID, startersRecord.getId());

        equipment(equipment, moreStep);

        EquipRecord EquipRecord = moreStep.returning(EQUIP.ID).fetchOne();

        return EquipRecord;
    }

    private InsertSetMoreStep<EquipRecord> equipment(Equipment equipment,
            InsertSetMoreStep<EquipRecord> moreStep) {
        moreStep.set(EQUIP.CODE, String.valueOf(equipment.getCode()));
        moreStep.set(EQUIP.TEXT, equipment.getText());
        return moreStep;
    }
}
