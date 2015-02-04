package hu.kifor.tamas.vonatinfo.model;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamas on 15. 01. 19..
 */
public class Trip {
    private final List<TrainInTrip> trains = new ArrayList<TrainInTrip>();

    private Double price;
    private String currency;

    public List<TrainInTrip> getTrains() {
        return trains;
    }

    //Calculate from station info later
    private ArrivalAndDeparture officialArrivalAndDeparture;
    private ArrivalAndDeparture expectedArrivalAndDeparture;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalTime getOfficialDeparture() {
        return null;
    }

    public LocalTime getOfficialArrival() {
        return null;
    }

    public LocalTime getExpectedDeparture() {
        return null;
    }

    public LocalTime getExpectedArrival() {
        return null;
    }

    public LocalTime getExpectedDelayAtDeparture() {
        return null;
    }

    public LocalTime getExpectedDelayAtDestination() {
        return null;
    }

    public ArrivalAndDeparture getOfficialArrivalAndDeparture() {
        return officialArrivalAndDeparture;
    }

    public void setOfficialArrivalAndDeparture(ArrivalAndDeparture officialArrivalAndDeparture) {
        this.officialArrivalAndDeparture = officialArrivalAndDeparture;
    }

    public ArrivalAndDeparture getExpectedArrivalAndDeparture() {
        return expectedArrivalAndDeparture;
    }

    public void setExpectedArrivalAndDeparture(ArrivalAndDeparture expectedArrivalAndDeparture) {
        this.expectedArrivalAndDeparture = expectedArrivalAndDeparture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip)) return false;

        Trip trip = (Trip) o;

        if (!officialArrivalAndDeparture.equals(trip.officialArrivalAndDeparture)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return officialArrivalAndDeparture.hashCode();
    }
}
