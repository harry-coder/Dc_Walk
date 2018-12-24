package com.dc_walk.DatabaseWorkManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.dc_walk.CustomClasses.MediaPojo;
import com.dc_walk.CustomClasses.UserData;
import com.dc_walk.CustomInterfaces.RequestListener;
import com.dc_walk.Model.UserEntriesPojo;
import com.dc_walk.ObstacleDatabase.ObstacleDB;
import com.dc_walk.VollyRequestType.VollyMediaRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.paperdb.Paper;

public class EntriesUpload extends Worker {

    Context context;
    WorkerParameters workerParameters;

    VollyMediaRequest request;
    ArrayList <UserEntriesPojo> userEntryList;


    public EntriesUpload(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super ( context, workerParams );

        this.context = context;
        this.workerParameters = workerParams;

        request = new VollyMediaRequest ( context );
    }

    @NonNull
    @Override
    public Worker.Result doWork() {

        try {
            System.out.println ("Inside do work" );
            getUserEntries ( );
        } catch (IOException | JSONException e) {

            System.out.println ("Inside the exception "+e );
            e.printStackTrace ( );
        }


       // System.out.println ("Inside the jon that needs to be done" );

        return Worker.Result.SUCCESS;
    }


    public void getUserEntries() throws IOException, JSONException {

        userEntryList = (ArrayList <UserEntriesPojo>) ObstacleDB.getInstance ( context ).obstacleDao ( ).getAllUserEntries ( );


        System.out.println ( "This is the list " + userEntryList.size ( ) );
        for (int i = 0; i < userEntryList.size ( ); i++) {

            sendDataToServer ( userEntryList.get ( i ) );
        }


    }


    public void sendDataToServer(UserEntriesPojo userEntries) throws IOException, JSONException {


        String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/insert_map";


        Map <String, String> map = prepareMapToSend ( userEntries );

        request.setOnRequestListner ( new RequestListener ( ) {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onPostRequest() {

            }

            @Override
            public void isRequestSuccessful(boolean result) {

                // progressDialog.dismiss ( );

                if (result) {

                    System.out.println ( "Data sent successfully" );
                    //  Toast.makeText ( context, "Data Sent Successfully", Toast.LENGTH_SHORT ).show ( );
                } else {
                    //  Toast.makeText ( context, "Something went wrong please try again!", Toast.LENGTH_SHORT ).show ( );
                    System.out.println ( "Data Didn't successfully" );


                }

            }

            @Override
            public void getJsonResponse(JSONObject response) {

                //   mediaList.clear ( );


            }
        } );


        // request.uploadMedia ( this, "video", url, uri, map );
        request.sendMultipleMediaToServer ( (ArrayList <MediaPojo>) userEntries.getUriList ( ), url, map );

    }

    public Map <String, String> prepareMapToSend(UserEntriesPojo userEntries) {
        Map <String, String> map = new HashMap <> ( );

        try {

            String empId = Paper.book ( ).read ( "emp_id" );
            String projectId = Paper.book ( ).read ( "project_id" );
            JSONArray userDataArray = new JSONArray ( );


            map.put ( "lat", userEntries.getLat ( ) );
            map.put ( "longg", userEntries.getLon ( ) );
            map.put ( "route_no", userEntries.getRoute_no ( ) );

            map.put ( "obstacle_type", userEntries.getObstacleTypeId ( ) );
            map.put ( "structure_type", userEntries.getStructureId ( ) );


            System.out.println ( "This is the sude " + userEntries.getSide ( ) );
            map.put ( "side", userEntries.getSide ( ) );

            map.put ( "emp_id", empId );
            map.put ( "project_id", projectId );


            map.put ( "obs", userEntries.getObsRemark ( ) );


            for (int i = 0; i < userEntries.getUserDataList ( ).size ( ); i++) {

                UserData userData = userEntries.getUserDataList ( ).get ( i );

                JSONObject jsonObject = new JSONObject ( );

                jsonObject.put ( "para", userData.getParameterId ( ) );


                if (userData.getParaDropId ( ) != null) {
                    jsonObject.put ( "drop", userData.getParaDropId ( ) );
                } else {
                    jsonObject.put ( "drop", "0" );
                }


                if (userData.getParameterRemark ( ) != null) {

                    jsonObject.put ( "remark", userData.getParameterRemark ( ) );

                } else {

                    jsonObject.put ( "remark", "0" );

                }


                userDataArray.put ( jsonObject );


            }

            map.put ( "userData", " " + userDataArray );


            System.out.println ( "This is the map m sending " + map );
            System.out.println ( "This is the user array  size " + userDataArray.length ( )
            );

            //map.put ( "imageName1", fileUri.getLastPathSegment ( ) );
            //  map.put ( "videoName", uri.getLastPathSegment ( ) );



        } catch (JSONException e) {
            System.out.println ("Inside map exception "+e );
            e.printStackTrace ( );
        }

        return map;
    }

}
