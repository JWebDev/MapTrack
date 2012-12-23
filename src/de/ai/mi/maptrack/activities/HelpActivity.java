package de.ai.mi.maptrack.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import de.ai.mi.maptrack.R;

public class HelpActivity extends Activity {
	
	WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_web_view_activity);
	    webView = (WebView) findViewById(R.id.help_webview);
	    webView.getSettings().setJavaScriptEnabled(true);
	    webView.loadUrl("http://j-web-dev.blogspot.com"); 
	    
	}

}
