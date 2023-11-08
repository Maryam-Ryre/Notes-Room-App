package com.example.bscs19072_reesnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class oneNoteView extends AppCompatActivity {

    private String pdfUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_note_view);

        pdfUrl = getIntent().getStringExtra("pdf_url");

        Toast.makeText(this, pdfUrl, Toast.LENGTH_SHORT).show();

        WebView webView = findViewById(R.id.web);

        webView.setWebViewClient(new WebViewClient());

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);

        //webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + "https://drive.google.com/drive/search?q=lab5");

        String url = "http://www.example.com";
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setPackage("com.android.chrome");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // Chrome is probably not installed
            // Try with the default browser
            i.setPackage(null);
            startActivity(i);
        }

    }
}