package hu.kifor.tamas.vonatinfo;

import java.util.List;

/**
 * Created by tamas on 15. 02. 16..
 */
public interface FavouriteStationsDao {
    List<String> getStations();
    void setStations(List<String> stations);
}
