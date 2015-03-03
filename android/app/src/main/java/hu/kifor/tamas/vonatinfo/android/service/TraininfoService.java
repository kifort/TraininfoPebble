package hu.kifor.tamas.vonatinfo.android.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;

import hu.kifor.tamas.vonatinfo.R;
import hu.kifor.tamas.vonatinfo.android.SearchEngine;

public class TraininfoService extends IntentService {
    private static final String LOG_TAG = "TraininfoService";

    private static TraininfoService instance;

    private static SearchEngine searchEngine;

    private PebbleKit.PebbleDataReceiver pebbleDataReceiver;

    public TraininfoService() {
        super("TraininfoService");
        Log.d(LOG_TAG, "created");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "starting handle intent");

        if(instance != null || searchEngine != null) {
            searchEngine.stopSearch();
        }
        instance = this;
        searchEngine = new SearchEngine(this);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String fromStation = intent.getStringExtra("fromStation");
            String toStation = intent.getStringExtra("toStation");
            if(fromStation != null && toStation != null) {
                searchEngine.search(fromStation, toStation);
            }
        } else {
            Toast.makeText(this, "Unable to connect to the Internet.", Toast.LENGTH_SHORT).show();
        }

        instance = null;
        searchEngine = null;
        Log.d(LOG_TAG, "stopping handle intent");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, R.string.info_message_train_search_started, Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    public void stopSearch() {
        if(searchEngine != null) {
            searchEngine.stopSearch();
        } else {
            Log.d(LOG_TAG, "no search engine to be stopped");
        }
    }

    public static TraininfoService getInstance() {
        return instance;
    }
}