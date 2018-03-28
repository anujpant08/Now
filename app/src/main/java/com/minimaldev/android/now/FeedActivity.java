package com.minimaldev.android.now;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import static android.os.Build.VERSION_CODES.M;
import static com.minimaldev.android.now.GridActivity.set;

/**
 * Created by ANUJ on 28/06/2017.
 */

public class FeedActivity extends AppCompatActivity {


    GridActivity gridActivity=new GridActivity();
    ArrayList<String> arrayList;
    ArrayList<String> bitmaps;
    Parcelable liststate;
    ArrayList<HashMap<String, String>> articlesList;
    ListView listView;
    boolean flag=false;
    RecyclerView.Adapter listAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String URLpart1 = "https://newsapi.org/v1/articles?source=";
    String URLpart2 = "&sortBy=top&apiKey=0941c4763ff44a8e83b92411e991304f";
    ProgressBar progressBar;
    ProgressDialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        arrayList=new ArrayList<>();

        articlesList = new ArrayList<>();
        bitmaps=new ArrayList<>();

        if(Build.VERSION.SDK_INT >= M)
        {

            if (ActivityCompat.checkSelfPermission(FeedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(FeedActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            }

            if(ActivityCompat.checkSelfPermission(FeedActivity.this, Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FeedActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)!=PackageManager.PERMISSION_GRANTED )
            {
                ActivityCompat.requestPermissions(FeedActivity.this, new String[]{Manifest.permission.INTERNET},1);
                ActivityCompat.requestPermissions(FeedActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE},1);
            }

        }


        setContentView(R.layout.mainscreen);

        TextView textView=(TextView)findViewById(R.id.time);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEEE");
        Date date=new Date();
        String day=simpleDateFormat.format(date);

        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        int d=calendar.get(Calendar.DAY_OF_MONTH);
        int month=calendar.get(Calendar.MONTH);
        String m="";
        switch (month)
        {
            case 0:
                m="January";
                break;
            case 1:
                m="February";
                break;
            case 2:
                m="March";
                break;
            case 3:
                m="April";
                break;
            case 4:
                m="May";
                break;
            case 5:
                m="June";
                break;
            case 6:
                m="July";
                break;
            case 7:
                m="August";
                break;
            case 8:
                m="September";
                break;
            case 9:
                m="October";
                break;
            case 10:
                m="November";
                break;
            case 11:
                m="December";
                break;

        }

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/latobold.ttf");
        textView.setTypeface(face);
        textView.setText(day+", "+d+" "+m);

        if(!isNetworkAvailable())
            Toast.makeText(this,"No network available",Toast.LENGTH_LONG).show();

       // RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.loading);
        //relativeLayout.setVisibility(View.VISIBLE);

        progressBar=(ProgressBar) findViewById(R.id.prgrsmain);



        /*swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe);
            swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#c2185b"));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    refresh();
                }
            }); */

            //progressBar = (ProgressBar) findViewById(R.id.progress);

            //this.dialog = new ProgressDialog(FeedActivity.this);

            Toolbar toolbar = (Toolbar) findViewById(R.id.feedtool);
            TextView t = (TextView) toolbar.findViewById(R.id.tooltext);
            toolbar.setTitle("");
            Typeface f = Typeface.createFromAsset(getAssets(), "fonts/latoblack.ttf");
            t.setTypeface(f);

            toolbar.setBackgroundResource(R.color.white);
            setSupportActionBar(toolbar);


            if (checkempty()) {
                TextView textView1 = (TextView) findViewById(R.id.error);
                textView1.setVisibility(View.VISIBLE);
            }



                TextView textView1 = (TextView) findViewById(R.id.error);
                if(textView1.getVisibility()==View.VISIBLE)
                textView1.setVisibility(View.GONE);


                recyclerView = (RecyclerView) findViewById(R.id.feedlist);
                //listView.setSmoothScrollbarEnabled(true);
                recyclerView.setHasFixedSize(true);

                layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);

                listAdapter = new ListAdapter(this, articlesList, bitmaps);
                recyclerView.setAdapter(listAdapter);

                storelist();


                fetchnews(arrayList);

