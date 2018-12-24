package com.dc_walk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dc_walk.CustomClasses.DialogBox;
import com.dc_walk.CustomClasses.InternetConnectionCheck;
import com.dc_walk.CustomInterfaces.OnConnectivityListener;
import com.dc_walk.CustomInterfaces.RequestListener;
import com.dc_walk.DatabaseWorkManager.EntriesUpload;
import com.dc_walk.Model.ObstaclePojo;
import com.dc_walk.Model.Para_DropDown;
import com.dc_walk.Model.ParameterPojo;
import com.dc_walk.Model.PlaceInfoPojo;
import com.dc_walk.Model.StructurePojo;
import com.dc_walk.ObstacleDatabase.ObstacleDB;
import com.dc_walk.ServerLatLonDatabase.LocationDB;
import com.dc_walk.VollyRequestType.VollyMediaRequest;
import com.dc_walk.walk.Walk_Activity;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import io.paperdb.Paper;


public class Home_Activity extends AppCompatActivity implements View.OnClickListener {
    ImageButton walk_btn;
    VollyMediaRequest request;
    TextView tv_loadingMsg;
    ProgressBar pb_progressBar;
    BottomSheetBehavior bottomSheetBehavior;

    View bottomSheet;

    TextView tv_uploadData;

    WorkManager manager;
    ArrayList <PlaceInfoPojo> placeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_home );


        walk_btn = findViewById ( R.id.walk_id );
        tv_loadingMsg = findViewById ( R.id.tv_loadingMsg );
        pb_progressBar = findViewById ( R.id.pb_progressBar );

        tv_uploadData = findViewById ( R.id.tv_uploadData );
        tv_uploadData.setOnClickListener ( this );
        placeList = new ArrayList <> ( );


        // tv_attachXml = findViewById ( R.id.tv_attachXml );

       /* tv_attachXml.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent ( Intent.ACTION_OPEN_DOCUMENT );

                intent.addCategory ( Intent.CATEGORY_OPENABLE );

                intent.setType ( "application/vnd.google-earth.kml+xm" );

                startActivityForResult ( intent, 100 );
            }
        } );
*/

        bottomSheet = findViewById ( R.id.upload );
        bottomSheetBehavior = BottomSheetBehavior.from ( bottomSheet );

        request = new VollyMediaRequest ( this );


        walk_btn.setOnClickListener ( new View.OnClickListener ( ) {

            public void onClick(View v) {
                // TODO Auto-generated method stub


                if (!Paper.book ( ).exist ( "FirstTimeUser" )) {

                    Paper.book ( ).write ( "FirstTimeUser", "yes" );

                    System.out.println ( "Inside dont" );

                    sendImei ( );

                    //   getKmlPointsFromServer ( );

                    walk_btn.setEnabled ( false );
                    pb_progressBar.setVisibility ( View.VISIBLE );
                    tv_loadingMsg.setVisibility ( View.VISIBLE );

                } else {

                    System.out.println ( "Inside exist" );


                    walk_btn.setEnabled ( false );
                    pb_progressBar.setVisibility ( View.VISIBLE );
                    tv_loadingMsg.setVisibility ( View.VISIBLE );

                    Intent i = new Intent ( Home_Activity.this, Walk_Activity.class );
                    startActivity ( i );
                    overridePendingTransition ( R.anim.right_in, R.anim.left_out );

                    new Handler ( ).postDelayed ( new Runnable ( ) {
                        @Override
                        public void run() {
                            pb_progressBar.setVisibility ( View.GONE );
                            tv_loadingMsg.setVisibility ( View.GONE );
                            walk_btn.setEnabled ( true );

                        }
                    }, 2000 );
                }


            }
        } );

        runTimePermission ( );

    }

    public void checkUserEntriesRecord() {

        int totalRecords = ObstacleDB.getInstance ( this ).obstacleDao ( ).getTotalUserRecords ( );

        System.out.println ( "Inside total records " + totalRecords );

        //bottomSheetBehavior.setPeekHeight ( totalRecords >= 1 ? 120 : 0 );

        /*if(totalRecords<1){
            System.out.println ("Inside <1" );
          bottomSheetBehavior.setPeekHeight ( 0 );
        }*/

        if (totalRecords < 1) {

            System.out.println ( "Inside the peek height 0" );
            bottomSheetBehavior.setPeekHeight ( 0 );

        } else {

            System.out.println ( "Inside the peek height 120" );
            bottomSheetBehavior.setPeekHeight ( 120 );
            //    bottomSheetBehavior.setState ( BottomSheetBehavior.STATE_EXPANDED );
        }
    }

    public void sendImei() {

        String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/mobile_authentiction";


        Map <String, String> map = new HashMap <> ( );

        map.put ( "IMEI_One", getDeviceID ( ) );
        map.put ( "current_version", BuildConfig.VERSION_NAME );
        request.setOnRequestListner ( new RequestListener ( ) {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onPostRequest() {

            }

            @Override
            public void isRequestSuccessful(boolean result) {

            }

            @Override
            public void getJsonResponse(JSONObject response) throws JSONException {

                System.out.println ( "This is the response " + response );


                if (response.getBoolean ( "success" )) {

                    parseUserResponse ( response );

                    getKmlPointsFromServer ( );

                } else {

                    Toast.makeText ( Home_Activity.this, "Phone not configured!", Toast.LENGTH_SHORT ).show ( );
                }


            }
        } );

        request.StringRequest ( this, url, map );

    }


    public void parseUserResponse(JSONObject response) throws JSONException {
        JSONArray dataArray = response.getJSONArray ( "data" );


        JSONObject dataObject = dataArray.getJSONObject ( 0 );


        String empId = dataObject.getString ( "emp_id" );
        String projectId = dataObject.getString ( "project_id" );
        Paper.book ( ).write ( "emp_id", "12345" );
        Paper.book ( ).write ( "project_id", projectId );


    }


    @SuppressLint("MissingPermission")
    public String getDeviceID() {
        String deviceId;
        TelephonyManager mTelephony = (TelephonyManager) getSystemService ( Context.TELEPHONY_SERVICE );
        if (Objects.requireNonNull ( mTelephony ).getDeviceId ( ) != null) {
            deviceId = mTelephony.getDeviceId ( );
        } else {
            deviceId = Settings.Secure.getString ( getApplicationContext ( ).getContentResolver ( ), Settings.Secure.ANDROID_ID );
        }
        return deviceId;
    }

    @Override
    protected void onResume() {
        super.onResume ( );

        checkUserEntriesRecord ( );

        final IntentFilter intentFilter = new IntentFilter ( );
        intentFilter.addAction ( ConnectivityManager.CONNECTIVITY_ACTION );

        InternetConnectionCheck connectivityReceiver = new InternetConnectionCheck ( );
        registerReceiver ( connectivityReceiver, intentFilter );


        MyApplication.setConnectivityListener ( new OnConnectivityListener ( ) {
            @Override
            public void isConnectivityAvailable(boolean isConnected) {

                if (!isConnected) {

                    showInternetConnectivityDialog ( "Cancel?" );

                }

            }
        } );
    }

    public void showInternetConnectivityDialog(String message) {

        final DialogBox dialogBox = new DialogBox ( this );
        final Dialog dialog = dialogBox.setRequestedDialog ( false, R.layout.no_internet_dialog );

        TextView tv_ignore = dialog.findViewById ( R.id.tv_ignore );
        tv_ignore.setText ( message );
        tv_ignore.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                dialog.cancel ( );
            }
        } );
        dialog.show ( );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {

                System.out.println ( "This is data" );
            }

        }

    }


    public void getKmlPointsFromServer() {

        tv_loadingMsg.setText ( "Parsing Kml Response" );
        String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/map";

        String empId = Paper.book ( ).read ( "emp_id" );
        String projectId = Paper.book ( ).read ( "project_id" );

        Map <String, String> map = new HashMap <> ( );
        map.put ( "emp_id", empId );
        map.put ( "project_id", projectId );

        System.out.println ( "This is the map sent " + map );
        request.setOnRequestListner ( new RequestListener ( ) {
            @Override
            public void onPreRequest() {


            }

            @Override
            public void onPostRequest() {

            }

            @Override
            public void isRequestSuccessful(boolean result) {


            }

            @Override
            public void getJsonResponse(JSONObject response) throws JSONException {
                // System.out.println ("This is the response calling"+response );

                if (response.length ( ) != 0) {


               SaveDataToDB saveDataToDB=new SaveDataToDB ();
               saveDataToDB.execute ( response );

                  //  parseKMLItems ( response );

                    // placeInfoAadapter.setSource(serverLatLonList);


                    //   initialiseMap();


                }
            }
        } );

        request.StringRequest ( this, url, map );

        //request.JsonRequest ( this, url, map );

    }

    public void getObstacleListFromServer() {

        tv_loadingMsg.setText ( "Getting Obstacle Data" );
        String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/download";
        Map <String, String> map = new HashMap <> ( );
        map.put ( "tag", "ob" );

        request.setOnRequestListner ( new RequestListener ( ) {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onPostRequest() {

            }

            @Override
            public void isRequestSuccessful(boolean result) {

            }

            @Override
            public void getJsonResponse(JSONObject response) throws JSONException {

                boolean success = response.getBoolean ( "success" );

                if (success) {

                    ObstacleDB.getInstance ( Home_Activity.this ).obstacleDao ( ).insertObstacleData ( parseObstacleRequestReaponse ( response ) );
                    //  obstacleAadapter.setSource(parseObstacleRequestReaponse(response));

                    getStructureDataFromServer ( );
                }
            }
        } );

        request.StringRequest ( this, url, map );
    }


    public void getStructureDataFromServer() {
        tv_loadingMsg.setText ( "Getting Structure Data" );

        String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/download";
        Map <String, String> map = new HashMap <> ( );
        map.put ( "tag", "structure" );

        request.setOnRequestListner ( new RequestListener ( ) {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onPostRequest() {

            }

            @Override
            public void isRequestSuccessful(boolean result) {

            }

            @Override
            public void getJsonResponse(JSONObject response) throws JSONException {

                boolean success = response.getBoolean ( "success" );

                if (success) {

                    ObstacleDB.getInstance ( Home_Activity.this ).obstacleDao ( ).insertStructureData ( parseStructureRequestResponse ( response ) );

                    getParameterDataFromServer ( );
                }

            }
        } );

        request.StringRequest ( this, url, map );
    }

    public void getParameterDataFromServer() {
        tv_loadingMsg.setText ( "Getting Parameter Data" );


        String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/download";
        Map <String, String> map = new HashMap <> ( );
        map.put ( "tag", "parameter" );

        request.setOnRequestListner ( new RequestListener ( ) {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onPostRequest() {

            }

            @Override
            public void isRequestSuccessful(boolean result) {

            }

            @Override
            public void getJsonResponse(JSONObject response) throws JSONException {

                boolean success = response.getBoolean ( "success" );

                if (success) {
                    ObstacleDB.getInstance ( Home_Activity.this ).obstacleDao ( ).insertParameterData ( parseParameterRequestResponse ( response ) );

                    getParameterDropDataFromServer ( );

                }

            }
        } );

        request.StringRequest ( this, url, map );
    }

    public void getParameterDropDataFromServer() {

        tv_loadingMsg.setText ( "Getting DropDown Data" );

        String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/download";
        Map <String, String> map = new HashMap <> ( );
        map.put ( "tag", "para_dropdown" );

        request.setOnRequestListner ( new RequestListener ( ) {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onPostRequest() {

            }

            @Override
            public void isRequestSuccessful(boolean result) {

            }

            @Override
            public void getJsonResponse(JSONObject response) throws JSONException {


                System.out.println ( "This is the para dropDown response " + response );
                boolean success = response.getBoolean ( "success" );

                if (success) {

                    ObstacleDB.getInstance ( Home_Activity.this ).obstacleDao ( ).insertParaDropDownData ( parsePsrameterDropDownRequestResponse ( response ) );

                    Intent i = new Intent ( Home_Activity.this, Walk_Activity.class );
                    startActivity ( i );
                    overridePendingTransition ( R.anim.right_in, R.anim.left_out );

                    new Handler ( ).postDelayed ( new Runnable ( ) {
                        @Override
                        public void run() {
                            pb_progressBar.setVisibility ( View.GONE );
                            tv_loadingMsg.setVisibility ( View.GONE );
                            walk_btn.setEnabled ( true );

                        }
                    }, 2000 );


                }

            }
        } );

        request.StringRequest ( this, url, map );
    }





    /* < ----------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE BEGINNING OF METHODS WHICH USED TO PARSE SERVER RESPONSE*/

    public ArrayList <ParameterPojo> parseParameterRequestResponse(JSONObject response) throws JSONException {

        ArrayList <ParameterPojo> itemList = new ArrayList <> ( );
        JSONArray responseArray = response.getJSONArray ( "resp" );


        for (int i = 0; i < responseArray.length ( ); i++) {

            JSONObject responseObject = responseArray.getJSONObject ( i );
            ParameterPojo parameterPojo = new ParameterPojo ( );


            parameterPojo.setStructureId ( responseObject.getString ( "structure_id" ) );
            parameterPojo.setParaId ( responseObject.getString ( "para_id" ) );
            parameterPojo.setParaName ( responseObject.getString ( "para_name" ) );
            itemList.add ( parameterPojo );

        }
        return itemList;

    }

    public ArrayList <Para_DropDown> parsePsrameterDropDownRequestResponse(JSONObject response) throws JSONException {

        ArrayList <Para_DropDown> itemList = new ArrayList <> ( );
        JSONArray responseArray = response.getJSONArray ( "resp" );


        for (int i = 0; i < responseArray.length ( ); i++) {

            JSONObject responseObject = responseArray.getJSONObject ( i );
            Para_DropDown para_dropDown = new Para_DropDown ( );


            para_dropDown.setParaDropId ( responseObject.getString ( "para_drop_id" ) );
            para_dropDown.setParaId ( responseObject.getString ( "para_id" ) );
            para_dropDown.setParaDropName ( responseObject.getString ( "para_drop_name" ) );
            itemList.add ( para_dropDown );

        }
        return itemList;

    }

    public ArrayList <StructurePojo> parseStructureRequestResponse(JSONObject response) throws JSONException {

        ArrayList <StructurePojo> itemList = new ArrayList <> ( );
        JSONArray responseArray = response.getJSONArray ( "resp" );


        for (int i = 0; i < responseArray.length ( ); i++) {

            JSONObject responseObject = responseArray.getJSONObject ( i );
            StructurePojo structurePojo = new StructurePojo ( );


            structurePojo.setStructureId ( responseObject.getString ( "structure_id" ) );
            structurePojo.setObstacleId ( responseObject.getString ( "obstacle_id" ) );
            structurePojo.setStructureName ( responseObject.getString ( "structure_name" ) );
            itemList.add ( structurePojo );

        }
        return itemList;

    }


    public ArrayList <ObstaclePojo> parseObstacleRequestReaponse(JSONObject response) throws JSONException {

        ArrayList <ObstaclePojo> itemList = new ArrayList <> ( );
        JSONArray responseArray = response.getJSONArray ( "resp" );


        for (int i = 0; i < responseArray.length ( ); i++) {

            JSONObject responseObject = responseArray.getJSONObject ( i );
            ObstaclePojo pojo = new ObstaclePojo ( );

            pojo.setObstacleId ( responseObject.getString ( "obstacle_id" ) );
            pojo.setObstacleName ( responseObject.getString ( "obstacle_name" ) );
            itemList.add ( pojo );

        }
        return itemList;

    }


    public ArrayList <PlaceInfoPojo> parseResponse(JSONObject response) throws JSONException {


        JSONArray locationArray = response.getJSONArray ( "resp" );

        ArrayList <PlaceInfoPojo> placeList = new ArrayList <> ( );
        for (int i = 0; i < locationArray.length ( ); i++) {


            JSONObject locationObject = locationArray.getJSONObject ( i );

            PlaceInfoPojo placeInfoPojo = new PlaceInfoPojo ( );
            placeInfoPojo.setLat ( locationObject.getString ( "lat" ) );
            placeInfoPojo.setLng ( locationObject.getString ( "lon" ) );
            placeInfoPojo.setType ( locationObject.getString ( "type" ) );


            // System.out.println ("route_no "+locationObject.getString ( "route_no" ) );
            placeInfoPojo.setRouteNo ( Integer.parseInt ( locationObject.getString ( "route_no" ) ) );
            placeInfoPojo.setDescription ( locationObject.getString ( "description" ) );
            placeInfoPojo.setMapped ( false );

            placeList.add ( placeInfoPojo );


        }

        return placeList;

    }

