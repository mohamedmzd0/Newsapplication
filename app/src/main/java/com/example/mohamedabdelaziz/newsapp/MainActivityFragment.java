package com.example.mohamedabdelaziz.newsapp;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ArrayList<datatype> alldata  ;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String JsonStr =null ;
    JSONArray articles ;
    SharedPreferences sharedPreferences ;
    GridView gridView ;
    ProgressDialog progress ;
    String baseurl ;
    ViewPager viewPager ;
    ArrayList<String>title ;
    ArrayList <String> url ;
    boolean wifi ;
    int selectedurl =0 ;
    database data ;
    SQLiteDatabase database ;
    String state="online" ;
    ArrayList<Integer> startindex  , endindex  ;
    boolean already_started = false ;
    listener listen ;
    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }
    void start_send_data(listener listen)
    {
        this.listen = listen ;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        wifi = sharedPreferences.getBoolean("wifi", true);
        data =new database(getContext());
        database = data.getWritableDatabase();
        progress= new ProgressDialog(getContext()) ;
        alldata =new ArrayList<>() ;
        url =new ArrayList<>();
        title = new ArrayList<>() ;
        startindex =new ArrayList<>();
        endindex =new ArrayList<>();
        viewPager= (ViewPager) view.findViewById(R.id.view_page) ;
        url.add("https://newsapi.org/v1/articles?source=abc-news-au&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=cnn&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=fox-sports&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=google-news&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=mtv-news&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=sky-news&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=sky-sports-news&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=usa-today&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=business-insider&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=cnbc&sortBy=top&apiKey=");
        url.add("https://newsapi.org/v1/articles?source=talksport&sortBy=top&apiKey=");
          if(new network().isOnline(getContext())){
           selecturl(selectedurl);
        }
        else {
            WifiManager wifiManager = (WifiManager) getActivity().getSystemService(getContext().WIFI_SERVICE);
            if (wifi && !wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        NotificationManager manager;
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(getContext())
                                .setContentText("WiFi Opened")
                                .setContentTitle("Connecting")
                                .setSound(alarmSound)
                                .setSmallIcon(R.drawable.image);
                        manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(1, builder.build());
                            if (new network().isOnline(getContext()))
                        selecturl(selectedurl);
                        else
                                gooffline() ;
                    }

                }, 5100);
            }
            else
                gooffline();
        }
        return view;
    }
    public void selecturl(int i) {
        state="online" ;
        baseurl=url.get(i) ;
        new getalldata().execute() ;
    }
public void gooffline()
{
    state="offline" ;
    startindex.add(0);
    alldata = data.restore_data();
    endindex.add(alldata.size());
    title.add("Favourites");
     viewPager.setAdapter(new swipe(getContext()));
}
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
           Intent intent = new Intent(getContext(), Settings.class) ;
            startActivity(intent);
            Intent intent1 = getActivity().getIntent();
            startActivity(intent1);
            return true;
        }
        if (id == R.id.action_favourite) {
            alldata.clear();
            startindex.clear();
            endindex.clear();
            title.clear();
            if(state.equals("online")) {
                gooffline();
                item.setTitle(R.string.online) ;
            }
            else
            {
                selectedurl=0;
                selecturl(selectedurl) ;
                item.setTitle(R.string.favourite) ;
            }

            return true;
        }
        if ((id==R.id.refresh))
        {
            alldata.clear();
            startindex.clear();
            endindex.clear();
            title.clear();
            if(state.equals("online")) {
                selectedurl=0;
                selecturl(selectedurl) ;
            }
            else
            {
                gooffline();
            }
                    return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public class swipe extends PagerAdapter {
        LayoutInflater layoutInflater ;
        Context context ;
         public swipe(Context context )
        {
             this.context=context ;
         }
        @Override
        public int getCount() {
            return  title.size();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view==(LinearLayout)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            layoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE) ;
            View view = layoutInflater.inflate(R.layout.page,container,false) ;
            TextView textView = (TextView)view.findViewById(R.id.pagename) ;
            gridView = (GridView) view.findViewById(R.id.gridview) ;
            if(title.size()!=0 && alldata.size()==0) {
                textView.setText(R.string.nodata);
                container.addView(view);
                return view;
            }
            else {
                if(position==0)
                    textView.setText(title.get(position) + "-->>");
                else if(position== title.size()-1)
                    textView.setText("<<--" + title.get(position));
                else
                textView.setText("<<--" + title.get(position) + "-->>");
            }
                  gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    datatype d = new datatype() ;
                    d.title=alldata.get(i+startindex.get(position)).title ;
                    d.url =alldata.get(i+startindex.get(position)).url ;
                    d.pageurl=alldata.get(i+startindex.get(position)).pageurl ;
                    d.description=alldata.get(i+startindex.get(position)).description ;
                    d.date=alldata.get(i+startindex.get(position)).date ;
                    d.author=alldata.get(i+startindex.get(position)).author ;
                    listen.send_data(d);
                }
            });
            container.addView(view);
            ArrayList<datatype>temp =new ArrayList<>() ;
            for (int i = startindex.get(position); i <endindex.get(position) ; i++) {
                temp.add(alldata.get(i)) ;
            }
            gridView.setAdapter(new custom_adapter(getContext(),temp));

            return view;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout)object);
        }
    }
    class getalldata extends AsyncTask<String ,String,String> {
        @Override
        protected void onPreExecute() {
            progress.setTitle(R.string.loadingdata);
            progress.setMessage(getContext().getString(R.string.plwait));
            progress.setCancelable(false);
            progress.show();
        }
        protected String doInBackground(String... args) {

            try {
                URL url = new URL(baseurl+BuildConfig.API_KEY);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                startindex.add(alldata.size()) ;
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                JsonStr = buffer.toString();
                urlConnection.disconnect();
                reader.close();
            } catch (Exception e) {
                return null;
            }
            try {
                JSONObject jsonObject = new JSONObject(JsonStr);
                title.add(jsonObject.getString("source")) ;
                articles = jsonObject.getJSONArray("articles") ;
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject object = articles.getJSONObject(i);
                    datatype dt = new datatype();
                    dt.pageurl = object.get("url").toString();
                    dt.url = object.get("urlToImage").toString();
                    dt.author = object.get("author").toString();
                    dt.title = object.get("title").toString();
                    dt.description = object.get("description").toString();
                    dt.date = object.get("publishedAt").toString();
                    alldata.add(dt);
                }

                endindex.add(alldata.size()) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String data) {
           if(selectedurl< url.size()-1)
               selecturl(++selectedurl);
            else {
               viewPager.setAdapter(new swipe(getContext()));
               progress.dismiss();
               if(sharedPreferences.getBoolean("guide",true) && !already_started){
               new AlertDialog.Builder(getContext())
                       .setTitle(R.string.alert_title)
                       .setMessage(R.string.alert_message)
                       .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               already_started=true ;
                           }
                       })
                       .setIcon(R.drawable.alert)
                       .show();
           }
           }
        }
    }
}
