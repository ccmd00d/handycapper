package com.robinhowlett.handycapper.controllers;

import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.charts.pdf.RaceTypeNameBlackTypeBreed;
import com.robinhowlett.chartparser.tracks.Track;
import com.robinhowlett.chartparser.tracks.TrackService;
import com.robinhowlett.handycapper.HandycapperApplication;
import com.robinhowlett.handycapper.components.AgeSlider;
import com.robinhowlett.handycapper.components.DistanceSlider;
import com.robinhowlett.handycapper.components.RunnersSlider;
import com.robinhowlett.handycapper.domain.tables.records.RacesRecord;
import com.robinhowlett.handycapper.dtos.SearchParams;
import com.robinhowlett.handycapper.models.QuerySummary;
import com.robinhowlett.handycapper.models.RaceSummary;
import com.robinhowlett.handycapper.models.Summary;
import com.robinhowlett.handycapper.services.RaceService;
import com.robinhowlett.handycapper.services.excel.ExcelService;
import com.robinhowlett.handycapper.views.ResultView;
import com.sun.javafx.application.HostServicesDelegate;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SegmentedButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.felixroske.jfxsupport.FXMLController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import static de.felixroske.jfxsupport.GUIState.getStage;

@FXMLController
public class DashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);
    private static final Pattern TRACK_CODE_PATTERN = Pattern.compile("([A-Z]+) - .*");

    private List<RaceSummary> raceSummaries = new ArrayList<>();
    @Autowired
    private RaceService raceService;
    @Autowired
    private TrackService trackService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private ChartParser chartParser;
    @Autowired
    private FileChooser fileChooser;
    @Autowired
    private HostServicesDelegate hostServices;
    @Autowired
    private ResultView resultView;
    // Main
    @FXML
    private Hyperlink subscribeLink;
    @FXML
    private Button chartBtn;
    @FXML
    private Button exportExcelBtn;
    @FXML
    private TableView<RaceSummary> racesTable;
    @FXML
    private TableColumn<RaceSummary, String> colDate;
    @FXML
    private TableColumn<RaceSummary, String> colTrack;
    @FXML
    private TableColumn<RaceSummary, String> colNum;
    @FXML
    private TableColumn<RaceSummary, String> colType;
    @FXML
    private TableColumn<RaceSummary, String> colName;
    @FXML
    private TableColumn<RaceSummary, String> colSurf;
    @FXML
    private TableColumn<RaceSummary, String> colDist;
    @FXML
    private TableColumn<RaceSummary, String> colRnrs;
    @FXML
    private TableColumn<RaceSummary, String> colTime;
    @FXML
    private TableColumn<RaceSummary, String> colWin;
    @FXML
    private TableColumn<RaceSummary, String> colJock;
    @FXML
    private TableColumn<RaceSummary, String> colTrnr;
    // Find Charts
    @FXML
    private ComboBox<Track> trackCombo;
    @FXML
    private DatePicker datePicker;
    @FXML
    private AnchorPane qAnchorDist;
    @FXML
    private AnchorPane qAnchorSex;
    @FXML
    private AnchorPane qAnchorAge;
    @FXML
    private AnchorPane qAnchorRnrs;
    @FXML
    private AnchorPane qAnchorSurf;
    @FXML
    private AnchorPane qAnchorType;
    @FXML
    private Button findBtn;
    @FXML
    private TableView<QuerySummary> queryTable;
    @FXML
    private TableColumn<RaceSummary, String> colQueryDate;
    @FXML
    private TableColumn<RaceSummary, String> colQueryTrack;
    @FXML
    private TableColumn<RaceSummary, String> colQueryNum;
    @FXML
    private TableColumn<RaceSummary, String> colQueryType;
    @FXML
    private TableColumn<RaceSummary, String> colQueryName;
    @FXML
    private TableColumn<RaceSummary, String> colQuerySurf;
    @FXML
    private TableColumn<RaceSummary, String> colQueryDist;
    @FXML
    private TableColumn<RaceSummary, String> colQueryRnrs;
    @FXML
    private TableColumn<RaceSummary, String> colQueryTime;
    @FXML
    private TableColumn<RaceSummary, String> colQueryWin;
    @FXML
    private TableColumn<RaceSummary, String> colQueryJock;
    @FXML
    private TableColumn<RaceSummary, String> colQueryTrnr;

    // components that may be reset
    private DistanceSlider distRangeSlider;
    private AgeSlider ageRangeSlider;
    private RunnersSlider runnersRangeSlider;
    private ToggleButton allSexes;
    private String selectedSex;
    private ToggleButton allSurfaces;
    private String selectedSurface;
    private CheckComboBox<String> raceTypes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        subscribeLink.setOnAction(event ->
                hostServices.showDocument("https://mailchi.mp/aba2a859dd29/subscribe-for-updates"));

        colDate.setCellValueFactory(new PropertyValueFactory<>("colDate"));
        colTrack.setCellValueFactory(new PropertyValueFactory<>("colTrack"));
        colNum.setCellValueFactory(new PropertyValueFactory<>("colNum"));
        colType.setCellValueFactory(new PropertyValueFactory<>("colType"));
        colName.setCellValueFactory(new PropertyValueFactory<>("colName"));
        colSurf.setCellValueFactory(new PropertyValueFactory<>("colSurf"));
        colDist.setCellValueFactory(new PropertyValueFactory<>("colDist"));
        colRnrs.setCellValueFactory(new PropertyValueFactory<>("colRnrs"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("colTime"));
        colWin.setCellValueFactory(new PropertyValueFactory<>("colWin"));
        colJock.setCellValueFactory(new PropertyValueFactory<>("colJock"));
        colTrnr.setCellValueFactory(new PropertyValueFactory<>("colTrnr"));

        colQueryDate.setCellValueFactory(new PropertyValueFactory<>("colQueryDate"));
        colQueryTrack.setCellValueFactory(new PropertyValueFactory<>("colQueryTrack"));
        colQueryNum.setCellValueFactory(new PropertyValueFactory<>("colQueryNum"));
        colQueryType.setCellValueFactory(new PropertyValueFactory<>("colQueryType"));
        colQueryName.setCellValueFactory(new PropertyValueFactory<>("colQueryName"));
        colQuerySurf.setCellValueFactory(new PropertyValueFactory<>("colQuerySurf"));
        colQueryDist.setCellValueFactory(new PropertyValueFactory<>("colQueryDist"));
        colQueryRnrs.setCellValueFactory(new PropertyValueFactory<>("colQueryRnrs"));
        colQueryTime.setCellValueFactory(new PropertyValueFactory<>("colQueryTime"));
        colQueryWin.setCellValueFactory(new PropertyValueFactory<>("colQueryWin"));
        colQueryJock.setCellValueFactory(new PropertyValueFactory<>("colQueryJock"));
        colQueryTrnr.setCellValueFactory(new PropertyValueFactory<>("colQueryTrnr"));

        // seed with latest races
        refreshTable();

        // multiple rows may be selected
        racesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // double-click on row will open race view
        racesTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                RaceSummary raceSummary = racesTable.getSelectionModel().getSelectedItem();

                ((ResultController) resultView.getPresenter()).loadRace(raceSummary);

                Stage newStage = new Stage();

                // start maximized; done this way to prevent window size shifting
                // at startup; see https://stackoverflow.com/a/41625296
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                newStage.setWidth(bounds.getWidth());
                newStage.setHeight(bounds.getHeight());

                Scene newScene;
                if (resultView.getView().getScene() != null) {
                    // This view was already shown so
                    // we have a scene for it and use this one.
                    newScene = resultView.getView().getScene();
                } else {
                    newScene = new Scene(resultView.getView());
                }

                newStage.setScene(newScene);
                newStage.initModality(Modality.NONE);
                newStage.initOwner(getStage());
                newStage.setTitle(String.format("%s %s R%d", raceSummary.getRaceDate(),
                        raceSummary.getTrackCode(), raceSummary.getRaceNumber()));

                newStage.showAndWait();
            }
        });

        // find charts: track picker combo box
        List<Track> chartTracks = trackService.getTracks().stream()
                .filter(track -> track.getCountry().equals("USA") ||
                        track.getCountry().equals("CAN") || track.getCountry().equals("PR"))
                .collect(Collectors.toList());
        trackCombo.setItems(FXCollections.observableArrayList(chartTracks));
        trackCombo.setCellFactory(
                new Callback<ListView<Track>, ListCell<Track>>() {
                    @Override
                    public ListCell<Track> call(ListView<Track> param) {
                        final ListCell<Track> cell = new ListCell<Track>() {
                            @Override
                            public void updateItem(Track track, boolean empty) {
                                super.updateItem(track, empty);
                                if (track != null) {
                                    setText(track.getCode() + " - " + track.getName());
                                } else {
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                });
        trackCombo.setConverter(new StringConverter<Track>() {
            @Override
            public String toString(Track track) {
                return (track != null ? track.getCode() + " - " + track.getName() : "");
            }

            @Override
            public Track fromString(String trackInfo) {
                Matcher matcher = TRACK_CODE_PATTERN.matcher(trackInfo);
                if (matcher.find()) {
                    return trackService.getTrack(matcher.group(1)).orElse(null);
                } else {
                    return null;
                }
            }
        });

        // find charts: date picker
        datePicker.setShowWeekNumbers(true);

        // find charts: distance slider
        distRangeSlider = new DistanceSlider();
        qAnchorDist.getChildren().add(distRangeSlider);

        // find charts: sex segmented button
        allSexes = new ToggleButton("All");
        allSexes.setSelected(true);
        ToggleButton femaleOnly = new ToggleButton("Fillies/Mares");
        SegmentedButton sexes = new SegmentedButton();
        sexes.getButtons().addAll(allSexes, femaleOnly);
        sexes.getToggleGroup().selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedSex = ((ToggleButton) newValue.getToggleGroup().getSelectedToggle())
                            .getText();
                });
        qAnchorSex.getChildren().add(sexes);

        // find charts: age slider
        ageRangeSlider = new AgeSlider();
        qAnchorAge.getChildren().add(ageRangeSlider);

        // find charts: runners slider
        runnersRangeSlider = new RunnersSlider();
        qAnchorRnrs.getChildren().add(runnersRangeSlider);

        // find charts: surface segmented button
        allSurfaces = new ToggleButton("All");
        allSurfaces.setSelected(true);
        ToggleButton dirtOnly = new ToggleButton("Dirt");
        ToggleButton turfOnly = new ToggleButton("Turf");
        ToggleButton syntheticOnly = new ToggleButton("Synthetic");
        SegmentedButton surfaces = new SegmentedButton();
        surfaces.getButtons().addAll(allSurfaces, dirtOnly, turfOnly, syntheticOnly);
        surfaces.getToggleGroup().selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedSurface = ((ToggleButton) newValue.getToggleGroup().getSelectedToggle())
                            .getText();
                });
        qAnchorSurf.getChildren().add(surfaces);
        AnchorPane.setTopAnchor(surfaces, 0d);
        AnchorPane.setBottomAnchor(surfaces, 0d);
        AnchorPane.setRightAnchor(surfaces, 0d);
        AnchorPane.setLeftAnchor(surfaces, 0d);

        // find charts: surface segmented button
        raceTypes = new CheckComboBox<>();
        raceTypes.getItems().addAll(
                RaceTypeNameBlackTypeBreed.RACE_TYPE_CODES.keySet().stream()
                        .sorted()
                        .collect(Collectors.toList()));
        qAnchorType.getChildren().add(raceTypes);
        AnchorPane.setTopAnchor(raceTypes, 0d);
        AnchorPane.setBottomAnchor(raceTypes, 0d);
        AnchorPane.setRightAnchor(raceTypes, 0d);
        AnchorPane.setLeftAnchor(raceTypes, 0d);

        // allow selecting multiple rows for update
        queryTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // find charts: double-click on row to open race view
        queryTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                QuerySummary querySummary = queryTable.getSelectionModel().getSelectedItem();

                ((ResultController) resultView.getPresenter()).loadRace(querySummary);
                HandycapperApplication.showView(ResultView.class, Modality.NONE);
            }
        });
    }

    private void refreshTable() {
        raceSummaries = new ArrayList<>();

        List<RaceResult> results = raceService.getDescendingById();
        for (RaceResult result : results) {
            raceSummaries.add(RaceSummary.fromRaceResult(result));
        }
        racesTable.setItems(
                FXCollections.observableArrayList(
                        raceSummaries.subList(0, Math.min(200, raceSummaries.size()))));
    }

    @FXML
    void selectCharts(final Event event) {
        ExtensionFilter extFilter = new ExtensionFilter("Equibase Charts (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(extFilter);
        List<File> charts = fileChooser.showOpenMultipleDialog(chartBtn.getScene().getWindow());

        List<RaceSummary> persistedRaceSummaries = new ArrayList<>();

        if (charts == null) {
            return;
        }

        for (File chart : charts) {
            List<RaceResult> results = chartParser.parse(chart);
            List<RaceResult> persistedRaces = insertRaces(results);
            // update table data
            for (RaceResult persistedRace : persistedRaces) {
                persistedRaceSummaries.add(RaceSummary.fromRaceResult(persistedRace));
            }
        }

        racesTable.getItems().addAll(0, persistedRaceSummaries);
    }

    @FXML
    void exportSelectedToExcel(final Event event) throws IOException {
        exportRaces(racesTable);
    }

    @FXML
    void exportQueryResultsToExcel(final Event event) throws IOException {
        exportRaces(queryTable);
    }

    private void exportRaces(TableView<? extends Summary> summaryTable) throws IOException {
        ExtensionFilter extFilter = new ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(exportExcelBtn.getScene().getWindow());

        if (file != null) {
            ObservableList<? extends Summary> selectedItems =
                    summaryTable.getSelectionModel().getSelectedItems();

            if (selectedItems != null) {
                List<RaceResult> raceResults = selectedItems.stream()
                        .map(raceSummary -> raceService.findByTrackAndDateAndNumber(
                                raceSummary.getTrackCode(),
                                LocalDate.parse(raceSummary.getRaceDate(),
                                        DateTimeFormatter.ISO_LOCAL_DATE),
                                raceSummary.getRaceNumber()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (!raceResults.isEmpty()) {
                    XSSFWorkbook workbook = excelService.create(raceResults);
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        workbook.write(outputStream);
                    }
                    Desktop.getDesktop().open(file);
                }
            }
        }
    }

    @FXML
    void resetAll(final Event event) {
        trackCombo.getSelectionModel().clearSelection();
        datePicker.getEditor().clear();
        distRangeSlider.setLowValue(DistanceSlider.LOW_VALUE);
        distRangeSlider.setHighValue(DistanceSlider.HIGH_VALUE);
        ageRangeSlider.setLowValue(AgeSlider.LOW_VALUE);
        ageRangeSlider.setHighValue(AgeSlider.HIGH_VALUE);
        runnersRangeSlider.setLowValue(RunnersSlider.LOW_VALUE);
        runnersRangeSlider.setHighValue(RunnersSlider.HIGH_VALUE);
        allSexes.setSelected(true);
        allSurfaces.setSelected(true);
        raceTypes.getCheckModel().clearChecks();
        queryTable.getItems().clear();
        queryTable.refresh();
    }

    @FXML
    void findRaces(final Event event) {
        List<QuerySummary> persistedRaceSummaries;

        Track track = trackCombo.getValue();
        LocalDate date = datePicker.getValue();
        double lowDistance = distRangeSlider.getLowValue();
        double highDistance = distRangeSlider.getHighValue();
        double lowAge = ageRangeSlider.getLowValue();
        double highAge = ageRangeSlider.getHighValue();
        double lowRunners = runnersRangeSlider.getLowValue();
        double highRunners = runnersRangeSlider.getHighValue();
        ObservableList<String> checkedTypes = raceTypes.getCheckModel().getCheckedItems();

        SearchParams searchParams = new SearchParams(track, date, lowDistance, highDistance,
                lowAge, highAge, lowRunners, highRunners, selectedSex, selectedSurface,
                checkedTypes);

        List<RaceResult> races = raceService.find(searchParams);

        // update table data
        persistedRaceSummaries = races.stream()
                .map(QuerySummary::fromRaceResult)
                .collect(Collectors.toList());

        queryTable.setItems(FXCollections.observableArrayList(persistedRaceSummaries));
        queryTable.refresh();


    }

    private List<RaceResult> insertRaces(List<RaceResult> results) {
        List<RaceResult> persistedRaces = new ArrayList<>();
        for (RaceResult raceResult : results) {
            RacesRecord raceRecord = null;
            try {
                raceRecord = raceService.createRace(raceResult);
            } catch (DuplicateKeyException dpe) {
                LOGGER.debug(String.format("Race already exists in database. Deleting and " +
                        "re-saving: %s", raceResult.simpleSummary()));
                try {
                    raceService.deleteRace(raceResult.getTrack().getCode(),
                            raceResult.getRaceDate(), raceResult.getRaceNumber());
                    raceRecord = raceService.createRace(raceResult);
                } catch (Exception e) {
                    LOGGER.error(String.format("Unable to delete and/or re-save race: %s",
                            raceResult.simpleSummary()), e);
                }
            }
            if (raceRecord != null) {
                RaceResult result = raceService.getRaceResultEntity(raceRecord);
                persistedRaces.add(result);
            }
        }
        return persistedRaces;
    }

    @FXML
    void openConsole(final Event event) {
        hostServices.showDocument("http://localhost:8080/sql");
    }
}
