package hu.kifor.tamas.vonatinfo.util;

import org.joda.time.LocalTime;

/**
 * Created by tamas on 15. 01. 29..
 */
public class RealTimeService implements TimeService {
    private static final long EXPECTED_SEARCH_TIME_IN_MILLIS = 2000L;
    private static final long MAX_SLEEP_TIME_IN_MILLIS = 1000L*60-EXPECTED_SEARCH_TIME_IN_MILLIS;

    @Override
    public void sleep(Thread thread) throws InterruptedException {
        LocalTime now = now();
        int seconds = 60-now.getSecondOfMinute();
        long timeToSleep = (1000*seconds)-now.getMillisOfSecond()-EXPECTED_SEARCH_TIME_IN_MILLIS;
        if( timeToSleep < EXPECTED_SEARCH_TIME_IN_MILLIS ) {
            timeToSleep = MAX_SLEEP_TIME_IN_MILLIS;
        }
        thread.sleep(timeToSleep);
    }

    @Override
    public void wakeUp(Thread thread) {
        thread.interrupt();
    }

    @Override
    public LocalTime now() {
        return LocalTime.now();
    }

}
