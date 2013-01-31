package de.ai.mi.maptrack.activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import de.ai.mi.maptrack.R;
import de.ai.mi.maptrack.src.DatabaseHelper;

public class HistoryActivity extends Activity implements OnItemClickListener {

	private final static String LOG_TAG = "History";
	private final static String TRAVEL_NAME = "travelName";
	private final static String TRAVEL_DESCRIPTION = "travelDescription";

	private ArrayList<HashMap<String, Object>> historyListData;
	private ListView historyListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.history_activity);
		historyListView = (ListView) findViewById(R.id.historyListView);
		historyListData = new ArrayList<HashMap<String, Object>>();

		fillListView();

		SimpleAdapter historyListAdapter = new SimpleAdapter(this, historyListData, R.layout.history_list_view_row, new String[] { TRAVEL_NAME, TRAVEL_DESCRIPTION }, new int[] { R.id.historyTravelName,
				R.id.historyTravelDescription });

		historyListView.setAdapter(historyListAdapter);
		historyListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		historyListView.setOnItemClickListener(this);

	}

	private HashMap<String, Object> fillListView() {
		HashMap<String, Object> fillMap = null;

		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
		Cursor cursor = db.query("mytable", null, null, null, null, null, null);

		if (cursor.moveToFirst()) {

			int nameColIndex = cursor.getColumnIndex("name");
			int descriptionColIndex = cursor.getColumnIndex("description");

			do {
				fillMap = new HashMap<String, Object>();

				fillMap.put(TRAVEL_NAME, cursor.getString(nameColIndex));
				fillMap.put(TRAVEL_DESCRIPTION, cursor.getString(descriptionColIndex));
				historyListData.add(fillMap);

			} while (cursor.moveToNext());

			cursor.close();
			db.close();
		} else {
			Toast.makeText(this, "0 ROWS: ", Toast.LENGTH_LONG).show();
		}

		return fillMap;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Intent intent = new Intent(this, AkuelleReise.class);
		// startActivity(intent);
		Toast.makeText(this, "Position: " + String.valueOf(position) + ", id: " + String.valueOf(id), Toast.LENGTH_SHORT).show();
	}
}
