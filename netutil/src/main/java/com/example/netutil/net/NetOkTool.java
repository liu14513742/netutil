package netutil.src.main.java.com.example.netutil.net;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.netutil.R;
import com.example.netutil.interfaces.NetCallBack;
import com.example.netutil.interfaces.NetInterface;
import com.example.netutil.utils.Utils;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Created by VolleyYang on 2017/2/10.
 */

public class NetOkTool implements NetInterface {
    private OkHttpClient mOkHttpClient;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Gson mGson;
    public static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");
    private String token = "token";
    private String user_client_token = "user_client_token";
    String rstError;
    //接口回调错误码
    int rstCode;
    private Context mContext;

    public NetOkTool(Context mContext) {
        //初始化Gson对象
        mGson = new Gson();
        //初始化对象
        this.mContext = mContext;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .addInterceptor(new TokenInterceptor())//Token拦截
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .cache(new Cache(Environment.getExternalStorageDirectory(), 10 * 1024 * 1024))
                .build();
    }


    /**
     * 调用接口，有错误码判断
     */

    @Override
    public <T> void startPostRequest(final Context context, final String url, final Map map, final Class<T> tClass, final NetCallBack<T> callBack) {

        final String jsonStr = mGson.toJson(map);

        //接口回调错误信息
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder().url(url)
                .post(requestBody)
                .build();
        Utils.showLoadingDialog(context);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Utils.hideLoadingDialog();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onError(mContext.getResources().getString(R.string.opreation_failure));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Utils.hideLoadingDialog();
                String str = response.body().string();
                Log.d("NetOkTool", str);
                final T result;
                try {
                    JSONObject jsonObject = new JSONObject(str);

                    rstCode = jsonObject.getInt("code");

                    if (Constant.KEY_SUCCESS == rstCode) {
                        result = mGson.fromJson(str, tClass);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(result);
                            }
                        });
                    } else {
                        rstError = jsonObject.getString("message");
                        callBack.onError(rstError);
                    }
                } catch (final Throwable e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onError(rstError);
                        }
                    });
                }
            }
        });
    }


    public <T> void startFileRequest(Context context, String url, File file, Map map, final Class<T> tClass, final NetCallBack<T> callBack) {
        final String jsonStr = mGson.toJson(map);
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            requestBody.addFormDataPart("file", file.getName(), body);
            requestBody.addFormDataPart("token", Constant.DEFAULTTOKEN);
            // requestBody.addFormDataPart("data[user_client_token]",SpUtils.getToken(context));

        }

        Request request = new Request.Builder().url(url).post(requestBody.build()).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Utils.hideLoadingDialog();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onError(mContext.getResources().getString(R.string.opreation_failure));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Utils.hideLoadingDialog();
                String str = response.body().string();
                final T result;
                try {
                    JSONObject jsonObject = new JSONObject(str);

                    rstCode = jsonObject.getInt("code");

                    if (Constant.KEY_SUCCESS == rstCode) {
                        result = mGson.fromJson(str, tClass);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(result);
                            }
                        });
                    } else {
                        rstError = jsonObject.getString("message");
                        callBack.onError(rstError);
                    }
                } catch (final Throwable e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onError(rstError);
                        }
                    });
                }
            }
        });
    }


    /**
     * get 请求 有返回值判断
     *
     * @param context
     * @param url
     * @param tClass
     * @param callBack
     * @param <T>
     */
    @Override
    public <T> void startGetRequest(final Context context, final String url, final Class<T> tClass, final NetCallBack<T> callBack) {

        Request request = new Request.Builder().url(url)
                .get()
                .addHeader(token, Constant.DEFAULTTOKEN)
                //.addHeader(user_client_token, SpUtils.getToken(context))
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        Utils.hideLoadingDialog();
                        callBack.onError(mContext.getString(R.string.opreation_failure));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();

                final T result;
                Utils.hideLoadingDialog();
                try {
                    JSONObject jsonObject = new JSONObject(str);

                    rstCode = jsonObject.getInt("code");
                    rstError = jsonObject.getString("message");
                    if (Constant.KEY_SUCCESS == rstCode) {
                        result = mGson.fromJson(str, tClass);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(result);
                            }
                        });
                    } else {
                        rstError = jsonObject.getString("message");
                        callBack.onError(rstError);
                    }
                } catch (final Throwable e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onError(rstError);
                        }
                    });
                }
            }
        });
    }

}
