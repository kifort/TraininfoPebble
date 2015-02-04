package hu.kifor.tamas.vonatinfo.model;

import org.joda.time.DateTime;

/**
 * Created by tamas on 15. 01. 19..
 */
public class StationInTrip {
    private ArrivalAndDeparture officialArrivalAndDeparture;
    private ArrivalAndDeparture expectedArrivalAndDeparture;
    private Station station;

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

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }
}
