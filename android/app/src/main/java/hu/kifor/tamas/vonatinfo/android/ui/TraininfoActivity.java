package hu.kifor.tamas.vonatinfo.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;

import hu.kifor.tamas.vonatinfo.R;
import hu.kifor.tamas.vonatinfo.pebble.PebbleListener;

/**
 * Created by tamas on 15. 02. 01..
 */
public class TraininfoActivity extends ActionBarActivity {
    private static final String LOG_TAG = "TraininfoActivity";

    private static final String NO_SEARCH = "Válasszon induló és célállomást az óráján";
    private static final String TRAIN_PASSED = "A keresett vonat elment.";

    private PebbleKit.PebbleDataReceiver pebbleDataReceiver = new PebbleListener(this);

//    private TextView titleView;
//    private TextView timeView;

    public TextView getTitleView() {
        return (TextView) findViewById(R.id.titleView);
    }

    public TextView getTimeView() {
        return (TextView) findViewById(R.id.timeView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traininfo);
        getTitleView().setText(NO_SEARCH);

        PebbleKit.registerReceivedDataHandler(this, pebbleDataReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
        final Activity activity = this;
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
        //unregisterReceiver(pebbleDataReceiver);
    }
}
