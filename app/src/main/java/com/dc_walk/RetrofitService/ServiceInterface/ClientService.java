package com.dc_walk.RetrofitService.ServiceInterface;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ClientService {

   /* @GET("/posts/{post_number}")
    Call<ResponseBody> getAllPost(@Path("post_number") int post_number);
*/
/*
    @POST("embc_app/insert_map/")
    Call<ResponseBody> postLatLon(@Body Location jsonObject);
*/

/*
    @FormUrlEncoded
    @POST("embc_app/insert_map/")
    Call<ResponseBody> postLatLon(@Field("lat") String lat, @Field("longg") String longg, @Field("route_no") String route_no);
*/


    @FormUrlEncoded
    @POST("embc_app/download")
    Call<ResponseBody> postLatLon(@FieldMap(encoded = true) Map<String, String> map);

}
