package com.robinhowlett.handycapper.models;

import com.robinhowlett.chartparser.charts.pdf.DistanceSurfaceTrackRecord;
import com.robinhowlett.chartparser.charts.pdf.Horse;
import com.robinhowlett.chartparser.charts.pdf.Jockey;
import com.robinhowlett.chartparser.charts.pdf.RaceConditions;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.charts.pdf.RaceTypeNameBlackTypeBreed;
import com.robinhowlett.chartparser.charts.pdf.Trainer;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.StringJoiner;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class QuerySummary implements Summary {
    private ObjectProperty<String> colQueryDate;
    private ObjectProperty<String> colQueryTrack;
    private ObjectProperty<Integer> colQueryNum;
    private ObjectProperty<String> colQueryType;
    private ObjectProperty<String> colQueryName;
    private ObjectProperty<String> colQuerySurf;
    private ObjectProperty<String> colQueryDist;
    private ObjectProperty<Integer> colQueryRnrs;
    private ObjectProperty<String> colQueryTime;
    private ObjectProperty<String> colQueryWin;
    private ObjectProperty<String> colQueryJock;
    private ObjectProperty<String> colQueryTrnr;

    public static QuerySummary fromRaceResult(RaceResult raceResult) {
        QuerySummary raceSummary = new QuerySummary();

        raceSummary.setColQueryDate(raceResult.getRaceDate().format(DateTimeFormatter
                .ISO_LOCAL_DATE));
        raceSummary.setColQueryTrack(raceResult.getTrack().getCode());
        raceSummary.setColQueryNum(raceResult.getRaceNumber());

        if (!raceResult.getCancellation().isCancelled()) {
            RaceConditions conditions = raceResult.getRaceConditions();

            if (conditions != null) {
                RaceTypeNameBlackTypeBreed typeBreed = conditions.getRaceTypeNameBlackTypeBreed();
                if (typeBreed != null) {
                    raceSummary.setColQueryName(typeBreed.getName());
                }

                raceSummary.setColQueryType(conditions.getSummary());
            }

            DistanceSurfaceTrackRecord distanceSurfaceTrackRecord =
                    raceResult.getDistanceSurfaceTrackRecord();
            if (distanceSurfaceTrackRecord != null) {
                raceSummary.setColQuerySurf(distanceSurfaceTrackRecord.getSurface());

                DistanceSurfaceTrackRecord.RaceDistance raceDistance =
                        distanceSurfaceTrackRecord.getRaceDistance();
                if (raceDistance != null) {
                    raceSummary.setColQueryDist(raceDistance.getCompact());
                }
            }

            raceSummary.setColQueryRnrs(raceResult.getNumberOfRunners());
            raceSummary.setColQueryTime(raceResult.getFinalTime());

            StringJoiner winners = new StringJoiner(",");
            raceResult.getWinners()
                    .forEach(starter -> {
                        Horse horse = starter.getHorse();
                        if (horse != null) {
                            String name = horse.getName();
                            if (name != null) {
                                winners.add(name);
                            }
                        }
                    });
            raceSummary.setColQueryWin(winners.toString());

            StringJoiner jockeys = new StringJoiner(",");
            raceResult.getWinners()
                    .forEach(starter -> {
                        Jockey jockey = starter.getJockey();
                        if (jockey != null) {
                            String name = jockey.getName();
                            if (name != null) {
                                jockeys.add(name);
                            }
                        }
                    });
            raceSummary.setColQueryJock(jockeys.toString());

            StringJoiner trainers = new StringJoiner(",");
            raceResult.getWinners()
                    .forEach(starter -> {
                        Trainer trainer = starter.getTrainer();
                        if (trainer != null) {
                            String name = trainer.getName();
                            if (name != null) {
                                trainers.add(name);
                            }
                        }
                    });
            raceSummary.setColQueryTrnr(trainers.toString());
        }

        return raceSummary;
    }

    public ObjectProperty<String> dateProperty() {
        if (colQueryDate == null) {
            colQueryDate = new SimpleObjectProperty<>(this, "colQueryDate");
        }
        return colQueryDate;
    }

    public ObjectProperty<String> trackProperty() {
        if (colQueryTrack == null) {
            colQueryTrack = new SimpleObjectProperty<>(this, "colQueryTrack");
        }
        return colQueryTrack;
    }

    public ObjectProperty<Integer> raceNumberProperty() {
        if (colQueryNum == null) {
            colQueryNum = new SimpleObjectProperty<Integer>(this, "colQueryNum");
        }
        return colQueryNum;
    }

    public ObjectProperty<String> surfaceProperty() {
        if (colQuerySurf == null) {
            colQuerySurf = new SimpleObjectProperty<>(this, "colQuerySurf");
        }
        return colQuerySurf;
    }

    public ObjectProperty<String> distanceProperty() {
        if (colQueryDist == null) {
            colQueryDist = new SimpleObjectProperty<>(this, "colQueryDist");
        }
        return colQueryDist;
    }

    public ObjectProperty<String> typeProperty() {
        if (colQueryType == null) {
            colQueryType = new SimpleObjectProperty<>(this, "colQueryType");
        }
        return colQueryType;
    }

    public ObjectProperty<String> nameProperty() {
        if (colQueryName == null) {
            colQueryName = new SimpleObjectProperty<>(this, "colQueryName");
        }
        return colQueryName;
    }

    public ObjectProperty<Integer> numRunnersProperty() {
        if (colQueryRnrs == null) {
            colQueryRnrs = new SimpleObjectProperty<Integer>(this, "colQueryRnrs");
        }
        return colQueryRnrs;
    }

    public ObjectProperty<String> finalTimeProperty() {
        if (colQueryTime == null) {
            colQueryTime = new SimpleObjectProperty<>(this, "colQueryTime");
        }
        return colQueryTime;
    }

    public ObjectProperty<String> winnerProperty() {
        if (colQueryWin == null) {
            colQueryWin = new SimpleObjectProperty<>(this, "colQueryWin");
        }
        return colQueryWin;
    }

    public ObjectProperty<String> jockeyProperty() {
        if (colQueryJock == null) {
            colQueryJock = new SimpleObjectProperty<>(this, "colQueryJock");
        }
        return colQueryJock;
    }

    public ObjectProperty<String> trainerProperty() {
        if (colQueryTrnr == null) {
            colQueryTrnr = new SimpleObjectProperty<>(this, "colQueryTrnr");
        }
        return colQueryTrnr;
    }

    public String getColQueryDate() {
        return dateProperty().get();
    }

    public void setColQueryDate(String colQueryDate) {
        dateProperty().set(colQueryDate);
    }

    public String getColQueryTrack() {
        return trackProperty().get();
    }

    public void setColQueryTrack(String colQueryTrack) {
        trackProperty().set(colQueryTrack);
    }

    public Integer getColQueryNum() {
        return raceNumberProperty().get();
    }

    public void setColQueryNum(Integer colQueryNum) {
        if (colQueryNum != null) {
            raceNumberProperty().set(colQueryNum);
        }
    }

    public String getColQuerySurf() {
        return surfaceProperty().get();
    }

    public void setColQuerySurf(String colQuerySurf) {
        if (colQuerySurf != null) {
            surfaceProperty().set(colQuerySurf);
        }
    }

    public String getColQueryDist() {
        return distanceProperty().get();
    }

    public void setColQueryDist(String colQueryDist) {
        if (colQueryDist != null) {
            distanceProperty().set(colQueryDist);
        }
    }

    public String getColQueryType() {
        return typeProperty().get();
    }

    public void setColQueryType(String colQueryType) {
        if (colQueryType != null) {
            typeProperty().set(colQueryType);
        }
    }

    public String getColQueryName() {
        return nameProperty().get();
    }

    public void setColQueryName(String colQueryName) {
        if (colQueryName != null) {
            nameProperty().set(colQueryName);
        }
    }

    public Integer getColQueryRnrs() {
        return numRunnersProperty().get();
    }

    public void setColQueryRnrs(Integer colQueryRnrs) {
        if (colQueryRnrs != null) {
            numRunnersProperty().set(colQueryRnrs);
        }
    }

    public String getColQueryTime() {
        return finalTimeProperty().get();
    }

    public void setColQueryTime(String colQueryTime) {
        if (colQueryTime != null) {
            finalTimeProperty().set(colQueryTime);
        }
    }

    public String getColQueryWin() {
        return winnerProperty().get();
    }

    public void setColQueryWin(String colQueryWin) {
        if (colQueryWin != null) {
            winnerProperty().set(colQueryWin);
        }
    }

    public String getColQueryJock() {
        return jockeyProperty().get();
    }

    public void setColQueryJock(String colQueryJock) {
        if (colQueryJock != null) {
            jockeyProperty().set(colQueryJock);
        }
    }

    public String getColQueryTrnr() {
        return trainerProperty().get();
    }

    public void setColQueryTrnr(String colQueryTrnr) {
        if (colQueryTrnr != null) {
            trainerProperty().set(colQueryTrnr);
        }
    }

    @Override
    public String toString() {
        return "RaceSummary{" +
                "colQueryDate=" + colQueryDate.get() +
                ", colQueryTrack=" + colQueryTrack.get() +
                ", colQueryNum=" + colQueryNum.get() +
                ", colQueryType=" + colQueryType.get() +
                ", colQueryName=" + colQueryName.get() +
                ", colQuerySurf=" + colQuerySurf.get() +
                ", colQueryDist=" + colQueryDist.get() +
                ", colQueryRnrs=" + colQueryRnrs.get() +
                ", colQueryTime=" + colQueryTime.get() +
                ", colQueryWin=" + colQueryWin.get() +
                ", colQueryJock=" + colQueryJock.get() +
                ", colQueryTrnr=" + colQueryTrnr.get() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuerySummary that = (QuerySummary) o;
        return Objects.equals(colQueryDate, that.colQueryDate) &&
                Objects.equals(colQueryTrack, that.colQueryTrack) &&
                Objects.equals(colQueryNum, that.colQueryNum) &&
                Objects.equals(colQueryType, that.colQueryType) &&
                Objects.equals(colQueryName, that.colQueryName) &&
                Objects.equals(colQuerySurf, that.colQuerySurf) &&
                Objects.equals(colQueryDist, that.colQueryDist) &&
                Objects.equals(colQueryRnrs, that.colQueryRnrs) &&
                Objects.equals(colQueryTime, that.colQueryTime) &&
                Objects.equals(colQueryWin, that.colQueryWin) &&
                Objects.equals(colQueryJock, that.colQueryJock) &&
                Objects.equals(colQueryTrnr, that.colQueryTrnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colQueryDate, colQueryTrack, colQueryNum, colQueryType, colQueryName,
                colQuerySurf, colQueryDist, colQueryRnrs, colQueryTime, colQueryWin, colQueryJock,
                colQueryTrnr);
    }

    @Override
    public String getTrackCode() {
        return getColQueryTrack();
    }

    @Override
    public String getRaceDate() {
        return getColQueryDate();
    }

    @Override
    public Integer getRaceNumber() {
        return getColQueryNum();
    }
}
