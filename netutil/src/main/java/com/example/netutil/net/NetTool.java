package netutil.src.main.java.com.example.netutil.net;

import android.content.Context;

import com.example.netutil.interfaces.NetCallBack;
import com.example.netutil.interfaces.NetInterface;

import java.io.File;
import java.util.Map;

/**
 * Created by yangshenglong on 2017/2/10.
 */
public class NetTool implements NetInterface {

    private static NetTool sNetTool;
    private NetInterface mInterface;


    public static NetTool getInstance(Context mContext) {
        //双重校验锁单例模式
        if (sNetTool == null) {
            synchronized (NetTool.class) {
                if (sNetTool == null) {
                    sNetTool = new NetTool(mContext);
                }
            }
        }
        return sNetTool;
    }

    private NetTool(Context mContext) {
        mInterface = new NetOkTool(mContext);
    }


    @Override
    public <T> void startPostRequest(Context context, String url, Map map, Class<T> tClass, NetCallBack<T> callBack) {
        mInterface.startPostRequest(context, url, map, tClass, callBack);
    }


    @Override
    public <T> void startGetRequest(Context context, String url, Class<T> tClass, NetCallBack<T> callBack) {
        mInterface.startGetRequest(context, url, tClass, callBack);

    }

    @Override
    public <T> void startFileRequest(Context context, String url, File file, Map map, Class<T> tClass, NetCallBack<T> callBack) {
        mInterface.startFileRequest(context, url, file, map, tClass, callBack);
    }


}