/*

    @SuppressLint("StaticFieldLeak")
    public void parseKMLItems(JSONObject responseObject) {

        new AsyncTask <JSONObject, Void, Void> ( ) {
            @Override
            protected Void doInBackground(JSONObject... jsonObjects) {
                try {

                    JSONObject locationObject = jsonObjects[0];

                    JSONObject typeObject = locationObject.getJSONObject ( "resp" );

                    Iterator <String> iterator = typeObject.keys ( );

                    for (Iterator <String> it = iterator; it.hasNext ( ); ) {
                        String key = it.next ( );

                        if (key.equalsIgnoreCase ( "lineString" )) {

                            parseItems ( typeObject.getJSONArray ( "linestring" ) );

                        } else if (key.equalsIgnoreCase ( "point" )) {
                            parseItems ( typeObject.getJSONArray ( "point" ) );


                        } else if (key.equalsIgnoreCase ( "polygon" )) {


                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                saveDataToDB ( );

            }
        }.execute ( responseObject );


    }
*/


    class SaveDataToDB extends AsyncTask <JSONObject, Void, Void> {


        @Override
        protected Void doInBackground(JSONObject... jsonObjects) {
            try {
                LocationDB.getInstance ( Home_Activity.this ).myDao ( ).insertData (parseResponse ( jsonObjects[0] ) );
            } catch (JSONException e) {
                e.printStackTrace ( );
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getObstacleListFromServer ( );

        }


    }

   /* public void parseItems(JSONArray typeArray) throws JSONException {

        for (int i = 0; i < typeArray.length ( ); i++) {

            JSONObject itemsObject = typeArray.getJSONObject ( i );


            JSONArray coordinateArray = itemsObject.getJSONArray ( "coordinates" );
            for (int j = 0; j < coordinateArray.length ( ); j++) {

                PlaceInfoPojo placeInfoPojo = new PlaceInfoPojo ( );

                placeInfoPojo.setType ( itemsObject.getString ( "type" ) );
                placeInfoPojo.setDescription ( itemsObject.getString ( "description" ) );

                JSONObject coordinateObject = coordinateArray.getJSONObject ( j );

                placeInfoPojo.setLat ( coordinateObject.getString ( "lat" ) );
                placeInfoPojo.setLng ( coordinateObject.getString ( "lon" ) );

                placeInfoPojo.setRouteNo ( Integer.parseInt ( coordinateObject.getString ( "route_no" ) ) );


                placeList.add ( placeInfoPojo );

            }


        }


    }*/



    /* < ----------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE BEGINNING OF METHODS REQUIRED FOR RUNTIME PERMISSIONS*/

    public boolean runTimePermission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission ( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission ( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission ( this, android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission ( this, android.Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission ( this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission ( this, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED

                ) {
            requestPermissions ( new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 100 );

            return true;
        }
        return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText ( this, "Permission Granted", Toast.LENGTH_SHORT ).show ( );

            } else {
                runTimePermission ( );
            }

        }

    }


    @Override
    public void onClick(View view) {

        if (checkInternetConnection ( this )) {


            tv_uploadData.setText ( "Uploading.." );

            tv_uploadData.setEnabled ( false );


            Constraints myConstraints = new Constraints.Builder ( )
                    .setRequiredNetworkType ( NetworkType.CONNECTED )

                    // Many other constraints are available, see the
                    // Constraints.Builder reference
                    .build ( );
            OneTimeWorkRequest uploadUserEnries =
                    new OneTimeWorkRequest.Builder ( EntriesUpload.class ).setConstraints ( myConstraints )
                            .build ( );
            manager = WorkManager.getInstance ( );
            manager.enqueue ( uploadUserEnries );


            try {
                setObserver ( uploadUserEnries.getId ( ) );
            } catch (ExecutionException | InterruptedException e) {

                System.out.println ( "Inside interruped " + e );
                e.printStackTrace ( );
            }


        } else {

            showInternetConnectivityDialog ( "Internet Connectivity Required. Cancel?" );
            bottomSheetBehavior.setState ( BottomSheetBehavior.STATE_COLLAPSED );

        }
    }

    public void setObserver(final UUID id) throws ExecutionException, InterruptedException {


        manager.getWorkInfoByIdLiveData ( id ).observe ( this, new android.arch.lifecycle.Observer <WorkInfo> ( ) {
            @Override
            public void onChanged(@Nullable WorkInfo workStatus) {
                try {
                    performAfterTask ( Objects.requireNonNull ( workStatus ).getState ( ).toString ( ) );

                    switch (Objects.requireNonNull ( workStatus ).getState ( )) {
                        case ENQUEUED:
                            System.out.println ( "Enqueued" );
                            break;
                        case FAILED:
                            System.out.println ( "Failed" );
                            break;
                        case BLOCKED:
                            System.out.println ( "Blocked" );
                            break;
                        case RUNNING:
                            System.out.println ( "Running" );
                            break;
                        case SUCCEEDED:
                            System.out.println ( "Succeeded" );

                            break;
                        case CANCELLED:
                            System.out.println ( "Cancelled" );
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace ( );
                }
            }
        } );
        //  System.out.println ( "This is the status " + workInfo.getState ( ) );
        //  manager.getStatusById ( id ).

    }

    public void performAfterTask(final String status) {
        tv_uploadData.setText ( status );


        if (status.equalsIgnoreCase ( "SUCCEEDED" )) {
            new Handler ( ).postDelayed ( new Runnable ( ) {
                @Override
                public void run() {
                    tv_uploadData.setEnabled ( true );

                    bottomSheetBehavior.setPeekHeight ( 0 );
                    bottomSheetBehavior.setHideable ( true );
                    bottomSheetBehavior.setState ( BottomSheetBehavior.STATE_COLLAPSED );

                    ObstacleDB.getInstance ( Home_Activity.this ).obstacleDao ( ).deleteUserEntriesTable ( );
                    tv_uploadData.setText ( "Upload data" );

                }
            }, 1500 );
        }

    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService ( Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo = Objects.requireNonNull ( manager ).getActiveNetworkInfo ( );
        if (networkInfo != null && networkInfo.isConnectedOrConnecting ( ) && networkInfo.getDetailedState ( ) == NetworkInfo.DetailedState.CONNECTED) {


            return true;

        } else if (networkInfo != null && networkInfo.getDetailedState ( ) == NetworkInfo.DetailedState.DISCONNECTED) {

            return false;
        } else return false;
    }
}