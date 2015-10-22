package activity;

import service.AutoUpdataService;
import util.HttpCallbackListenner;
import util.HttpUtil;
import util.Utility;
import wang.proweather.R;
import android.R.integer;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;
	private RelativeLayout relativeLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshWeather;
	ImageView img1;
	ImageView img2;
	ImageView img3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout);
		cityNameText=(TextView) findViewById(R.id.county_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherDespText=(TextView) findViewById(R.id.weather_desp);
		temp1Text=(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_date);
		switchCity=(Button) findViewById(R.id.switch_city);
		refreshWeather=(Button) findViewById(R.id.refresh_weather);
		img1=(ImageView) findViewById(R.id.img1);
		img2=(ImageView) findViewById(R.id.img2);
		img3=(ImageView) findViewById(R.id.img3);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		String countyCode=getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)) {
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			qureyWeatherCode(countyCode);//��ѯ��������
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
					if(!TextUtils.isEmpty(response)) {//����ж��б�Ҫ�� ����
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
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
		String weatherDesp = prefs.getString("weather_desp", "");
		String temp1=prefs.getString("temp1", "");
		String temp2=prefs.getString("temp2", "");
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(temp1);
		temp2Text.setText(temp2);
		weatherDespText.setText(weatherDesp);
		publishText.setText("����"+prefs.getString("publish_time", "")+"����");
		currentDateText.setText(prefs.getString("current_date", ""));
		switchImage(weatherDesp,temp1,temp2);
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent=new Intent(this, AutoUpdataService.class);
		startService(intent);
	}
	private void switchImage(String weatherDesp,String temp1,String temp2) {
		// TODO Auto-generated method stub
		if(!TextUtils.isEmpty(weatherDesp)){
			String[] arr = weatherDesp.split("ת");
			if(arr.length==2) {
				img3.setVisibility(View.INVISIBLE);
				img2.setVisibility(View.VISIBLE);
				img1.setVisibility(View.VISIBLE);
				String str1=arr[0];
				String str2=arr[1];
				if(str1.equals("��")){
					img1.setImageResource(R.drawable.sunny);
				}else if(str1.equals("��")){
					img1.setImageResource(R.drawable.overcast);
				}else if(str1.equals("����")){
					img1.setImageResource(R.drawable.cloudy);
				}else if(str1.equals("С��")){
					img1.setImageResource(R.drawable.lightrain);
				}else if(str1.equals("����")){
					img1.setImageResource(R.drawable.tstorm);
				}else if(str1.equals("С��")||str1.equals("����")){
					img1.setImageResource(R.drawable.bigrain);
				}else if(str1.equals("����")){
					img1.setImageResource(R.drawable.fuchen);
				}else{
					img1.setImageResource(R.drawable.no);
				}
				if(str2.equals("��")){
					img2.setImageResource(R.drawable.sunny_night);
				}else if(str2.equals("��")){
					img2.setImageResource(R.drawable.overcast);
				}else if(str2.equals("����")){
					img2.setImageResource(R.drawable.cloudy_night);
				}else if(str2.equals("С��")){
					img2.setImageResource(R.drawable.lightrain);
				}else if(str2.equals("����")){
					img2.setImageResource(R.drawable.tstorm);
				}else if(str2.equals("С��")||str2.equals("����")){
					img2.setImageResource(R.drawable.bigrain);
				}else if(str2.equals("����")){
					img2.setImageResource(R.drawable.fuchen_night);
				}else {
					img2.setImageResource(R.drawable.no);
				}
			}
			else if(arr.length==1){
				img3.setVisibility(View.VISIBLE);
				img2.setVisibility(View.INVISIBLE);
				img1.setVisibility(View.INVISIBLE);
				String str = arr[0];
				if(str.equals("��")){
					img3.setImageResource(R.drawable.sunny);
				}else if(str.equals("��")){
					img3.setImageResource(R.drawable.overcast);
				}else if(str.equals("����")){
					img3.setImageResource(R.drawable.cloudy);
				}else if(str.equals("С��")){
					img3.setImageResource(R.drawable.lightrain);
				}else if(str.equals("����")){
					img3.setImageResource(R.drawable.tstorm);
				}else if(str.equals("С��")||str.equals("����")){
					img3.setImageResource(R.drawable.bigrain);
				}else if(str.equals("����")){
					img3.setImageResource(R.drawable.fuchen);
				}else {
					img3.setImageResource(R.drawable.no);
				}
			}
		}
//		�����¶�
		if(!TextUtils.isEmpty(temp1)&&!TextUtils.isEmpty(temp2)){
			String templow=temp1.substring(0, temp1.length()-1);
			String temphigh=temp2.substring(0, temp2.length()-1);
			int a = Integer.valueOf(templow).intValue()+Integer.valueOf(temphigh).intValue();
			if(a>=48){
				int b=(int) (Math.random()*2);
				if(b==0){
					relativeLayout.setBackgroundResource(R.drawable.jay251);
				}else{
					relativeLayout.setBackgroundResource(R.drawable.jay252);
				}
			}else if(a>=30){
				int b=(int) (Math.random()*5);
				if(b==0){
					relativeLayout.setBackgroundResource(R.drawable.jay151);
				}else if(b==1){
					relativeLayout.setBackgroundResource(R.drawable.jay152);
				}else if(b==2){
					relativeLayout.setBackgroundResource(R.drawable.jay153);
				}else{
					relativeLayout.setBackgroundResource(R.drawable.jay154);
				}
			}else if(a>=10){
				int b=(int) (Math.random()*3);
				if(b==0){
					relativeLayout.setBackgroundResource(R.drawable.jay51);
				}else if(b==1){
					relativeLayout.setBackgroundResource(R.drawable.jay52);
				}else{
					relativeLayout.setBackgroundResource(R.drawable.jay53);
				}
			}else{
				int b=(int) (Math.random()*3);
				if(b==0){
					relativeLayout.setBackgroundResource(R.drawable.jay_51);
				}else if(b==1){
					relativeLayout.setBackgroundResource(R.drawable.jay_52);
				}else{
					relativeLayout.setBackgroundResource(R.drawable.jay_53);
				}
			}
		}
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
			publishText.setText("ͬ����...");
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
			Toast.makeText(this, "˫���˳�ProWeather", 2000).show();
		}
		else{
			finish();
		}
	}
}
