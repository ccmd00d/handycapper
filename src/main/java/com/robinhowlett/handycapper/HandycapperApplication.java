package com.robinhowlett.handycapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.tracks.TrackService;
import com.robinhowlett.handycapper.views.DashboardView;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.SplashScreen;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

@SpringBootApplication
@EnableTransactionManagement
public class HandycapperApplication extends AbstractJavaFxApplicationSupport {

    @Autowired
    public Flyway flyway;

    @Value("${handycapper.flyway.locations.override:}")
    public String flywayLocations;

    public static void main(String[] args) {
        launchApp(HandycapperApplication.class, DashboardView.class,
                new HandycapperSplashScreen(), args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // start maximized; done this way to prevent window size shifting
        // at startup; see https://stackoverflow.com/a/41625296
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        super.start(stage);
    }

    @Bean
    public ChartParser chartParser() {
        return ChartParser.create();
    }

    @Bean
    public TrackService trackService() {
        return chartParser().getTrackService();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return ChartParser.getObjectMapper();
    }

    @Bean
    public FileChooser fileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        return fileChooser;
    }

    // for launching web browser (e.g. H2 console) from app
    @Bean
    public HostServicesDelegate hostServices() {
        return HostServicesFactory.getInstance(this);
    }

    @PostConstruct
    public Flyway setLocations() {
        if (flywayLocations != null || !flywayLocations.isEmpty()) {
            flyway.setLocations(flywayLocations);
        }
        return flyway;
    }

    private static class HandycapperSplashScreen extends SplashScreen {
        @Override
        public String getImagePath() {
            return "/handycapper-logo.png";
        }
    }
}
