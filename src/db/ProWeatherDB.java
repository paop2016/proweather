package db;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProWeatherDB {
	private static final String DB_NAME = "pro_weather";
	private static final int VERSION = 1;
	private static ProWeatherDB proWeatherDB;
	private SQLiteDatabase db;
	public ProWeatherDB(Context context) {
		ProWeatherOpenHelper dbHelper=new ProWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	//返回实例
	public synchronized static ProWeatherDB getInstance(Context context) {
		if(proWeatherDB == null) {
			proWeatherDB = new ProWeatherDB(context);
		}
		return proWeatherDB;
	}
	//保存省份数据到数据库************************************************************************************************************************************************************
	public void saveProvince(Province province) {
		if(province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()) {
			do {
				Province province=new Province(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("province_name")), 
						cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		if(cursor != null)
			cursor.close();
		return list;
	}
	//保存城市数据到数据库************************************************************************************************************************************************************
	public void saveCity(City city) {
		if(city !=null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	public List<City> loadCity(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_Id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()) {
			do {
				City city = new City(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("city_name")), 
						cursor.getString(cursor.getColumnIndex("city_code")), provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		if(cursor != null)
			cursor.close();
		return list;
	}
	//保存县数据到数据库************************************************************************************************************************************************************
	public void saveCounty(County county) {
		ContentValues values = new ContentValues();
		values.put("county_name", county.getCountyName());
		values.put("county_code", county.getCountyCode());
		values.put("city_id", county.getCityId());
		db.insert("County", null, values);
	}
	public List<County> loadCounty(int cityId) {
		List<County> list=new ArrayList<County>();
		Cursor cursor=db.query("County", null, "city_Id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()) {
			do {
				County county=new County(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("county_name")), 
						cursor.getString(cursor.getColumnIndex("county_code")), cityId);
				list.add(county);
			}while(cursor.moveToNext());
		}
		if(cursor != null)
			cursor.close();
		return list;
	}
}
