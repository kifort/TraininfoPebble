package hu.kifor.tamas.vonatinfo.elvira;

import org.joda.time.LocalTime;

import hu.kifor.tamas.vonatinfo.TrainDao;
import hu.kifor.tamas.vonatinfo.model.ArrivalAndDeparture;
import hu.kifor.tamas.vonatinfo.model.Timetable;
import hu.kifor.tamas.vonatinfo.model.TrainInTrip;
import hu.kifor.tamas.vonatinfo.model.Trip;

/**
 * Created by tamas on 15. 01. 29..
 */
public class MockElviraDao implements TrainDao {
    private static final int MAX_NEXT_DEPARTURE = 3;
    private static final int DELAY = 2;
    private int nextDeparture = 0;
    private Timetable timetable;

    @Override
    public Timetable search(String fromStation, String toStation) {
        if(nextDeparture == 0) {
            nextDeparture = MAX_NEXT_DEPARTURE;

            timetable = new Timetable();
            Trip trip = new Trip();
            TrainInTrip trainTrip = new TrainInTrip();
            LocalTime departure = LocalTime.now().plusMinutes(MAX_NEXT_DEPARTURE);
            LocalTime arrival = departure.plusMinutes(MAX_NEXT_DEPARTURE);
            ArrivalAndDeparture officialArrivalAndDeparture = new ArrivalAndDeparture();
            officialArrivalAndDeparture.setDeparture(departure);
            officialArrivalAndDeparture.setArrival(arrival);
            trip.setOfficialArrivalAndDeparture(officialArrivalAndDeparture);
            ArrivalAndDeparture expectedArrivalAndDeparture = new ArrivalAndDeparture();
            expectedArrivalAndDeparture.setDeparture(departure.plusMinutes(DELAY));
            expectedArrivalAndDeparture.setArrival(arrival.plusMinutes(DELAY));
            trip.setExpectedArrivalAndDeparture(expectedArrivalAndDeparture);
            trip.getTrains().add(trainTrip);
            timetable.getTrips().add(trip);
        }
        nextDeparture--;

        return timetable;
    }
}
