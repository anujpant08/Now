package com.minimaldev.android.now;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by ANUJ on 30/06/2017.
 */

public class WebviewClass  extends AppCompatActivity {


    WebView webView;
    String url,title;

    ProgressBar progressBar;

    SwipeRefreshLayout swipeRefreshLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.webview);

        Intent intent=getIntent();

        url=intent.getStringExtra("url");

        title=intent.getStringExtra("title");

        setContentView(R.layout.webview);


        progressBar=(ProgressBar)findViewById(R.id.progrssbar);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swiperefreshweb);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                webView.loadUrl(url);

            }
        });

        //Toast.makeText(this,url, Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolweb);
        TextView textView = (TextView) toolbar.findViewById(R.id.url);
        toolbar.setTitle("");
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/latomediumitalic.ttf");
        textView.setTypeface(face);

        textView.setText(title);

        toolbar.setBackgroundResource(R.color.almostblack);
        setSupportActionBar(toolbar);

        webView=(WebView)findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url)
            {
                webView.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                if(progress<100 && progressBar.getVisibility()==ProgressBar.GONE)
                    progressBar.setVisibility(View.VISIBLE);

                progressBar.setProgress(progress);
                if(progress==100) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.loadUrl(url);

        //final Activity activity=this;


        //webView.loadUrl(url);


    }


    @Override
    public void onBackPressed()
    {
        if(webView.canGoBack())
            webView.goBack();

        else
            super.onBackPressed();
    }

       /* private class CustomWebView extends  WebViewClient
        {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url)
            {
                webView.loadUrl(url);
                return true;

            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError error)
            {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url)

            {



            }

        } */


        public void share(View view)

        {

            String text=title+" "+url+" \n via Now.";

            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(intent,getResources().getText(R.string.sharewith)));

        }

        public void finish(View view)
        {
            finish();

        }

}
