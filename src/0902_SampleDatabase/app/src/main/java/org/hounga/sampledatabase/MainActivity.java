package org.hounga.sampledatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

	static String TAG = "SampleDatabase";

	boolean databaseCreated;
	boolean tableCreated;

	SQLiteDatabase db;

	String databaseName;
	String tableName;

	EditText databaseNameEditText;
	EditText tableNameEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		databaseNameEditText = (EditText) findViewById(R.id.databaseNameInput);
		tableNameEditText = (EditText) findViewById(R.id.tableNameInput);

		Button createDatabaseButton = (Button) findViewById(R.id.createDatabaseBtn);
		createDatabaseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				databaseName = databaseNameEditText.getText().toString();

				if (databaseName != null && databaseName.length() > 0) {
					createDatabase(databaseName);
				}
			}
		});


		Button createTableButton = (Button) findViewById(R.id.createTableBtn);
		createTableButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tableName = tableNameEditText.getText().toString();

				if (tableName != null && tableName.length() > 0) {
					createTable(tableName);
					Log.e(TAG, "" + insertRecord() + "records inserted.");
				}
			}
		});
	}

	private void createDatabase(String dbname) {
		Log.e(TAG, "Creating Database. [" + dbname + "]");

		db = openOrCreateDatabase(dbname, Context.MODE_PRIVATE, null);
		databaseCreated = true;
	}

	private void createTable(String tbname) {
		Log.e(TAG, "Creating Table. [" + tbname + "]");

		if (db == null) {
			Log.e(TAG, "Database is null.");
			return;
		}

		db.execSQL("create table " + tbname + "("
						   + "_id integer PRIMARY KEY autoincrement,"
						   + " ename text, "
						   + "age integer, "
						   + "phone text);");

		tableCreated = true;
	}

	private int insertRecord() {
		Log.e(TAG, "inserting records.");

		if (db == null) {
			Log.e(TAG, "Database is null.");
			return 0;
		}

		int count = 3;

		db.execSQL("insert into employee(ename, age, phone) values ('John', 20, '010-7788-1234');");
		db.execSQL("insert into employee(ename, age, phone) values ('Susan', 24, '010-3333-1222');");
		db.execSQL("insert into employee(ename, age, phone) values ('Mike', 22, '010-7668-1777');");

		return count;
	}
}
