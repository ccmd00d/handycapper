package com.robinhowlett.handycapper.controllers;

import com.robinhowlett.chartparser.charts.pdf.Horse;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.tracks.Track;
import com.robinhowlett.chartparser.tracks.TrackService;
import com.robinhowlett.handycapper.components.AgeSlider;
import com.robinhowlett.handycapper.components.DistanceSlider;
import com.robinhowlett.handycapper.components.RunnersSlider;
import com.robinhowlett.handycapper.dtos.LifetimePP;
import com.robinhowlett.handycapper.dtos.SearchParams;
import com.robinhowlett.handycapper.services.CancelledRaceService;
import com.robinhowlett.handycapper.services.RaceService;
import com.robinhowlett.handycapper.services.StarterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.robinhowlett.handycapper.domain.tables.Starters.STARTERS;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
public class APIController {

    @Autowired
    private RaceService raceService;

    @Autowired
    private CancelledRaceService cancelledRaceService;

    @Autowired
    private StarterService starterService;
    @Autowired
    private TrackService trackService;

    @GetMapping("/races")
    public List<RaceResult> getRaces(
            @RequestParam(name = "track") String trackCode,
            @DateTimeFormat(iso = DATE)
            @RequestParam(name = "date") LocalDate date,
            @RequestParam(name = "num", required = false) Integer raceNumber,
            @RequestParam(name = "canc", required = false,
                    defaultValue = "false") Boolean includeCancelled) {
        List<RaceResult> raceResults = new ArrayList<>();

        if (raceNumber != null) {
            RaceResult raceResult =
                    raceService.findByTrackAndDateAndNumber(trackCode, date, raceNumber);
            if (raceResult != null) {
                raceResults.add(raceResult);
            } else if (Boolean.TRUE.equals(includeCancelled)) {
                raceResult = cancelledRaceService.findByTrackAndDateAndNumber(trackCode, date,
                        raceNumber);
                if (raceResult != null) {
                    raceResults.add(raceResult);
                }
            }
        } else {
            raceResults = raceService.findByTrackAndDate(trackCode, date);
            if (Boolean.TRUE.equals(includeCancelled)) {
                raceResults.addAll(cancelledRaceService.findByTrackAndDate(trackCode, date));
            }
        }

        return raceResults;
    }

    @GetMapping("/search/races")
    public List<RaceResult> getRaces(
            @RequestParam(name = "track", required = false) String trackCode,
            @DateTimeFormat(iso = DATE)
            @RequestParam(name = "date", required = false) LocalDate date,
            @RequestParam(name = "lowDist", required = false) Double lowDistance,
            @RequestParam(name = "highDist", required = false) Double highDistance,
            @RequestParam(name = "lowAge", required = false) Integer lowAge,
            @RequestParam(name = "highAge", required = false) Integer highAge,
            @RequestParam(name = "lowRnrs", required = false) Integer lowRunners,
            @RequestParam(name = "highRnrs", required = false) Integer highRunners,
            @RequestParam(name = "female", required = false) Boolean isFemaleOnly,
            @RequestParam(name = "surface", required = false) String surface,
            @RequestParam(name = "types", required = false) List<String> raceTypes) {
        Track track = null;
        if (trackCode != null) {
            Optional<Track> optionalTrack = trackService.getTrack(trackCode);
            if (optionalTrack.isPresent()) {
                track = optionalTrack.get();
            }
        }
        SearchParams searchParams = new SearchParams(
                track,
                date,
                (lowDistance != null ? lowDistance.doubleValue() : DistanceSlider.LOW_VALUE),
                (highDistance != null ? highDistance.doubleValue() : DistanceSlider.HIGH_VALUE),
                (lowAge != null ? lowAge.doubleValue() : AgeSlider.LOW_VALUE),
                (highAge != null ? highAge.doubleValue() : AgeSlider.HIGH_VALUE),
                (lowRunners != null ? lowRunners.doubleValue() : RunnersSlider.LOW_VALUE),
                (highRunners != null ? highRunners.doubleValue() : RunnersSlider.HIGH_VALUE),
                (isFemaleOnly != null && isFemaleOnly == Boolean.TRUE ? "Female" : "All"),
                surface,
                raceTypes);

        return raceService.find(searchParams);
    }

    @GetMapping("/search/horses")
    public List<Horse> searchHorses(
            @RequestParam(name = "name", required = false) String horseName,
            @RequestParam(name = "startsWith", required = false) String horseNameStartsWith,
            @RequestParam(name = "like", required = false) String horseNameLike) {
        if (horseNameStartsWith != null && horseNameStartsWith.length() >= 2) {
            return starterService.findByHorseName(STARTERS.HORSE.startsWith(horseNameStartsWith));
        } else if (horseNameLike != null && horseNameLike.length() >= 2) {
            return starterService.findByHorseName(STARTERS.HORSE.likeIgnoreCase(horseNameLike));
        } else {
            return starterService.findByHorseName(STARTERS.HORSE.equalIgnoreCase(horseName));
        }
    }

    @GetMapping("/pps")
    public List<LifetimePP> getPastPerformances(
            @RequestParam(name = "horses", required = false) List<String> horseNames) {
        List<LifetimePP> pps = new ArrayList<>();

        if (horseNames != null && !horseNames.isEmpty()) {
            for (String horseName : horseNames) {
                List<Horse> horses =
                        starterService.findByHorseName(STARTERS.HORSE.equalIgnoreCase(horseName));

                if (!horses.isEmpty()) {
                    Horse horse = horses.get(0);
                    List<RaceResult> races = raceService.findByStarterHorseName(horse.getName());
                    pps.add(new LifetimePP(horse, races));
                }
            }
        }

        return pps;
    }
}
