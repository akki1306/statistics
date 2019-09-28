package com.solactive.statistics.service;

import com.solactive.model.Statistics;
import com.solactive.model.Tick;
import com.solactive.service.StatisticsService;
import com.solactive.service.impl.StatisticsServiceImpl;
import com.solactive.store.Store;
import com.solactive.util.DateTimeUtil;
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

    private DateTimeUtil dateTimeUtil;

    @Before
    public void setUp() {
        dateTimeUtil = mock(DateTimeUtil.class);
        statsService = new StatisticsServiceImpl(new Store(), dateTimeUtil);
    }

    @Test
    public void shouldBeAbleToSaveTheIncomingTransactionData() {

        long timestamp = System.currentTimeMillis();
        when(dateTimeUtil.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);

        Tick tick = new Tick("ORACLE", 45.0, 12345000L);
        Statistics statistics = statsService.updateStatistics(tick);


        assertThat(statistics.getCount().get(), is(1L));
        assertThat(statistics.getAvg().get(), is(45.0));
        assertThat(statistics.getSum().get(), is(45.0));
        assertThat(statistics.getMin().get(), is(45.0));
        assertThat(statistics.getMax().get(), is(45.0));
    }

    @Test
    public void shouldBeAbleToReturnUpdatedSummaryGivenThereAreAlreadyTransactionsInBuffer() {
        long timestamp = System.currentTimeMillis();

        when(dateTimeUtil.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);

        Tick tick1 = new Tick("IBM", 45.0, 12345000L);
        Tick tick2 = new Tick("ORACLE", 47.0, 12345000L);
        Tick tick3 = new Tick("GOOGLE", 49.0, 12345000L);
        statsService.updateStatistics(tick1);
        statsService.updateStatistics(tick2);
        Statistics statistics = statsService.updateStatistics(tick3);

        assertThat(statistics.getCount().get(), is(3L));
        assertThat(statistics.getAvg().get(), is(47.0));
        assertThat(statistics.getSum().get(), is(141.0));
        assertThat(statistics.getMin().get(), is(45.0));
        assertThat(statistics.getMax().get(), is(49.0));
    }

    @Test
    public void shouldBeAbleToReturnUpdatedSummaryIfThereAreOlderTransactionsThan60SecondsWhichAreToBeCleaned() {
        when(dateTimeUtil.convertTimeInMillisToSeconds(12288000L)).thenReturn(12288L);
        when(dateTimeUtil.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        when(dateTimeUtil.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        when(dateTimeUtil.currentSeconds()).thenReturn(12348L);


        Tick oldTransaction = new Tick("IBM", 45.0, 12288000L);
        Tick newTransactionOne = new Tick("ORACLE", 47.0, 12345000L);
        Tick newTransactionTwo = new Tick("GOOGLE", 49.0, 12345000L);
        statsService.updateStatistics(oldTransaction);
        statsService.updateStatistics(newTransactionOne);
        Statistics statistics = statsService.updateStatistics(newTransactionTwo);

        statsService.cleanOldStatsPerSecond();

        assertThat(statistics.getCount().get(), is(2L));
        assertThat(statistics.getAvg().get(), is(48.0));
        assertThat(statistics.getSum().get(), is(96.0));
        assertThat(statistics.getMin().get(), is(47.0));
        assertThat(statistics.getMax().get(), is(49.0));
    }

    @Test
    public void shouldBeAbleToReturnOriginalSummaryIfThereAreNoOlderTransactionsThan60SecondsWhichAreToBeCleaned() {
        when(dateTimeUtil.currentSeconds()).thenReturn(12348L);

        Tick oldTransaction = new Tick("IBM", 45.0, 12346000L);
        Tick newTransactionOne = new Tick("GOOGLE", 47.0, 12346000L);
        Tick newTransactionTwo = new Tick("MICROSOFT", 49.0, 12346000L);
        statsService.updateStatistics(oldTransaction);
        statsService.updateStatistics(newTransactionOne);
        Statistics statistics = statsService.updateStatistics(newTransactionTwo);

        statsService.cleanOldStatsPerSecond();

        assertThat(statistics.getCount().get(), is(3L));
        assertThat(statistics.getAvg().get(), is(47.0));
        assertThat(statistics.getSum().get(), is(141.0));
        assertThat(statistics.getMin().get(), is(45.0));
        assertThat(statistics.getMax().get(), is(49.0));
    }

    @Test
    public void shouldReturnDefaultStatsIfThereAreNoTransactionsInStore() {
        Statistics statsSummary = statsService.getStatistics();

        assertThat(statsSummary.getCount().get(), CoreMatchers.is(0L));
        assertThat(statsSummary.getSum().get(), CoreMatchers.is(0.0));
        assertThat(statsSummary.getAvg().get(), CoreMatchers.is(0.0));
        assertThat(statsSummary.getMax().get(), CoreMatchers.is(0.0d));
        assertThat(statsSummary.getMin().get(), CoreMatchers.is(0.0));
    }
}