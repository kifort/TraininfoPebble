package hu.kifor.tamas.vonatinfo.android;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;

import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import hu.kifor.tamas.vonatinfo.TrainDao;
import hu.kifor.tamas.vonatinfo.TrainInfoConsumer;
import hu.kifor.tamas.vonatinfo.android.ui.TraininfoActivity;
import hu.kifor.tamas.vonatinfo.elvira.ElviraDao;
import hu.kifor.tamas.vonatinfo.elvira.MockElviraDao;
import hu.kifor.tamas.vonatinfo.model.Timetable;
import hu.kifor.tamas.vonatinfo.model.Trip;
import hu.kifor.tamas.vonatinfo.pebble.PebbleTrainInfoConsumer;
import hu.kifor.tamas.vonatinfo.util.MockTimeService;
import hu.kifor.tamas.vonatinfo.util.RealTimeService;
import hu.kifor.tamas.vonatinfo.util.TimeService;

/**
 * Created by tamas on 15. 01. 29..
 */
public class AsyncSearchEngine extends AsyncTask<String, Void, String> {
    private TrainDao trainDao;

    private TrainInfoConsumer pebbleTrainInfoConsumer;
    private AndroidTraininfoConsumer androidTraininfoConsumer;

    private Context applicationContext;

    private TimeService timeService;

    public AsyncSearchEngine(TraininfoActivity traininfoActivity) {
        this.applicationContext = traininfoActivity.getApplicationContext();
        this.pebbleTrainInfoConsumer = new PebbleTrainInfoConsumer();
        this.androidTraininfoConsumer = new AndroidTraininfoConsumer(traininfoActivity);
        this.trainDao = new ElviraDao();
        this.timeService = new RealTimeService();
//        this.trainDao = new MockElviraDao();
//        this.timeService = new MockTimeService();
    }

    @Override
    protected String doInBackground(String... parameters) {
        try {
            String departure = parameters[0];
            String destination = parameters[1];

            boolean newSearchNeeded = true;
            Trip nextTrip = null;

            while (newSearchNeeded) {
                if (!isFirstSearch(nextTrip)) {
                    timeService.sleep();
                }

                Timetable timetable = trainDao.search(departure, destination);
                if (isNoMoreTrain(timetable)) {
                    pebbleTrainInfoConsumer.noMoreTrainToday(applicationContext);
                    androidTraininfoConsumer.noMoreTrainToday(applicationContext);
                    newSearchNeeded = false;
                } else if (isFirstSearch(nextTrip)) {
                    nextTrip = timetable.getTrips().get(0);
                } else if (isTrainPassed(nextTrip, timetable)) {
                    pebbleTrainInfoConsumer.bye(applicationContext);
                    androidTraininfoConsumer.bye(applicationContext);
                    newSearchNeeded = false;
                }

                if (newSearchNeeded) {
                    LocalTime nextTime = timetable.getTrips().get(0).getOfficialArrivalAndDeparture().getDeparture();
                    Period timeTillNextTrain = new Period(timeService.now(), nextTime, PeriodType.time());
                    pebbleTrainInfoConsumer.updateTime(applicationContext, timeTillNextTrain);
                    androidTraininfoConsumer.updateTime(applicationContext, timeTillNextTrain);
                }
            }

            return null;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return "Unable to retrieve train information.";
        }
    }

    @Override
    protected void onPostExecute(String result) {
//        new AlertDialog.Builder(activity)
//                .setTitle("Response")
//                .setMessage(result)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
    }

    private boolean isFirstSearch(Trip nextTrip) {
        return nextTrip == null;
    }

    private boolean isNoMoreTrain(Timetable timetable) {
        return timetable.getTrips().size() == 0;
    }

    private boolean isTrainPassed(Trip nextTrip, Timetable timetable) {
        return !nextTrip.equals(timetable.getTrips().get(0));
    }
}
