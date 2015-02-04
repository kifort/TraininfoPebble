package hu.kifor.tamas.vonatinfo.pebble;

import android.content.Context;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import hu.kifor.tamas.vonatinfo.TrainInfoConsumer;

/**
 * Created by tamas on 15. 01. 29..
 */
public class PebbleTrainInfoConsumer implements TrainInfoConsumer {
    private static final String LOG_TAG = "PebbleTrainInfoConsumer";

    private static final PeriodFormatter PERIOD_FORMATTER = new PeriodFormatterBuilder()
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .toFormatter();

    @Override
    public void updateTime(Context context, Period timeTillNextTrain) {
        String timeRemaining = PERIOD_FORMATTER.print(timeTillNextTrain);
        sendDataToPebble(context, PebbleConstants.TIME_UPDATE, timeRemaining, "TIME_UPDATE: " + timeRemaining);
    }

    @Override
    public void bye(Context context) {
        sendDataToPebble(context, PebbleConstants.BYE, "A vonat elment", "BYE: A vonat elment");
    }

    @Override
    public void noMoreTrainToday(Context context) {
        sendDataToPebble(context, PebbleConstants.TIME_UPDATE, "Ma már nincs több vonat", "TIME_UPDATE: Ma már nincs több vonat");
    }

    private void sendDataToPebble(Context context, int key, String message, String log) {
        Log.d(LOG_TAG, log);
        PebbleDictionary data = new PebbleDictionary();
        data.addString(key, message);
        PebbleKit.sendDataToPebble(context, PebbleConstants.WATCHAPP_UUID, data);
    }
}
