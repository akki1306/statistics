package com.solactive.statistics.service;

import com.solactive.model.Statistics;
import com.solactive.model.Tick;
import com.solactive.service.StatisticsService;
import com.solactive.service.impl.StatisticsServiceImpl;
import com.solactive.store.Store;
import com.solactive.util.TimeUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceTest {

    private StatisticsService statsService;

    private TimeUtil timeUtil;

    @Before
    public void setUp() {
        timeUtil = mock(TimeUtil.class);
        statsService = new StatisticsServiceImpl(new Store(), timeUtil);
    }

    @Test
    public void shouldBeAbleToSaveTheIncomingTickData() {
        //given
        when(timeUtil.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        Tick tick = new Tick("ORACLE", 45.0, 12345000L);

        //when
        Statistics statistics = statsService.updateStatistics(tick);

        //then
        assertThat(statistics.getCount().get(), is(1L));
        assertThat(statistics.getAvg().get(), is(45.0));
        assertThat(statistics.getSum().get(), is(45.0));
        assertThat(statistics.getMin().get(), is(45.0));
        assertThat(statistics.getMax().get(), is(45.0));
    }

    @Test
    public void shouldBeAbleToReturnUpdatedSummaryGivenThereAreAlreadyTicksInBuffer() {
        //given
        when(timeUtil.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        Tick tick1 = new Tick("IBM", 45.0, 12345000L);
        Tick tick2 = new Tick("ORACLE", 47.0, 12345000L);
        Tick tick3 = new Tick("GOOGLE", 49.0, 12345000L);

        //when
        statsService.updateStatistics(tick1);
        statsService.updateStatistics(tick2);
        Statistics statistics = statsService.updateStatistics(tick3);

        //then
        assertThat(statistics.getCount().get(), is(3L));
        assertThat(statistics.getAvg().get(), is(47.0));
        assertThat(statistics.getSum().get(), is(141.0));
        assertThat(statistics.getMin().get(), is(45.0));
        assertThat(statistics.getMax().get(), is(49.0));
    }

    @Test
    public void shouldBeAbleToReturnUpdatedSummaryIfThereAreOlderTicksThan60SecondsWhichAreToBeCleaned() {
        //given
        when(timeUtil.convertTimeInMillisToSeconds(12288000L)).thenReturn(12288L);
        when(timeUtil.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        when(timeUtil.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        when(timeUtil.currentSeconds()).thenReturn(12348L);

        Tick oldTick = new Tick("IBM", 45.0, 12288000L);
        Tick newTickOne = new Tick("ORACLE", 47.0, 12345000L);
        Tick newTickTwo = new Tick("GOOGLE", 49.0, 12345000L);

        statsService.updateStatistics(oldTick);
        statsService.updateStatistics(newTickOne);
        Statistics statistics = statsService.updateStatistics(newTickTwo);

        //when
        statsService.cleanOldStatsPerSecond();

        //then
        assertThat(statistics.getCount().get(), is(2L));
        assertThat(statistics.getAvg().get(), is(48.0));
        assertThat(statistics.getSum().get(), is(96.0));
        assertThat(statistics.getMin().get(), is(47.0));
        assertThat(statistics.getMax().get(), is(49.0));
    }

    @Test
    public void shouldBeAbleToReturnOriginalSummaryIfThereAreNoOlderTicksThan60SecondsWhichAreToBeCleaned() {
        //given
        when(timeUtil.currentMillis()).thenReturn(12348L);

        Tick oldTick = new Tick("IBM", 45.0, 12346000L);
        Tick newTickOne = new Tick("GOOGLE", 47.0, 12346000L);
        Tick newTickTwo = new Tick("MICROSOFT", 49.0, 12346000L);

        //when
        statsService.updateStatistics(oldTick);
        statsService.updateStatistics(newTickOne);
        Statistics statistics = statsService.updateStatistics(newTickTwo);
        statsService.cleanOldStatsPerSecond();

        //then
        assertThat(statistics.getCount().get(), is(3L));
        assertThat(statistics.getAvg().get(), is(47.0));
        assertThat(statistics.getSum().get(), is(141.0));
        assertThat(statistics.getMin().get(), is(45.0));
        assertThat(statistics.getMax().get(), is(49.0));
    }

    @Test
    public void shouldReturnDefaultStatsIfThereAreNoTicksInStore() {
        //when
        Statistics statsSummary = statsService.getStatistics();

        //then
        assertThat(statsSummary.getCount().get(), CoreMatchers.is(0L));
        assertThat(statsSummary.getSum().get(), CoreMatchers.is(0.0));
        assertThat(statsSummary.getAvg().get(), CoreMatchers.is(0.0));
        assertThat(statsSummary.getMax().get(), CoreMatchers.is(0.0d));
        assertThat(statsSummary.getMin().get(), CoreMatchers.is(0.0));
    }

    @Test
    public void shouldBeAbleToReturnSummaryAsPerDataFromStore() {

        when(timeUtil.convertTimeInMillisToSeconds(12346000L)).thenReturn(12288L);
        when(timeUtil.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        when(timeUtil.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);

        Tick oldTick = new Tick("IBM", 45.0, 12346000L);
        Tick newTickOne = new Tick("ORACLE", 47.0, 12345000L);
        Tick newTickTwo = new Tick("GOOGLE", 49.0, 12345000L);
        statsService.updateStatistics(oldTick);
        statsService.updateStatistics(newTickOne);
        Statistics statistics = statsService.updateStatistics(newTickTwo);

        assertThat(statistics.getCount(), is(3L));
        assertThat(statistics.getAvg(), is(47.0));
        assertThat(statistics.getSum(), is(141.0));
        assertThat(statistics.getMin(), is(45.0));
        assertThat(statistics.getMax(), is(49.0));
    }

    @Test
    public void shouldReturnStatisticsBaseOnInstrumentId() {
        //given
        when(timeUtil.convertTimeInMillisToSeconds(12348000L)).thenReturn(12348L);
        when(timeUtil.currentMillis()).thenReturn(12348L);

        Tick tick = new Tick("IBM", 45.0, 12348000L);

        //when
        statsService.updateStatistics(tick);
        Statistics statistics = statsService.getStatistics("IBM");

        //then
        assertThat(statistics.getAvg().get(), is(45.0));
        assertThat(statistics.getSum().get(), is(45.0));
        assertThat(statistics.getCount().get(), is(1L));
        assertThat(statistics.getMax().get(), is(45.0));
        assertThat(statistics.getMin().get(), is(45.0));

    }
}