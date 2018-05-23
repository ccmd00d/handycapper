package com.robinhowlett.handycapper;

import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.charts.pdf.Starter;
import com.robinhowlett.handycapper.services.RaceService;

import org.flywaydb.test.annotation.FlywayTest;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static com.robinhowlett.handycapper.domain.tables.Races.RACES;
import static com.robinhowlett.handycapper.domain.tables.Starters.STARTERS;

import static org.hamcrest.Matchers.equalTo;

/**
 * Basic sanity test of parsing, saving, and loading from an embedded test database
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HandycapperApplication.class)
@ActiveProfiles("test")
@FlywayTest
public class HandycapperApplicationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandycapperApplicationTest.class);

    @Autowired
    private DSLContext dsl;

    @Autowired
    private RaceService raceService;

    @Test
    public void testQueryingAfterMigration_InsertsAndRetrievesChartResults() throws Exception {
        // load a chart file
        File file = Paths.get(HandycapperApplicationTest.class.getClassLoader()
                .getResource("examples/ARP_2016-07-24_race-charts.pdf").toURI()).toFile();

        // convert them to races (in memory)
        List<RaceResult> raceResults = ChartParser.create().parse(file);

        // save the races to the database
        raceService.createRaces(raceResults);

        // query the database
        Result<?> result =
                dsl.select(
                        RACES.ID,
                        RACES.DATE,
                        RACES.TRACK,
                        RACES.NUMBER,
                        RACES.DISTANCE_COMPACT,
                        RACES.TYPE,
                        RACES.FINAL_TIME,
                        STARTERS.HORSE.as("WINNER")
                )
                        .from(RACES)
                        .join(STARTERS).on(RACES.ID.eq(STARTERS.RACE_ID))
                        .where(STARTERS.OFFICIAL_POSITION.eq(1))
                        .orderBy(RACES.NUMBER.asc())
                        .fetch();

        // print a summary of the day's results
        LOGGER.info(System.lineSeparator() + result.toString());

        Assert.assertEquals(9, result.size());

        Record eighthRace = result.get(7);

        Assert.assertThat(eighthRace.get("DATE").toString(), equalTo("2016-07-24"));
        Assert.assertThat(eighthRace.get("TRACK").toString(), equalTo("ARP"));
        Assert.assertThat(eighthRace.get("NUMBER").toString(), equalTo("8"));
        Assert.assertThat(eighthRace.get("DISTANCE_COMPACT").toString(), equalTo("1 1/16m"));
        Assert.assertThat(eighthRace.get("TYPE").toString(), equalTo("STAKES"));
        Assert.assertThat(eighthRace.get("FINAL_TIME").toString(), equalTo("1:48.720"));
        Assert.assertThat(eighthRace.get("WINNER").toString(), equalTo("Lady Jila"));
    }
}
