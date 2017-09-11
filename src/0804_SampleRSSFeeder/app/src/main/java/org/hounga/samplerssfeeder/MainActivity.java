package org.hounga.samplerssfeeder;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

	public static String TAG = "SampleRSSFeeder";

	private static String rssUrl = "http://newssearch.naver.com/search.naver?where=rss&query=%EB%84%A4%EC%9D%B4%EB%B2%84%20%EB%89%B4%EC%8A%A4%20rss&field=0&nx_search_query=&nx_and_query=&nx_sub_query=&nx_search_hlquery=";

	Handler handler = new Handler();

	EditText edit01;
	ProgressDialog progressDialog;
	ArrayList<RSSNewsItem> newsItemList;

	RSSListView list;
	RSSListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		edit01 = (EditText) findViewById(R.id.editText);

		// create a ListView instance
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		list = new RSSListView(this);
		adapter = new RSSListAdapter(this);

		list.setAdapter(adapter);
		list.setOnDataSelectionListener(new OnDataSelectionListener() {
			@Override
			public void onDataSelected(AdapterView parent, View v, int position, long id) {
				RSSNewsItem curItem = (RSSNewsItem) adapter.getItem(position);

				String curTitle = curItem.getTitle();

				Toast.makeText(getApplicationContext(), "Selected: " + curTitle, Toast.LENGTH_LONG).show();
			}
		});

		newsItemList = new ArrayList<RSSNewsItem>();

		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		mainLayout.addView(list, params);

		Button showBtn = (Button) findViewById(R.id.showButton);
		showBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String inputStr = edit01.getText().toString();

				if (inputStr == null || inputStr.length() == 0) {
					showRSS(rssUrl);
				} else {
					showRSS(inputStr);
				}
			}
		});
	}


	private void showRSS(String urlStr) {
		Log.d(TAG, "=== showRSS ===");
		try {
			Log.d(TAG, "=== showRSS ===");
			progressDialog = ProgressDialog.show(this, "RSS Refresh", "Updating RSS information...", true, true);

			Log.d(TAG, "=== showRSS ===");
			RefreshThread thread = new RefreshThread(urlStr);
			thread.start();
		} catch (Exception e) {
			Log.e(TAG, "Error", e);
		}
	}


	class RefreshThread extends Thread {

		String urlStr;

		public RefreshThread(String str) {
			urlStr = str;
		}

		@Override
		public void run() {
			Log.d(TAG, "=== RefrechThread::run() ===");
			try {
				Log.d(TAG, "=== RefrechThread::run() ===");
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = builderFactory.newDocumentBuilder();

				URL urlForHttp = new URL(urlStr);

				InputStream inStream = getInputStreamUsingHTTP(urlForHttp);

				Document document = builder.parse(inStream);

				int countItem = processDocument(document);
				Log.d(TAG, countItem + " news item processed.");

				handler.post(updateRSSRunnable);
			} catch (Exception e) {
				Log.d(TAG, "=== RefrechThread::run() ===");
				e.printStackTrace();
			}
		}
	}

	public InputStream getInputStreamUsingHTTP(URL url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);

		int resCode = conn.getResponseCode();
		Log.d(TAG, "Response Code : " + resCode);

		InputStream instream = conn.getInputStream();

		return instream;
	}


	/**
	 * process DOM document for RSS
	 *
	 * @param doc
	 */
	private int processDocument(Document doc) {
		Log.d(TAG, "=== processDocument() ===");

		newsItemList.clear();

		Element docEle = doc.getDocumentElement();
		NodeList nodelist = docEle.getElementsByTagName("item");
		int count = 0;
		if ((nodelist != null) && (nodelist.getLength() > 0)) {
			for (int i = 0; i < nodelist.getLength(); i++) {
				RSSNewsItem newsItem = dissectNode(nodelist, i);
				if (newsItem != null) {
					Log.d(TAG, newsItem.getTitle());
					newsItemList.add(newsItem);
					count++;
				}
			}
		} else {
			Log.d(TAG, "=== processDocument() ===");
		}

		return count;
	}


	private RSSNewsItem dissectNode(NodeList nodelist, int index) {
		RSSNewsItem newsItem = null;

		try {
			Element entry = (Element) nodelist.item(index);

			Element title = (Element) entry.getElementsByTagName("title").item(0);
			Element link = (Element) entry.getElementsByTagName("link").item(0);
			Element description = (Element) entry.getElementsByTagName("description").item(0);

			NodeList pubDataNode = entry.getElementsByTagName("pubDate");
			if (pubDataNode == null) {
				pubDataNode = entry.getElementsByTagName("dc:date");
			}
			Element pubDate = (Element) pubDataNode.item(0);

			Element author = (Element) entry.getElementsByTagName("author").item(0);
			Element category = (Element) entry.getElementsByTagName("category").item(0);

			String titleValue = null;
			if (title != null) {
				Node firstChild = title.getFirstChild();
				if (firstChild != null) {
					titleValue = firstChild.getNodeValue();
				}
			}
			String linkValue = null;
			if (link != null) {
				Node firstChild = link.getFirstChild();
				if (firstChild != null) {
					linkValue = firstChild.getNodeValue();
				}
			}

			String descriptionValue = null;
			if (description != null) {
				Node firstChild = description.getFirstChild();
				if (firstChild != null) {
					descriptionValue = firstChild.getNodeValue();
				}
			}

			String pubDateValue = null;
			if (pubDate != null) {
				Node firstChild = pubDate.getFirstChild();
				if (firstChild != null) {
					pubDateValue = firstChild.getNodeValue();
				}
			}

			String authorValue = null;
			if (author != null) {
				Node firstChild = author.getFirstChild();
				if (firstChild != null) {
					authorValue = firstChild.getNodeValue();
				}
			}

			String categoryValue = null;
			if (category != null) {
				Node firstChild = category.getFirstChild();
				if (firstChild != null) {
					categoryValue = firstChild.getNodeValue();
				}
			}

			Log.d(TAG, "item node : " + titleValue + ", " + linkValue + ", " + descriptionValue +
							   ", " + pubDateValue + ", " + authorValue + ", " + categoryValue);

			newsItem = new RSSNewsItem(titleValue, linkValue, descriptionValue,
									   pubDateValue, authorValue, categoryValue);

		} catch (DOMException e) {
			e.printStackTrace();
		}

		return newsItem;
	}


	Runnable updateRSSRunnable = new Runnable() {
		public void run() {

			try {

				Resources res = getResources();
				Drawable rssIcon = res.getDrawable(R.drawable.rss_icon);
				for (int i = 0; i < newsItemList.size(); i++) {
					RSSNewsItem newsItem = (RSSNewsItem) newsItemList.get(i);
					newsItem.setIcon(rssIcon);
					adapter.addItem(newsItem);
				}

				adapter.notifyDataSetChanged();

				progressDialog.dismiss();
			} catch(Exception ex) {
				ex.printStackTrace();
			}

		}
	};
}
