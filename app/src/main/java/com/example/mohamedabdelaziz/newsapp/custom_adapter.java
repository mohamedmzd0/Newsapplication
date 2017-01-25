package com.example.mohamedabdelaziz.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Mohamed Abd ELaziz on 1/11/2017.
 */

public class custom_adapter extends BaseAdapter{
    private Context context ;
    private ArrayList<datatype> arrayList;
    File f=new File(Environment.getExternalStorageDirectory()+"/newsapp/others/");

    OutputStream outStream = null;

    public custom_adapter(Context context , ArrayList<datatype> arrayList)
    {
        this.context = context ;
        this.arrayList = arrayList ;
        if(!f.exists()){
            f.mkdirs();
        }
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE) ;
        view = inflater.inflate(R.layout.grid_view_item,null) ;
        final ImageView imageView = (ImageView)view.findViewById(R.id.imageView) ;
        TextView textView = (TextView)view.findViewById(R.id.textView) ;
        final String title = arrayList.get(i).title ;
        final String page = arrayList.get(i).pageurl ;
        textView.setText(title);

        try {
            File f = new File(Environment.getExternalStorageDirectory() + "/newsapp/" + title+page + ".d");
            FileInputStream fis = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {

            Picasso.with(context)
                    .load(arrayList.get(i).url)
                    .resize(150,200)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap= ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                            File file = new File(Environment.getExternalStorageDirectory() + "/newsapp/"+title+page+".d");
                            try {
                                outStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                                outStream.flush();
                                outStream.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError() {
                        }
                    });
        }

        return view;

    }
}
