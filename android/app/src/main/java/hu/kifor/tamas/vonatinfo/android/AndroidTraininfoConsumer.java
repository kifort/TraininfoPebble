package hu.kifor.tamas.vonatinfo.android;

import android.content.Context;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import hu.kifor.tamas.vonatinfo.TrainInfoConsumer;
import hu.kifor.tamas.vonatinfo.android.ui.TraininfoActivity;
import hu.kifor.tamas.vonatinfo.pebble.PebbleConstants;

/**
 * Created by tamas on 15. 02. 01..
 */
public class AndroidTraininfoConsumer implements TrainInfoConsumer {
    private static final String LOG_TAG = "AndroidTraininfoConsumer";

    private static final PeriodFormatter PERIOD_FORMATTER = new PeriodFormatterBuilder()
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .toFormatter();

    private TraininfoActivity traininfoActivity;

    public AndroidTraininfoConsumer(TraininfoActivity traininfoActivity) {
        this.traininfoActivity = traininfoActivity;
    }

    @Override
    public void updateTime(Context context, Period timeTillNextTrain) {
        String timeRemaining = PERIOD_FORMATTER.print(timeTillNextTrain);
        traininfoActivity.getTimeView().setText(timeRemaining);
    }

    @Override
    public void bye(Context context) {
        traininfoActivity.getTitleView().setText("A vonat elment");
        traininfoActivity.getTimeView().setText("");
    }

    @Override
    public void noMoreTrainToday(Context context) {
        traininfoActivity.getTitleView().setText("Ma már nincs több vonat");
        traininfoActivity.getTimeView().setText("");
    }
}
