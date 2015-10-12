package util;

public interface HttpCallbackListenner {
	void onFinish(String response);
	void onError(Exception e);
}
