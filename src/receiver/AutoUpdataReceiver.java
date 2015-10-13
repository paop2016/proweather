package receiver;

import service.AutoUpdataService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdataReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i=new Intent(context, AutoUpdataService.class);
		context.startService(i);
	}
	
}
