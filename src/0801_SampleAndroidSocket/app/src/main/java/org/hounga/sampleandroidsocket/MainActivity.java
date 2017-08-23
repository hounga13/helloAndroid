package org.hounga.sampleandroidsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

	Button button1;
	EditText editText1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editText1 = (EditText) findViewById(R.id.editText1);

		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String address = editText1.getText().toString().trim();

				ConnectThread thread = new ConnectThread(address);
				thread.start();
			}
		});
	}

	private class ConnectThread extends Thread {
		String hostName;

		public ConnectThread(String addr) {
			hostName = addr;
		}

		@Override
		public void run() {
			try {
				int port = 5001;
				Socket sock = new Socket(hostName, port);

				ObjectOutputStream outStream = new ObjectOutputStream(sock.getOutputStream());
				outStream.writeObject("Hello hounga on Android.");
				outStream.flush();

				ObjectInputStream inStream = new ObjectInputStream(sock.getInputStream());
				String obj = (String) inStream.readObject();

				Log.d("MainActivity", "서버에서 받은 메시지: " + obj);
				Toast.makeText(getApplicationContext(), "서버에서 받은 메시지: " + obj, Toast.LENGTH_SHORT).show();

				sock.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
