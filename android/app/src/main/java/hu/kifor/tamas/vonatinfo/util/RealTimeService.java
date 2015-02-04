package hu.kifor.tamas.vonatinfo.util;

import org.joda.time.LocalTime;

/**
 * Created by tamas on 15. 01. 29..
 */
public class RealTimeService implements TimeService {
    private static final long SLEEP_TIME = 60 * 1000;

    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(SLEEP_TIME);
    }

    @Override
    public LocalTime now() {
        return LocalTime.now();
    }
}
