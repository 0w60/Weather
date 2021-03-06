package cn.weather;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


public class DbSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "DbSyncAdapter";
    static final Uri CONTENT_URI = Uri.parse(
            "content://cn.weather.WeatherDbProvider/weathertable");

    static final String CITY_COLUMN = "city";
    static final String DAY_COLUMN = "day";
    static final String WEATHER_CONDITION_COLUMN = "weatherCondition";
    static final String TEMPERATURE_COLUMN = "temperature";
    Cursor existingCitiesCursor;
    HashSet<String> citiesInBaseSet;
    String city;


    ContentResolver contentResolver;
    URL[] urls;

    String day;
    String weatherCondtns;
    String temperature;
    XmlPullParserFactory factory;
    XmlPullParser parser;

    public DbSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();

        Log.i(TAG, "In the constructor 1");
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "In onPerformSync");

        setExistingCitiesCursor(provider);
        setCitiesInBaseSet();
        setUrls();
        getDataFromServerAndPopulateDB(urls, provider);
    }

    void setExistingCitiesCursor(ContentProviderClient provider) {
        try {
            String[] projection = {CITY_COLUMN};
            existingCitiesCursor = provider.query(CONTENT_URI,
                    projection, null, null, null, null);
        } catch (RemoteException e) {
            Log.e(TAG, "Can't get city names from CONTENT_URI", e);
        }
    }

    void setCitiesInBaseSet() {
        int size = existingCitiesCursor.getCount();
        citiesInBaseSet = new HashSet<>(size);
        int cityColumnIndex = existingCitiesCursor.getColumnIndex(CITY_COLUMN);
        Log.i(TAG, "existingCitiesCursor.getCount(): " + existingCitiesCursor.getCount());
        if (existingCitiesCursor.moveToFirst()) {
            do {
                citiesInBaseSet.add(existingCitiesCursor.getString(cityColumnIndex));
            } while (existingCitiesCursor.moveToNext());
        }
        citiesInBaseSet.remove(null);
        Log.i(TAG, "citiesInBaseSet: " + citiesInBaseSet);
    }

    void setUrls() {
        //Number of days to query
        int daysNumber = 3;
        try {
            urls = new URL[citiesInBaseSet.size()];
            int index = 0;
            for (String cityUrl : citiesInBaseSet) {
                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" +
                        cityUrl + "&mode=xml&units=metric&cnt=" + daysNumber;
                urls[index++] = new URL(baseUrl);
            }
            Log.i("urls: \n", Arrays.toString(urls));
        } catch (MalformedURLException e) {
            Log.e(TAG, "Bad URL", e);
        }
    }

    void getDataFromServerAndPopulateDB(URL[] links, ContentProviderClient provider) {
        ArrayList<Weather> weatherList = new ArrayList<>(links.length);
        for (URL link : links) {
            String xmlString = connectToServerAndGetXml(link);
            weatherList.addAll(parse(xmlString));
        }
        setContentValuesAndPopulateDB(weatherList, provider);
    }

    String connectToServerAndGetXml(URL url) {
        String xmlString = null;
        BufferedReader inStrmReader = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            inStrmReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder xmlStrngBldr = new StringBuilder();
            String line;
            while ((line = inStrmReader.readLine()) != null) {
                xmlStrngBldr.append(line);
            }
            xmlString = xmlStrngBldr.toString();
            Log.i(TAG, "xmlString: " + xmlString);

        } catch (IOException e) {
            Log.e(TAG, "Error while receiving data from server", e);
        } finally {
            try {
                if (inStrmReader != null) {
                    inStrmReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error while closing reader or connection", e);
            }
        }
        return xmlString;
    }

    ArrayList<Weather> parse(String xmlString) {
        StringReader xmlReader = new StringReader(xmlString);
        ArrayList<Weather> parsedList = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(xmlReader);
            parser.nextTag();

            parsedList = parseXML(parser);
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Failed to parse the XML string", e);
        } finally {
            if (xmlReader != null) {
                xmlReader.close();
            }
        }
        return parsedList;
    }

    ArrayList<Weather> parseXML(XmlPullParser parsr) {
        ArrayList<Weather> weathers = new ArrayList<>();
        String wDay = null;
        String wConds = null;
        String wTemp = null;
        try {
            while (parsr.getEventType() != XmlPullParser.END_DOCUMENT) {
                parsr.next();
                if (parsr.getEventType() == XmlPullParser.END_DOCUMENT) {
                    break;
                }
                String tag = parsr.getName();
                if (tag == null) {
                    parsr.nextTag();
                    tag = parsr.getName();
                }
                if (parsr.getEventType() == XmlPullParser.END_TAG) {
                    parsr.next();
                }
                if (parsr.getEventType() == XmlPullParser.START_TAG) {
                    switch (tag) {
                        case "name":
                            city = parsr.nextText();
                            break;
                        case "time":
                            if (day == null) {
                                day = parsr.getAttributeValue(null, "day");
                            } else {
                                wDay = parsr.getAttributeValue(null, "day");
                            }
                            break;
                        case "symbol":
                            if (weatherCondtns == null) {
                                weatherCondtns = parsr.getAttributeValue(null, "name");
                            } else {
                                wConds = parsr.getAttributeValue(null, "name");
                            }
                            break;
                        case "temperature":
                            if (temperature == null) {
                                temperature = parsr.getAttributeValue(null, "day");
                            } else {
                                wTemp = parsr.getAttributeValue(null, "day");

                                Weather wInstance = new Weather();
                                wInstance.city = city;
                                wInstance.day = wDay;
                                wInstance.weathrCondtns = wConds;
                                wInstance.temperature = wTemp;
                                weathers.add(wInstance);
                            }
                            break;
                    }
                }
            }
            weathers.add(new Weather(city, day, temperature, weatherCondtns));
        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, "Failed to parse the XML string", e);
        }
        return weathers;
    }


    void setContentValuesAndPopulateDB(ArrayList<Weather> list, ContentProviderClient provider) {
        try {
            ContentValues values = new ContentValues();
            for (Weather w : list) {
                values.put(CITY_COLUMN, w.city);
                values.put(DAY_COLUMN, w.day);
                values.put(WEATHER_CONDITION_COLUMN, w.weathrCondtns);
                values.put(TEMPERATURE_COLUMN, w.temperature);

                provider.insert(CONTENT_URI, values);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Can't insert content values into database via CONTENT_URI", e);
        }
    }
}
