package com.solactive.statistics.store;

import com.solactive.model.Statistics;
import com.solactive.store.Store;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class StoreTest {
    @InjectMocks
    private Store store;

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
        store.updateStatistics("IBM", 1234L, 7.0);
        store.updateStatistics("IBM", 5678L, 8.0);
        store.removeFromStore(1234L);
        assertFalse(store.get(1234L).isPresent());
        assertTrue(store.get(5678L).isPresent());

    }

}
