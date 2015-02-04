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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import hu.kifor.tamas.vonatinfo.TrainDao;
import hu.kifor.tamas.vonatinfo.model.ArrivalAndDeparture;
import hu.kifor.tamas.vonatinfo.model.Timetable;
import hu.kifor.tamas.vonatinfo.model.TrainInTrip;
import hu.kifor.tamas.vonatinfo.model.Trip;

//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;

/**
 * Created by tamas on 15. 01. 19..
 */
public class ElviraDao implements TrainDao {
    private static final String LOG_TAG = "ElviraDao";

    private static final String ELVIRA_HOST = "http://elvira.mav-start.hu";

    private static final String ELVIRA_EDITION_URL = ELVIRA_HOST + "/elvira.dll/xslvzs/?language=1";

    private static final String ELVIRA_SEARCH_URL = ELVIRA_HOST + "/elvira.dll/xslvzs/uf";

    private String elviraEdition;

    private String today;

    public ElviraDao() {
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
                Elements tripDetailElements = tripElement.children();
                if (tripDetailElements.size() < 3) {
                    continue;
                }
                Trip trip = new Trip();
                TrainInTrip trainTrip = new TrainInTrip();
                ArrivalAndDeparture officialArrivalAndDeparture = new ArrivalAndDeparture();
                Element departureElement = tripDetailElements.get(1);
                String departureAsString = departureElement.text();
                LocalTime departure = LocalTime.parse(departureAsString);
                if (departure.isAfter(LocalTime.now())) {
                    String arrivalAsString = tripDetailElements.get(2).text().substring(0, 5);
                    LocalTime arrival = LocalTime.parse(arrivalAsString);
                    officialArrivalAndDeparture.setDeparture(departure);
                    officialArrivalAndDeparture.setArrival(arrival);
                    trip.setOfficialArrivalAndDeparture(officialArrivalAndDeparture);
                    trip.getTrains().add(trainTrip);
                    timetable.getTrips().add(trip);
                    if (timetable.getTrips().size() > 2) {
                        break;
                    }
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Log.e(LOG_TAG, "Unable to retrieve train information.", throwable);
            throw new RuntimeException("Unable to retrieve train information.");
        }

        return timetable;
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

        //CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(ELVIRA_EDITION_URL);

            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);

            return responseBody;
        } finally {
            //httpclient.close();
        }
    }

    private String downloadTimetable(String fromStation, String toStation, String viaStation)
            throws IOException{
        HttpClient httpclient = new DefaultHttpClient();

        //CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(ELVIRA_SEARCH_URL);

            ArrayList<NameValuePair> postParameters;

            postParameters = new ArrayList<NameValuePair>();
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

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            String responseBody = httpclient.execute(httppost, responseHandler);

            return responseBody;
        } finally {
            //httpclient.close();
        }
    }
}