        //relativeLayout.setVisibility(View.GONE);



        }


    public boolean checkempty()

    {

        try {


            InputStream inputStream = FeedActivity.this.openFileInput("FavoriteSources.txt");
           // ArrayList<String> arr = new ArrayList<>();
            int i = 0;
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                //StringBuilder stringBuilder = new StringBuilder();

                if (bufferedReader.readLine() == null) {

                        return  true;
                   // arr.add(i, receiveString);

                    // Toast.makeText(GridActivity.this, "" + receiveString,
                    //Toast.LENGTH_SHORT).show();

                }
                inputStream.close();

            }
        }

        catch (FileNotFoundException e) {
            Log.e("login activity", "File exception " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  false;
    }

    @Override
    protected  void  onSaveInstanceState(Bundle state)
    {
       liststate=recyclerView.getLayoutManager().onSaveInstanceState();

        state.putParcelable("SAVE RECYCLER VIEW",liststate);


        super.onSaveInstanceState(state);

    }

    @Override
    public void onBackPressed()
    {

        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton("No",null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create()
                .show();

    }

    @Override
    protected  void onRestoreInstanceState(Bundle state)
    {
        if(state!=null)
            liststate=state.getParcelable("SAVE RECYCLER VIEW");

    }



    @Override
    protected void onResume()
    {
        super.onResume();

        if(checkempty()) {

            TextView textView=(TextView) findViewById(R.id.error);
            textView.setVisibility(View.VISIBLE);
            new ListAdapter(this,articlesList,bitmaps).clear();


        }

        else {
            TextView t = (TextView) findViewById(R.id.error);
            t.setVisibility(View.GONE);
            //listAdapter.notifyDataSetChanged();

            //storelist();
            //fetchnews(arrayList);
        }

        if(liststate!=null)
            layoutManager.onRestoreInstanceState(liststate);

    }


    public void storelist()
    {
        try {
            InputStream inputStream = openFileInput("FavoriteSources.txt");
            ArrayList<String> arr = new ArrayList<>();




            int i = 0;
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();


                while ((receiveString = bufferedReader.readLine()) != null) {
                    arr.add(i, receiveString);
                    i++;

                    // Toast.makeText(GridActivity.this, "" + receiveString,
                    //Toast.LENGTH_SHORT).show();

                }

                inputStream.close();






                set = new HashSet<String>(arr);
                arrayList.addAll(set);


                //ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());

            TextView textView=(TextView)findViewById(R.id.error);
            textView.setVisibility(View.VISIBLE);

            OutputStreamWriter outputStreamWriter;
            try {
                outputStreamWriter = new OutputStreamWriter(FeedActivity.this.openFileOutput("FavoriteSources.txt", Context.MODE_APPEND));
                outputStreamWriter.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(FeedActivity.this, "Permission granted", Toast.LENGTH_LONG).show();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(FeedActivity.this, "Permission denied to read your Internal storage", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }





    public void fetchnews(ArrayList<String> list)

    {

        int i=0;
        String URL="";
        //ProgressBar p=(ProgressBar)findViewById(R.id.progress);

        while(i<list.size())
        {
            switch (Integer.parseInt(arrayList.get(i)))
            {

                case 0: URL=URLpart1+"abc-news-au"+URLpart2;
                    break;

                case 1:URL=URLpart1+"al-jazeera-english"+URLpart2;
                    break;

                case 2:URL=URLpart1+"bbc-news"+URLpart2;
                    break;

                case 3:URL=URLpart1+"bbc-sport"+URLpart2;
                    break;

                case 4:URL=URLpart1+"bloomberg"+URLpart2;
                    break;

                case 5:URL=URLpart1+"business-insider"+URLpart2;
                    break;

                case 6:URL=URLpart1+"buzzfeed"+URLpart2;
                    break;

                case 7:URL=URLpart1+"cnbc"+URLpart2;
                    break;

                case 8:URL=URLpart1+"cnn"+URLpart2;
                    break;

                case 9:URL=URLpart1+"daily-mail"+URLpart2;
                    break;

                case 10: URL=URLpart1+"the-economist"+URLpart2;
                    break;

                case 11:URL=URLpart1+"engadget"+URLpart2;
                    break;

                case 12:URL=URLpart1+"entertainment-weekly"+URLpart2;
                    break;

                case 13:URL=URLpart1+"espn"+URLpart2;
                    break;

                case 14:URL=URLpart1+"espn-cric-info"+URLpart2;
                    break;

                case 15:URL=URLpart1+"financial-times"+URLpart2;
                    break;

                case 16:URL=URLpart1+"four-four-two"+URLpart2;
                    break;

                case 17:URL=URLpart1+"fox-sports"+URLpart2;
                    break;

                case 18:URL=URLpart1+"google-news"+URLpart2;
                    break;

                case 19:URL=URLpart1+"ign"+URLpart2;
                    break;

                case 20:URL=URLpart1+"independent"+URLpart2;
                    break;

                case 21:URL=URLpart1+"mashable"+URLpart2;
                    break;

                case 22:URL=URLpart1+"metro"+URLpart2;
                    break;

                case 23:URL=URLpart1+"mtv-news"+URLpart2;
                    break;

                case 24:URL=URLpart1+"national-geographic"+URLpart2;
                    break;

                case 25:URL=URLpart1+"nfl-news"+URLpart2;
                    break;

                case 26:URL=URLpart1+"polygon"+URLpart2;
                    break;

                case 27:URL=URLpart1+"reddit-r-all"+URLpart2;
                    break;

                case 28:URL=URLpart1+"talksport"+URLpart2;
                    break;

                case 29:URL=URLpart1+"techcrunch"+URLpart2;
                    break;

                case 30:URL=URLpart1+"techradar"+URLpart2;
                    break;

                case 31:URL=URLpart1+"the-guardian-uk"+URLpart2;
                    break;

                case 32:URL=URLpart1+"the-hindu"+URLpart2;
                    break;

                case 33:URL=URLpart1+"the=next-web"+URLpart2;
                    break;

                case 34:URL=URLpart1+"the-new-york-times"+URLpart2;
                    break;

                case 35:URL=URLpart1+"the-verge"+URLpart2;
                    break;

                case 36:URL=URLpart1+"the-times-of-india"+URLpart2;
                    break;

                case 37:URL=URLpart1+"the-wall-street-journal"+URLpart2;
                    break;

            }


            NewsAsync task = new NewsAsync(this, URL);
            task.execute(URL);


           // progressBar.setVisibility(View.GONE);
            //if(this.dialog.isShowing())
              //  this.dialog.dismiss();

            i++;
        }


       // swipeRefreshLayout.setRefreshing(false);

    }


    class NewsAsync extends AsyncTask<String, Void, String> {


        // private Context context;
        String new_url;

        private FeedActivity feedActivity;

        //HashMap<String, String> article = new HashMap<>();
        LinearLayout linearLayout;

        Context context;

        //MainActivity mainActivity;
        //Setview setview=new Setview();


        public NewsAsync(FeedActivity feed, String url) {

            this.feedActivity = feed;
            dialog=new ProgressDialog(feed);
            new_url = url;
        }


        @Override
        protected void onPreExecute() {

               FeedActivity.this.progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... urls) {
            // this news service method will be called after the service executes.
            // it will run as a seperate process, and will populate the activity in the onPostExecute
            // method below


            String response = "";
            try {
                HttpURLConnection con = (HttpURLConnection) (new URL(new_url)).openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(false);
                con.connect();
                //HttpResponse execute = client.execute(httpGet);
                InputStream content = (InputStream) con.getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response = response + s;
                }
                con.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response != null) {
                try {


                    JSONObject jsonObject = new JSONObject(response);

                    String source=jsonObject.getString("source");

                    JSONArray articles = jsonObject.getJSONArray("articles");
                    int index=0;


                        JSONObject j = articles.getJSONObject(0);


                        String author = j.getString("author");
                        String title = j.getString("title");
                        String description = j.getString("description");
                        String u = j.getString("url");
                        String utoimg = j.getString("urlToImage");
                        String published = j.getString("publishedAt");


                        HashMap<String, String> article=new HashMap<>();


                        article.put("source", source);
                        article.put("author", author);
                        article.put("title", title);
                        article.put("description", description);
                        article.put("url", u);
                        article.put("urlToimg", utoimg);
                        article.put("published", published);



                        FeedActivity.this.articlesList.add(article);

                        FeedActivity.this.bitmaps.add(utoimg);

                       // store(article, utoimg);




                       /* Bitmap mIcon11 = null;
                        Bitmap b=BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.noimageavailable);
                        try
                        {
                            InputStream in = new java.net.URL(utoimg).openStream();

                                mIcon11 = BitmapFactory.decodeStream(in);
                                int width = mIcon11.getWidth();
                                int height = mIcon11.getHeight();

                            ByteArrayOutputStream out=new ByteArrayOutputStream();
                            mIcon11.compress(Bitmap.CompressFormat.JPEG,30,out);
                            Bitmap bitmap=BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));


                                bitmaps.add(index, bitmap);




                        }
                        catch (Exception e) {

                            Log.e("Error", e.getMessage());
                            bitmaps.add(index,b);
                            e.printStackTrace();
                        } */
                        // return mIcon11;
                            index++;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }


        @Override
        public void onPostExecute(String res) {


            listAdapter=new ListAdapter(FeedActivity.this,articlesList,bitmaps);
            recyclerView.setAdapter(listAdapter);
            recyclerView.scrollToPosition(0);

            //listAdapter.notifyDataSetChanged();

              //  FeedActivity.this.progressBar.setVisibility(View.GONE);



           // if(flag) {
                //FeedActivity.this.recyclerView.setAdapter(listAdapter);
                //FeedActivity.this.listAdapter.notifyDataSetChanged();
            //}
            //else
            //    recyclerView.setAdapter(listAdapter);

                //finish();
            /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

                @Override
                public void onRefresh() {
                    fetchnews(arrayList);

                }
            });

            if(swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setVisibility(View.GONE); */


        }
    }


    public void store(HashMap<String,String> hashMap, String weburl)
    {
        articlesList.add(hashMap);



        //String urldisplay = urls[0];
        bitmaps.add(weburl);
    }


    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>  {



        private LayoutInflater inflater=null;

        ArrayList<HashMap<String,String>> string;

        ArrayList<String> bitmp;

        ViewHolder holder;

        String source="";

        Context context;


        public void clear()
        {

            int size=this.string.size();
            this.string.clear();
            this.bitmp.clear();
            FeedActivity.this.listAdapter.notifyDataSetChanged();

        }

        public ListAdapter(FeedActivity mainActivity, ArrayList<HashMap<String, String>> articlesList, ArrayList<String> bitmaps) {
            //super(context, R.layout.list_item, null);

            super();
            context=mainActivity;
            string=articlesList;
            bitmp=bitmaps;

            //inflater = ( LayoutInflater )mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.homescreen_item,parent,false);
            //holder=new Holder(view);
            holder=new ViewHolder(view);
            //holder.progressBar.setVisibility(View.VISIBLE);
            return  holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/latoblack.ttf");
            holder.tv.setTypeface(face);
            holder.tv.setText(string.get(position).get("title"));

            Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/latoitalic.ttf");
            holder.tv1.setTypeface(face1);
            if (string.get(position).get("author").equals("null")) {
                holder.tv1.setText(" "+" | ");
            } else
                holder.tv1.setText("By " + articlesList.get(position).get("author")+" | ");


            source=string.get(position).get("source");
            String d=string.get(position).get("description");


            String src="";

            for(int i=0;i<source.length();i++)
            {
                if(source.charAt(i)=='-')
                    src=src+" ";
                else
                    src=src+source.charAt(i);
            }

            src=src.toUpperCase();

            holder.tv2.setTypeface(face);
            holder.tv2.setText(src);

            Typeface f = Typeface.createFromAsset(getAssets(), "fonts/latomedium.ttf");
            holder.des.setTypeface(f);
            holder.des.setText(d);

            Glide.with(FeedActivity.this)
                    .load(bitmp.get(position))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.noimageavailable)
                    .centerCrop()
                    .into(holder.img);



            holder.progressBar.setVisibility(View.GONE);

            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String t=string.get(position).get("title");

                    shareit(t,string.get(position).get("url"));

                }
            });

            if(position==(string.size()-1))
                FeedActivity.this.progressBar.setVisibility(View.GONE);



               /* holder.setClickListener(new ItemClickListener()
                {
                public void onClick(View view, int position, boolean isLongClick) {

                String url= articlesList.get(position).get("url");
                String title=articlesList.get(position).get("title");
                //Toast.makeText(FeedActivity.this,url,Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(FeedActivity.this, WebviewClass.class);
                intent.putExtra("url",url);
                intent.putExtra("title",title);
                startActivity(intent);
            }
            });  */

            final String finalSrc = src;
            holder.img.setOnLongClickListener(new View.OnLongClickListener() {
                   @Override
                   public boolean onLongClick(View v) {

                       String url=URLpart1+string.get(position).get("source")+URLpart2;

                      // Toast.makeText(FeedActivity.this,url ,Toast.LENGTH_SHORT).show();


                       Intent intent=new Intent(FeedActivity.this, DialogArticle.class);
                       intent.putExtra("url",url);
                       intent.putExtra("source", finalSrc);
                       startActivity(intent);

                       return true;
                   }
               });



            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //holder.read.setVisibility(View.VISIBLE);

                    Intent intent=new Intent(FeedActivity.this,WebviewClass.class);
                    intent.putExtra("url",string.get(position).get("url"));
                    intent.putExtra("title",string.get(position).get("title"));


                    holder.tv1.setTextColor(Color.parseColor("#616161"));
                    holder.tv2.setTextColor(Color.parseColor("#616161"));

                    listAdapter.notifyDataSetChanged();
                    startActivity(intent);
                }
            });


        }


        @Override
        public int getItemCount() {

            return string.size();

        }


        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv, tv1, tv2,des;
            ImageView img,share;

            ProgressBar progressBar;

            CardView cardView;

           // private  ItemClickListener clickListener;

            public ViewHolder(View itemView) {

                super(itemView);

                //cardView=(CardView)findViewById(R.id.cardview);
                tv = (TextView) itemView.findViewById(R.id.firsttext);
                tv1 = (TextView) itemView.findViewById(R.id.authortext);
                tv2=(TextView) itemView.findViewById(R.id.newssource);
                des=(TextView)itemView.findViewById(R.id.destext);
                img = (ImageView) itemView.findViewById(R.id.firstimage);
                //read = (ImageView) itemView.findViewById(R.id.readtick);
                progressBar=(ProgressBar)itemView.findViewById(R.id.progress);
                share=(ImageView)itemView.findViewById(R.id.share);

                /*itemView.setOnClickListener(new AdapterView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onClick(v,getAdapterPosition(),false);
                    }
                }); */


            }


        }


    }


    public void go(View view)
    {
        /*OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(FeedActivity.this.openFileOutput("FavoriteSources.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();

        }

        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        } */


        Intent intent=new Intent(FeedActivity.this, GridActivity.class);
        startActivity(intent);
    }

    public void refresh(View view)

    {

        if(isNetworkAvailable()) {

            if (checkempty()) {
                TextView t = (TextView) findViewById(R.id.error);
                t.setVisibility(View.VISIBLE);
                new ListAdapter(this, articlesList, bitmaps).clear();

                Toast.makeText(this, "Nothing to show...select favorites and refresh !", Toast.LENGTH_LONG).show();

            } else {

                TextView t = (TextView) findViewById(R.id.error);
                t.setVisibility(View.GONE);

                //progressBar.setVisibility(View.VISIBLE);
                //articlesList.clear();
                articlesList.clear();
                arrayList.clear();
                bitmaps.clear();

                flag = true;
                storelist();
                fetchnews(arrayList);
                flag = false;
                //FeedActivity.ListAdapter listAdapter=new ListAdapter(FeedActivity.this, articlesList,bitmaps);

                Toast.makeText(this, "Refreshing your feed... !", Toast.LENGTH_LONG).show();
                //swipeRefreshLayout.setRefreshing(false);
            }
        }
        else {
            Toast.makeText(this, "Check network connection",Toast.LENGTH_LONG).show();
        }

        //listView.setAdapter(new ListAdapter(FeedActivity.this, articlesList,bitmaps));
        //listView.getAdapter().notifyAll();
        //fetchnews(arrayList);
        //progressBar.setVisibility(View.GONE);

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public void shareit(String title, String URL)

    {
        String text= title +" "+URL+" \n via Now.";

        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent,getResources().getText(R.string.sharewith)));

    }

}
