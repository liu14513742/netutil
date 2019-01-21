package netutil.src.main.java.com.example.netutil.net;

import android.content.Intent;



import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @Description:Token验证拦截
 */
public class TokenInterceptor implements Interceptor {

	private static final String TAG = TokenInterceptor.class.getSimpleName();

	private static final Charset UTF8 = Charset.forName("UTF-8");

	@Override
	public Response intercept(Chain chain) throws IOException {

		Request request = chain.request();
		Response originalResponse = chain.proceed(request);

		/**
		 * 曲线取到请求完成的数据(待考虑更好的方法)
		 */
		ResponseBody responseBody = originalResponse.body();
		BufferedSource source = responseBody.source();
		source.request(Long.MAX_VALUE); // Buffer the entire body.
		Buffer buffer = source.buffer();
		Charset charset = UTF8;
		MediaType contentType = responseBody.contentType();
		if (contentType != null) {
			charset = contentType.charset(UTF8);
		}

		String bodyString = buffer.clone().readString(charset);
		//Log.e(TAG, "response---------->" + bodyString);
		/***************************************************************************/
		//根据和服务端的约定判断token过期
		if (bodyString.contains("用户登录超时或在其他设备上登录，请重新登录！")) {
			// token认证失败
//			Intent i = new Intent(Constant.BaseReceiverAction.ACTION_TOKEN_EXPIRE);
//			MyApp.getmContext().sendBroadcast(i);
		}
		return originalResponse;
	}
}