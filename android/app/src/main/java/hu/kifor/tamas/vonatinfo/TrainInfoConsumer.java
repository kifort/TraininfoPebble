package hu.kifor.tamas.vonatinfo;

import android.content.Context;

import org.joda.time.Period;

/**
 * Created by tamas on 15. 01. 29..
 */
public interface TrainInfoConsumer {
    void updateTime(Context context, Period timeTillNextTrain);

    void bye(Context context);

    void noMoreTrainToday(Context context);
}
