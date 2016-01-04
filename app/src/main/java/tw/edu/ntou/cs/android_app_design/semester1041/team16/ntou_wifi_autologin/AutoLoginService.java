package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AutoLoginService extends IntentService {

	public AutoLoginService(){
		super("AutoLoginService");
	}

	private static final String TAG = "AutoLoginService";

	String student_id = "";
	String password = "";

	protected void onHandleIntent(Intent i){
		Log.v(TAG, "自動登入服務啟動");
		loadPreferences();
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.application_logo).setContentTitle("正在自動登入海大校園無線網路……").setContentText("請稍候片刻");
	}

	public void onDestroy(){
		super.onDestroy();
		Log.v(TAG, "自動登入服務中止");
	}

	public void loadPreferences(){

		Log.i(TAG, "載入設定……");
		SharedPreferences config = getSharedPreferences(LoginSettingsActivity.CONFIG_FILENAME, Context.MODE_PRIVATE);

		student_id = config.getString("student_id", "");
		password = config.getString("password", "");
	}
}
