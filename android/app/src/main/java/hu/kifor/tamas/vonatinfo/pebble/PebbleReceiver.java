package hu.kifor.tamas.vonatinfo.pebble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONException;

import java.util.List;
import java.util.UUID;

import hu.kifor.tamas.vonatinfo.FavouriteStationsDao;
import hu.kifor.tamas.vonatinfo.android.db.DatabaseFavouriteStationsDao;
import hu.kifor.tamas.vonatinfo.android.service.TraininfoService;

/**
 * Created by tamas on 15. 02. 08..
 */
public class PebbleReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "PebbleReceiver";
    private static final String PEBBLE_LOG_TAG = "Pebble";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.INTENT_APP_RECEIVE)) {
            final UUID receivedUuid = (UUID) intent.getSerializableExtra(Constants.APP_UUID);

            // Pebble-enabled apps are expected to be good citizens and only inspect broadcasts containing their UUID
            if (!PebbleConstants.WATCHAPP_UUID.equals(receivedUuid)) {
                Log.d(LOG_TAG, "not my UUID");
                return;
            }

            final int transactionId = intent.getIntExtra(Constants.TRANSACTION_ID, -1);
            final String jsonData = intent.getStringExtra(Constants.MSG_DATA);
            if (jsonData == null
                    //|| jsonData.isEmpty()
               ) {
                Log.d(LOG_TAG, "jsonData null");
                return;
            }

            try {
                final PebbleDictionary data = PebbleDictionary.fromJson(jsonData);
                // do what you need with the data
                Log.d(LOG_TAG, "Pebble message arrived");

                PebbleKit.sendAckToPebble(context, transactionId);

                if(data.getString(PebbleConstants.FROM_STATION) != null) {
                    String fromStation = data.getString(PebbleConstants.FROM_STATION);
                    String toStation = data.getString(PebbleConstants.TO_STATION);
                    Log.d(LOG_TAG, "Search from " + fromStation + " to " + toStation);
                    search(context, fromStation, toStation);
                } else if(data.getString(PebbleConstants.LOG_MESSAGE) != null) {
                    Log.d(PEBBLE_LOG_TAG, data.getString(PebbleConstants.LOG_MESSAGE));
                } else if(data.getUnsignedIntegerAsLong(PebbleConstants.ACK) != null) {
                    Log.d(LOG_TAG, "ACK");
                } else if(data.getString(PebbleConstants.GET_STATIONS) != null) {
                    Log.d(LOG_TAG, "GET_STATIONS");
                    FavouriteStationsDao favouriteStationsDao = new DatabaseFavouriteStationsDao(context);
                    List<String> favouriteStations = favouriteStationsDao.getStations();
                    while(favouriteStations.remove(""));
                    sendDataToPebble(context, PebbleConstants.GET_STATIONS, favouriteStations, "");
                } else if(data.getString(PebbleConstants.STOP_TIMETABLE_UPDATE) != null) {
                    Log.d(LOG_TAG, "STOP_TIMETABLE_UPDATE");
                    stopSearch();
                }
                else {
                    Log.w(LOG_TAG, "Unexpected Pebble message");
                }

            } catch (JSONException e) {
                Log.d(LOG_TAG, "failed reived -> dict" + e);
                return;
            }
        }
    }

    private void search(Context context, String fromStation, String toStation) {
        stopSearch();
        Intent intent = new Intent(context, TraininfoService.class);
        intent.putExtra("fromStation", fromStation);
        intent.putExtra("toStation", toStation);
        context.startService(intent);
    }

    private void stopSearch() {
        if(TraininfoService.getInstance() != null) {
            TraininfoService.getInstance().stopSearch();
        } else {
            Log.d(LOG_TAG, "no train info service to be stopped");
        }
    }

    private void sendDataToPebble(Context context, int key, String message, String log) {
        Log.d(LOG_TAG, log);
        PebbleDictionary data = new PebbleDictionary();
        data.addString(key, message);
        PebbleKit.sendDataToPebble(context, PebbleConstants.WATCHAPP_UUID, data);
    }

    private void sendDataToPebble(Context context, int key, List<String> messages, String log) {
        Log.d(LOG_TAG, log);
        PebbleDictionary data = new PebbleDictionary();
        Integer i = key+1;
        for(String message : messages) {
            data.addString(i++, message);
        }
        PebbleKit.sendDataToPebble(context, PebbleConstants.WATCHAPP_UUID, data);
    }
}
