package com.buct.museumguide.util;

import android.content.Context;
import android.util.Log;

import com.buct.museumguide.R;
import com.buct.museumguide.Service.CollectionMsg;
import com.buct.museumguide.Service.CommentMsg;
import com.buct.museumguide.Service.EducationMsg;
import com.buct.museumguide.Service.ExhibitionMsg;
import com.buct.museumguide.Service.MuseumInfoMsg;
import com.buct.museumguide.Service.NewsMsg;
import com.buct.museumguide.ui.home.HomeFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import okhttp3.HttpUrl;

public class RequestHelper {
    /*
        按博物馆名字模糊搜索，得到博物馆list
        museumName="",返回所有博物馆
    */
    public void getMuseumInfo(final Context activity, String name, int order) {
        String url = activity.getResources().getString(R.string.get_museum_info_url);
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if(!name.equals(""))
            urlBuilder.addQueryParameter("name", name);
        if(order != -1)
            urlBuilder.addQueryParameter("order_by", String.valueOf(order));
        EventBus.getDefault().postSticky(new MuseumInfoMsg(urlBuilder.build().toString()));
    }
    public void getExhibition(final Context activity, int id, String name) {
        String url = activity.getResources().getString(R.string.get_exhibition_info_url);
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if(id != -1)
            urlBuilder.addQueryParameter("id", String.valueOf(id));
        if(!name.equals(""))
            urlBuilder.addQueryParameter("name", name);
        EventBus.getDefault().postSticky(new ExhibitionMsg(urlBuilder.build().toString()));
    }
    public void getCollection(final Context activity, int id, String name) {
        String url = activity.getResources().getString(R.string.get_collection_info_url);
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if(id != -1)
            urlBuilder.addQueryParameter("id", String.valueOf(id));
        if(!name.equals(""))
            urlBuilder.addQueryParameter("name", name);
        EventBus.getDefault().postSticky(new CollectionMsg(urlBuilder.build().toString()));
    }
    public void getNews(final Context activity, int id, String name) {
        id = 4; //测试用!!!以后删掉!
        String url = activity.getResources().getString(R.string.get_new_info_url);
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if(id != -1)
            urlBuilder.addQueryParameter("id", String.valueOf(id));
        if(!name.equals(""))
            urlBuilder.addQueryParameter("name", name);
        EventBus.getDefault().postSticky(new NewsMsg(urlBuilder.build().toString()));
    }
    public void getEducation(final Context activity, int id, String name) {
        String url = activity.getResources().getString(R.string.get_education_activity_info_url);
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if(id != -1)
            urlBuilder.addQueryParameter("id", String.valueOf(id));
        if(!name.equals(""))
            urlBuilder.addQueryParameter("name", name);
        EventBus.getDefault().postSticky(new EducationMsg(urlBuilder.build().toString()));
    }
    // 获取评论，id是必要参数
    public void getComment(final Context activity, int id) {
        Log.d(HomeFragment.TAG, "getComment: 发送评论请求");
        String url = activity.getResources().getString(R.string.get_museum_comment_url);
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id));
        EventBus.getDefault().postSticky(new CommentMsg(urlBuilder.build().toString()));
    }
}
