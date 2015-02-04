package hu.kifor.tamas.vonatinfo.model;

import org.joda.time.LocalTime;

/**
 * Created by tamas on 15. 01. 19..
 */
public class ArrivalAndDeparture {
    private LocalTime arrival;
    private LocalTime departure;

    public LocalTime getArrival() {
        return arrival;
    }

    public void setArrival(LocalTime arrival) {
        this.arrival = arrival;
    }

    public LocalTime getDeparture() {
        return departure;
    }

    public void setDeparture(LocalTime departure) {
        this.departure = departure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrivalAndDeparture)) return false;

        ArrivalAndDeparture that = (ArrivalAndDeparture) o;

        if (arrival != null ? !arrival.equals(that.arrival) : that.arrival != null) return false;
        if (departure != null ? !departure.equals(that.departure) : that.departure != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = arrival != null ? arrival.hashCode() : 0;
        result = 31 * result + (departure != null ? departure.hashCode() : 0);
        return result;
    }
}
