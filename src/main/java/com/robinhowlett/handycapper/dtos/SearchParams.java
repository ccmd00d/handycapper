package com.robinhowlett.handycapper.dtos;

import com.robinhowlett.chartparser.tracks.Track;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class SearchParams {
    private final Track track;
    private final LocalDate date;
    private final double lowDistance;
    private final double highDistance;
    private final double lowAge;
    private final double highAge;
    private final double lowRunners;
    private final double highRunners;
    private final String sex;
    private final String surface;
    private final List<String> types;

    public SearchParams(Track track, LocalDate date, double lowDistance, double highDistance,
            double lowAge, double highAge, double lowRunners, double highRunners, String sex,
            String surface, List<String> types) {
        this.track = track;
        this.date = date;
        this.lowDistance = lowDistance;
        this.highDistance = highDistance;
        this.lowAge = lowAge;
        this.highAge = highAge;
        this.lowRunners = lowRunners;
        this.highRunners = highRunners;
        this.sex = sex;
        this.surface = surface;
        this.types = types;
    }

    public Track getTrack() {
        return track;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getLowDistance() {
        return lowDistance;
    }

    public double getHighDistance() {
        return highDistance;
    }

    public double getLowAge() {
        return lowAge;
    }

    public double getHighAge() {
        return highAge;
    }

    public double getLowRunners() {
        return lowRunners;
    }

    public double getHighRunners() {
        return highRunners;
    }

    public String getSex() {
        return sex;
    }

    public String getSurface() {
        return surface;
    }

    public List<String> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "SearchParams{" +
                "track=" + track +
                ", date=" + date +
                ", lowDistance=" + lowDistance +
                ", highDistance=" + highDistance +
                ", lowAge=" + lowAge +
                ", highAge=" + highAge +
                ", lowRunners=" + lowRunners +
                ", highRunners=" + highRunners +
                ", sex='" + sex + '\'' +
                ", surface='" + surface + '\'' +
                ", types=" + types +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchParams that = (SearchParams) o;
        return Double.compare(that.lowDistance, lowDistance) == 0 &&
                Double.compare(that.highDistance, highDistance) == 0 &&
                Double.compare(that.lowAge, lowAge) == 0 &&
                Double.compare(that.highAge, highAge) == 0 &&
                Double.compare(that.lowRunners, lowRunners) == 0 &&
                Double.compare(that.highRunners, highRunners) == 0 &&
                Objects.equals(track, that.track) &&
                Objects.equals(date, that.date) &&
                Objects.equals(sex, that.sex) &&
                Objects.equals(surface, that.surface) &&
                Objects.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(track, date, lowDistance, highDistance, lowAge, highAge, lowRunners,
                highRunners, sex, surface, types);
    }
}
