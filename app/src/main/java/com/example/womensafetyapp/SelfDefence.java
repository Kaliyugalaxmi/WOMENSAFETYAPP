package com.example.womensafetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SelfDefence extends AppCompatActivity {

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_self_defence);

        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelfDefence.this, SafetyTips.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        // Find Views by ID
        ImageView videoThumbnail1 = findViewById(R.id.video_thumbnail_1);
        ImageView videoThumbnail2 = findViewById(R.id.video_thumbnail_2);
        WebView webViewVideo1 = findViewById(R.id.webview_video_1);
        WebView webViewVideo2 = findViewById(R.id.webview_video_2);

        // Corrected YouTube Embed URLs
        String videoUrl1 = "https://www.youtube.com/embed/KVpxP3ZZtAc";
        String videoUrl2 = "https://www.youtube.com/embed/lHIqBqBt4iE";

        // Set up WebView function
        setupWebView(videoThumbnail1, webViewVideo1, videoUrl1);
        setupWebView(videoThumbnail2, webViewVideo2, videoUrl2);
    }

    private void setupWebView(ImageView thumbnail, WebView webView, String videoUrl) {
        thumbnail.setOnClickListener(v -> {
            webView.setVisibility(View.VISIBLE);
            thumbnail.setVisibility(View.GONE);

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setMediaPlaybackRequiresUserGesture(false);

            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(videoUrl);
        });
    }
}