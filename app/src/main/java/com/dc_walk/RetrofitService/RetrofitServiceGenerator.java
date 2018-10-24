package com.dc_walk.RetrofitService;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceGenerator {

    Context context;
    public static String apiBaseUrl;

    public RetrofitServiceGenerator(Context context) {
        this.context = context;
    }


    public static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            //   System.out.println("This is request body " + originalRequest.);
           /* HttpUrl originalUrl=originalRequest.url();

            HttpUrl url=originalUrl.newBuilder().addQueryParameter("abc","abc").build();

            Request request=originalRequest.newBuilder().url(url).build();*/

            return chain.proceed(originalRequest);

        }
    }).connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS);


    public static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
  //  String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/download";

    public static Retrofit.Builder builder = new Retrofit.Builder().baseUrl("http://monitorpm.feedbackinfra.com/dcnine_highways/")
            .addConverterFactory(GsonConverterFactory.create());

    public static Retrofit retrofit;

    public static void changeBaseUrl(String baseUrl) {


        apiBaseUrl = baseUrl;

    }

    public static <S> S createService(Class<S> serviceClass) {

        if (!httpClient.interceptors().contains(interceptor)) {
            httpClient.addInterceptor(interceptor);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}
