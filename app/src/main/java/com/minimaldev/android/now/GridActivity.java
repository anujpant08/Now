package com.minimaldev.android.now;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.minimaldev.android.now.R.layout.select;

/**
 * Created by ANUJ on 28/06/2017.
 */

public class GridActivity extends AppCompatActivity {


    String path;

    public static Set<String> set;

    ArrayList<String> arrayList;

    int [] array=new int[38];
    int prevposition=-1;


    Integer[] mThumbIds = {
            R.drawable.abc,
            R.drawable.aljazeera,
            R.drawable.bbc,
            R.drawable.bbcsport,
            R.drawable.bloomberg,
            R.drawable.businessinsider,
            R.drawable.buzzfeed,
            R.drawable.cnbc,
            R.drawable.cnn,
            R.drawable.dailymail,
            R.drawable.economist,
            R.drawable.engadget,
            R.drawable.entertainmentweekly,
            R.drawable.espn,
            R.drawable.espncric,
            R.drawable.financialtimes,
            R.drawable.fourfourtwo,
            R.drawable.foxsports,
            R.drawable.googlenews,
            R.drawable.ign,
            R.drawable.independent,
            R.drawable.mashable,
            R.drawable.metro,
            R.drawable.mtv,
            R.drawable.nationalg,
            R.drawable.nfl,
            R.drawable.polygon,
            R.drawable.reddit,
            R.drawable.talksport,
            R.drawable.techcrunch,
            R.drawable.techradar,
            R.drawable.theguardian,
            R.drawable.thehindu,
            R.drawable.thenextweb,
            R.drawable.thenytimes,
            R.drawable.theverge,
            R.drawable.toi,
            R.drawable.wallstreet

    };


    FileWriter fileWriter;
    FileReader fileReader;
    File file, favorites;
    int index=0;
    OutputStreamWriter outputStreamWriter;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();

        arrayList=new ArrayList<>();
        setContentView(select);
        final GridView gridview = (GridView) findViewById(R.id.grid);
        gridview.setSmoothScrollbarEnabled(true);


        path = Environment.getExternalStorageDirectory().toString();

        file=new File(path);
       // favorites=new File(this.getFilesDir(),"FavoriteSources.txt");
        final Context context;

        final Toolbar toolbar=(Toolbar)findViewById(R.id.toolselect);
        TextView t = (TextView) toolbar.findViewById(R.id.tooltitle);
        toolbar.setTitle("");
        Typeface f = Typeface.createFromAsset(getAssets(), "fonts/latoblack.ttf");
        t.setTypeface(f);

        toolbar.setBackgroundResource(R.color.white);
        setSupportActionBar(toolbar);

        final ImageAdapter imageAdapter=new ImageAdapter(this,mThumbIds);
        gridview.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();


            //String ret = "";

            try {
                InputStream inputStream = GridActivity.this.openFileInput("FavoriteSources.txt");
                ArrayList<String> arr = new ArrayList<>();
                int i=0;
                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                   // StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        arr.add(i,receiveString);
                        i++;

                       // Toast.makeText(GridActivity.this, "" + receiveString,
                                //Toast.LENGTH_SHORT).show();

                    }
                    inputStream.close();

                    set = new HashSet<String>(arr);
                    arrayList.addAll(set);


                    //ret = stringBuilder.toString();
                }
            }
            catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }


    }


    class ImageAdapter extends BaseAdapter
    {
        private Context context;
        private LayoutInflater inflater=null;
        Integer[] img;
        List<Integer> selectedposition=new ArrayList<>();

        public ImageAdapter(Context c, Integer [] imgs)
        {
            this.context=c;
            this.img=imgs;

            inflater = ( LayoutInflater )c.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return img.length;
        }

        @Override
        public Object getItem(int position) {
            return img[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public View getView(final int position, View convertView, ViewGroup parent) {
            //ImageView imageView, selectionview;
            final Holder holder;
            View rowView;
            holder=new Holder();

            if (convertView==null) {


                convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            }

                // if it's not recycled, initialize some attributes
                //holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.gridrelative);
                holder.imageView=(ImageView)convertView.findViewById(R.id.gridimg);
                //holder.selectionview=(ImageView)convertView.findViewById(R.id.selection);

                convertView.setTag(holder);

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(array[position]!=1)
                        {
                            array[position]=1;

                           holder.imageView.setBackgroundResource(R.drawable.itemselectiontransparent);

                            try {
                                outputStreamWriter = new OutputStreamWriter(GridActivity.this.openFileOutput("FavoriteSources.txt", Context.MODE_APPEND));
                                outputStreamWriter.append(position + "\n");
                                outputStreamWriter.close();

                            } catch (IOException e) {
                                Log.e("Exception", "File write failed: " + e.toString());
                            }




                        }
                        else {

                            holder.imageView.setBackgroundResource(R.drawable.custom_shape);

                            try {
                                outputStreamWriter = new OutputStreamWriter(GridActivity.this.openFileOutput("FavoriteSources.txt", Context.MODE_APPEND));
                                outputStreamWriter.append("");
                                outputStreamWriter.close();

                            } catch (IOException e) {
                                Log.e("Exception", "File write failed: " + e.toString());
                            }

                            array[position]=0;


                        }

                    }
                });



            holder.imageView.setImageResource(mThumbIds[position]);
            //holder.selectionview.setImageResource(R.drawable.item_selected);
            return convertView;
        }

    }


    class Holder{

        ImageView imageView,selectionview;
        RelativeLayout relativeLayout;

    }


    public void finishactivity(View view)
    {
        finish();

    }


    public void reset(View view)

    {

        final AlertDialog.Builder alert= new AlertDialog.Builder(this);

        alert.setMessage("Do you want to reset the saved preferences ?");

        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GridActivity.this,"All preferences have been reset",Toast.LENGTH_LONG).show();

                OutputStreamWriter outputStreamWriter;

                try {
                    outputStreamWriter = new OutputStreamWriter(GridActivity.this.openFileOutput("FavoriteSources.txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write("");
                    outputStreamWriter.close();

                } catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }


            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();

            }
        });

        alert.create();
        alert.show();


    }

    @Override
    public void onBackPressed()
    {

        finish();
    }



}
