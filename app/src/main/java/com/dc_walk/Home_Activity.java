package com.dc_walk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dc_walk.CustomInterfaces.RequestListener;
import com.dc_walk.Model.ObstaclePojo;
import com.dc_walk.Model.Para_DropDown;
import com.dc_walk.Model.ParameterPojo;
import com.dc_walk.Model.StructurePojo;
import com.dc_walk.ObstacleDatabase.ObstacleDB;
import com.dc_walk.VollyRequestType.VollyMediaRequest;
import com.dc_walk.walk.Walk_Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;


/**
 * Created by nitinb on 28-01-2016.
 */
public class Home_Activity extends AppCompatActivity {
    ImageButton walk_btn;
    VollyMediaRequest request;
    TextView tv_loadingMsg, tv_attachXml;
    ProgressBar pb_progressBar;


    String permit_id;
    public static String imeiSIM1;
    public static String imeiSIM2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_home );


        walk_btn = findViewById ( R.id.walk_id );
        tv_loadingMsg = findViewById ( R.id.tv_loadingMsg );
        pb_progressBar = findViewById ( R.id.pb_progressBar );
        tv_attachXml = findViewById ( R.id.tv_attachXml );

        tv_attachXml.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent ( Intent.ACTION_OPEN_DOCUMENT );

                intent.addCategory ( Intent.CATEGORY_OPENABLE );

                intent.setType ( "application/vnd.google-earth.kml+xm" );

                startActivityForResult ( intent, 100 );
            }
        } );

        request = new VollyMediaRequest ( this );


        walk_btn.setOnClickListener ( new View.OnClickListener ( ) {

            public void onClick(View v) {
                // TODO Auto-generated method stub


                if (!Paper.book ( ).exist ( "FirstTimeUser" )) {

                    Paper.book ( ).write ( "FirstTimeUser", "yes" );

                    System.out.println ( "Inside dont" );
                    getObstacleListFromServer ( );

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



                /*if (Paper.book().exist("FirstTimeUser")) {

                    boolean firstTimeUser = Paper.book().read("FirstTimeUser");
                    if (!firstTimeUser) {
                        Paper.book().write("FirstTimeUser", false);

                        System.out.println("Inside exist");
                        walk_btn.setEnabled(false);
                        pb_progressBar.setVisibility(View.VISIBLE);
                        tv_loadingMsg.setVisibility(View.VISIBLE);

                        Intent i = new Intent(Home_Activity.this, Walk_Activity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pb_progressBar.setVisibility(View.GONE);
                                tv_loadingMsg.setVisibility(View.GONE);
                                walk_btn.setEnabled(true);

                            }
                        }, 2000);
                    } else {

                        System.out.println("Inside dont");
                        getObstacleListFromServer();

                        walk_btn.setEnabled(false);
                        pb_progressBar.setVisibility(View.VISIBLE);
                        tv_loadingMsg.setVisibility(View.VISIBLE);

                    }


                }
                else {
                    System.out.println("Inside dont");
                    getObstacleListFromServer();

                    walk_btn.setEnabled(false);
                    pb_progressBar.setVisibility(View.VISIBLE);
                    tv_loadingMsg.setVisibility(View.VISIBLE);

                }*/


            }
        } );

        runTimePermission ( );

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


}