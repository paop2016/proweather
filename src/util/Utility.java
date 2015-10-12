package util;

import model.City;
import model.County;
import model.Province;
import android.text.TextUtils;
import db.ProWeatherDB;

public class Utility {
	public synchronized static boolean handleProvincesResponse(ProWeatherDB proWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(",");
			if(allProvinces != null && allProvinces.length>0){
//				for (int i = 0; i < allProvinces.length; i++) {
//					String arr[]=allProvinces[i].split("\\|");
//					//1为任意数
//					Province province=new Province(1, arr[1], arr[0]);
//					proWeatherDB.saveProvince(province);
//				}
				for(String p:allProvinces){
					String[] arr=p.split("\\|");
					Province province=new Province(1,arr[1],arr[0]);
					proWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	public synchronized static boolean handleCitiesResponse(ProWeatherDB proWeatherDB, String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities=response.split(",");
			if(allCities != null && allCities.length>0){
				for(String c:allCities){
					String[] arr=c.split("\\|");
					City city=new City(1, arr[1], arr[0], provinceId);
					proWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	public synchronized static boolean handleCountiesResponse(ProWeatherDB proWeatherDB, String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties=response.split(",");
			if(allCounties != null && allCounties.length>0){
				for(String c:allCounties){
					String[] arr=c.split("\\|");
					County county=new County(1, arr[1], arr[0], cityId);
					proWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
}
