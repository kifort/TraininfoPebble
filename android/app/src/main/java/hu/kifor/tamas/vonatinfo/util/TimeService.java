package hu.kifor.tamas.vonatinfo.util;

import org.joda.time.LocalTime;

/**
 * Created by tamas on 15. 01. 29..
 */
public interface TimeService {
    void sleep(Thread thread) throws InterruptedException;

    void wakeUp(Thread thread);

    LocalTime now();
}
