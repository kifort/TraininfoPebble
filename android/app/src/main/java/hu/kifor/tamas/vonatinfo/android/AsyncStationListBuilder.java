package hu.kifor.tamas.vonatinfo.android;

import android.os.AsyncTask;

import hu.kifor.tamas.vonatinfo.android.ui.StationsActivity;
import hu.kifor.tamas.vonatinfo.model.Timetable;
import hu.kifor.tamas.vonatinfo.model.Trip;

/**
 * Created by tamas on 15. 01. 29..
 */
public class AsyncStationListBuilder extends AsyncTask<Void, Void, Throwable> {
    private StationsActivity stationsActivity;

    public AsyncStationListBuilder(StationsActivity stationsActivity) {
        this.stationsActivity = stationsActivity;
    }

    @Override
    protected Throwable doInBackground(Void... parameters) {
        try {
            stationsActivity.getTrainDao().refreshStations();

            return null;
        } catch (Throwable throwable) {
            return throwable;
        }
    }

    @Override
    protected void onPostExecute(Throwable throwable) {
        if(throwable == null) {
            stationsActivity.stationsRefreshed();
        } else {
            stationsActivity.failedToRefreshStations(throwable);
        }
    }
}
