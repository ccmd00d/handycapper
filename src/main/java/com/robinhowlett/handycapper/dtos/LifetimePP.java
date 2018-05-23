package com.robinhowlett.handycapper.dtos;

import com.robinhowlett.chartparser.charts.pdf.Horse;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;

import java.util.List;

public class LifetimePP {

    private final Horse horse;
    private final List<RaceResult> races;

    public LifetimePP(Horse horse, List<RaceResult> races) {
        this.horse = horse;
        this.races = races;
    }

    public Horse getHorse() {
        return horse;
    }

    public List<RaceResult> getRaces() {
        return races;
    }

    @Override
    public String toString() {
        return "LifetimePP{" +
                "horse=" + horse +
                ", races=" + races +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LifetimePP pp = (LifetimePP) o;

        if (horse != null ? !horse.equals(pp.horse) : pp.horse != null) return false;
        return races != null ? races.equals(pp.races) : pp.races == null;
    }

    @Override
    public int hashCode() {
        int result = horse != null ? horse.hashCode() : 0;
        result = 31 * result + (races != null ? races.hashCode() : 0);
        return result;
    }
}
