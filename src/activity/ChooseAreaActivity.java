package activity;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;

import db.ProWeatherDB;
import util.HttpCallbackListenner;
import util.HttpUtil;
import util.Utility;
import wang.proweather.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{
	public static final int LEVEl_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private ProWeatherDB proWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private Province selectProvince;
	private City selectCity;
	private County selectCounty;
	private int currentLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
//		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
//		if(prefs.getBoolean("city_selected", false)) {
//			Intent intent=new Intent (this,WeatherActivity.class);
//			startActivity(intent);
//			finish();
//			return;
//		}
		listView=(ListView) findViewById(R.id.list_view);
		titleText=(TextView) findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		proWeatherDB=ProWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(currentLevel==LEVEl_PROVINCE) {
					selectProvince=provinceList.get(position);
					queryCities();
				}else if(currentLevel==LEVEL_CITY) {
					selectCity=cityList.get(position);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){
					String countyCode=countyList.get(position).getCountyCode();
					Intent intent=new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
//					finish();
				}
			}
		});
		queryProvinces();
	}
	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList=proWeatherDB.loadProvinces();
		if(provinceList.size()>0){
			dataList.clear();
			for (Province province:provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel=LEVEl_PROVINCE;
		}
		else{
			queryFromServer(null, "province");
		}
	}
	private void queryCities() {
		// TODO Auto-generated method stub
		cityList=proWeatherDB.loadCity(selectProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}
		else {
			queryFromServer(selectProvince.getProvinceCode(),"city");
		}
	}
	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList=proWeatherDB.loadCounty(selectCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}
		else{
			queryFromServer(selectCity.getCityCode(),"county");
		}
	}
	private void queryFromServer(String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}
		else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListenner() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if(type.equals("province")){
					result=Utility.handleProvincesResponse(proWeatherDB, response);
				}
				else if(type.equals("city")){
					result=Utility.handleCitiesResponse(proWeatherDB, response, selectProvince.getId());
				}
				else if(type.equals("county")){
					result=Utility.handleCountiesResponse(proWeatherDB, response, selectCity.getId());
				}
				if(result){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if(type.equals("province")) {
								queryProvinces();
							}
							else if(type.equals("city")){
								queryCities();
							}
							else if(type.equals("county")){
								queryCounties();
							}
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
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", 3000).show();
					}
				});
			}
		});
	}
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	long time=0;
	public void onBackPressed(){
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}
		else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}
		else{
			if(System.currentTimeMillis()-time>2000){
				time=System.currentTimeMillis();
				Toast.makeText(this, "双击退出ProWeather", 2000).show();
			}
			else{
				finish();
			}
		}
	}
}
