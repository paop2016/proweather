package activity;

import receiver.AutoUpdataReceiver;
import util.HttpCallbackListenner;
import util.HttpUtil;
import util.Utility;
import wang.proweather.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshWeather;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText=(TextView) findViewById(R.id.county_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherDespText=(TextView) findViewById(R.id.weather_desp);
		temp1Text=(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_date);
		switchCity=(Button) findViewById(R.id.switch_city);
		refreshWeather=(Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		String countyCode=getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)) {
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			qureyWeatherCode(countyCode);//查询天气代码
		}
		else {
			showWeather();
		}
	}
	private void qureyWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		qureyFromServer(address, "countyCode");
	}
	private void qureyWeatherInfo(String weatherCode) {
		String address= "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		qureyFromServer(address, "weatherCode");
	}
	private void qureyFromServer(String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListenner() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if(type.equals("countyCode")){
					if(!TextUtils.isEmpty(response)) {//这个判断有必要吗 存疑
						String[] array=response.split("\\|");
						if(array != null && array.length == 2) {
							String weatherCode = array[1];
							qureyWeatherInfo(weatherCode);
						}
					}
				}
				else if(type.equals("weatherCode")) {
					 Utility.handleWeatherResponse(WeatherActivity.this, response);
					 runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
			}
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("同步失败");
					}
				});
			}
		});
	}
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天"+prefs.getString("publish_time", "")+"发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent=new Intent(this, AutoUpdataReceiver.class);
		startService(intent);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent=new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)) {
				qureyWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	long time=0;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(System.currentTimeMillis()-time>2000){
			time=System.currentTimeMillis();
			Toast.makeText(this, "双击退出ProWeather", 2000).show();
		}
		else{
			finish();
		}
	}
}
