package hu.kifor.tamas.vonatinfo.pebble;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import hu.kifor.tamas.vonatinfo.android.AsyncSearchEngine;
import hu.kifor.tamas.vonatinfo.android.ui.TraininfoActivity;

/**
 * Created by tamas on 15. 01. 29..
 */
public class PebbleListener extends PebbleKit.PebbleDataReceiver {
    private static final String LOG_TAG = "PebbleListener";

    private static final String PEBBLE_LOG_TAG = "Pebble";

    private TraininfoActivity traininfoActivity;

    public PebbleListener(TraininfoActivity traininfoActivity) {
        super(PebbleConstants.WATCHAPP_UUID);
        this.traininfoActivity = traininfoActivity;
    }

    @Override
    public void receiveData(Context context, int transactionId, PebbleDictionary data) {
        Log.d(LOG_TAG, "Pebble message arrived");

        PebbleKit.sendAckToPebble(context, transactionId);

        if(data.getString(PebbleConstants.FROM_STATION) != null) {
            String fromStation = data.getString(PebbleConstants.FROM_STATION);
            String toStation = data.getString(PebbleConstants.TO_STATION);
            Log.d(LOG_TAG, "Search from " + fromStation + " to " + toStation);
            search(null, fromStation, toStation);
        } else if(data.getString(PebbleConstants.LOG_MESSAGE) != null) {
            Log.d(PEBBLE_LOG_TAG, data.getString(PebbleConstants.LOG_MESSAGE));
        } else if(data.getUnsignedIntegerAsLong(PebbleConstants.ACK) != null) {
            Log.w(LOG_TAG, "ACK");
        } else {
            Log.w(LOG_TAG, "Unexpected Pebble message");
        }
    }

    public void search(View view, String fromStation, String toStation) {
        ConnectivityManager connMgr = (ConnectivityManager) traininfoActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            traininfoActivity.getTitleView().setText(fromStation + " - " + toStation);
            new AsyncSearchEngine(traininfoActivity).execute(fromStation, toStation);
        } else {
            new AlertDialog.Builder(traininfoActivity)
                    .setTitle("Connection error")
                    .setMessage("Unable to connect to the Internet.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
