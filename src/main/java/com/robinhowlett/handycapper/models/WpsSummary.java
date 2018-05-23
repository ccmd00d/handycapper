package com.robinhowlett.handycapper.models;

import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff.WinPlaceShow;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class WpsSummary {
    private ObjectProperty<Double> colWpsUnit;
    private ObjectProperty<String> colWpsType;
    private ObjectProperty<String> colWpsPgm;
    private ObjectProperty<String> colWpsHorse;
    private ObjectProperty<Double> colWpsPayoff;
    private ObjectProperty<Double> colWpsOdds;

    public static List<WpsSummary> fromWPSPayoff(WinPlaceShowPayoff wpsPayoff) {
        List<WpsSummary> wpsSummaries = new ArrayList<>();

        for (WinPlaceShow wps : wpsPayoff.getWinPlaceShows()) {
            WpsSummary wpsSummary = new WpsSummary();
            wpsSummary.setColWpsUnit(wps.getUnit());
            wpsSummary.setColWpsType(wps.getType());
            wpsSummary.setColWpsPgm(wpsPayoff.getProgram());
            wpsSummary.setColWpsHorse(wpsPayoff.getHorse().getName());
            wpsSummary.setColWpsPayoff(wps.getPayoff());
            wpsSummary.setColWpsOdds(wps.getOdds());
            wpsSummaries.add(wpsSummary);
        }

        return wpsSummaries;
    }

    public ObjectProperty<Double> unit() {
        if (colWpsUnit == null) {
            colWpsUnit = new SimpleObjectProperty<Double>(this, "colWpsUnit");
        }
        return colWpsUnit;
    }

    public ObjectProperty<String> type() {
        if (colWpsType == null) {
            colWpsType = new SimpleObjectProperty<String>(this, "colWpsType");
        }
        return colWpsType;
    }

    public ObjectProperty<String> program() {
        if (colWpsPgm == null) {
            colWpsPgm = new SimpleObjectProperty<String>(this, "colWpsPgm");
        }
        return colWpsPgm;
    }

    public ObjectProperty<String> horse() {
        if (colWpsHorse == null) {
            colWpsHorse = new SimpleObjectProperty<String>(this, "colWpsHorse");
        }
        return colWpsHorse;
    }

    public ObjectProperty<Double> payoff() {
        if (colWpsPayoff == null) {
            colWpsPayoff = new SimpleObjectProperty<Double>(this, "colWpsPayoff");
        }
        return colWpsPayoff;
    }

    public ObjectProperty<Double> odds() {
        if (colWpsOdds == null) {
            colWpsOdds = new SimpleObjectProperty<Double>(this, "colWpsOdds");
        }
        return colWpsOdds;
    }

    public String getColWpsPgm() {
        return program().get();
    }

    public void setColWpsPgm(String colWpsPgm) {
        if (colWpsPgm != null) {
            program().set(colWpsPgm);
        }
    }

    public String getColWpsHorse() {
        return horse().get();
    }

    public void setColWpsHorse(String colWpsHorse) {
        if (colWpsHorse != null) {
            horse().set(colWpsHorse);
        }
    }

    public Double getColWpsUnit() {
        return unit().get();
    }

    public void setColWpsUnit(Double colWpsUnit) {
        if (colWpsUnit != null) {
            unit().set(colWpsUnit);
        }
    }

    public String getColWpsType() {
        return type().get();
    }

    public void setColWpsType(String colWpsType) {
        if (colWpsType != null) {
            type().set(colWpsType);
        }
    }

    public Double getColWpsPayoff() {
        return payoff().get();
    }

    public void setColWpsPayoff(Double colWpsPayoff) {
        if (colWpsPayoff != null) {
            payoff().set(colWpsPayoff);
        }
    }

    public Double getColWpsOdds() {
        return odds().get();
    }

    public void setColWpsOdds(Double colWpsOdds) {
        if (colWpsOdds != null) {
            odds().set(colWpsOdds);
        }
    }
}
