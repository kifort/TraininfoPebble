package hu.kifor.tamas.vonatinfo;

import java.util.List;

import hu.kifor.tamas.vonatinfo.model.Timetable;

/**
 * Created by tamas on 15. 01. 19..
 */
public interface TrainDao {
    Timetable search(String departure, String destination);
    void refreshStations();
    List<String> getAllStations();
    //List<String> getFavouriteStations();
    //void setFavouriteStations(List<String> favouriteStations);

}
