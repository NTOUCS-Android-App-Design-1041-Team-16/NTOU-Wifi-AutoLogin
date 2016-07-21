package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by lee on 2016/3/30.
 */
public class execThread implements Runnable {

    private final Process process;
    private Context context;

    public execThread(Process process, Context context) {
        this.process = process;
        this.context = context;
    }
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            BufferedReader ErrReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );

            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }

            char[] buffer2 = new char[4096];
            StringBuffer output2 = new StringBuffer();
            while ((read = ErrReader.read(buffer2)) > 0) {
                output2.append(buffer2, 0, read);
            }

            reader.close();
            ErrReader.close();

            Log.e("thread-exec-msg", output.toString());

            processNotify(output.toString());

            Log.e("thread-error-msg", output2.toString());

        }
        catch (Exception e) {
            Log.e("thread-exec-error", e.toString());
        }
    }

    private void processNotify(String contentTxt) {
        NotificationCompat.Builder builder;
        NotificationManager mNotificationManager;

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
