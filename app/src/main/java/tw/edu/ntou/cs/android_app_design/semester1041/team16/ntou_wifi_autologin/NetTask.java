package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NetTask extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... params) {
        InetAddress addr = null;
        ArrayList<String> addressList = new ArrayList<String>();

        try {
            addr = InetAddress.getByName(params[0]);
            addressList.add(addr.getHostAddress());

            addr = InetAddress.getByName(params[1]);
            addressList.add(addr.getHostAddress());

            addr = InetAddress.getByName(params[2]);
            addressList.add(addr.getHostAddress());

        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return TextUtils.join(",", addressList);
    }
}