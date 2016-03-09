package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
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

public class LoginHandle {
    private Context context;

    private static final String TAG = "AutoLoginService";

    private boolean checkUrlConnect = false;
    private boolean checkUrlConnect2 = false;

    static final String COOKIES_HEADER = "Set-Cookie";

    String student_id = "";
    String password = "";

    NotificationCompat.Builder builder;
    NotificationManager mNotificationManager;

    public LoginHandle(Context context) {
        this.context = context;
    }

    public void HandleLogin() {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (!isConnectedViaWifi(context)) {
            Log.e("non-wifi", "It did not use wifi");
            Toast.makeText(context, "未連線到 wifi", Toast.LENGTH_LONG).show();
            return;
        }

        if (isNetworkConnected()) {
            Log.e("is-connected", "It is on the internet.");
            Toast.makeText(context, "已經連線至網路", Toast.LENGTH_LONG).show();
            return;
        }


        checkConnectedLoginUrl("https://140.121.40.253/user/user_login_auth.jsp?");
        checkConnectedLoginUrl("https://securelogin.arubanetworks.com/cgi-bin/login");

        if(checkUrlConnect) {

            builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.application_logo).setContentTitle("正在自動登入海大校園無線網路……").setContentText("請稍候片刻").setAutoCancel(true);

            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(1, builder.build());

            Log.v(TAG, "SSID:".concat(manager.getConnectionInfo().getSSID()));

            try {
                CookieManager cookiemanager = new CookieManager();
                CookieHandler.setDefault(cookiemanager);
                URL ruckus_url = new URL("https://140.121.40.253/user/user_login_auth.jsp?");
                URL ruckus_url_2 = new URL("https://140.121.40.253/user/user_login_auth.jsp?");
                URL ruckus_url_3 = new URL("https://140.121.40.253/user/_allowuser.jsp?");
                HttpURLConnection connection = (HttpURLConnection) ruckus_url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0");
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes("username=".concat(student_id).concat("&password=".concat(password).concat("&ok=").concat("登入")));
                Map<String, List<String>> headerFields = connection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        cookiemanager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                }

                wr.flush();
                wr.close();

                HttpURLConnection connection_2 = (HttpURLConnection) ruckus_url_2.openConnection();
                connection_2.setReadTimeout(10000);
                connection_2.setConnectTimeout(15000);
                connection_2.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0");

                HttpURLConnection connection_3 = (HttpURLConnection) ruckus_url_3.openConnection();
                connection_3.setReadTimeout(10000);
                connection_3.setConnectTimeout(15000);
                connection_3.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0");

                builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.application_logo).setContentTitle("已經完成登入程序").setContentText("如仍無法上網請建檔軟體缺陷報告").setAutoCancel(true);

                mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(1, builder.build());
            }
            catch (MalformedURLException e) {
                Log.d(TAG, "MalformedURLException");
            }
            catch (IOException e) {
                Log.d(TAG, "IOException");
            }
        }
        else if(checkUrlConnect2) {
            //login url: https://secure.....
            //1. fetch magic id (html parse)
            //2. request this url e-mail and password vi http method post
            //ref: https://github.com/peter279k/wifi_login_php

            builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.application_logo).setContentTitle("正在自動登入海大校園無線網路……").setContentText("請稍候片刻").setAutoCancel(true);

            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(1, builder.build());

            OkHttpUtils.post().addParams("user", student_id)
                    .addParams("password", password)
                    .addParams("authenticate", "authenticate")
                    .addParams("accept_aup", "accept_aup")
                    .addParams("requested_url", "")
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e) {
                            Log.e("okHttp-response-error", e.getMessage());
                            builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.application_logo).setContentTitle("已經完成登入程序").setContentText("如仍無法上網請建檔軟體缺陷報告").setAutoCancel(true);
                        }

                        @Override
                        public void onResponse(String response) {
                            Log.e("okHttp-response", response);
                            builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.application_logo).setContentTitle("已經完成登入程序").setContentText("如仍無法上網請建檔軟體缺陷報告").setAutoCancel(true);
                        }
                    });

            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(1, builder.build());
        }
        else {
            builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.application_logo).setContentTitle("海大校園無線網路").setContentText("目前校園網路訊號不佳，無法登入").setAutoCancel(true);

            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(1, builder.build());
        }
    }

    private boolean isConnectedViaWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo= connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null) {
            return activeNetworkInfo.isConnected();
        }
        else {
            return false;
        }
    }

    private void checkConnectedLoginUrl(final String url) {

        OkHttpUtils.get().url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        if (url.contains("secure"))
                            checkUrlConnect2 = false;
                        else
                            checkUrlConnect = false;
                        Log.e("GET-method-error", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        if (url.contains("secure"))
                            checkUrlConnect2 = true;
                        else
                            checkUrlConnect = true;
                        Log.e("GET-method", response);
                    }
                });
    }
}
