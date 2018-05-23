package com.robinhowlett.handycapper.dtos;

import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.charts.pdf.Starter;

import java.util.List;

public class RacePP {

    private final Starter starter;
    private final List<RaceResult> races;

    public RacePP(Starter starter, List<RaceResult> races) {
        this.starter = starter;
        this.races = races;
    }

    public Starter getStarter() {
        return starter;
    }

    public List<RaceResult> getRaces() {
        return races;
    }

    @Override
    public String toString() {
        return "RacePP{" +
                "starter=" + starter +
                ", races=" + races +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RacePP pp = (RacePP) o;

        if (starter != null ? !starter.equals(pp.starter) : pp.starter != null) return false;
        return races != null ? races.equals(pp.races) : pp.races == null;
    }

    @Override
    public int hashCode() {
        int result = starter != null ? starter.hashCode() : 0;
        result = 31 * result + (races != null ? races.hashCode() : 0);
        return result;
    }
}
