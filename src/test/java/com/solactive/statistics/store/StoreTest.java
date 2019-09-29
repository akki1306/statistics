package com.solactive.statistics.store;

import com.solactive.model.Statistics;
import com.solactive.store.Store;
import com.solactive.util.TimeUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StoreTest {

    private Store store;

    @Spy
    TimeUtil timeUtil;

    @Before
    public void setUp() {
        timeUtil = mock(TimeUtil.class);
        store = new Store(timeUtil);
    }


    @Test
    public void shouldSaveGivenTimeKeyAndValueAndReturnDoubleSummaryStatistics() {

        Statistics statistics = store.updateStatistics("IBM", 5678L, 78.0);
        assertThat(statistics.getAvg().get(), is(78.0));
        assertThat(statistics.getCount().get(), is(1L));
        assertThat(statistics.getSum().get(), is(78.0));
        assertThat(statistics.getMax().get(), is(78.0));
        assertThat(statistics.getMin().get(), is(78.0));

    }

    @Test
    public void shouldReturnUpdatedSummaryIfGivenTimeKeyIsAlreadyPresentInStatsBuffer() {
        Statistics statistics = store.updateStatistics("IBM", 5678L, 78.0);
        store.updateStatistics("IBM", 5678L, 79.0);
        assertThat(statistics.getAvg().get(), is(78.5));
        assertThat(statistics.getCount().get(), is(2L));
        assertThat(statistics.getSum().get(), is(157.0));
        assertThat(statistics.getMax().get(), is(79.0));
        assertThat(statistics.getMin().get(), is(78.0));
    }

    @Test
    public void shouldRemoveTheGivenTimeKeyFromTheStatsBuffer() {
        when(timeUtil.currentSeconds()).thenReturn(5678L);
        store.updateStatistics("IBM", 1234L, 7.0);
        store.updateStatistics("IBM", 5678L, 8.0);
        store.removeOldEntriesFromStore();
        assertFalse(store.get(1234L).isPresent());
        assertTrue(store.get(5678L).isPresent());

    }

}
