package hu.kifor.tamas.vonatinfo.pebble;

import java.util.UUID;

/**
 * Created by tamas on 15. 01. 29..
 */
public class PebbleConstants {
    public static final UUID WATCHAPP_UUID = UUID.fromString("3b468842-41e6-4c35-8ae3-8318cd073631");

    public static final int
            LOG_MESSAGE = 0,
            FROM_STATION = 1,
            TO_STATION = 2,
            TIME_UPDATE = 3,
            ACK = 4,
            BYE = 5,
            STOP_TIMETABLE_UPDATE = 6,
            GET_STATIONS = 7;
}
