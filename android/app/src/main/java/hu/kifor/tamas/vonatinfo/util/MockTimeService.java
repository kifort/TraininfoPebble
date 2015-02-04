package hu.kifor.tamas.vonatinfo.util;

import org.joda.time.LocalTime;

/**
 * Created by tamas on 15. 01. 29..
 */
public class MockTimeService implements TimeService {
    private static final long SLEEP_TIME = 3 * 1000;

    private LocalTime now = LocalTime.now().minusMinutes(1);

    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(SLEEP_TIME);
    }

    @Override
    public LocalTime now() {
        now = now.plusMinutes(1);
        return now;
    }
}
