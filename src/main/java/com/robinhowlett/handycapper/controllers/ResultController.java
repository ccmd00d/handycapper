package com.robinhowlett.handycapper.controllers;

import com.robinhowlett.chartparser.charts.pdf.DistanceSurfaceTrackRecord;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.charts.pdf.Starter;
import com.robinhowlett.chartparser.charts.pdf.running_line.LastRaced;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.ExoticPayoffPool;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools.WinPlaceShowPayoffPool
        .WinPlaceShowPayoff;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Fractional;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Split;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition
        .TotalLengthsBehind;
import com.robinhowlett.handycapper.models.ExoticsSummary;
import com.robinhowlett.handycapper.models.PerfSummary;
import com.robinhowlett.handycapper.models.Summary;
import com.robinhowlett.handycapper.models.TimesSummary;
import com.robinhowlett.handycapper.models.WpsSummary;
import com.robinhowlett.handycapper.services.RaceService;
import com.robinhowlett.handycapper.services.excel.ExcelService;
import com.sun.javafx.application.HostServicesDelegate;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import de.felixroske.jfxsupport.FXMLController;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Border;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import static com.robinhowlett.chartparser.ChartParser.ordinal;

@FXMLController
public class ResultController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultController.class);
    private static final String CENTER_ALIGNED = "-fx-alignment: CENTER";
    private static final String ALIGNED_RIGHT = "-fx-alignment: CENTER-RIGHT";
    private static final DecimalFormat TWO_DECIMAL_PLACE_FORMAT = new DecimalFormat("0.00");

    @Autowired
    private HostServicesDelegate hostServices;
    @Autowired
    private RaceService raceService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private FileChooser fileChooser;

    @FXML
    private Hyperlink raceInfo;
    @FXML
    private Label condsSummary;
    @FXML
    private Label raceDist;
    @FXML
    private TextArea condsArea;
    @FXML
    private Button excelBtn;
    @FXML
    private Button csvBtn;

    @FXML
    private TableView<PerfSummary> perfTable;
    @FXML
    private TableColumn<PerfSummary, LastRaced> colLastRaced;
    @FXML
    private TableColumn<PerfSummary, String> colPgm;
    @FXML
    private TableColumn<PerfSummary, String> colHorse;
    @FXML
    private TableColumn<PerfSummary, Integer> colWgt;
    @FXML
    private TableColumn<PerfSummary, String> colME;
    @FXML
    private TableColumn<PerfSummary, Integer> colPp;
    @FXML
    private TableColumn<PerfSummary, String> colJockey;
    @FXML
    private TableColumn<PerfSummary, String> colTrainer;
    @FXML
    private TableColumn<PerfSummary, Double> colOdds;
    @FXML
    private TableColumn<PerfSummary, Integer> colPos;
    @FXML
    private TableColumn<PerfSummary, Integer> colChoice;
    @FXML
    private TableColumn<PerfSummary, String> colAhead;
    @FXML
    private TableColumn<PerfSummary, Double> colBehind;
    @FXML
    private TableColumn<PerfSummary, Integer> colPrice;
    @FXML
    private TableColumn<PerfSummary, Boolean> colClaimed;

    @FXML
    private TextArea footnotes;

    @FXML
    private TableView<TimesSummary> timesTable;
    @FXML
    private TableColumn<TimesSummary, String> colTimesDist;
    @FXML
    private TableColumn<TimesSummary, String> colTimesTime;
    @FXML
    private TableColumn<TimesSummary, String> colTimesSplit;
    @FXML
    private TableColumn<TimesSummary, Double> colTimesMph;

    @FXML
    private TableView<Starter> callsTable;
    private TableColumn<Starter, Integer> colCallsPos = new TableColumn<>("Fin");
    private TableColumn<Starter, String> colCallsPgm = new TableColumn<>("Pgm");
    private TableColumn<Starter, String> colCallsHorse = new TableColumn<>("Horse");
    private TableColumn<Starter, Integer> colCallsPp = new TableColumn<>("PP");

    @FXML
    private TableView<Starter> fractionsTable;
    private TableColumn<Starter, Integer> colFracPos = new TableColumn<>("Fin");
    private TableColumn<Starter, String> colFracPgm = new TableColumn<>("Pgm");
    private TableColumn<Starter, String> colFracHorse = new TableColumn<>("Horse");
    private TableColumn<Starter, Integer> colFracPp = new TableColumn<>("PP");

    @FXML
    private TableView<Starter> splitsTable;
    private TableColumn<Starter, Integer> colSplitPos = new TableColumn<>("Fin");
    private TableColumn<Starter, String> colSplitPgm = new TableColumn<>("Pgm");
    private TableColumn<Starter, String> colSplitHorse = new TableColumn<>("Horse");
    private TableColumn<Starter, Integer> colSplitPp = new TableColumn<>("PP");

    @FXML
    private TableView<WpsSummary> wpsTable;
    @FXML
    private TableColumn<WpsSummary, Double> colWpsUnit;
    @FXML
    private TableColumn<WpsSummary, String> colWpsType;
    @FXML
    private TableColumn<WpsSummary, String> colWpsPgm;
    @FXML
    private TableColumn<WpsSummary, String> colWpsHorse;
    @FXML
    private TableColumn<WpsSummary, Double> colWpsPayoff;
    @FXML
    private TableColumn<WpsSummary, Double> colWpsOdds;

    @FXML
    private TableView<ExoticsSummary> exoticsTable;
    @FXML
    private TableColumn<ExoticsSummary, Double> colExUnit;
    @FXML
    private TableColumn<ExoticsSummary, String> colExType;
    @FXML
    private TableColumn<ExoticsSummary, String> colExNums;
    @FXML
    private TableColumn<ExoticsSummary, Integer> colExCorrect;
    @FXML
    private TableColumn<ExoticsSummary, Double> colExPayoff;
    @FXML
    private TableColumn<ExoticsSummary, Double> colExOdds;
    @FXML
    private TableColumn<ExoticsSummary, Double> colExPool;
    @FXML
    private TableColumn<ExoticsSummary, Double> colExCarry;

    private RaceResult raceResult = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DecimalFormat decimalFormatWithGrouping = new DecimalFormat("0.00");
        decimalFormatWithGrouping.setGroupingUsed(true);
        decimalFormatWithGrouping.setGroupingSize(3);

        // result
        colPos.setCellValueFactory(new PropertyValueFactory<>("colPos"));
        colPos.setStyle(CENTER_ALIGNED);

        colPgm.setCellValueFactory(new PropertyValueFactory<>("colPgm"));
        colPgm.setStyle(CENTER_ALIGNED);

        colHorse.setCellValueFactory(new PropertyValueFactory<>("colHorse"));

        colWgt.setCellValueFactory(new PropertyValueFactory<>("colWgt"));
        colWgt.setStyle(CENTER_ALIGNED);

        colME.setCellValueFactory(new PropertyValueFactory<>("colME"));
        colME.setStyle(CENTER_ALIGNED);

        colPp.setCellValueFactory(new PropertyValueFactory<>("colPp"));
        colPp.setStyle(CENTER_ALIGNED);

        colJockey.setCellValueFactory(new PropertyValueFactory<>("colJockey"));
        colTrainer.setCellValueFactory(new PropertyValueFactory<>("colTrainer"));

        colOdds.setCellFactory(new DecimalColumnFactory<>(decimalFormatWithGrouping));
        colOdds.setCellValueFactory(new PropertyValueFactory<>("colOdds"));
        colOdds.setStyle(ALIGNED_RIGHT);

        colChoice.setCellValueFactory(new PropertyValueFactory<>("colChoice"));
        colChoice.setStyle(CENTER_ALIGNED);

        colAhead.setCellValueFactory(new PropertyValueFactory<>("colAhead"));
        colAhead.setStyle(CENTER_ALIGNED);

        colBehind.setCellFactory(new DecimalColumnFactory<>(TWO_DECIMAL_PLACE_FORMAT));
        colBehind.setCellValueFactory(new PropertyValueFactory<>("colBehind"));
        colBehind.setStyle(CENTER_ALIGNED);

        colPrice.setCellFactory(new NumberCommasColumnFactory());
        colPrice.setCellValueFactory(new PropertyValueFactory<>("colPrice"));
        colPrice.setStyle(ALIGNED_RIGHT);

        colClaimed.setCellFactory(new BooleanTrueOnlyColumnFactory<>());
        colClaimed.setCellValueFactory(new PropertyValueFactory<>("colClaimed"));
        colClaimed.setStyle(CENTER_ALIGNED);

        colLastRaced.setCellFactory(new LastRacedLink<>());
        colLastRaced.setCellValueFactory(new PropertyValueFactory<>("colLastRaced"));

        // times
        colTimesDist.setCellValueFactory(new PropertyValueFactory<>("colTimesDist"));
        colTimesDist.setStyle(CENTER_ALIGNED);

        colTimesTime.setCellValueFactory(new PropertyValueFactory<>("colTimesTime"));
        colTimesTime.setStyle(ALIGNED_RIGHT);

        colTimesSplit.setCellValueFactory(new PropertyValueFactory<>("colTimesSplit"));
        colTimesSplit.setStyle(ALIGNED_RIGHT);

        colTimesMph.setCellFactory(new DecimalColumnFactory<>(TWO_DECIMAL_PLACE_FORMAT));
        colTimesMph.setCellValueFactory(new PropertyValueFactory<>("colTimesMph"));
        colTimesMph.setStyle(ALIGNED_RIGHT);

        // calls
        colCallsPos.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getFinishPosition()));
        colCallsPos.setStyle(ALIGNED_RIGHT);
        colCallsPgm.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getProgram()));
        colCallsPgm.setStyle(ALIGNED_RIGHT);
        colCallsHorse.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getHorse().getName()));
        colCallsPp.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getPostPosition()));
        colCallsPp.setStyle(ALIGNED_RIGHT);

        // fractions
        colFracPos.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getFinishPosition()));
        colFracPos.setStyle(ALIGNED_RIGHT);
        colFracPgm.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getProgram()));
        colFracPgm.setStyle(ALIGNED_RIGHT);
        colFracHorse.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getHorse().getName()));
        colFracPp.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getPostPosition()));
        colFracPp.setStyle(ALIGNED_RIGHT);

        // splits
        colSplitPos.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getFinishPosition()));
        colSplitPos.setStyle(ALIGNED_RIGHT);
        colSplitPgm.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getProgram()));
        colSplitPgm.setStyle(ALIGNED_RIGHT);
        colSplitHorse.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getHorse().getName()));
        colSplitPp.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getPostPosition()));
        colSplitPp.setStyle(ALIGNED_RIGHT);

        // wagering: wps
        colWpsUnit.setCellFactory(new DecimalColumnFactory<>(new DecimalFormat("0.00")));
        colWpsUnit.setCellValueFactory(new PropertyValueFactory<>("colWpsUnit"));
        colWpsUnit.setStyle(ALIGNED_RIGHT);

        colWpsType.setCellValueFactory(new PropertyValueFactory<>("colWpsType"));
        colWpsType.setStyle(CENTER_ALIGNED);

        colWpsPgm.setCellValueFactory(new PropertyValueFactory<>("colWpsPgm"));
        colWpsPgm.setStyle(CENTER_ALIGNED);

        colWpsHorse.setCellValueFactory(new PropertyValueFactory<>("colWpsHorse"));

        colWpsPayoff.setCellFactory(new DecimalColumnFactory<>(decimalFormatWithGrouping));
        colWpsPayoff.setCellValueFactory(new PropertyValueFactory<>("colWpsPayoff"));
        colWpsPayoff.setStyle(ALIGNED_RIGHT);

        colWpsOdds.setCellFactory(new DecimalColumnFactory<>(decimalFormatWithGrouping));
        colWpsOdds.setCellValueFactory(new PropertyValueFactory<>("colWpsOdds"));
        colWpsOdds.setStyle(ALIGNED_RIGHT);

        // wagering: exotics
        colExUnit.setCellFactory(new DecimalColumnFactory<>(new DecimalFormat("0.00")));
        colExUnit.setCellValueFactory(new PropertyValueFactory<>("colExUnit"));
        colExUnit.setStyle(ALIGNED_RIGHT);

        colExType.setCellValueFactory(new PropertyValueFactory<>("colExType"));
        colExType.setStyle(CENTER_ALIGNED);

        colExNums.setCellValueFactory(new PropertyValueFactory<>("colExNums"));

        colExCorrect.setCellValueFactory(new PropertyValueFactory<>("colExCorrect"));
        colExCorrect.setStyle(CENTER_ALIGNED);

        colExPayoff.setCellFactory(new DecimalColumnFactory<>(decimalFormatWithGrouping));
        colExPayoff.setCellValueFactory(new PropertyValueFactory<>("colExPayoff"));
        colExPayoff.setStyle(ALIGNED_RIGHT);

        colExOdds.setCellFactory(new DecimalColumnFactory<>(decimalFormatWithGrouping));
        colExOdds.setCellValueFactory(new PropertyValueFactory<>("colExOdds"));
        colExOdds.setStyle(ALIGNED_RIGHT);

        colExPool.setCellFactory(new DecimalColumnFactory<>(decimalFormatWithGrouping));
        colExPool.setCellValueFactory(new PropertyValueFactory<>("colExPool"));
        colExPool.setStyle(ALIGNED_RIGHT);

        colExCarry.setCellFactory(new DecimalColumnFactory<>(decimalFormatWithGrouping));
        colExCarry.setCellValueFactory(new PropertyValueFactory<>("colExCarry"));
        colExCarry.setStyle(ALIGNED_RIGHT);
    }

    // reset and re-populate the various UI elements (labels, tables, etc.)
    public void loadRace(final Summary summary) {
        perfTable.getItems().clear();
        perfTable.refresh();

        timesTable.getItems().clear();
        timesTable.refresh();

        callsTable.getColumns().clear(); // these change per race
        callsTable.getItems().clear();
        callsTable.refresh();

        fractionsTable.getColumns().clear(); // these change per race
        fractionsTable.getItems().clear();
        fractionsTable.refresh();

        splitsTable.getColumns().clear(); // these change per race
        splitsTable.getItems().clear();
        splitsTable.refresh();

        wpsTable.getItems().clear();
        wpsTable.refresh();

        exoticsTable.getItems().clear();
        exoticsTable.refresh();

        raceResult = raceService.findByTrackAndDateAndNumber(
                summary.getTrackCode(),
                LocalDate.parse(summary.getRaceDate(), DateTimeFormatter.ISO_LOCAL_DATE),
                summary.getRaceNumber());

        refreshUserInterface(raceResult);
    }

    private void refreshUserInterface(RaceResult raceResult) {
        if (raceResult == null) {
            return;
        }

        raceInfo.setBorder(Border.EMPTY);
        raceInfo.setPadding(new Insets(4, 0, 4, 0));
        raceInfo.setText(raceResult.getRaceDate().toString() +
                " - " + raceResult.getTrack().getName() + (raceResult.getRaceNumber() != null ?
                " - Race " + raceResult.getRaceNumber() : ""));
        raceInfo.setOnAction(event -> {
            hostServices.showDocument(raceResult.getLink("web").isPresent() ?
                    raceResult.getLink("web").get().getHref() : null);
        });

        condsSummary.setText(raceResult.getRaceConditions() != null ?
                raceResult.getRaceConditions().getSummary() : "");

        String description = "";
        if (raceResult.getRaceConditions() != null) {
            if (raceResult.getRaceConditions().getRaceTypeNameBlackTypeBreed() != null) {
                if (raceResult.getRaceConditions().getRaceTypeNameBlackTypeBreed()
                        .getName() != null) {
                    description = raceResult.getRaceConditions().getRaceTypeNameBlackTypeBreed()
                            .getName().concat(System.lineSeparator());
                }
            }
            description = description.concat(raceResult.getRaceConditions().getText());
        }

        condsArea.setText(description);

        String raceDistText = "";
        DistanceSurfaceTrackRecord distanceSurfaceTrackRecord =
                raceResult.getDistanceSurfaceTrackRecord();
        if (distanceSurfaceTrackRecord != null) {
            DistanceSurfaceTrackRecord.RaceDistance raceDistance =
                    distanceSurfaceTrackRecord.getRaceDistance();
            if (raceDistance != null) {
                raceDistText = raceDistance.getCompact();
            }
            if (distanceSurfaceTrackRecord.getCourse() != null) {
                raceDistText = raceDistText.concat(" - " + distanceSurfaceTrackRecord.getCourse());
            }
        }
        raceDist.setText(raceDistText);

        callsTable.getColumns().add(colCallsPos);
        callsTable.getColumns().add(colCallsPgm);
        callsTable.getColumns().add(colCallsHorse);
        callsTable.getColumns().add(colCallsPp);

        fractionsTable.getColumns().add(colFracPos);
        fractionsTable.getColumns().add(colFracPgm);
        fractionsTable.getColumns().add(colFracHorse);
        fractionsTable.getColumns().add(colFracPp);

        splitsTable.getColumns().add(colSplitPos);
        splitsTable.getColumns().add(colSplitPgm);
        splitsTable.getColumns().add(colSplitHorse);
        splitsTable.getColumns().add(colSplitPp);

        List<Starter> winners = raceResult.getWinners();
        if (winners != null && !winners.isEmpty()) {
            Starter winner = winners.get(0);

            // lengths behind at each point of call
            List<PointOfCall> winnerCalls = winner.getPointsOfCall();
            for (int i = 0; i < winnerCalls.size(); i++) {
                int finalI = i;
                PointOfCall winnerPointOfCall = winnerCalls.get(i);
                if (winnerPointOfCall.getFeet() != null) {
                    TableColumn<Starter, Double> column = new TableColumn<>(
                            winnerPointOfCall.getText() +
                                    " (" + winnerPointOfCall.getCompact() + ")");
                    column.setCellFactory(new DecimalColumnFactory<>(TWO_DECIMAL_PLACE_FORMAT));
                    column.setCellValueFactory(starter -> {
                        List<PointOfCall> starterPointsOfCall =
                                starter.getValue().getPointsOfCall();

                        if (finalI < starterPointsOfCall.size()) {
                            PointOfCall pointOfCall = starterPointsOfCall.get(finalI);
                            if (pointOfCall != null) {
                                RelativePosition relativePosition =
                                        pointOfCall.getRelativePosition();
                                if (relativePosition != null) {
                                    TotalLengthsBehind totalLengthsBehind =
                                            relativePosition.getTotalLengthsBehind();
                                    if (totalLengthsBehind != null) {
                                        return new SimpleObjectProperty<>(
                                                totalLengthsBehind.getLengths());
                                    } else if (relativePosition.getPosition() == 1) {
                                        return new SimpleObjectProperty<>(0d);
                                    }
                                }
                            }
                        }

                        return null;
                    });
                    column.setStyle(ALIGNED_RIGHT);
                    callsTable.getColumns().add(column);
                }
            }

            // individual fractionals
            List<Fractional> winnerFractionals = winner.getFractionals();
            for (int i = 0; i < winnerFractionals.size(); i++) {
                int finalI = i;
                Fractional fractional = winnerFractionals.get(i);
                TableColumn<Starter, Object> column = new TableColumn<>(fractional.getText() +
                        " (" + fractional.getCompact() + ")");
                column.setCellValueFactory(starter -> {
                    List<Fractional> starterFractionals = starter.getValue().getFractionals();
                    if (finalI < starterFractionals.size()) {
                        return new ReadOnlyObjectWrapper<>(
                                starterFractionals.get(finalI).getTime());
                    } else {
                        return null;
                    }
                });
                column.setStyle(ALIGNED_RIGHT);

                fractionsTable.getColumns().add(column);
            }

            // individual splits
            List<Split> winnerSplits = winner.getSplits();
            for (int i = 0; i < winnerSplits.size(); i++) {
                int finalI = i;
                Split split = winnerSplits.get(i);
                TableColumn<Starter, Object> column = new TableColumn<>(split.getCompact());
                column.setCellValueFactory(starter -> {
                    List<Split> starterSplits = starter.getValue().getSplits();
                    if (finalI < starterSplits.size()) {
                        return new ReadOnlyObjectWrapper<>(starterSplits.get(finalI).getTime());
                    } else {
                        return null;
                    }
                });
                column.setStyle(ALIGNED_RIGHT);
                splitsTable.getColumns().add(column);
            }
        }

        List<TimesSummary> timesSummaries = TimesSummary.fromRaceResult(raceResult);
        timesTable.setItems(FXCollections.observableArrayList(timesSummaries));

        List<PerfSummary> perfSummaries = new ArrayList<>();
        for (Starter starter : raceResult.getStarters()) {
            perfSummaries.add(PerfSummary.fromStarter(starter));
            callsTable.getItems().add(starter);
            fractionsTable.getItems().add(starter);
            splitsTable.getItems().add(starter);
        }

        String notes = "";
        if (raceResult.getFootnotes() != null && !raceResult.getFootnotes().isEmpty()) {
            notes = raceResult.getFootnotes();
        }
        footnotes.setText(notes);

        perfTable.setItems(FXCollections.observableArrayList(perfSummaries));

        WagerPayoffPools wagerPayoffPools = raceResult.getWagerPayoffPools();
        if (wagerPayoffPools != null) {
            WinPlaceShowPayoffPool wpsPayoffPools = wagerPayoffPools.getWinPlaceShowPayoffPools();
            List<WpsSummary> wpsSummaries = new ArrayList<>();
            for (WinPlaceShowPayoff wpsPayoff : wpsPayoffPools.getWinPlaceShowPayoffs()) {
                wpsSummaries.addAll(WpsSummary.fromWPSPayoff(wpsPayoff));
            }
            wpsTable.setItems(FXCollections.observableArrayList(wpsSummaries));

            List<ExoticPayoffPool> exoticPayoffPools = wagerPayoffPools.getExoticPayoffPools();
            List<ExoticsSummary> exoticSummaries = new ArrayList<>();
            for (ExoticPayoffPool exoticPayoffPool : exoticPayoffPools) {
                exoticSummaries.add(ExoticsSummary.fromExoticPayoffPool(exoticPayoffPool));
            }
            exoticsTable.setItems(FXCollections.observableArrayList(exoticSummaries));
        }
    }

    @FXML
    void generateExcel(final Event event) throws IOException {
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(excelBtn.getScene().getWindow());

        if (file != null) {
            XSSFWorkbook workbook = excelService.create(Arrays.asList(raceResult));
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
            Desktop.getDesktop().open(file);
        }
    }

    class DecimalColumnFactory<S, T extends Number>
            implements Callback<TableColumn<S, T>, TableCell<S, T>> {
        private DecimalFormat format;

        public DecimalColumnFactory(DecimalFormat format) {
            super();
            this.format = format;
        }

        @Override
        public TableCell<S, T> call(TableColumn<S, T> param) {
            return new TableCell<S, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    if (!empty && item != null) {
                        setText(format.format(item.doubleValue()));
                    } else {
                        setText("");
                    }
                }
            };
        }
    }

    class NumberCommasColumnFactory<S, T extends Number>
            implements Callback<TableColumn<S, T>, TableCell<S, T>> {

        @Override
        public TableCell<S, T> call(TableColumn<S, T> param) {
            return new TableCell<S, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    if (!empty && item != null) {
                        setText(NumberFormat.getNumberInstance(Locale.US).format(item.intValue()));
                    } else {
                        setText("");
                    }
                }
            };
        }
    }

    class BooleanTrueOnlyColumnFactory<S>
            implements Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>> {

        @Override
        public TableCell<S, Boolean> call(TableColumn<S, Boolean> param) {
            return new TableCell<S, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    if (!empty && item != null && item.equals(Boolean.TRUE)) {
                        setText(item.toString().toUpperCase(Locale.US));
                    } else {
                        setText("");
                    }
                }
            };
        }
    }

    class LastRacedLink<S> implements Callback<TableColumn<S, LastRaced>, TableCell<S, LastRaced>> {
        @Override
        public TableCell<S, LastRaced> call(TableColumn<S, LastRaced> param) {
            return new TableCell<S, LastRaced>() {
                @Override
                protected void updateItem(LastRaced lastRaced, boolean empty) {
                    if (!empty && lastRaced != null) {
                        Integer officialPosition = lastRaced.getLastRacePerformance()
                                .getOfficialPosition();
                        Integer raceNumber = lastRaced.getLastRacePerformance().getRaceNumber();
                        String colLastRaced = lastRaced.getRaceDate() + " " +
                                lastRaced.getLastRacePerformance().getTrack().getCode() +
                                " R" + (raceNumber != null ? raceNumber : "X") +
                                " (" + (officialPosition != null ? ordinal(officialPosition) :
                                "-") + ")";

                        Optional<Link> webLink = lastRaced.getLink("web");

                        if (webLink.isPresent()) {
                            Hyperlink link = new Hyperlink();
                            link.setText(colLastRaced);
                            link.setTooltip(new Tooltip(
                                    lastRaced.getLastRacePerformance().getTrack().getName() +
                                            " - " + lastRaced.getDaysSince() + " days ago"));
                            link.setOnAction(event ->
                                    hostServices.showDocument(webLink.get().getHref()));
                            link.setStyle("-fx-text-fill: ladder(-fx-background, " +
                                    "-fx-light-text-color 50%, -fx-accent 51%); -fx-padding: 0 0 " +
                                    "0 0");
                            setGraphic(link);
                        } else {
                            setText(colLastRaced);
                        }
                    } else {
                        setText("");
                    }
                }
            };
        }
    }

}
