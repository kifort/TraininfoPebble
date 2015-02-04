package hu.kifor.tamas.vonatinfo;

import hu.kifor.tamas.vonatinfo.model.Timetable;

/**
 * Created by tamas on 15. 01. 19..
 */
public interface TrainDao {
    Timetable search(String departure, String destination);
}
