package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.util.Log;

public class LoginSettingsActivity extends AppCompatActivity {
	private static final String TAG = "LoginSettingsActivity";
	protected static final String CONFIG_FILENAME = "NTOU_Wifi_Autologin.LoginSettingsActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_settings);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login_settings);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		loadPreferences();
	}

	@Override
	protected void onStart(){
		super.onStart();
		loadPreferences();
	}

	@Override
	protected void onPause(){
		super.onPause();
	}

	@Override
	protected void onStop(){
		super.onStop();

		savePreferences();
	}

	public void loadPreferences(){

		Log.i(TAG, "載入設定……");
		SharedPreferences config = getSharedPreferences(CONFIG_FILENAME, Context.MODE_PRIVATE);

		((EditText) findViewById(R.id.editText_login_settings_student_id)).setText(config.getString("student_id", ""));
		((EditText) findViewById(R.id.editText_login_settings_password)).setText(config.getString("password", ""));
	}

	public void savePreferences(){
		Log.i(TAG, "保存設定……");

		SharedPreferences config = getSharedPreferences(CONFIG_FILENAME, Context.MODE_PRIVATE);

		SharedPreferences.Editor config_editor = config.edit();
		EditText student_id = (EditText) findViewById(R.id.editText_login_settings_student_id);
		//Log.i(TAG, "讀到 student_id 為：".concat(student_id.getText().toString()));
		config_editor.putString("student_id", student_id.getText().toString());
		EditText password = (EditText) findViewById(R.id.editText_login_settings_password);
		//Log.i(TAG, "讀到 password 為：".concat(password.getText().toString()));
		config_editor.putString("password", password.getText().toString());

		config_editor.commit();
	}
}
