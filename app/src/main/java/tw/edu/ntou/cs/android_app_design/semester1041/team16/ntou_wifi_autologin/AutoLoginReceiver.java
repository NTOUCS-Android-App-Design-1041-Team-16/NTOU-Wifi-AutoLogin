package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.DataInputStream;
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

public class AutoLoginReceiver extends BroadcastReceiver {
	private static final String TAG = "AutoLoginReceiver";
	static final String COOKIES_HEADER = "Set-Cookie";
	private String student_id;
	private String password;

	@Override
	public void onReceive(Context context, Intent intent){
		Log.v(TAG, "自動登入服務啟動");
		loadPreferences(context);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.application_logo).setContentTitle("正在自動登入海大校園無線網路……").setContentText("請稍候片刻").setAutoCancel(true);

		NotificationManager mNotificationManager =
						(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(1, builder.build());
		Bundle extra_data = intent.getExtras();
		if (extra_data != null){
			WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if(!isConnectedViaWifi(context)){
				return;
			}
			Log.v(TAG, "SSID:".concat(manager.getConnectionInfo().getSSID()));
			if(/*manager.getConnectionInfo().getSSID() != "NTOU"*/ true){
				return;
			}else{
				try {
					CookieManager cookiemanager = new CookieManager();
					CookieHandler.setDefault(cookiemanager);
					URL ruckus_url = new URL("https://140.121.40.253/user/user_login_auth.jsp?");
					URL ruckus_url_2 = new URL("https://140.121.40.253/user/user_login_auth.jsp?");
					URL ruckus_url_3 = new URL("https://140.121.40.253/user/_allowuser.jsp?");
					HttpURLConnection connection = (HttpURLConnection)ruckus_url.openConnection();
					connection.setReadTimeout(10000);
					connection.setConnectTimeout(15000);
					connection.setRequestMethod("POST");
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0");
					connection.setDoOutput(true);

					DataOutputStream wr = new 					DataOutputStream(connection.getOutputStream());
					wr.writeBytes("username=".concat(student_id).concat("&password=".concat(password).concat("&ok=").concat("登入")));
					Map<String, List<String>> headerFields = connection.getHeaderFields();
					List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

					if(cookiesHeader != null)
					{
						for (String cookie : cookiesHeader)
						{
							cookiemanager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
						}
					}

					wr.flush();
					wr.close();

					HttpURLConnection connection_2 = (HttpURLConnection)ruckus_url_2.openConnection();
					connection_2.setReadTimeout(10000);
					connection_2.setConnectTimeout(15000);
					connection_2.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0");

					HttpURLConnection connection_3 = (HttpURLConnection)ruckus_url_3.openConnection();
					connection_3.setReadTimeout(10000);
					connection_3.setConnectTimeout(15000);
					connection_3.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0");

					NotificationCompat.Builder builder2 = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.application_logo).setContentTitle("已經完成登入程序").setContentText("如仍無法上網請建檔軟體缺陷報告").setAutoCancel(true);

					NotificationManager mNotificationManager2 =
									(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

					mNotificationManager2.notify(1, builder.build());
				}catch(MalformedURLException e) {
					Log.d(TAG, "MalformedURLException");
				}	catch(IOException e){
					Log.d(TAG, "IOException");
				}
			}
		}
	}

	private boolean isConnectedViaWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}

	public void loadPreferences(Context context){

		Log.i(TAG, "載入設定……");
		SharedPreferences config = context.getSharedPreferences(LoginSettingsActivity.CONFIG_FILENAME, Context.MODE_PRIVATE);

		student_id = config.getString("student_id", "");
		password = config.getString("password", "");
	}
}
