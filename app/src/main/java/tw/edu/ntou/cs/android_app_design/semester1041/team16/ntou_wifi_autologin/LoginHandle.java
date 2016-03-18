package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class LoginHandle {
    private Context context;

    NotificationCompat.Builder builder;
    NotificationManager mNotificationManager;
    WifiManager manager;
    String scriptRes = "";

    public LoginHandle(Context context) {
        this.context = context;
    }

    public void HandleLogin(String student_id, String password) {
        File phpRun = new File(context.getFilesDir().getPath() + "/php-cgi");
        File phpLibs = new File(context.getFilesDir().getPath() + "/libs");
        File phpScript = new File(context.getFilesDir().getPath() + "/auth_with_arg.php");

        if (phpRun.exists() == false) {
            copyAssetsExecute("php-cgi");
            phpRun.setExecutable(true);
        }
        if(phpLibs.exists() == false) {
            phpLibs.mkdirs();
            copyAssetsExecute("LIB_http.php");
            copyAssetsExecute("LIB_parse.php");

        }
        if(phpScript.exists() == false) {
            copyAssetsExecute("auth_with_arg.php");
        }

        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!isConnectedViaWifi(context)) {
            Log.e("non-wifi", "It did not use wifi");
            processNotify("尚未啟用 Wifi！");
        }
        else {
            String ssid = "";
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                ssid = wifiInfo.getSSID();
            }

            if(ssid.equals("") == false) {
                if (student_id.length() == 0) {
                    Toast.makeText(context.getApplicationContext(), "請輸入學校信箱", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() == 0) {
                    Toast.makeText(context.getApplicationContext(), "請輸入密碼", Toast.LENGTH_SHORT).show();
                }
                else {
                    //run php script
                    if(ssid.contains("nttu") || ssid.contains("ntou") || ssid.contains("NCTU") || ssid.contains("nctu"))
                        exec(context.getFilesDir().getPath() + "/php-cgi " + context.getFilesDir().getPath() + "/auth_with_arg.php " + student_id + " " + password);
                }
            }
            else {
                //notification 請確定連入的 SSID
                processNotify("請確認連入的 SSID！");
            }

        }
    }

    private void copyAssetsExecute(String fileName) {
        String appFileDirectory;
        appFileDirectory = context.getFilesDir().getPath();
        if(fileName.equals("php-cgi") == false && fileName.equals("auth_with_arg.php") == false)
            appFileDirectory = context.getFilesDir().getPath() + "/libs";
        AssetManager assets = context.getAssets();

        InputStream in;
        OutputStream out;

        try {
            in = assets.open(fileName);
            File outFile;
            outFile = new File(appFileDirectory, fileName);
            out = new FileOutputStream(outFile);

            //Apache common io libs jar https://commons.apache.org/proper/commons-io/
            IOUtils.copy(in, out);
            in.close();
            out.flush();
            out.close();

            Log.e("copy-status", "Copy success: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("error-copy", e.getMessage().toString());
        }
    }

    private boolean isConnectedViaWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    private void exec(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            ProcessWithTimeout processWithTimeout = new ProcessWithTimeout(process);
            int exitCode = processWithTimeout.waitForProcess(600000);

            if(exitCode == Integer.MIN_VALUE) {
                //Timeout
                //建立連線逾時！(notification)
                process.destroy();
                processNotify("處理登入逾時！");
            }

            else {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                int read;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }

                reader.close();
                process.waitFor();

                if (output.toString().length() != 0) {
                    Log.e("output-" + cmd, "output: " + output.toString());
                    scriptRes = output.toString();
                }
                else {
                    reader = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()));

                    buffer = new char[4096];
                    output = new StringBuffer();

                    while ((read = reader.read(buffer)) > 0) {
                        output.append(buffer, 0, read);
                    }

                    reader.close();

                    scriptRes = output.toString();
                    Log.e("output-error-" + cmd, output.toString());
                }

                scriptRes = scriptRes.trim();

                if(scriptRes.equals("It's auth or you are not in this wireless access point.")) {
                    processNotify("已經正確連上網路或是還沒認證！");
                }
                else if(scriptRes.equals("auth_success")) {
                    processNotify("登入成功！");
                }
                else if(scriptRes.equals("please input email or password")) {
                    processNotify("請設定學校信箱與密碼！");
                }
                else {
                    //processNotify("登入失敗！");
                    processNotify(scriptRes);
                    Log.e("error-msg", scriptRes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processNotify(String contentTxt) {
        builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("校園 wifi");
        builder.setSmallIcon(R.drawable.ic_stat_action_perm_scan_wifi);
        builder.setContentText(contentTxt);
        builder.setAutoCancel(true);
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        mNotificationManager.notify(1, notification);
    }
}
