package hu.kifor.tamas.vonatinfo.android;

import android.content.Context;
import android.util.Log;

import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.HashSet;
import java.util.Set;

import hu.kifor.tamas.vonatinfo.TrainDao;
import hu.kifor.tamas.vonatinfo.TrainInfoConsumer;
import hu.kifor.tamas.vonatinfo.elvira.ElviraDao;
import hu.kifor.tamas.vonatinfo.model.Timetable;
import hu.kifor.tamas.vonatinfo.model.Trip;
import hu.kifor.tamas.vonatinfo.pebble.PebbleTrainInfoConsumer;
import hu.kifor.tamas.vonatinfo.util.RealTimeService;
import hu.kifor.tamas.vonatinfo.util.TimeService;

/**
 * Created by tamas on 15. 01. 29..
 */
public class SearchEngine {
    private static final String LOG_TAG = "SearchEngine";

    private TrainDao trainDao;

    private final Set<TrainInfoConsumer> traininfoConsumers = new HashSet<TrainInfoConsumer>();

    private Context applicationContext;

    private TimeService timeService;

    private boolean searchStopped;

    private Thread searchThread;

    public SearchEngine(Context applicationContext) {
        this.applicationContext = applicationContext;
        traininfoConsumers.add(new PebbleTrainInfoConsumer());
        this.trainDao = ElviraDao.getInstance();
        this.timeService = new RealTimeService();
        //this.trainDao = new MockElviraDao();
        //this.timeService = new MockTimeService();
        this.searchStopped = false;
    }

    public void search(String fromStation, String toStation) {
        try {
            boolean trainIsComing = true;
            Trip nextTrip = null;

            while (trainIsComing && !searchStopped) {
                if (!isFirstSearch(nextTrip)) {
                    searchThread = Thread.currentThread();
                    try {
                        timeService.sleep(searchThread);
                    } catch (InterruptedException InterruptedException) {
                    }
                }

                if(searchStopped) {
                    break;
                }

                Timetable timetable = trainDao.search(fromStation, toStation);
                if (isNoMoreTrain(timetable)) {
                    for(TrainInfoConsumer traininfoConsumer : traininfoConsumers) {
                        traininfoConsumer.noMoreTrainToday(applicationContext);
                    }
                    trainIsComing = false;
                } else if (isFirstSearch(nextTrip)) {
                    nextTrip = timetable.getTrips().get(0);
                } else if (isTrainPassed(nextTrip, timetable)) {
                    for(TrainInfoConsumer traininfoConsumer : traininfoConsumers) {
                        traininfoConsumer.bye(applicationContext);
                    }
                    trainIsComing = false;
                }

                if (trainIsComing) {
                    LocalTime nextTime = timetable.getTrips().get(0).getExpectedArrivalAndDeparture().getDeparture();
                    LocalTime now = timeService.now();
                    nextTime = round(now, nextTime);
                    Period timeTillNextTrain = new Period(now, nextTime, PeriodType.time());
                    for(TrainInfoConsumer traininfoConsumer : traininfoConsumers) {
                        traininfoConsumer.updateTime(applicationContext, timeTillNextTrain);
                    }
                }
            }
            Log.i(LOG_TAG, "search stopped");
        } catch (Throwable throwable) {
            Log.e(LOG_TAG, "Unable to retrieve train information.", throwable);
        }
    }

    public void stopSearch() {
        this.searchStopped = true;
        if(searchThread != null) {
            timeService.wakeUp(searchThread);
        } else {
            Log.d(LOG_TAG, "no search thread to be stopped");
        }
    }

    private LocalTime round(LocalTime now, LocalTime nextTime) {
        LocalTime roundedTime = nextTime;
        //we should round at 30, but we do it only at 50 for psychological reasons
        if(now.getSecondOfMinute() < 50) {
            roundedTime = nextTime.plusMinutes(1);
        }
        return roundedTime;
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
