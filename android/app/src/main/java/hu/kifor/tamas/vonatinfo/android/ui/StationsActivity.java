package hu.kifor.tamas.vonatinfo.android.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hu.kifor.tamas.vonatinfo.FavouriteStationsDao;
import hu.kifor.tamas.vonatinfo.R;
import hu.kifor.tamas.vonatinfo.TrainDao;
import hu.kifor.tamas.vonatinfo.android.AsyncStationListBuilder;
import hu.kifor.tamas.vonatinfo.android.db.DatabaseFavouriteStationsDao;
import hu.kifor.tamas.vonatinfo.elvira.ElviraDao;

public class StationsActivity extends ActionBarActivity {
    private static final String LOG_TAG = "StationsActivity";

    private final TrainDao trainDao = ElviraDao.getInstance();

    private FavouriteStationsDao favouriteStationsDao;

    private List<AutoCompleteTextView> favouriteStations;

    private TextWatcher stationSelectionListener;

    public TrainDao getTrainDao() {
        return this.trainDao;
    }

    public FavouriteStationsDao getFavouriteStationsDao() {
        return favouriteStationsDao;
    }

    public List<AutoCompleteTextView> getFavouriteStations() {
        return favouriteStations;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favouriteStationsDao = new DatabaseFavouriteStationsDao(this);
        stationSelectionListener = new StationSelectionListener(this);
        setContentView(R.layout.activity_stations);

        favouriteStations = new ArrayList<>();
        favouriteStations.add((AutoCompleteTextView) findViewById(R.id.favouriteStation1));
        favouriteStations.add((AutoCompleteTextView) findViewById(R.id.favouriteStation2));
        favouriteStations.add((AutoCompleteTextView) findViewById(R.id.favouriteStation3));
        favouriteStations.add((AutoCompleteTextView) findViewById(R.id.favouriteStation4));
        favouriteStations.add((AutoCompleteTextView) findViewById(R.id.favouriteStation5));

        disableStations();
        refreshStations();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stations, menu);
        List<String> stations = favouriteStationsDao.getStations();
        for(int i = 0; i < stations.size(); i++) {
            favouriteStations.get(i).setText(stations.get(i));
        }

        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

    public void stationsRefreshed() {
        enableStations();
    }

    public void failedToRefreshStations(Throwable throwable) {
        Log.e(LOG_TAG, "Nem sikerült lekérdezni az állomások listáját", throwable);
        showErrorMessage(R.string.error_message_failed_to_query_stations);
    }

    public boolean validateFavouriteStations() {
        for(AutoCompleteTextView favouriteStation : favouriteStations) {
            if(!isValid(favouriteStation)) {
                favouriteStation.invalidate();
                return false;
            } else {
                //favouriteStation.valid
            }
        }
        return true;
    }

    public List<String> getFavouriteStationNames() {
        List<String> favouriteStationNames = new ArrayList<>();
        for(AutoCompleteTextView favouriteStation : favouriteStations) {
            favouriteStationNames.add(favouriteStation.getText().toString());
        }
        return favouriteStationNames;
    }

    private void disableStations() {
        for(AutoCompleteTextView favouriteStation : favouriteStations) {
            disableStation(favouriteStation);
        }
    }

    private void disableStation(AutoCompleteTextView favouriteStation) {
        favouriteStation.setEnabled(false);
    }

    private void enableStations() {
        int numberOfFavouriteStations = 0;
        for(AutoCompleteTextView favouriteStation : favouriteStations) {
            enableStation(favouriteStation);
            if(isSet(favouriteStation) && isValid(favouriteStation)) {
                numberOfFavouriteStations++;
            }
        }
        showUsageHint(numberOfFavouriteStations);
    }

    public void showUsageHint(int numberOfFavouriteStations) {
        if(numberOfFavouriteStations <2) {
            showInfoMessage(R.string.ask_message_set_favourite_station);
            checkMandatoryStation(favouriteStations.get(0));
            checkMandatoryStation(favouriteStations.get(1));
        } else {
            showInfoMessage(R.string.ask_message_start_search);
        }
    }

    private void checkMandatoryStation(AutoCompleteTextView favouriteStation) {
        if(!isSet(favouriteStation)) {
            favouriteStation.setError(getResources().getString(R.string.error_message_mandatory_station));
        }
    }

    private void enableStation(AutoCompleteTextView favouriteStation) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.list_item, trainDao.getAllStations());
        favouriteStation.setAdapter(adapter);
        favouriteStation.setEnabled(true);
        favouriteStation.addTextChangedListener(stationSelectionListener);
    }

    public boolean isValid(AutoCompleteTextView favouriteStation) {
        boolean isValid = true;
        if(isSet(favouriteStation)) {
            String stationName = favouriteStation.getText().toString().trim();
            isValid = trainDao.getAllStations().contains(stationName);
            if(!isValid) {
                favouriteStation.setError(getResources().getString(R.string.error_message_unknown_station));
            } else {
                favouriteStation.setError(null);
            }
        }
        return isValid;
    }

    public boolean isSet(AutoCompleteTextView favouriteStation) {
        return favouriteStation.getText().toString().trim().length() > 0;
    }

    private void refreshStations() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new AsyncStationListBuilder(this).execute();
        } else {
            showErrorMessage(R.string.error_message_failed_to_connect_internet);
        }
    }

    private void showErrorMessage(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_LONG).show();
    }

    private void showInfoMessage(int messageId) {
        TextView messageTextView = (TextView)findViewById(R.id.messageTextView);
        messageTextView.setText(messageId);
    }
}
