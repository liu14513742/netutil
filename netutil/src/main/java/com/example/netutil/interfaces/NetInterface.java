package netutil.src.main.java.com.example.netutil.interfaces;

import android.content.Context;

import java.io.File;
import java.util.Map;

/**
 * Created by yangshenglong on 2017/2/10.
 */

public interface NetInterface {

    //正常请求，回调判断0000状态
    <T> void startPostRequest(Context context, String url, Map map, Class<T> tClass, NetCallBack<T> callBack);

    //正常请求，回调判断0000状态
    <T> void startGetRequest(Context context, String url, Class<T> tClass, NetCallBack<T> callBack);

    //正常请求，回调判断0000状态
    <T> void startFileRequest(Context context, String url, File file, Map map, Class<T> tClass, NetCallBack<T> callBack);

}
