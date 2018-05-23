package com.robinhowlett.handycapper.models;

import com.robinhowlett.chartparser.charts.pdf.Starter;
import com.robinhowlett.chartparser.charts.pdf.running_line.LastRaced;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition
        .LengthsAhead;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition
        .TotalLengthsBehind;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PerfSummary {
    private ObjectProperty<LastRaced> colLastRaced;
    private ObjectProperty<String> colPgm;
    private ObjectProperty<String> colHorse;
    private ObjectProperty<Integer> colWgt;
    private ObjectProperty<String> colME;
    private ObjectProperty<Integer> colPp;
    private ObjectProperty<String> colJockey;
    private ObjectProperty<String> colTrainer;
    private ObjectProperty<Double> colOdds;
    private ObjectProperty<Integer> colChoice;
    private ObjectProperty<Integer> colPos;
    private ObjectProperty<String> colAhead;
    private ObjectProperty<Double> colBehind;
    private ObjectProperty<Integer> colPrice;
    private ObjectProperty<Boolean> colClaimed;

    public static PerfSummary fromStarter(Starter starter) {
        PerfSummary perfSummary = new PerfSummary();

        perfSummary.setColLastRaced(starter.getLastRaced());
        perfSummary.setColPgm(starter.getProgram());
        perfSummary.setColHorse(starter.isDisqualified() ?
                "DQ-".concat(starter.getHorse().getName()) : starter.getHorse().getName());
        perfSummary.setColWgt(starter.getWeight() != null ?
                starter.getWeight().getWeightCarried() : null);
        perfSummary.setColME(starter.getMedicationEquipment() != null ?
                starter.getMedicationEquipment().getText() : "");
        perfSummary.setColPp(starter.getPostPosition());
        perfSummary.setColJockey(starter.getJockey().getName() +
                ((starter.getWeight() != null && starter.getWeight().getJockeyAllowance() > 0) ?
                        " (" + starter.getWeight().getJockeyAllowance() + ")" : ""));
        perfSummary.setColTrainer(starter.getTrainer().getName());
        perfSummary.setColOdds(starter.getOdds());
        perfSummary.setColChoice(starter.getChoice());
        perfSummary.setColPos(starter.getOfficialPosition());

        PointOfCall finishPointOfCall = starter.getFinishPointOfCall();
        if (finishPointOfCall != null) {
            RelativePosition relativePosition = finishPointOfCall.getRelativePosition();
            if (relativePosition != null) {
                LengthsAhead lengthsAhead = relativePosition.getLengthsAhead();
                if (lengthsAhead != null) {
                    perfSummary.setColAhead(lengthsAhead.getText());
                }

                TotalLengthsBehind lengthsBehind =
                        relativePosition.getTotalLengthsBehind();

                if (lengthsBehind != null) {
                    perfSummary.setColBehind(lengthsBehind.getLengths());
                }
            }
        }

        perfSummary.setColPrice(starter.getClaim() != null ? starter.getClaim().getPrice() : null);
        perfSummary.setColClaimed(
                starter.getClaim() != null ? starter.getClaim().isClaimed() : null);

        return perfSummary;
    }

    public ObjectProperty<LastRaced> lastRacedProperty() {
        if (colLastRaced == null) {
            colLastRaced = new SimpleObjectProperty<>(this, "colLastRaced");
        }
        return colLastRaced;
    }

    public ObjectProperty<String> program() {
        if (colPgm == null) {
            colPgm = new SimpleObjectProperty<>(this, "colPgm");
        }
        return colPgm;
    }

    public ObjectProperty<String> horse() {
        if (colHorse == null) {
            colHorse = new SimpleObjectProperty<>(this, "colHorse");
        }
        return colHorse;
    }

    public ObjectProperty<Integer> weight() {
        if (colWgt == null) {
            colWgt = new SimpleObjectProperty<>(this, "colWgt");
        }
        return colWgt;
    }

    public ObjectProperty<String> medEquip() {
        if (colME == null) {
            colME = new SimpleObjectProperty<>(this, "colME");
        }
        return colME;
    }

    public ObjectProperty<Integer> postPosition() {
        if (colPp == null) {
            colPp = new SimpleObjectProperty<>(this, "colPp");
        }
        return colPp;
    }

    public ObjectProperty<String> jockey() {
        if (colJockey == null) {
            colJockey = new SimpleObjectProperty<>(this, "colJockey");
        }
        return colJockey;
    }

    public ObjectProperty<String> trainer() {
        if (colTrainer == null) {
            colTrainer = new SimpleObjectProperty<>(this, "colTrainer");
        }
        return colTrainer;
    }

    public ObjectProperty<Double> odds() {
        if (colOdds == null) {
            colOdds = new SimpleObjectProperty<>(this, "colOdds");
        }
        return colOdds;
    }

    public ObjectProperty<Integer> choice() {
        if (colChoice == null) {
            colChoice = new SimpleObjectProperty<>(this, "colChoice");
        }
        return colChoice;
    }

    public ObjectProperty<Integer> position() {
        if (colPos == null) {
            colPos = new SimpleObjectProperty<>(this, "colPos");
        }
        return colPos;
    }

    public ObjectProperty<String> ahead() {
        if (colAhead == null) {
            colAhead = new SimpleObjectProperty<String>(this, "colAhead");
        }
        return colAhead;
    }

    public ObjectProperty<Double> behind() {
        if (colBehind == null) {
            colBehind = new SimpleObjectProperty<>(this, "colBehind");
        }
        return colBehind;
    }

    public ObjectProperty<Integer> price() {
        if (colPrice == null) {
            colPrice = new SimpleObjectProperty<>(this, "colPrice");
        }
        return colPrice;
    }

    public ObjectProperty<Boolean> claimed() {
        if (colClaimed == null) {
            colClaimed = new SimpleObjectProperty<>(this, "colClaimed");
        }
        return colClaimed;
    }

    public LastRaced getColLastRaced() {
        return lastRacedProperty().get();
    }

    public void setColLastRaced(LastRaced colLastRaced) {
        if (colLastRaced != null) {
            lastRacedProperty().set(colLastRaced);
        }
    }

    public String getColPgm() {
        return program().get();
    }

    public void setColPgm(String colPgm) {
        if (colPgm != null) {
            program().set(colPgm);
        }
    }

    public String getColHorse() {
        return horse().get();
    }

    public void setColHorse(String colHorse) {
        if (colHorse != null) {
            horse().set(colHorse);
        }
    }

    public Integer getColWgt() {
        return weight().get();
    }

    public void setColWgt(Integer colWgt) {
        if (colWgt != null) {
            weight().set(colWgt);
        }
    }

    public String getColME() {
        return medEquip().get();
    }

    public void setColME(String colME) {
        if (colME != null) {
            medEquip().set(colME);
        }
    }

    public Integer getColPp() {
        return postPosition().get();
    }

    public void setColPp(Integer colPp) {
        if (colPp != null) {
            postPosition().set(colPp);
        }
    }

    public String getColJockey() {
        return jockey().get();
    }

    public void setColJockey(String colJockey) {
        if (colJockey != null) {
            jockey().set(colJockey);
        }
    }

    public String getColTrainer() {
        return trainer().get();
    }

    public void setColTrainer(String colTrainer) {
        if (colTrainer != null) {
            trainer().set(colTrainer);
        }
    }

    public Double getColOdds() {
        return odds().get();
    }

    public void setColOdds(Double colOdds) {
        if (colOdds != null) {
            odds().set(colOdds);
        }
    }

    public Integer getColChoice() {
        return choice().get();
    }

    public void setColChoice(Integer colChoice) {
        if (colChoice != null) {
            choice().set(colChoice);
        }
    }

    public Integer getColPos() {
        return position().get();
    }

    public void setColPos(Integer colPos) {
        if (colPos != null) {
            position().set(colPos);
        }
    }

    public String getColAhead() {
        return ahead().get();
    }

    public void setColAhead(String colAhead) {
        if (colAhead != null) {
            ahead().set(colAhead);
        }
    }

    public Double getColBehind() {
        return behind().get();
    }

    public void setColBehind(Double colBehind) {
        if (colBehind != null) {
            behind().set(colBehind);
        }
    }

    public Integer getColPrice() {
        return price().get();
    }

    public void setColPrice(Integer colPrice) {
        if (colPrice != null) {
            price().set(colPrice);
        }
    }

    public Boolean getColClaimed() {
        return claimed().get();
    }

    public void setColClaimed(Boolean colClaimed) {
        if (colClaimed != null) {
            claimed().set(colClaimed);
        }
    }
}
