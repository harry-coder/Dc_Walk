package com.dc_walk.VollyRequestType;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dc_walk.CustomClasses.MediaPojo;
import com.dc_walk.CustomClasses.VollyErrors;
import com.dc_walk.CustomInterfaces.RequestListener;
import com.dc_walk.R;
import com.dc_walk.Volly.vollySingleton;
import com.dc_walk.VollyMultiPart.VolleyMultipartRequest;
import com.dc_walk.walk.Walk_Activity;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;


public class VollyMediaRequest {


    private byte[] byteFile;
    private RequestQueue queue;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;


    private RequestListener requestListener;

    private Context context;
    android.os.Handler handler;

    public VollyMediaRequest(Context context) {
        this.context = context;

        queue = vollySingleton.getInstance ( ).getRequestQueue ( );

        //setting notification...
        settingUpIndeterminateNotification ( );
        handler = new android.os.Handler ( );
    }


    public void setOnRequestListner(RequestListener listner) {

        this.requestListener = listner;

    }

    public void uploadMedia(final Context context, final String mediaType, final String url, final Uri uri, final Map map) {


        //mNotifyManager.notify ( 0, mBuilder.build ( ) );

        //calling on pre request listener;
        requestListener.onPreRequest ( );


        new Thread ( new Runnable ( ) {
            @Override
            public void run() {


                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest ( Request.Method.POST, url, new Response.Listener <NetworkResponse> ( ) {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        String resultResponse = new String ( response.data );
                        try {
                            JSONObject result = new JSONObject ( resultResponse );

                            System.out.println ( "This is the total response " + resultResponse );
                            final boolean status = result.getBoolean ( "success" );

                            System.out.println ( "This is the status " + status );

                            //calling on request successful

                            handler.post ( new Runnable ( ) {
                                @Override
                                public void run() {
                                    requestListener.isRequestSuccessful ( status );

                                }
                            } );


                            if (status) {


                                //  mBuilder.setContentText ( mediaType + " Successfully uploaded" );
                                requestListener.getJsonResponse ( result );


                            } else {


                                //  mBuilder.setContentText ( "Upload Failed" );

                                Toast.makeText ( context, "" + result.getString ( "msg" ), Toast.LENGTH_SHORT ).show ( );


                            }

                        } catch (JSONException e) {

                            Toast.makeText ( context, "Something went wrong please try again.", Toast.LENGTH_SHORT ).show ( );

                            //  mBuilder.setContentText ( "Upload Failed" );


                        }
                        //  mBuilder.setProgress ( 0, 0, false );
                        //mNotifyManager.notify ( 0, mBuilder.build ( ) );

                    }


                }, new Response.ErrorListener ( ) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = VollyErrors.getInstance ( ).showVollyError ( error );
                        Toast.makeText ( context, "" + errorMessage, Toast.LENGTH_SHORT ).show ( );
                        error.printStackTrace ( );

                        //  mBuilder.setContentText ( "Upload Failed" );

                        // mBuilder.setProgress ( 0, 0, false );
                        // mNotifyManager.notify ( 0, mBuilder.build ( ) );

                    }
                } ) {
                    @Override
                    protected Map <String, String> getParams() {


                        return map;
                    }

                    @Override
                    protected Map <String, DataPart> getByteData() throws IOException {
                        Map <String, DataPart> params = new HashMap <> ( );

                        byteFile = Walk_Activity.getBytes ( Objects.requireNonNull ( context.getContentResolver ( ).openInputStream ( uri ) ) );

                        if (byteFile != null) {
                            if (mediaType.equalsIgnoreCase ( "audio" )) {
                                // byteFile = Walk_Activity.getBytes ( Objects.requireNonNull ( context.getContentResolver ( ).openInputStream ( uri ) ) );

                                params.put ( "audioData", new DataPart ( "" + uri.getLastPathSegment ( ), byteFile, "video/mp4" ) );

                            } else if (mediaType.equalsIgnoreCase ( "video" )) {
                                // byteFile = Walk_Activity.getBytes ( Objects.requireNonNull ( context.getContentResolver ( ).openInputStream ( uri ) ) );

                                params.put ( "videoData", new DataPart ( "" + uri.getLastPathSegment ( ), byteFile, "video/mp4" ) );


                            } else if (mediaType.equalsIgnoreCase ( "image" )) {
                                //params.put("image1", new DataPart("" + uri.getLastPathSegment(), byteFile));
                                params.put ( "image1", new DataPart ( "" + System.currentTimeMillis ( ), byteFile ) );

                            }


                        } else {

                            Toast.makeText ( context, "No data to send", Toast.LENGTH_SHORT ).show ( );


                        }


                        System.out.println ( "This is the image map " + params );
                        return params;
                    }

                };

                int socketTimeout = 0;
                RetryPolicy policy = new DefaultRetryPolicy ( socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT );
                multipartRequest.setRetryPolicy ( policy );


                queue.add ( multipartRequest );


            }
        } ).start ( );
        requestListener.onPostRequest ( );


    }

    public byte[] convertBitMapToByteArray(Bitmap bitmap) {


        ByteArrayOutputStream stream = new ByteArrayOutputStream ( );
        bitmap.compress ( Bitmap.CompressFormat.PNG, 100, stream );
        byte[] byteArray = stream.toByteArray ( );
        bitmap.recycle ( );

        return byteArray;
    }


    private void settingUpIndeterminateNotification() {
        mNotifyManager =
                (NotificationManager) context.getSystemService ( Context.NOTIFICATION_SERVICE );
        mBuilder = new NotificationCompat.Builder ( context );
        mBuilder.setContentTitle ( "Uploading file" )
                .setContentText ( "Uploading in progress" )
                .setSmallIcon ( R.drawable.walk_logo );
        mBuilder.setProgress ( 0, 0, true );

    }


    public void uploadMultipleImages(ArrayList <MediaPojo> mediaTypeList, String imageUploadUrl, Map <String, String> map) throws InterruptedException {
        Uri uri;

        for (int i = 0; i < mediaTypeList.size ( ); i++) {
            if (mediaTypeList.get ( i ).getMediaType ( ).equalsIgnoreCase ( "image" )) {

                uri = mediaTypeList.get ( i ).getMediaUri ( );
                map.put ( "imageName1", uri.getLastPathSegment ( ) );
                uploadMedia ( context, "image", imageUploadUrl, uri, map );

                System.out.println ("Inside image" );

            } else {
                uri = mediaTypeList.get ( i ).getMediaUri ( );
                map.put ( "videoName", uri.getLastPathSegment ( ) );
                uploadMedia ( context, "video", imageUploadUrl, mediaTypeList.get ( i ).getMediaUri ( ), map );

                System.out.println ("Inside video" );

            }

        }


    }

    public void JsonRequest(final Context context, final String url, final Map map) {

        requestListener.onPreRequest ( );

        System.out.println ( "This is the object sent " + map );

        new Thread ( new Runnable ( ) {
            @Override
            public void run() {

                JsonObjectRequest request_json = new JsonObjectRequest ( Request.Method.POST, url, null,
                        new Response.Listener <JSONObject> ( ) {
                            @Override
                            public void onResponse(final JSONObject response) {

                                System.out.println ( "This is response " + response );

                                try {

                                    if (response.length ( ) != 0) {
                                        handler.post ( new Runnable ( ) {
                                            @Override
                                            public void run() {
                                                requestListener.isRequestSuccessful ( true );

                                                try {
                                                    requestListener.getJsonResponse ( response );
                                                } catch (JSONException e) {
                                                    e.printStackTrace ( );
                                                }
                                            }
                                        } );
                                    } else {

                                        handler.post ( new Runnable ( ) {
                                            @Override
                                            public void run() {

                                                requestListener.isRequestSuccessful ( false );
                                                Toast.makeText ( context, "Something went wrong try again!", Toast.LENGTH_LONG ).show ( );
                                            }
                                        } );
                                    }


                                } catch (Exception e) {

                                    Toast.makeText ( context, "" + e, Toast.LENGTH_SHORT ).show ( );
                                    e.printStackTrace ( );
                                }

                            }
                        }, new Response.ErrorListener ( ) {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        requestListener.isRequestSuccessful ( false );

                        String message = VollyErrors.getInstance ( ).showVollyError ( error );

                        Toast.makeText ( context, "" + message, Toast.LENGTH_SHORT ).show ( );
                        error.printStackTrace ( );

                    }
                } )


                {

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }


                    @Override
                    protected Map <String, String> getParams() {

                        return map;


                    }


                }; /*{
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }


                };
*/
                queue.add ( request_json );


            }
        } ).start ( );
    }


    public void StringRequest(final Context context, String url, final Map <String, String> map) {
        //  String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/download";

        requestListener.onPreRequest ( );
        StringRequest stringRequest = new StringRequest ( Request.Method.POST, url,
                new Response.Listener <String> ( ) {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject;
                        if (!response.isEmpty ( )) {

                            try {
                                jsonObject = new JSONObject ( response );
                                requestListener.isRequestSuccessful ( jsonObject.getBoolean ( "success" ) );
                                requestListener.getJsonResponse ( jsonObject );

                            } catch (JSONException e) {
                                requestListener.isRequestSuccessful ( false );

                                e.printStackTrace ( );
                            }

                        } else {
                            requestListener.isRequestSuccessful ( false );
                        }


                    }
                },
                new Response.ErrorListener ( ) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestListener.isRequestSuccessful ( false );

                        String message = VollyErrors.getInstance ( ).showVollyError ( error );

                        Toast.makeText ( context, "" + message, Toast.LENGTH_SHORT ).show ( );
                        error.printStackTrace ( );

                    }
                } ) {
            @Override
            protected Map <String, String> getParams() {


                return map;
            }

        };

        queue.add ( stringRequest );

    }

}
