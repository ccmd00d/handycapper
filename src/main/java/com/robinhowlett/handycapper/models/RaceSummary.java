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

public class RaceSummary implements Summary {
    private ObjectProperty<String> colDate;
    private ObjectProperty<String> colTrack;
    private ObjectProperty<Integer> colNum;
    private ObjectProperty<String> colType;
    private ObjectProperty<String> colName;
    private ObjectProperty<String> colSurf;
    private ObjectProperty<String> colDist;
    private ObjectProperty<Integer> colRnrs;
    private ObjectProperty<String> colTime;
    private ObjectProperty<String> colWin;
    private ObjectProperty<String> colJock;
    private ObjectProperty<String> colTrnr;

    public static RaceSummary fromRaceResult(RaceResult raceResult) {
        RaceSummary raceSummary = new RaceSummary();

        raceSummary.setColDate(raceResult.getRaceDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        raceSummary.setColTrack(raceResult.getTrack().getCode());
        raceSummary.setColNum(raceResult.getRaceNumber());

        if (!raceResult.getCancellation().isCancelled()) {
            RaceConditions conditions = raceResult.getRaceConditions();

            if (conditions != null) {
                RaceTypeNameBlackTypeBreed typeBreed = conditions.getRaceTypeNameBlackTypeBreed();
                if (typeBreed != null) {
                    raceSummary.setColName(typeBreed.getName());
                }

                raceSummary.setColType(conditions.getSummary());
            }

            DistanceSurfaceTrackRecord distanceSurfaceTrackRecord =
                    raceResult.getDistanceSurfaceTrackRecord();
            if (distanceSurfaceTrackRecord != null) {
                raceSummary.setColSurf(distanceSurfaceTrackRecord.getSurface());

                DistanceSurfaceTrackRecord.RaceDistance raceDistance =
                        distanceSurfaceTrackRecord.getRaceDistance();
                if (raceDistance != null) {
                    raceSummary.setColDist(raceDistance.getCompact());
                }
            }

            raceSummary.setColRnrs(raceResult.getNumberOfRunners());
            raceSummary.setColTime(raceResult.getFinalTime());

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
            raceSummary.setColWin(raceResult.getWinners().size() > 1 ?
                    "DH-".concat(winners.toString()) : winners.toString());

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
            raceSummary.setColJock(raceResult.getWinners().size() > 1 ?
                    "DH-".concat(jockeys.toString()) : jockeys.toString());

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
            raceSummary.setColTrnr(raceResult.getWinners().size() > 1 ?
                    "DH-".concat(trainers.toString()) : trainers.toString());
        }

        return raceSummary;
    }

    public ObjectProperty<String> dateProperty() {
        if (colDate == null) {
            colDate = new SimpleObjectProperty<>(this, "colDate");
        }
        return colDate;
    }

    public ObjectProperty<String> trackProperty() {
        if (colTrack == null) {
            colTrack = new SimpleObjectProperty<>(this, "colTrack");
        }
        return colTrack;
    }

    public ObjectProperty<Integer> raceNumberProperty() {
        if (colNum == null) {
            colNum = new SimpleObjectProperty<Integer>(this, "colNum");
        }
        return colNum;
    }

    public ObjectProperty<String> surfaceProperty() {
        if (colSurf == null) {
            colSurf = new SimpleObjectProperty<>(this, "colSurf");
        }
        return colSurf;
    }

    public ObjectProperty<String> distanceProperty() {
        if (colDist == null) {
            colDist = new SimpleObjectProperty<>(this, "colDist");
        }
        return colDist;
    }

    public ObjectProperty<String> typeProperty() {
        if (colType == null) {
            colType = new SimpleObjectProperty<>(this, "colType");
        }
        return colType;
    }

    public ObjectProperty<String> nameProperty() {
        if (colName == null) {
            colName = new SimpleObjectProperty<>(this, "colName");
        }
        return colName;
    }

    public ObjectProperty<Integer> numRunnersProperty() {
        if (colRnrs == null) {
            colRnrs = new SimpleObjectProperty<Integer>(this, "colRnrs");
        }
        return colRnrs;
    }

    public ObjectProperty<String> finalTimeProperty() {
        if (colTime == null) {
            colTime = new SimpleObjectProperty<>(this, "colTime");
        }
        return colTime;
    }

    public ObjectProperty<String> winnerProperty() {
        if (colWin == null) {
            colWin = new SimpleObjectProperty<>(this, "colWin");
        }
        return colWin;
    }

    public ObjectProperty<String> jockeyProperty() {
        if (colJock == null) {
            colJock = new SimpleObjectProperty<>(this, "colJock");
        }
        return colJock;
    }

    public ObjectProperty<String> trainerProperty() {
        if (colTrnr == null) {
            colTrnr = new SimpleObjectProperty<>(this, "colTrnr");
        }
        return colTrnr;
    }

    public String getColDate() {
        return dateProperty().get();
    }

    public void setColDate(String colDate) {
        dateProperty().set(colDate);
    }

    public String getColTrack() {
        return trackProperty().get();
    }

    public void setColTrack(String colTrack) {
        trackProperty().set(colTrack);
    }

    public Integer getColNum() {
        return raceNumberProperty().get();
    }

    public void setColNum(Integer colNum) {
        if (colNum != null) {
            raceNumberProperty().set(colNum);
        }
    }

    public String getColSurf() {
        return surfaceProperty().get();
    }

    public void setColSurf(String colSurf) {
        if (colSurf != null) {
            surfaceProperty().set(colSurf);
        }
    }

    public String getColDist() {
        return distanceProperty().get();
    }

    public void setColDist(String colDist) {
        if (colDist != null) {
            distanceProperty().set(colDist);
        }
    }

    public String getColType() {
        return typeProperty().get();
    }

    public void setColType(String colType) {
        if (colType != null) {
            typeProperty().set(colType);
        }
    }

    public String getColName() {
        return nameProperty().get();
    }

    public void setColName(String colName) {
        if (colName != null) {
            nameProperty().set(colName);
        }
    }

    public Integer getColRnrs() {
        return numRunnersProperty().get();
    }

    public void setColRnrs(Integer colRnrs) {
        if (colRnrs != null) {
            numRunnersProperty().set(colRnrs);
        }
    }

    public String getColTime() {
        return finalTimeProperty().get();
    }

    public void setColTime(String colTime) {
        if (colTime != null) {
            finalTimeProperty().set(colTime);
        }
    }

    public String getColWin() {
        return winnerProperty().get();
    }

    public void setColWin(String colWin) {
        if (colWin != null) {
            winnerProperty().set(colWin);
        }
    }

    public String getColJock() {
        return jockeyProperty().get();
    }

    public void setColJock(String colJock) {
        if (colJock != null) {
            jockeyProperty().set(colJock);
        }
    }

    public String getColTrnr() {
        return trainerProperty().get();
    }

    public void setColTrnr(String colTrnr) {
        if (colTrnr != null) {
            trainerProperty().set(colTrnr);
        }
    }

    @Override
    public String toString() {
        return "RaceSummary{" +
                "colDate=" + colDate.get() +
                ", colTrack=" + colTrack.get() +
                ", colNum=" + colNum.get() +
                ", colType=" + colType.get() +
                ", colName=" + colName.get() +
                ", colSurf=" + colSurf.get() +
                ", colDist=" + colDist.get() +
                ", colRnrs=" + colRnrs.get() +
                ", colTime=" + colTime.get() +
                ", colWin=" + colWin.get() +
                ", colJock=" + colJock.get() +
                ", colTrnr=" + colTrnr.get() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RaceSummary that = (RaceSummary) o;
        return Objects.equals(colDate, that.colDate) &&
                Objects.equals(colTrack, that.colTrack) &&
                Objects.equals(colNum, that.colNum) &&
                Objects.equals(colType, that.colType) &&
                Objects.equals(colName, that.colName) &&
                Objects.equals(colSurf, that.colSurf) &&
                Objects.equals(colDist, that.colDist) &&
                Objects.equals(colRnrs, that.colRnrs) &&
                Objects.equals(colTime, that.colTime) &&
                Objects.equals(colWin, that.colWin) &&
                Objects.equals(colJock, that.colJock) &&
                Objects.equals(colTrnr, that.colTrnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colDate, colTrack, colNum, colType, colName, colSurf, colDist,
                colRnrs, colTime, colWin, colJock, colTrnr);
    }

    @Override
    public String getTrackCode() {
        return getColTrack();
    }

    @Override
    public String getRaceDate() {
        return getColDate();
    }

    @Override
    public Integer getRaceNumber() {
        return getColNum();
    }
}
