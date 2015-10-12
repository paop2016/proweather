package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListenner listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection=null;
				try {
					URL url=new URL(address);
					connection=(HttpURLConnection) url.openConnection();
					connection.setReadTimeout(8000);
					connection.setConnectTimeout(8000);
					connection.setRequestMethod("GET");
					InputStream is=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(is));
					String str;
					StringBuilder response=new StringBuilder();
					while((str=reader.readLine())!=null){
						response.append(str);
					}
					if(listener!=null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(listener!=null)
						listener.onError(e);
				}
				finally{
					if(connection!=null)
						connection.disconnect();
				}
			}
		}).start();
	}
}
