package com.buct.museumguide.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Switch;

import com.buct.museumguide.R;
import com.buct.museumguide.bean.PostResultMessage;
import com.buct.museumguide.bean.WebRequestMessage;
import com.buct.museumguide.util.WebHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.Nullable;
import okhttp3.RequestBody;

public class OnOpenGetMessage extends Service {
    private ExecutorService fixedThreadPool;
    private Runnable command;
    private Runnable setInforunnable(String url){
       return new Runnable(){
            @Override
            public void run() {
                try {
                    String res=WebHelper.getInfo(url);
                    EventBus.getDefault().post(new ResultMessage(res));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    private Runnable setStaterunnable(String url,String cookie){
        return new Runnable(){
            @Override
            public void run() {
                try {
                    String res=WebHelper.getInfoWithCookie(url,cookie);
                    EventBus.getDefault().post(new loginstatemessage(res));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    private Runnable postRunnable(String url, RequestBody body){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    String res=WebHelper.postInfo(url,body);
                    EventBus.getDefault().post(new PostResultMessage(res));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    private Runnable postRunnableWithCookie(String url,String cookie,RequestBody body){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    String res=WebHelper.postWithCookie(url,body,cookie);
                    EventBus.getDefault().post(new PostResultMessage(res));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    private Runnable setMuseumInforunnable(String url, int type) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    String res = WebHelper.getInfo(url);
                    EventBus.getDefault().post(new GetInfoResultMessage(type, res));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Subscribe
    public void getInfo(GetInfoMessage msg) {
        command = setMuseumInforunnable(msg.url, msg.type);
        fixedThreadPool.execute(command);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        fixedThreadPool = Executors.newFixedThreadPool(5);
        System.out.println("服务被创建了");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().post(new StateBroadCast(1));
        System.out.println("服务被启动了");
        /*博物馆列表信息*/
        String cookie=WebHelper.getCookie(this);
        command=setInforunnable("http://192.144.239.176:8080/api/android/get_museum_info");
        fixedThreadPool.execute(command);
        fixedThreadPool.execute(setStaterunnable(getResources().getString(R.string.get_login_state_url),cookie));
        return super.onStartCommand(intent, flags, startId);
    }

    public class binder extends Binder{
        public void getcommand(){
            System.out.println("555");
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("服务被绑定了");

        return new binder();
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }
    /*只有无cookie请求的get*/
    @Subscribe
    public void get(CommandRequest msg){
        System.out.println(msg.url);
        command=setInforunnable(msg.url);
        fixedThreadPool.execute(command);
    }
    @Subscribe
    public void getRequest(WebRequestMessage msg){
        String url=msg.url;
        int code=msg.requestcode;
        System.out.println(url+" "+code);
        switch(code){
            case 100 :
                fixedThreadPool.execute(setStaterunnable(url,msg.cookie));
                //语句
                break;
            case 200 :
                //语句
                fixedThreadPool.execute(postRunnable(url,msg.body));
                break;
            case 300:
                fixedThreadPool.execute(postRunnableWithCookie(url,msg.cookie,msg.body));
                break;
            case 400:
                command=setInforunnable(msg.url);
                fixedThreadPool.execute(command);
                break;
            default :
                break;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        fixedThreadPool.shutdown();
    }
}
