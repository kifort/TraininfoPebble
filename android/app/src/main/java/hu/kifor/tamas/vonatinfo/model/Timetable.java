package hu.kifor.tamas.vonatinfo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamas on 15. 01. 19..
 */
public class Timetable {
    private String departure;
    private String destination;
    private final List<Trip> trips = new ArrayList<Trip>();

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<Trip> getTrips() {
        return trips;
    }
}
