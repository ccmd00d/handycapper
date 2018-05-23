package com.robinhowlett.handycapper.models;

import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Fractional;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Split;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TimesSummary {
    private ObjectProperty<String> colTimesDist;
    private ObjectProperty<String> colTimesTime;
    private ObjectProperty<String> colTimesSplit;
    private ObjectProperty<Double> colTimesMph;

    public static List<TimesSummary> fromRaceResult(RaceResult raceResult) {
        List<TimesSummary> timesSummaries = new ArrayList<>();

        List<Fractional> raceFractionals = raceResult.getFractionals();
        List<Split> raceSplits = raceResult.getSplits();
        for (int i = 0; i < raceFractionals.size(); i++) {
            TimesSummary timesSummary = new TimesSummary();

            Fractional fractional = raceFractionals.get(i);
            if (fractional != null) {
                timesSummary.setColTimesDist(fractional.getCompact());
                timesSummary.setColTimesTime(fractional.getTime());

                // calculate miles per hour from feet per millisecond
                // ((feet / (ms / 1000)) / 5280) * 3600
                double mph =
                        ((double) (fractional.getFeet() / (fractional.getMillis() / 1000)) // fps
                                / 5280) // mile
                                * 3600; // hour

                timesSummary.setColTimesMph(mph);
            }

            try {
                Split split = raceSplits.get(i);
                if (split != null) {
                    timesSummary.setColTimesSplit(split.getTime());
                }
            } catch (IndexOutOfBoundsException e) {
                // move right along
            }

            timesSummaries.add(timesSummary);
        }

        return timesSummaries;
    }

    public ObjectProperty<String> dist() {
        if (colTimesDist == null) {
            colTimesDist = new SimpleObjectProperty<>(this, "colTimesDist");
        }
        return colTimesDist;
    }

    public ObjectProperty<String> time() {
        if (colTimesTime == null) {
            colTimesTime = new SimpleObjectProperty<>(this, "colTimesTime");
        }
        return colTimesTime;
    }

    public ObjectProperty<String> split() {
        if (colTimesSplit == null) {
            colTimesSplit = new SimpleObjectProperty<>(this, "colTimesSplit");
        }
        return colTimesSplit;
    }

    public ObjectProperty<Double> mph() {
        if (colTimesMph == null) {
            colTimesMph = new SimpleObjectProperty<>(this, "colTimesMph");
        }
        return colTimesMph;
    }

    public String getColTimesTime() {
        return time().get();
    }

    public void setColTimesTime(String colTimesTime) {
        if (colTimesTime != null) {
            time().set(colTimesTime);
        }
    }

    public String getColTimesSplit() {
        return split().get();
    }

    public void setColTimesSplit(String colTimesSplit) {
        if (colTimesSplit != null) {
            split().set(colTimesSplit);
        }
    }

    public String getColTimesDist() {
        return dist().get();
    }

    public void setColTimesDist(String colTimesDist) {
        if (colTimesDist != null) {
            dist().set(colTimesDist);
        }
    }

    public Double getColTimesMph() {
        return mph().get();
    }

    public void setColTimesMph(Double colTimesMph) {
        if (colTimesMph != null) {
            mph().set(colTimesMph);
        }
    }
}
