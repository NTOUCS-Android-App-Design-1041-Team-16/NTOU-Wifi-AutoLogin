package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class LoginSettings extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_settings);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login_settings);
		setSupportActionBar(toolbar);

	}

}
