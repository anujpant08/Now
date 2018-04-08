package com.minimaldev.android.now;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ANUJ on 02/07/2017.
 */

public class DialogArticle extends Activity {

    String url;

    ArrayList<String> arrayList;
    ArrayList<String> bitmaps;

    RecyclerView.Adapter listAdapter;
    RecyclerView recyclerView;

    ProgressBar progressBar;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<HashMap<String,String>> articlesList;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        arrayList=new ArrayList<>();

        articlesList = new ArrayList<>();
        bitmaps=new ArrayList<>();

        Intent intent=getIntent();

        url=intent.getStringExtra("url");

        String src=intent.getStringExtra("source");

        src=src.toUpperCase();


            //char c=Character.toUpperCase(src.charAt(0));
            //src=src.replace(src.charAt(0),c);



        setContentView(R.layout.dialogactivity);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/latomedium.ttf");

        TextView textView=(TextView) findViewById(R.id.morefrom);

        textView.setTypeface(face);

        textView.setText("Stories from "+src);




       // WindowManager.LayoutParams params=getWindow().getAttributes();
        //params.x=-20;
        //params.height=900;
        //params.width=900;
        //params.y=-10;

        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        progressBar=(ProgressBar) findViewById(R.id.prgrs);



        recyclerView = (RecyclerView) findViewById(R.id.feedlist);
        //listView.setSmoothScrollbarEnabled(true);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        listAdapter = new ListAdapter(this, articlesList, bitmaps);
        recyclerView.setAdapter(listAdapter);


        NewsAsync task = new NewsAsync(this, url);
        task.execute(url);




    }

    public void close(View view)
    {
        finish();
    }



    class NewsAsync extends AsyncTask<String, Void, String> {


        // private Context context;
        String new_url;

        private DialogArticle dialogArticle;

        //HashMap<String, String> article = new HashMap<>();
        LinearLayout linearLayout;

        Context context;

        //MainActivity mainActivity;
        //Setview setview=new Setview();


        public NewsAsync(DialogArticle feed, String url) {

            this.dialogArticle = feed;
            //dialog=new ProgressDialog(feed);
            new_url = url;
        }


        @Override
        protected void onPreExecute() {



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

                    String source = jsonObject.getString("source");

                    JSONArray articles = jsonObject.getJSONArray("articles");

                    int i,index = 0;
                    for (i = 1; i < articles.length(); i++) {



                        JSONObject j = articles.getJSONObject(i);



                        String author = j.getString("author");
                        String title = j.getString("title");
                        String description = j.getString("description");
                        String u = j.getString("url");
                        String utoimg = j.getString("urlToImage");
                        String published = j.getString("publishedAt");


                        HashMap<String, String> article = new HashMap<>();


                        article.put("source", source);
                        article.put("author", author);
                        article.put("title", title);
                        article.put("description", description);
                        article.put("url", u);
                        article.put("urlToimg", utoimg);
                        article.put("published", published);


                        DialogArticle.this.articlesList.add(article);

                        DialogArticle.this.bitmaps.add(utoimg);

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
                        //index++;
                    }
                }

                     catch(JSONException e){
                        e.printStackTrace();
                    }
                }

                return null;
            }


        @Override
        public void onPostExecute(String res) {


            listAdapter=new DialogArticle.ListAdapter(DialogArticle.this,articlesList,bitmaps);
            recyclerView.setAdapter(listAdapter);
            //listAdapter.notifyDataSetChanged();

            if(DialogArticle.this.progressBar.isShown())
                DialogArticle.this.progressBar.setVisibility(View.GONE);


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


    private class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>  {



        private LayoutInflater inflater=null;

        ArrayList<HashMap<String,String>> string;

        ArrayList<String> bitmp;

        ViewHolder holder;

        String t="";

        String source="";

        Context context;


        public void clear()
        {

            int size=this.string.size();
            this.string.clear();
            this.bitmp.clear();
            DialogArticle.this.listAdapter.notifyDataSetChanged();

        }

        public ListAdapter(DialogArticle mainActivity, ArrayList<HashMap<String, String>> articlesList, ArrayList<String> bitmaps) {
            //super(context, R.layout.list_item, null);

            super();
            context=mainActivity;
            string=articlesList;
            bitmp=bitmaps;

            //inflater = ( LayoutInflater )mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }




        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.dialogactivity_item,parent,false);
            //holder=new Holder(view);
            holder=new ViewHolder(view);
            //holder.progressBar.setVisibility(View.VISIBLE);
            return  holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/latomedium.ttf");
            holder.tv.setTypeface(face);

           // gettitle(string.get(position).get("title"));

            holder.tv.setText(string.get(position).get("title"));

            Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/latoitalic.ttf");
            holder.tv1.setTypeface(face1);
            if (string.get(position).get("author").equals("null")) {
                holder.tv1.setText(" ");
            } else
                holder.tv1.setText("By " + articlesList.get(position).get("author"));


            source=string.get(position).get("source");

             //t=string.get(position).get("title");



            String des=articlesList.get(position).get("description");







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

            holder.des.setTypeface(face);
            holder.des.setText(des);




            Glide.with(DialogArticle.this)
                    .load(bitmp.get(position))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.noimageavailable)
                    .centerCrop()
                    .into(holder.img);


            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String t=string.get(position).get("title");
                    shareit(t,string.get(position).get("url"));

                }
            });



            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(DialogArticle.this,WebviewClass.class);
                    intent.putExtra("url",string.get(position).get("url"));
                    intent.putExtra("title",string.get(position).get("title"));
                    startActivity(intent);
                }
            });



            //  holder.progressBar.setVisibility(View.GONE);


        }


        @Override
        public int getItemCount() {

            return string.size();

        }


        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv, tv1, tv2, des;
            ImageView img,share;

           // ProgressBar progressBar;

            CardView cardView;

            // private  ItemClickListener clickListener;

            public ViewHolder(View itemView) {

                super(itemView);

                //cardView=(CardView)findViewById(R.id.cardview);
                tv = (TextView) itemView.findViewById(R.id.textheading);
                tv1 = (TextView) itemView.findViewById(R.id.author);
                tv2=(TextView) itemView.findViewById(R.id.news);
                des=(TextView)itemView.findViewById(R.id.des);
                img = (ImageView) itemView.findViewById(R.id.image);
                share=(ImageView) itemView.findViewById(R.id.shr);

              //  progressBar=(ProgressBar)itemView.findViewById(R.id.progress);

                /*itemView.setOnClickListener(new AdapterView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onClick(v,getAdapterPosition(),false);
                    }
                }); */


            }


        }


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
