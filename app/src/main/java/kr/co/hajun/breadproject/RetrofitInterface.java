package kr.co.hajun.breadproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {
    @GET("http://openapi.seoul.go.kr:8088/{KEY}/{TYPE}/{SERVICE}/{START_INDEX}/{END_INDEX}")
    Call<Example> getLocaldata(@Path("KEY") String key, @Path("TYPE") String type,
                               @Path("SERVICE") String service, @Path("START_INDEX") int start,
                               @Path("END_INDEX") int end);
}