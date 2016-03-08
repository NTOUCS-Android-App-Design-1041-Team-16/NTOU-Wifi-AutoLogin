package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class AutoLoginService extends Service {

	private static final String TAG = "AutoLoginService";

	String student_id = "";
	String password = "";

	public void onDestroy(){
		super.onDestroy();
		Log.v(TAG, "自動登入服務中止");
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "自動登入服務啟動");
		loadPreferences();

		LoginHandle handleLogin = new LoginHandle(getApplicationContext());
		handleLogin.HandleLogin();

		super.onStart(intent, startId);
	}

	public void loadPreferences(){

		Log.i(TAG, "載入設定……");
		SharedPreferences config = getSharedPreferences(LoginSettingsActivity.CONFIG_FILENAME, Context.MODE_PRIVATE);

		student_id = config.getString("student_id", "");
		password = config.getString("password", "");
	}
}
