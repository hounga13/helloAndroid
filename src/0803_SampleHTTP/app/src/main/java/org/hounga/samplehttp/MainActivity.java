package org.hounga.samplehttp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

	TextView textView;
	EditText inputText;
	Button requestButton;

	public static String defaultUrl = "http://m.naver.com";
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textView = (TextView) findViewById(R.id.txtMsg);
		inputText = (EditText) findViewById(R.id.input01);

		requestButton = (Button) findViewById(R.id.requestButton);
		requestButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String urlStr = inputText.getText().toString();

				ConnectThread thread = new ConnectThread(urlStr);
				thread.start();
			}
		});
	}


	class ConnectThread extends Thread {
		String urlStr;

		public ConnectThread(String inStr) {
			urlStr = inStr;
		}

		@Override
		public void run() {
			try {
				final String output = request(urlStr);
				handler.post(new Runnable() {
					@Override
					public void run() {
						textView.setText(output);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private String request(String urlStr) {
			StringBuilder output = new StringBuilder();

			try {
				URL url;
				if (urlStr.isEmpty() == true) {
					url = new URL(defaultUrl);
				} else {
					url = new URL(urlStr);
				}

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				if (connection != null) {
					connection.setConnectTimeout(10000);
					connection.setRequestMethod("GET");
					connection.setDoInput(true);
					connection.setDoOutput(true);

					int responseCode = connection.getResponseCode();

					if (responseCode == HttpURLConnection.HTTP_OK) {
						Log.e("SampleHTTP", "HTTP_OK");
					}

					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

					String line;

					while(true) {
						line = reader.readLine();
						if (line == null) {
							break;
						}

						output.append(line + "\n");
					}

					reader.close();
					connection.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("SampleHTTP", "Exception in processing response.", e);
			}

			return output.toString();
		}
	}
}
