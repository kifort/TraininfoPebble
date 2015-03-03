package hu.kifor.tamas.vonatinfo.android.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;

import hu.kifor.tamas.vonatinfo.FavouriteStationsDao;
import hu.kifor.tamas.vonatinfo.android.db.DatabaseFavouriteStationsDao;

/**
 * Created by tamas on 15. 02. 16..
 */
public class StationSelectionListener implements TextWatcher {
    private StationsActivity stationsActivity;

    public StationSelectionListener(StationsActivity stationsActivity) {
        this.stationsActivity = stationsActivity;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable stationField) {
        if(stationsActivity.validateFavouriteStations()) {
            stationsActivity.getFavouriteStationsDao().setStations(stationsActivity.getFavouriteStationNames());
        }
        showUsageHint();
    }

    private void showUsageHint() {
        int numberOfFavouriteStations = 0;
        for(AutoCompleteTextView favouriteStation : stationsActivity.getFavouriteStations()) {
            if(stationsActivity.isSet(favouriteStation) && stationsActivity.isValid(favouriteStation)) {
                numberOfFavouriteStations++;
            }
        }
        stationsActivity.showUsageHint(numberOfFavouriteStations);
    }
}
