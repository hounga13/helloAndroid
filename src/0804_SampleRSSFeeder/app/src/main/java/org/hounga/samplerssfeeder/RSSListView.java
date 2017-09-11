package org.hounga.samplerssfeeder;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by houng on 2017-09-09.
 */

public class RSSListView extends ListView {

	public static String TAG = "RSSListView";
	/**
	 * DataAdapter for this instance
	 */
	private RSSListAdapter adapter;

	/**
	 * Listener for data selection
	 */
	private OnDataSelectionListener selectionListener;

	public RSSListView(Context context) {
		super(context);

		init();
	}

	public RSSListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	/**
	 * set initial properties
	 */
	private void init() {
		// set OnItemClickListener for processing OnDataSelectionListener
		setOnItemClickListener(new OnItemClickAdapter());
	}

	/**
	 * set DataAdapter
	 *
	 * @param adapter
	 */
	public void setAdapter(BaseAdapter adapter) {
		super.setAdapter(adapter);

	}

	/**
	 * get DataAdapter
	 *
	 * @return
	 */
	public BaseAdapter getAdapter() {
		return (BaseAdapter)super.getAdapter();
	}

	/**
	 * set OnDataSelectionListener
	 *
	 * @param listener
	 */
	public void setOnDataSelectionListener(OnDataSelectionListener listener) {
		this.selectionListener = listener;
	}

	/**
	 * get OnDataSelectionListener
	 *
	 * @return
	 */
	public OnDataSelectionListener getOnDataSelectionListener() {
		return selectionListener;
	}

	class OnItemClickAdapter implements OnItemClickListener {

		public OnItemClickAdapter() {

		}

		public void onItemClick(AdapterView parent, View v, int position, long id) {

			Log.e(TAG, "onItemClick");

			if (selectionListener == null) {
				Log.e(TAG, "onItemClick - selectionListener is null.");
				return;
			}

			// get row and column
			int rowIndex = -1;
			int columnIndex = -1;

			// call the OnDataSelectionListener method
			selectionListener.onDataSelected(parent, v, position, id);

		}

	}

}
