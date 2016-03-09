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

    static final String COOKIES_HEADER = "Set-Cookie";

    String student_id = "";
    String password = "";

    NotificationCompat.Builder builder;
    NotificationManager mNotificationManager;
    WifiManager manager;

    public LoginHandle(Context context) {
        this.context = context;
    }

    public void HandleLogin() {
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!isConnectedViaWifi(context)) {
            Log.e("non-wifi", "It did not use wifi");
        }
        else {
            isNetworkConnected();
        }
    }

    private boolean isConnectedViaWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    private void isNetworkConnected() {
        OkHttpUtils.get().url("https://www.google.com.tw")
            .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0")
            .build()
            .execute(new StringCallback() {

                @Override
                public void onError(Call call, Exception e) {
                    Log.e("okhttp-network-status", e.getMessage());
                }

                @Override
                public void onResponse(String s) {
                    if(s.contains("台東大學無線網路驗證系統")) {
                        //checkConnectedLoginUrl("https://140.121.40.253/user/user_login_auth.jsp?");
                        //http://www.gstatic.com/generate_204
                        //http://10.1.230.254:1000/fgtauth?magic
                    }
                    else if(s.contains("海洋大學無線網路")) {
                        checkConnectedLoginUrl("https://140.121.40.253/user/user_login_auth.jsp?");
                    }
                    else if(s.contains("USERNAME")) {
                        checkConnectedLoginUrl("https://securelogin.arubanetworks.com/cgi-bin/login");
                    }
                    else {
                        builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_stat_action_perm_scan_wifi).setContentTitle("校園無線網路").setContentText("已經連上網路").setAutoCancel(true);

                        mNotificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        mNotificationManager.notify(1, builder.build());
                    }
                }
            });
    }

    private void loginWifi() {

    }

    private void checkConnectedLoginUrl(final String url) {

        OkHttpUtils.get().url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_stat_action_perm_scan_wifi).setContentTitle("校園無線網路").setContentText("目前校園網路訊號不佳，無法登入").setAutoCancel(true);

                        mNotificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        mNotificationManager.notify(1, builder.build());
                        Log.e("GET-method-error", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        if (url.contains("secure")) {
                            //login url: https://secure.....
                            //1. fetch magic id (html parse)
                            //2. request this url e-mail and password vi http method post
                            //ref: https://github.com/peter279k/wifi_login_php

                            builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_stat_action_perm_scan_wifi).setContentTitle("正在自動登入校園無線網路……").setContentText("請稍候片刻").setAutoCancel(true);

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
                                            builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_stat_action_perm_scan_wifi).setContentTitle("校園無線網路").setContentText("登入失敗").setAutoCancel(true);
                                            mNotificationManager =
                                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                                            mNotificationManager.notify(1, builder.build());
                                        }

                                        @Override
                                        public void onResponse(String response) {
                                            Log.e("okHttp-response", response);
                                            builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_stat_action_perm_scan_wifi).setContentTitle("已經完成登入程序").setContentText("如仍無法上網請建檔軟體缺陷報告").setAutoCancel(true);
                                            mNotificationManager =
                                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                                            mNotificationManager.notify(1, builder.build());
                                        }
                                    });
                        }
                        else {
                            builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_stat_action_perm_scan_wifi).setContentTitle("正在自動登入海大校園無線網路……").setContentText("請稍候片刻").setAutoCancel(true);

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
                            } catch (MalformedURLException e) {
                                Log.d(TAG, "MalformedURLException");
                            } catch (IOException e) {
                                Log.d(TAG, "IOException");
                            }
                        }
                        Log.e("GET-method", response);
                    }
                });
    }
}
