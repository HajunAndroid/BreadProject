package kr.co.hajun.breadproject;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Java 디자인 패턴 중 싱글톤 패턴이 적용되었습니다.
public class RetrofitClient {
    private static RetrofitClient instance = null;
    private static RetrofitInterface retrofitInterface;
    private static String baseUrl = "http://openapi.seoul.go.kr:8088";

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    public static RetrofitInterface getRetrofitInterface() {
        return retrofitInterface;
    }
}