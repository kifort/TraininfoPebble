package hu.kifor.tamas.vonatinfo.elvira;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import hu.kifor.tamas.vonatinfo.TrainDao;
import hu.kifor.tamas.vonatinfo.model.ArrivalAndDeparture;
import hu.kifor.tamas.vonatinfo.model.Timetable;
import hu.kifor.tamas.vonatinfo.model.TrainInTrip;
import hu.kifor.tamas.vonatinfo.model.Trip;

/**
 * Created by tamas on 15. 01. 19..
 */
public class ElviraDao implements TrainDao {
    private static final String LOG_TAG = "ElviraDao";

    private static final String ELVIRA_HOST = "http://elvira.mav-start.hu";

    private static final String EDITION_URL = ELVIRA_HOST + "/elvira.dll/xslvzs/?language=1";

    private static final String STATIONS_URL = ELVIRA_HOST + "/elvira.js";

    private static final String SEARCH_URL = ELVIRA_HOST + "/elvira.dll/xslvzs/uf";

    private static ElviraDao instance;

    private String elviraEdition;

    private List<String> stations;

    private String today;

    private ElviraDao() {
        today = new org.joda.time.LocalDate().toString("YY.MM.dd");
    }

    @Override
    public Timetable search(String fromStation, String toStation) {
        Timetable timetable = new Timetable();

        if(elviraEdition == null) {
            setElviraEdition();
        }

        try{
            String timetableHtml = downloadTimetable(fromStation, toStation, "");

            Document doc = Jsoup.parse(timetableHtml);
            Element timetableElement = doc.getElementById("timetable");
            Elements tripElements = timetableElement.child(0).child(1).children();

            for (Element tripElement : tripElements) {
                //11 cells: contains only official departure and arrival
                //1 cell: contains the details
                Elements tripDetailElements = tripElement.children();
                if (tripDetailElements.size() > 1) {
                    continue;
                }
                tripDetailElements = tripDetailElements.get(0).getElementsByTag("tbody").get(0).children();

                Trip trip = new Trip();
                TrainInTrip trainTrip = new TrainInTrip();
                ArrivalAndDeparture officialArrivalAndDeparture = new ArrivalAndDeparture();
                ArrivalAndDeparture expectedArrivalAndDeparture = new ArrivalAndDeparture();

                LocalTime officialDeparture = getTime(tripDetailElements.get(0).children().get(1));
                LocalTime expectedDeparture = getTime(tripDetailElements.get(0).children().get(2));
                if(expectedDeparture == null) {
                    expectedDeparture = officialDeparture;
                }

                if (expectedDeparture.isAfter(LocalTime.now())) {
                    LocalTime officialArrival = getTime(tripDetailElements.last().children().get(1));
                    LocalTime expectedArrival = getTime(tripDetailElements.last().children().get(2));
                    if(expectedArrival == null) {
                        expectedArrival = officialArrival;
                    }
                    officialArrivalAndDeparture.setDeparture(officialDeparture);
                    officialArrivalAndDeparture.setArrival(officialArrival);
                    trip.setOfficialArrivalAndDeparture(officialArrivalAndDeparture);
                    expectedArrivalAndDeparture.setDeparture(expectedDeparture);
                    expectedArrivalAndDeparture.setArrival(expectedArrival);
                    trip.setExpectedArrivalAndDeparture(expectedArrivalAndDeparture);
                    trip.getTrains().add(trainTrip);
                    timetable.getTrips().add(trip);
                    if (timetable.getTrips().size() > 2) {
                        break;
                    }
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException("Unable to retrieve train information.");
        }

        return timetable;
    }

    @Override
    public void refreshStations() {
        try{
            String stationsString = downloadStations().substring(8); //cut "var mav=" to get a JSON array
            JSONArray stationsJsonArray = new JSONArray(stationsString);
            stations = new ArrayList<>(stationsJsonArray.length());
            for(int i = 0; i < stationsJsonArray.length(); i++) {
                stations.add(stationsJsonArray.getString(i));
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException("Unable to retrieve train stations.");
        }
    }

    @Override
    public List<String> getAllStations() {
        if(stations == null) {
            refreshStations();
        }
        return stations;
    }

    private LocalTime getTime(Element timeElement) {
        LocalTime resultTime = null;

        if(timeElement != null) {
            String timeAsString = timeElement.text();
            if(timeAsString != null && timeAsString.trim().length() > 0) {
                if (timeAsString.indexOf(" ") > 0) {
                    timeAsString = timeAsString.substring(0, timeAsString.indexOf(" "));
                }
                resultTime = LocalTime.parse(timeAsString);
            }
        }
        return resultTime;
    }

    private void setElviraEdition() {
        try{
            elviraEdition = downloadElviraEdition();
            elviraEdition = elviraEdition.substring(elviraEdition.indexOf("name=\"ed\""));
            elviraEdition = elviraEdition.substring(elviraEdition.indexOf("value=\"") + 7, elviraEdition.indexOf("\">"));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Log.e(LOG_TAG, "Unable to retrieve train information.", throwable);
            throw new RuntimeException("Unable to retrieve train information.");
        }
    }

    private String downloadElviraEdition() throws IOException{
        HttpClient httpclient = new DefaultHttpClient();

        HttpGet httpget = new HttpGet(EDITION_URL);

        Log.d(LOG_TAG, "Executing request " + httpget.getRequestLine());

        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
        return httpclient.execute(httpget, responseHandler);
    }

    private String downloadStations() throws IOException{
        HttpClient httpclient = new DefaultHttpClient();

        HttpGet httpget = new HttpGet(STATIONS_URL);

        Log.d(LOG_TAG, "Executing request " + httpget.getRequestLine());

        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity, "ISO8859-2") : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };

        return httpclient.execute(httpget, responseHandler);
    }

    private String downloadTimetable(String fromStation, String toStation, String viaStation)
            throws IOException{
        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost(SEARCH_URL);

        ArrayList<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("ed", elviraEdition));
        postParameters.add(new BasicNameValuePair("mikor", "-1"));
        postParameters.add(new BasicNameValuePair("isz", "0"));
        postParameters.add(new BasicNameValuePair("language", "1"));
        postParameters.add(new BasicNameValuePair("k", ""));
        postParameters.add(new BasicNameValuePair("ref", ""));
        postParameters.add(new BasicNameValuePair("retur", ""));
        postParameters.add(new BasicNameValuePair("nyit", ""));
        postParameters.add(new BasicNameValuePair("vparam", ""));
        postParameters.add(new BasicNameValuePair("i", URLEncoder.encode(fromStation, "UTF-8")));
        postParameters.add(new BasicNameValuePair("e", URLEncoder.encode(toStation, "UTF-8")));
        postParameters.add(new BasicNameValuePair("v", URLEncoder.encode(viaStation, "UTF-8")));
        postParameters.add(new BasicNameValuePair("d", today));
        postParameters.add(new BasicNameValuePair("u", "27"));
        //postParameters.add(new BasicNameValuePair("go", "Menetrend"));

        httppost.setEntity(new UrlEncodedFormEntity(postParameters));

        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };

        return httpclient.execute(httppost, responseHandler);
    }

    public static TrainDao getInstance() {
        if(instance == null) {
            instance = new ElviraDao();
        }
        return instance;
    }
}
