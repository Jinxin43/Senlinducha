package com.dingtu.DTGIS.HTTP;

import dingtu.ZRoadMap.PubVar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHttp {
	
	private static HttpInterface singleton;

    public static HttpInterface getRetrofit(OkHttpClient client) {
        if (singleton == null) {
            synchronized (RetrofitHttp.class) {
                singleton = createRetrofit(client).create(HttpInterface.class);
            }
        }
        return singleton;
    }


    private static Retrofit createRetrofit(OkHttpClient client) {
       
//    	HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PubVar.serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

}
