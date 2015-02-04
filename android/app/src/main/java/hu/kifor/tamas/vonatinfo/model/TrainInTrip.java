package hu.kifor.tamas.vonatinfo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamas on 15. 01. 19..
 */
public class TrainInTrip {
    private final List<StationInTrip> stationsTrainInTrip = new ArrayList<StationInTrip>();
    private String note;
    private Train train;

    public List<StationInTrip> getStationsTrainInTrip() {
        return stationsTrainInTrip;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }
}
