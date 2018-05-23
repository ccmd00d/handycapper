package com.robinhowlett.handycapper.models;

import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.ExoticPayoffPool;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ExoticsSummary {
    private ObjectProperty<Double> colExUnit;
    private ObjectProperty<String> colExType;
    private ObjectProperty<String> colExNums;
    private ObjectProperty<Integer> colExCorrect;
    private ObjectProperty<Double> colExPayoff;
    private ObjectProperty<Double> colExOdds;
    private ObjectProperty<Double> colExPool;
    private ObjectProperty<Double> colExCarry;

    public static ExoticsSummary fromExoticPayoffPool(ExoticPayoffPool exoticPayoffPool) {
        ExoticsSummary exoticsSummary = new ExoticsSummary();
        exoticsSummary.setColExUnit(exoticPayoffPool.getUnit());
        exoticsSummary.setColExType(exoticPayoffPool.getName());
        exoticsSummary.setColExNums(exoticPayoffPool.getWinningNumbers());
        exoticsSummary.setColExCorrect(exoticPayoffPool.getNumberCorrect());
        exoticsSummary.setColExPayoff(exoticPayoffPool.getPayoff());
        exoticsSummary.setColExOdds(exoticPayoffPool.getOdds());
        exoticsSummary.setColExPool(exoticPayoffPool.getPool());
        exoticsSummary.setColExCarry(exoticPayoffPool.getCarryover());
        return exoticsSummary;
    }

    public ObjectProperty<Double> unit() {
        if (colExUnit == null) {
            colExUnit = new SimpleObjectProperty<Double>(this, "colExUnit");
        }
        return colExUnit;
    }

    public ObjectProperty<String> type() {
        if (colExType == null) {
            colExType = new SimpleObjectProperty<String>(this, "colExType");
        }
        return colExType;
    }

    public ObjectProperty<String> winningNumbers() {
        if (colExNums == null) {
            colExNums = new SimpleObjectProperty<>(this, "colExNums");
        }
        return colExNums;
    }

    public ObjectProperty<Integer> correct() {
        if (colExCorrect == null) {
            colExCorrect = new SimpleObjectProperty(this, "colExCorrect");
        }
        return colExCorrect;
    }

    public ObjectProperty<Double> payoff() {
        if (colExPayoff == null) {
            colExPayoff = new SimpleObjectProperty(this, "colExPayoff");
        }
        return colExPayoff;
    }

    public ObjectProperty<Double> odds() {
        if (colExOdds == null) {
            colExOdds = new SimpleObjectProperty(this, "colExOdds");
        }
        return colExOdds;
    }

    public ObjectProperty<Double> pool() {
        if (colExPool == null) {
            colExPool = new SimpleObjectProperty(this, "colExPool");
        }
        return colExPool;
    }

    public ObjectProperty<Double> carryover() {
        if (colExCarry == null) {
            colExCarry = new SimpleObjectProperty(this, "colExCarry");
        }
        return colExCarry;
    }

    public String getColExNums() {
        return winningNumbers().get();
    }

    public void setColExNums(String colExNums) {
        if (colExNums != null) {
            winningNumbers().set(colExNums);
        }
    }

    public Integer getColExCorrect() {
        return correct().get();
    }

    public void setColExCorrect(Integer colExCorrect) {
        if (colExCorrect != null) {
            correct().set(colExCorrect);
        }
    }

    public Double getColExUnit() {
        return unit().get();
    }

    public void setColExUnit(Double colExUnit) {
        if (colExUnit != null) {
            unit().set(colExUnit);
        }
    }

    public String getColExType() {
        return type().get();
    }

    public void setColExType(String colExType) {
        if (colExType != null) {
            type().set(colExType);
        }
    }

    public Double getColExPayoff() {
        return payoff().get();
    }

    public void setColExPayoff(Double colExPayoff) {
        if (colExPayoff != null) {
            payoff().set(colExPayoff);
        }
    }

    public Double getColExOdds() {
        return odds().get();
    }

    public void setColExOdds(Double colExOdds) {
        if (colExOdds != null) {
            odds().set(colExOdds);
        }
    }

    public Double getColExPool() {
        return pool().get();
    }

    public void setColExPool(Double colExPool) {
        if (colExPool != null) {
            pool().set(colExPool);
        }
    }

    public Double getColExCarry() {
        return carryover().get();
    }

    public void setColExCarry(Double colExCarry) {
        if (colExCarry != null) {
            carryover().set(colExCarry);
        }
    }
}
