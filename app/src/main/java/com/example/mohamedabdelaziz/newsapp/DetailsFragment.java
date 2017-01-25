package com.example.mohamedabdelaziz.newsapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {
     TextView ttitle , tauthor , tdate , tdescrip ;
     WebView webView ;
     Button homepage,favourite ;
     ImageView img ;
     database datab;
     String title , page ;
    boolean isloaded = false ;
    boolean savedata ;
    SharedPreferences sharedPreferences ;

    public DetailsFragment() {
            }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_details, container, false) ;
        setHasOptionsMenu(true);
        final Bundle intent= getArguments() ;
        datab= new database(getContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        savedata = sharedPreferences.getBoolean("data",true) ;
        ttitle = (TextView)view.findViewById(R.id.thetitle) ;
        tauthor = (TextView)view.findViewById(R.id.theauthor) ;
        tdate = (TextView)view.findViewById(R.id.thedate) ;
        tdescrip = (TextView)view.findViewById(R.id.thedescription) ;
        homepage =(Button)view.findViewById(R.id.thehomepage) ;
        favourite =(Button)view.findViewById(R.id.favourite) ;
        webView=(WebView) view.findViewById(R.id.webView);
        webView.setWebViewClient(new myview());
        webView.getSettings().setJavaScriptEnabled(true);
        img =(ImageView)view.findViewById(R.id.thepicture) ;
        try {
            title = intent.getString("title");
            final String author = intent.getString("author");
            page = intent.getString("page");
            final String date = intent.getString("date");
            final String decsrip = intent.getString("descrip");
            final String url = intent.getString("url");
            ttitle.setText(title);
            tdate.setText(date);
            tdescrip.setText(decsrip);
            tauthor.setText("\n" + author);
            checkdatabase(title);
            if(!savedata) {
                webView.loadUrl(page);
                homepage.setEnabled(false);
            }
            Picasso.with(getContext())
                    .load(url)
                    .resize(500,550)
                    .into(img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError() {
                            try{
                                File f = new File(Environment.getExternalStorageDirectory() + "/newsapp/" +title+page+ ".d"); //as id for image
                                FileInputStream fis = new FileInputStream(f);
                                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                                fis.close();
                                img.setImageBitmap(bitmap);
                            }catch (Exception e)
                            {}
                        }
                    });


            homepage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        webView.loadUrl(page);
                        homepage.setEnabled(false);
                         }
            });

            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datatype dt = new datatype();
                    if (checkdatabase(title)) {
                        datab.delete_it(title);
                        favourite.setBackgroundResource(R.drawable.disloved);
                    } else {
                        dt.url = url;
                        dt.title = title;
                        dt.author = author;
                        dt.description = decsrip;
                        dt.pageurl = page;
                        dt.date = date;
                        datab.insert_data(dt);
                        favourite.setBackgroundResource(R.drawable.loved);

                    }
                }
            });
        }
        catch (Exception e)
        {}
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, page);
            startActivity(Intent.createChooser(sendIntent, getContext().getString(R.string.sharing)));
        return true ;
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean checkdatabase(String title)
    {
        if(datab.is_exists(title)) {
            favourite.setBackgroundResource(R.drawable.loved);
            return true ;
        }
        else{
            favourite.setBackgroundResource(R.drawable.disloved);
            return false ;
        }
    }
    class myview extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try{
            if(!isloaded){
            view.loadUrl(url);
             isloaded=true;}
            else {
                Uri uri = Uri.parse(url);
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(uri);
                startActivity(intent1);
            }
            }catch (Exception e)
            {}
            return true;
    }
    }
}