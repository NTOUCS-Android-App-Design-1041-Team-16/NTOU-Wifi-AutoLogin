package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class AutoLoginService extends Service {

	private static final String TAG = "AutoLoginService";

	private boolean checkUrlConnect = false;
	private boolean checkUrlConnect2 = false;

	static final String COOKIES_HEADER = "Set-Cookie";

	String student_id = "";
	String password = "";

	NotificationCompat.Builder builder;
	NotificationManager mNotificationManager;


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
