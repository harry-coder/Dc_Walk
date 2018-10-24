package com.dc_walk.walk;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.dc_walk.CustomClasses.MediaPojo;
import com.dc_walk.CustomInterfaces.RequestListener;
import com.dc_walk.KmlDatabase.KmlDB;
import com.dc_walk.KmlDatabase.KmlItems;
import com.dc_walk.Model.ObstaclePojo;
import com.dc_walk.Model.Para_DropDown;
import com.dc_walk.Model.ParameterPojo;
import com.dc_walk.Model.PlaceInfoPojo;
import com.dc_walk.Model.StructurePojo;
import com.dc_walk.Model.UserCoordinatesPojo;
import com.dc_walk.ObstacleDatabase.ObstacleDB;
import com.dc_walk.R;
import com.dc_walk.RetrofitService.RetrofitServiceGenerator;
import com.dc_walk.RetrofitService.ServiceInterface.ClientService;
import com.dc_walk.ServerLatLonDatabase.LocationDB;
import com.dc_walk.UserCoordinatesDatabase.UserCoordinatesDB;
import com.dc_walk.Volly.vollySingleton;
import com.dc_walk.VollyRequestType.VollyMediaRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Walk_Activity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {


    Button raid_btn, locobs_btn, struct_param_done_btn, structure_btn, rem_cancel, rem_ok;
    ImageButton pointer_btn, mapclose_btn, actestclose_btn, actest_btn, struct_param_close_btn, structure_close_btn, remBtn;
    FrameLayout mapbox, struct_param_box;
    ScrollView actest, struct_box;
    //AppCompatRadioButton checkStruct_btn, checkEnv_btn, checkBuilt_btn, checkUtil_btn;
    LinearLayout remark_pop;

    boolean isObservationDialogOpen, isMapDialogOpen;

    public static String obstacleId, structureId, parameterId, paraDropId;

    public int routeNo = 0;

    private RecyclerView rv_routeContainer;
    private RecyclerView rv_checkboxContainer;
    private RecyclerView rv_structureContainer;
    private RecyclerView rv_parameterContainer;

    private VollyMediaRequest request;

    ProgressDialog progressDialog;
    ArrayList <PlaceInfoPojo> serverLatLonList;

    private PlaceInfoAadapter placeInfoAadapter;
    private ObstacleAadapter obstacleAadapter;
    private StructureAdapter structureAdapter;
    private ParameterAdapter parameterAdapter;

    EditText et_pointer, et_remarks;
    TextView tv_location;

    private static final int PLACE_PICKER_REQUEST = 1000;
    private static final int IMAGE_TAKEN_REQUEST = 2000;
    private static final int REQUEST_VIDEO_CAPTURE = 3000;


    private GoogleApiClient mClient;
    double lat, lon;
    RequestQueue queue;
    Button bt_picture;
    ArrayList <UserCoordinatesPojo> userCoordinatesArrayList;

    ArrayList <MediaPojo> mediaList;

    Uri fileUri;

    Bitmap combileBitmap, bitmapTest;
    Handler handler;
    int itemPositionClicked;

    private GoogleMap googleMap;
    private FrameLayout fl_pictureLayout;

    private Button bt_forMoreInfo, bt_forMoreObstacle, bt_finish;

    private Button bt_takePicture1, bt_takePicture2, bt_takePicture3, bt_takePicture4, bt_takePicture5, bt_takePicture6, bt_takeVideo1, bt_takeVideo2, bt_takeVideo3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.walk_details );

        request = new VollyMediaRequest ( this );

        rv_routeContainer = findViewById ( R.id.rv_routeContainer );
        rv_routeContainer.setLayoutManager ( new LinearLayoutManager ( this ) );
        rv_routeContainer.setHasFixedSize ( true );

        rv_checkboxContainer = findViewById ( R.id.rv_checkboxContainer );
        rv_checkboxContainer.setLayoutManager ( new LinearLayoutManager ( this ) );
        rv_checkboxContainer.setHasFixedSize ( true );

        rv_structureContainer = findViewById ( R.id.rv_structureContainer );
        rv_structureContainer.setLayoutManager ( new StaggeredGridLayoutManager ( 3, StaggeredGridLayoutManager.VERTICAL ) );
        rv_structureContainer.setHasFixedSize ( true );

        rv_parameterContainer = findViewById ( R.id.rv_parameterContainer );
        rv_parameterContainer.setLayoutManager ( new LinearLayoutManager ( this ) );
        rv_parameterContainer.setHasFixedSize ( true );

        progressDialog = new ProgressDialog ( this );
        serverLatLonList = new ArrayList <> ( );

        placeInfoAadapter = new PlaceInfoAadapter ( this );
        obstacleAadapter = new ObstacleAadapter ( this );
        structureAdapter = new StructureAdapter ( this );
        parameterAdapter = new ParameterAdapter ( this );

        rv_routeContainer.setAdapter ( placeInfoAadapter );
        rv_checkboxContainer.setAdapter ( obstacleAadapter );
        rv_structureContainer.setAdapter ( structureAdapter );
        rv_parameterContainer.setAdapter ( parameterAdapter );

        et_pointer = findViewById ( R.id.et_pointer );
        tv_location = findViewById ( R.id.tv_location );
        tv_location.getDrawingCache ( true );
        userCoordinatesArrayList = new ArrayList <> ( );

        fl_pictureLayout = findViewById ( R.id.fl_pictureLayout );


        bt_picture = findViewById ( R.id.bt_takePicture );

        bt_forMoreInfo = findViewById ( R.id.bt_forMoreInfo );
        bt_forMoreObstacle = findViewById ( R.id.bt_moreObstacle );
        bt_finish = findViewById ( R.id.bt_finish );

        bt_finish.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                try {
                    sendImageToServer ( );
                } catch (Exception e) {
                    e.printStackTrace ( );
                }
            }
        } );

        bt_takePicture1 = findViewById ( R.id.bt_takePicture1 );
        bt_takePicture2 = findViewById ( R.id.bt_takePicture2 );
        bt_takePicture3 = findViewById ( R.id.bt_takePicture3 );
        bt_takePicture4 = findViewById ( R.id.bt_takePicture4 );
        bt_takePicture5 = findViewById ( R.id.bt_takePicture5 );
        bt_takePicture6 = findViewById ( R.id.bt_takePicture6 );
        bt_takeVideo1 = findViewById ( R.id.bt_video1 );
        bt_takeVideo2 = findViewById ( R.id.bt_video2 );
        bt_takeVideo3 = findViewById ( R.id.bt_video3 );

        bt_takePicture1.setOnClickListener ( this );
        bt_takePicture2.setOnClickListener ( this );
        bt_takePicture3.setOnClickListener ( this );
        bt_takePicture4.setOnClickListener ( this );
        bt_takePicture5.setOnClickListener ( this );
        bt_takePicture6.setOnClickListener ( this );
        bt_takeVideo1.setOnClickListener ( this );
        bt_takeVideo2.setOnClickListener ( this );
        bt_takeVideo3.setOnClickListener ( this );


        mediaList = new ArrayList <> ( );


//        et_remarks = findViewById ( R.id.et_remarks );


        queue = vollySingleton.getInstance ( ).getRequestQueue ( );
        handler = new android.os.Handler ( );


        //connecting the app with google client for place picker
        mClient = new GoogleApiClient
                .Builder ( this )
                .addApi ( Places.GEO_DATA_API )
                .addApi ( Places.PLACE_DETECTION_API )
                .build ( );

        tv_location.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                launchPlacePickerDialog ( );
                tv_location.setEnabled ( false );

                handler.postDelayed ( new Runnable ( ) {
                    @Override
                    public void run() {
                        tv_location.setEnabled ( true );
                    }
                }, 3000 );

            }
        } );

        bt_picture.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                fl_pictureLayout.setVisibility ( View.VISIBLE );

                struct_param_box.setVisibility ( View.GONE );


                // invokeCamera ( );
            }
        } );


        //taking runtime permissions
        //runTimePermission();

        //   pointer_btn = findViewById(R.id.pointerId);
        mapbox = findViewById ( R.id.mapBoxId );
        mapclose_btn = findViewById ( R.id.mapCloseId );
        //   locobs_btn = findViewById(R.id.locObs_Id);
        actest = findViewById ( R.id.actest );
        // actest_btn = findViewById ( R.id.actest_done );
        actestclose_btn = findViewById ( R.id.actest_close );

    /*    checkUtil_btn = findViewById(R.id.check_utilId);
        checkBuilt_btn = findViewById(R.id.check_builtId);
        checkEnv_btn = findViewById(R.id.check_envId);
        checkStruct_btn = findViewById(R.id.check_structId);
*/
        structure_close_btn = findViewById ( R.id.structure_close_id );
        //   structure_btn = findViewById(R.id.structure_id);
        struct_box = findViewById ( R.id.pop_structId );

        struct_param_close_btn = findViewById ( R.id.struct_param_close_id );
        struct_param_done_btn = findViewById ( R.id.struct_param_done_id );
        struct_param_box = findViewById ( R.id.struct_param_id );

        //remBtn = findViewById(R.id.remButton);
        remark_pop = findViewById ( R.id.rem_pop1 );
        // rem_cancel = findViewById(R.id.rem_pop1_cancel);
        // rem_ok = findViewById(R.id.rem_pop1_ok);
        // pointer_btn=findViewById(R.id)

       /* pointer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapbox.setVisibility(View.VISIBLE);

            }

        });*/

        mapclose_btn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                mapbox.setVisibility ( View.GONE );
            }

        } );

        /*locobs_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actest.setVisibility(View.VISIBLE);
            }

        });
        */

        /*actest_btn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                actest.setVisibility ( View.GONE );

                //  sendImageToServer ( );


                updateServerLatLonDb ( );

                //deleting the fields when user presses the send button
                //et_remarks.setText ( "" );
                tv_location.setText ( "" );
                lat = 0;
                lon = 0;


                //   et_pointer.setText("");

                //  sendUserCoordinatesDataToServer();

                //  sendLocationDetails();

                //  sendLocationToServer();

                //sendStringToServer();

            }

        } );
        */

        actestclose_btn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                actest.setVisibility ( View.GONE );
            }

        } );

        /*checkEnv_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    struct_box.setVisibility(View.VISIBLE);
                }
            }
        });*/

      /*  checkEnv_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    // perform logic

                }
            }
        });*/

        structure_close_btn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                struct_box.setVisibility ( View.GONE );
            }

        } );

        /*structure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                struct_param_box.setVisibility(View.VISIBLE);

            }

        });*/

        /*struct_param_done_btn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                struct_param_box.setVisibility ( View.GONE );
            }

        } );
        */

        struct_param_close_btn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                struct_param_box.setVisibility ( View.GONE );
            }
        } );


    }


    @Override
    public void onClick(View view) {

        int id = view.getId ( );
        switch (id) {

            case R.id.bt_takePicture1: {
                invokeCamera ( );
                bt_takePicture1.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;

            }
            case R.id.bt_takePicture2: {
                invokeCamera ( );
                bt_takePicture2.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_takePicture3: {
                invokeCamera ( );
                bt_takePicture3.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_takePicture4: {
                invokeCamera ( );
                bt_takePicture4.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_takePicture5: {
                invokeCamera ( );
                bt_takePicture4.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_takePicture6: {
                invokeCamera ( );
                bt_takePicture4.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_video1: {
                invokeVideoRecorder ( );
                bt_takeVideo1.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_video2: {
                invokeVideoRecorder ( );
                bt_takeVideo2.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_video3: {
                invokeVideoRecorder ( );
                bt_takeVideo3.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }

        }

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction ( );
        int keyCode = event.getKeyCode ( );

        if (isMapDialogOpen) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                if (action == KeyEvent.ACTION_DOWN) {

                    mapbox.setVisibility ( View.GONE );
                    isMapDialogOpen = false;
                    return true;
                }

            }
        } else if (isObservationDialogOpen) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                if (action == KeyEvent.ACTION_DOWN) {

                    tv_location.setText ( "" );
                    isObservationDialogOpen = false;
                    actest.setVisibility ( View.GONE );
                    return true;
                }

            }
        }

        return super.dispatchKeyEvent ( event );


    }


    /* < ----------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE BEGINNING OF METHODS REQUIRED FOR CAMARE TO OPEN AND PREPARE IMAGE TO SEND TO SERVER*/


    private void invokeVideoRecorder() {
        Intent takeVideoIntent = new Intent ( MediaStore.ACTION_VIDEO_CAPTURE );
        takeVideoIntent.putExtra ( android.provider.MediaStore.EXTRA_VIDEO_QUALITY, 0 );
        if (takeVideoIntent.resolveActivity ( getPackageManager ( ) ) != null) {

            startActivityForResult ( takeVideoIntent, REQUEST_VIDEO_CAPTURE );
        }
    }

    private void invokeCamera() {

        Intent camIntent = new Intent ( "android.media.action.IMAGE_CAPTURE" );
        fileUri = Uri.fromFile ( getOutputMediaFile ( ) );


        camIntent.putExtra ( android.provider.MediaStore.EXTRA_OUTPUT, fileUri );

        camIntent.putExtra ( "return-data", true );

        camIntent.putExtra ( MediaStore.EXTRA_OUTPUT, fileUri );

        startActivityForResult ( camIntent, IMAGE_TAKEN_REQUEST );


    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File ( Environment.getExternalStoragePublicDirectory (
                Environment.DIRECTORY_PICTURES ), "Dc_Walk" );

        if (!mediaStorageDir.exists ( )) {
            if (!mediaStorageDir.mkdirs ( )) {
                return null;

            }
        }


        String timeStamp = new SimpleDateFormat ( "yyyyMMdd_HHmmss" ).format ( new Date ( ) );
        File file = new File ( mediaStorageDir.getPath ( ) + File.separator +
                "IMG_" + timeStamp + ".jpg" );


        return file;

    }


    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream ( );
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read ( buffer )) != -1) {
            byteBuffer.write ( buffer, 0, len );
        }

        byteBuffer.close ( );

        return byteBuffer.toByteArray ( );


    }





    /* < ----------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE GOOGLE PLACE PICKER DIALOG METHOD AND ITS ACTIVITY RESULT*/

    private void launchPlacePickerDialog() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder ( );
        try {

            startActivityForResult ( builder.build ( Walk_Activity.this ), PLACE_PICKER_REQUEST );

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace ( );
        }
    }

    //In this method we are taking user-coordinates
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace ( Objects.requireNonNull ( data ), this );

                lat = place.getLatLng ( ).latitude;
                lon = place.getLatLng ( ).longitude;


                String address = Objects.requireNonNull ( place.getAddress ( ) ).toString ( );


                UserCoordinatesPojo userCoordinatesPojo = new UserCoordinatesPojo ( );
                userCoordinatesPojo.setLat ( lat );
                userCoordinatesPojo.setLon ( lon );
                userCoordinatesPojo.setRoute ( Double.parseDouble ( et_pointer.getText ( ).toString ( ) ) );


                userCoordinatesArrayList.add ( userCoordinatesPojo );

                tv_location.setText ( address );


                insertUserCoordinatesToDatabase ( userCoordinatesPojo );

            }
        } else if (requestCode == IMAGE_TAKEN_REQUEST) {
            if (resultCode == RESULT_OK) {

                setMediaUri ( fileUri, "image" );

                /*Bitmap background = null;


                try {
                    background = MediaStore.Images.Media.getBitmap ( getContentResolver ( ), fileUri );
                } catch (IOException e) {
                    e.printStackTrace ( );
                }
                System.out.println ( "This is file uri " + background );

                bitmapTest = combineBitMap ( background );*/


                Toast.makeText ( this, "Image ready to send", Toast.LENGTH_SHORT ).show ( );

                //  sendImageToServer ( );
            }

        } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Uri videoUri = data.getData ( );

                setMediaUri ( videoUri, "video" );


            }

        }
    }

    public void setMediaUri(Uri uri, String mediaType) {
        MediaPojo mediaPojo = new MediaPojo ( );
        mediaPojo.setMediaUri ( uri );
        mediaPojo.setMediaType ( mediaType );

        mediaList.add ( mediaPojo );

    }


    public Bitmap combineBitMap(Bitmap src) {

        // Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.yourimage); // the original file yourimage.jpg i added in resources
        Bitmap dest = Bitmap.createBitmap ( src.getWidth ( ), src.getHeight ( ), Bitmap.Config.ARGB_8888 );

        String yourText = "This is lat lon with time";

        Canvas cs = new Canvas ( dest );
        Paint tPaint = new Paint ( );
        tPaint.setTextSize ( 35 );
        tPaint.setColor ( Color.BLUE );
        tPaint.setStyle ( Paint.Style.FILL );
        cs.drawBitmap ( src, 0f, 0f, null );
        float height = tPaint.measureText ( "yY" );
        float width = tPaint.measureText ( yourText );
        float x_coord = (src.getWidth ( ) - width) / 2;
        cs.drawText ( yourText, x_coord, height + 15f, tPaint ); // 15f is to put space between top edge and the text, if you want to change it, you can
        try {
            dest.compress ( Bitmap.CompressFormat.JPEG, 100, new FileOutputStream ( getOutputMediaFile ( ) ) );
            // dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ( );
        }
        return dest;
    }

    /*public Bitmap combineImages(Bitmap background, Bitmap foreground) {

        int width = 0, height = 0;


        width = getWindowManager ( ).getDefaultDisplay ( ).getWidth ( );
        height = getWindowManager ( ).getDefaultDisplay ( ).getHeight ( );

        combileBitmap = Bitmap.createBitmap ( width ,height, Bitmap.Config.ARGB_8888 );
        Canvas comboImage = new Canvas ( combileBitmap );
        background = Bitmap.createScaledBitmap ( background, width, height, true );
        comboImage.drawBitmap ( background, 0, 0, null );
        comboImage.drawBitmap ( foreground, 0, 0, null );

        return combileBitmap;
    }*/


    /* < ----------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE BEGINNING OF ACTIVITY LIFECYCLE METHODS*/

    @Override
    protected void onStart() {
        super.onStart ( );


        initialiseMap ( );

        /*if (!Paper.book().exist("FirstTimeDownload")) {
            //  getPlaceLatLon();

            initialiseMap();

            Paper.book().write("FirstTimeDownload", true);


        } else {

            readInfoFromDatabase();


        }*/
        mClient.connect ( );

        getObstacleItemsFromDatabase ( );


        //   getObstacleListFromServer();
        // request.sendStringToServer();
        //  getObstacleFromServer();
        //getObstacleListFromServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ( );

        mClient.disconnect ( );
    }




    /* < ----------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE BEGINNING OF METHODS REQUIRED FOR VARIOUS SERVER REQUESTS*/


    public void getPlaceLatLon() {

        String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/map";

        Map <String, String> map = new HashMap <> ( );
        map.put ( "request_by", "FEDCO" );

        request.setOnRequestListner ( new RequestListener ( ) {
            @Override
            public void onPreRequest() {

                progressDialog.setMessage ( "Getting Requested Points" );
                progressDialog.show ( );

            }

            @Override
            public void onPostRequest() {

            }

            @Override
            public void isRequestSuccessful(boolean result) {

                progressDialog.dismiss ( );
            }

            @Override
            public void getJsonResponse(JSONObject response) throws JSONException {

                if (response.length ( ) != 0) {


                    serverLatLonList = parseResponse ( response );

                    insertDataToDataBase ( serverLatLonList );

                    // placeInfoAadapter.setSource(serverLatLonList);


                    //   initialiseMap();


                }
            }
        } );

        request.JsonRequest ( this, url, map );

    }

    public void sendImageToServer() throws InterruptedException {

        String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/insert_map";


        Map <String, String> map = prepareMapToSend ( );

        request.setOnRequestListner ( new RequestListener ( ) {
            @Override
            public void onPreRequest() {

                progressDialog.setMessage ( "Sending image to server.." );
                progressDialog.show ( );
            }

            @Override
            public void onPostRequest() {

            }

            @Override
            public void isRequestSuccessful(boolean result) {

                progressDialog.dismiss ( );

                if (result) {

                    Toast.makeText ( Walk_Activity.this, "Data Sent Successfully", Toast.LENGTH_SHORT ).show ( );
                } else {
                    Toast.makeText ( Walk_Activity.this, "Something went wrong please try again!", Toast.LENGTH_SHORT ).show ( );


                }

            }

            @Override
            public void getJsonResponse(JSONObject response) throws JSONException {

                mediaList.clear ();

            }
        } );


        // request.uploadMedia ( this, "video", url, uri, map );
        request.uploadMultipleImages ( mediaList, url, map );

    }

    public Map <String, String> prepareMapToSend() {

        Map <String, String> map = new HashMap <> ( );
        map.put ( "lat", "" + lat );
        map.put ( "longg", "" + lon );
        map.put ( "route_no", et_pointer.getText ( ).toString ( ) );

        map.put ( "obstacle_type", obstacleId );
        map.put ( "structure_type", structureId );
        map.put ( "parameter_type", parameterId );
        map.put ( "paraDrop_type", paraDropId );


        //map.put ( "imageName1", fileUri.getLastPathSegment ( ) );
        //  map.put ( "videoName", uri.getLastPathSegment ( ) );


        return map;

    }


    //this is retrofit url-encoded-request
    public void getiItemsObstacleListFromServer() {


        Map <String, String> map = new HashMap <> ( );
        map.put ( "tag", "ob" );

        ClientService clientService = RetrofitServiceGenerator.createService ( ClientService.class );

        Call <ResponseBody> call = clientService.postLatLon ( map );

        call.enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {

                if (response.isSuccessful ( )) {
                    Toast.makeText ( Walk_Activity.this, "Data Submitted Successfully", Toast.LENGTH_SHORT ).show ( );
                } else {
                    Toast.makeText ( Walk_Activity.this, "Request was not successful " + response.message ( ), Toast.LENGTH_SHORT ).show ( );

                }


            }

            @Override
            public void onFailure(Call <ResponseBody> call, Throwable t) {

                System.out.println ( "This is the error " + t.getMessage ( ) );

                tv_location.setText ( "" );

            }
        } );


    }






    /* < ----------------------------------------------------------------------------------------------------------->*/

    /* THIS IS THE BEGINNING OF MAP RELATED METHODS FOR PARSING KML AND PLOTTING COORDINATES*/
    public ArrayList <PlaceInfoPojo> parseResponse(JSONObject response) throws JSONException {
        JSONArray locationArray = response.getJSONArray ( "success" );
        ArrayList <PlaceInfoPojo> placeList = new ArrayList <> ( );
        for (int i = 0; i < locationArray.length ( ); i++) {

            JSONObject locationObject = locationArray.getJSONObject ( i );
            PlaceInfoPojo placeInfoPojo = new PlaceInfoPojo ( );
            placeInfoPojo.setLat ( locationObject.getString ( "lat" ) );
            placeInfoPojo.setLng ( locationObject.getString ( "lon" ) );
            placeInfoPojo.setRouteNo ( Integer.parseInt ( locationObject.getString ( "route_no" ) ) );
            placeInfoPojo.setMapped ( false );

            placeList.add ( placeInfoPojo );


        }

        return placeList;

    }

    public ArrayList <KmlItems> parseKmlCoordinates(KmlLayer layer, GoogleMap googleMap) {
        ArrayList <KmlItems> list = new ArrayList <> ( );

        LatLng latLng;

        KmlContainer container = layer.getContainers ( ).iterator ( ).next ( );
        for (KmlContainer nestedContainer : container.getContainers ( )) {
            for (KmlPlacemark placemark : nestedContainer.getPlacemarks ( )) {


                KmlItems items = new KmlItems ( );

                items.setRouteNo ( Integer.parseInt ( placemark.getProperty ( "name" ) ) );
                items.setDescription ( placemark.getProperty ( "description" ) );
                items.setMapped ( false );


                if (placemark.getGeometry ( ) instanceof KmlPoint) {
                    KmlPoint point = (KmlPoint) placemark.getGeometry ( );

                    items.setLat ( String.valueOf ( point.getGeometryObject ( ).latitude ) );
                    items.setLng ( String.valueOf ( point.getGeometryObject ( ).longitude ) );


                }

                list.add ( items );
            }

        }
        // googleMap.moveCamera ( CameraUpdateFactory.newLatLngZoom ( new LatLng ( Double.parseDouble (kmlCoordinates.get ( 0 ).getLng ()),Double.parseDouble (kmlCoordinates.get ( 0 ).getLat ())), 15 ) );


        //this is for storing all the coordinates into db on main thread. You can convert it into background thread according to your need.
        KmlDB.getInstance ( this ).kmlDao ( ).insertKmlItems ( list );

        return list;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        KmlLayer layer = null;
        ArrayList <KmlItems> kmlCoordinates;


        googleMap.getUiSettings ( ).setIndoorLevelPickerEnabled ( true );
        googleMap.getUiSettings ( ).setZoomControlsEnabled ( true );
        googleMap.getUiSettings ( ).setMapToolbarEnabled ( true );
        googleMap.getUiSettings ( ).setZoomGesturesEnabled ( true );
        googleMap.getUiSettings ( ).setScrollGesturesEnabled ( true );
        googleMap.getUiSettings ( ).setTiltGesturesEnabled ( true );
        googleMap.getUiSettings ( ).setRotateGesturesEnabled ( true );

        googleMap.setMyLocationEnabled ( true );
        googleMap.getUiSettings ( ).setMyLocationButtonEnabled ( true );
        googleMap.getUiSettings ( ).setCompassEnabled ( true );


        try {
            layer = new KmlLayer ( googleMap, R.raw.test, getApplicationContext ( ) );
            layer.addLayerToMap ( );

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace ( );
        }

        if (!Paper.book ( ).exist ( "KmlCoordinatesParsed" )) {

            Paper.book ( ).write ( "KmlCoordinatesParsed", "yes" );
            System.out.println ( "Inside very" );

            parseKmlCoordinates ( layer, googleMap );

            kmlCoordinates = (ArrayList <KmlItems>) getKmlCoordinatres ( );

            placeInfoAadapter.setSource ( kmlCoordinates );

        } else {


            System.out.println ( "Regular" );
            kmlCoordinates = (ArrayList <KmlItems>) getKmlCoordinatres ( );

            placeInfoAadapter.setSource ( kmlCoordinates );

        }

/*

        final PatternItem DOT = new Dot ( );
        final PatternItem GAP = new Gap ( 5 );

        Marker marker = null;
*/

/*
        // Create a stroke pattern of a gap followed by a dot.
        final List <PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList ( GAP, DOT );


        ArrayList <LatLng> latLngArrayList = getArrayListOfLatLon ( kmlCoordinates );

        Polyline polyline;
        PolylineOptions polylineOptions = new PolylineOptions ( ).width ( 8 ).color ( Color.BLUE ).geodesic ( true );
        for (int i = 0; i < latLngArrayList.size ( ); i++) {

            polylineOptions.add ( latLngArrayList.get ( i ) );

            MarkerOptions markerOptions = new MarkerOptions ( );
            markerOptions.position ( latLngArrayList.get ( i ) );
            markerOptions.title ( "Route " + (i + 1) );

            // markerOptions.snippet("Blah");


            if (i == 0) {

                markerOptions.icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_BLUE ) );
                markerOptions.title ( "Route Starting" );
                // googleMap.addMarker(new MarkerOptions().position(latLngArrayList.get(i)).title("Route " + i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


            } else if (i == latLngArrayList.size ( ) - 1) {

                //   googleMap.addMarker(new MarkerOptions().position(latLngArrayList.get(i)).title("Route " + i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                markerOptions.icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_GREEN ) );
                markerOptions.title ( "Route Ending" );

            }

            marker = googleMap.addMarker ( markerOptions );


        }


        polyline = googleMap.addPolyline ( polylineOptions );
        polyline.setPattern ( PATTERN_POLYLINE_DOTTED );*/


        googleMap.setMapType ( GoogleMap.MAP_TYPE_HYBRID );


        googleMap.moveCamera ( CameraUpdateFactory.newLatLngZoom ( new LatLng ( Double.parseDouble ( kmlCoordinates.get ( routeNo ).getLat ( ) ), Double.parseDouble ( kmlCoordinates.get ( routeNo ).getLng ( ) ) ), 17 ) );


        readUserCoordinates ( googleMap );


    }

    public List <KmlItems> getKmlCoordinatres() {

        return KmlDB.getInstance ( this ).kmlDao ( ).getKmlCoordinates ( );
    }

    public void drawUserCoordinatesPolyLineOnMap(GoogleMap googleMap, ArrayList <UserCoordinatesPojo> coordinatesList) {


        ArrayList <LatLng> list = getUserCoordinateList ( coordinatesList );

        System.out.println ( "This is usercoordinates size " + list.size ( ) );

        if (list.size ( ) != 0) {
            Polyline polyline;
            PolylineOptions polylineOptions = new PolylineOptions ( ).width ( 10 ).color ( Color.RED ).geodesic ( true );
            for (int i = 0; i < list.size ( ); i++) {

                polylineOptions.add ( list.get ( i ) );

                MarkerOptions markerOptions = new MarkerOptions ( );
                markerOptions.position ( list.get ( i ) );
                markerOptions.icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_MAGENTA ) );

                markerOptions.title ( "position " + (coordinatesList.get ( i ).getRoute ( )) );
                markerOptions.snippet ( "This is some remarks " );

                googleMap.addMarker ( markerOptions );

            }

            polyline = googleMap.addPolyline ( polylineOptions );
            //    polyline.setPattern(PATTERN_POLYLINE_DOTTED);
            googleMap.moveCamera ( CameraUpdateFactory.newLatLngZoom ( list.get ( 0 ), 17 ) );
        }

    }

    public ArrayList <LatLng> getUserCoordinateList(ArrayList <UserCoordinatesPojo> userCoordinatesList) {

        if (userCoordinatesList != null) {
            System.out.println ( "This is inside user1" );
            ArrayList <LatLng> list = new ArrayList <> ( );
            for (int i = 0; i < userCoordinatesList.size ( ); i++) {


                // System.out.println("This is user lat "+);
                list.add ( new LatLng ( userCoordinatesList.get ( i ).getLat ( ), userCoordinatesList.get ( i ).getLon ( ) ) );


            }
            return list;
        }
        return null;
    }

    public ArrayList <LatLng> getArrayListOfLatLon(ArrayList <KmlItems> kmlItems) {

        ArrayList <LatLng> list = new ArrayList <> ( );
        for (int i = 0; i < kmlItems.size ( ); i++) {

            list.add ( new LatLng ( Double.parseDouble ( kmlItems.get ( i ).getLng ( ) ), Double.parseDouble ( kmlItems.get ( i ).getLat ( ) ) ) );


        }
        return list;

    }

    public void initialiseMap() {

        FragmentManager supportFragmentManager = getSupportFragmentManager ( );
        Fragment fragment = supportFragmentManager.findFragmentById ( R.id.map );
        SupportMapFragment supportmapfragment = (SupportMapFragment) fragment;

        Objects.requireNonNull ( supportmapfragment ).getMapAsync ( Walk_Activity.this );


    }





    /* < ----------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE BEGINNING OF VARIOUS DATABASES METHODS TO STORE AND READ DATA*/

    public void getObstacleItemsFromDatabase() {

        new Thread ( new Runnable ( ) {
            @Override
            public void run() {

                obstacleAadapter.setSource ( (ArrayList <ObstaclePojo>) ObstacleDB.getInstance ( Walk_Activity.this ).obstacleDao ( ).getObstacleItems ( ) );


            }
        } ).start ( );

    }


    public void updateServerLatLonDb() {

        new Thread ( new Runnable ( ) {
            @Override
            public void run() {
                System.out.println ( "This is item clicked " + itemPositionClicked );

                KmlDB.getInstance ( Walk_Activity.this ).kmlDao ( ).updateKmlItems ( true, (itemPositionClicked + 1) );

                // placeInfoAadapter.notifyDataSetChanged ( );


                // LocationDB.getInstance ( Walk_Activity.this ).myDao ( ).getUpdatedList ( true, (itemPositionClicked + 1) );

                //      System.out.println("This is route no. "+pojo.getRouteNo());
                //    System.out.println("This is isMapped. "+pojo.isMapped);

                placeInfoAadapter.setSource ( (ArrayList <KmlItems>) KmlDB.getInstance ( Walk_Activity.this ).kmlDao ( ).getKmlCoordinates ( ) );


            }
        } ).start ( );


    }


    public void insertUserCoordinatesToDatabase(final UserCoordinatesPojo userCoordinatesPojo) {
        new Thread ( new Runnable ( ) {
            @Override
            public void run() {
                UserCoordinatesDB.getInstance ( Walk_Activity.this ).myDao ( ).insertData ( userCoordinatesPojo );

            }
        } ).start ( );

    }

    public void readInfoFromDatabase() {

        new Thread ( new Runnable ( ) {
            @Override
            public void run() {

                ///  serverLatLonList = (ArrayList<PlaceInfoPojo>) LocationDB.getInstance(Walk_Activity.this).myDao().getUserList();
                System.out.println ( "This is return list size " + serverLatLonList.size ( ) );

                //  placeInfoAadapter.setSource(serverLatLonList);


            }
        } ).start ( );

        //      initialiseMap();

    }

    public void insertDataToDataBase(final ArrayList <PlaceInfoPojo> list) {
        new Thread ( new Runnable ( ) {
            @Override
            public void run() {
                LocationDB.getInstance ( Walk_Activity.this ).myDao ( ).insertData ( list );

            }
        } ).start ( );

    }

    @SuppressLint("StaticFieldLeak")
    public void readUserCoordinates(final GoogleMap googleMap) {

        new AsyncTask <Void, Void, ArrayList <UserCoordinatesPojo>> ( ) {
            @Override
            protected ArrayList <UserCoordinatesPojo> doInBackground(Void... voids) {
                return (ArrayList <UserCoordinatesPojo>) UserCoordinatesDB.getInstance ( Walk_Activity.this ).myDao ( ).getUserCoordinatesList ( );

            }

            @Override
            protected void onPostExecute(ArrayList <UserCoordinatesPojo> userCoordinatesPojos) {

                System.out.println ( "This is very size " + userCoordinatesPojos.size ( ) );

                if (userCoordinatesPojos.size ( ) != 0) {
                    drawUserCoordinatesPolyLineOnMap ( googleMap, userCoordinatesPojos );

                } else {

                    Toast.makeText ( Walk_Activity.this, "No user plotted coordinates", Toast.LENGTH_SHORT ).show ( );
                }
            }
        }.execute ( );


    }









    /* < -------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE BEGINNING OF THE ADAPTERS FOR VARIOUS DATASETS*/

    //this class plots the data obtained from the kml
   /* public class PlaceInfoAadapter extends RecyclerView.Adapter<PlaceInfoAadapter.PlaceHolder> {

        ArrayList<PlaceInfoPojo> placesInfoList = new ArrayList<>();

        LayoutInflater inflater;
        Context context;

        PlaceInfoAadapter(Context context) {
            inflater = LayoutInflater.from(context);

            this.context = context;

        }

        @Override
        public PlaceInfoAadapter.PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.route_info_single_item, parent, false);

            return new PlaceInfoAadapter.PlaceHolder(view);
        }

        @Override
        public void onBindViewHolder(final PlaceInfoAadapter.PlaceHolder holder, final int position) {

            holder.tv_route.setText("" + placesInfoList.get(position).getRouteNo());

            System.out.println("This is the mapped status " + placesInfoList.get(position).isMapped());
            if (placesInfoList.get(position).isMapped()) {

                holder.im_location.setImageDrawable(getResources().getDrawable(R.drawable.btn_loc_actv));
            } else {
                holder.im_location.setImageDrawable(getResources().getDrawable(R.drawable.btn_loc));


            }

            holder.im_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    routeNo = holder.getAdapterPosition();
                    isMapDialogOpen = true;
                    mapbox.setVisibility(View.VISIBLE);

                    holder.im_location.setImageDrawable(getResources().getDrawable(R.drawable.btn_loc_actv));
                    initialiseMap();


                }

            });

        }


        @Override
        public int getItemCount() {

            //  System.out.println("This is the size " + placesInfoList.size());
            return placesInfoList.size();

        }

        public void setSource(ArrayList<PlaceInfoPojo> list) {
            if (list.size() != 0) {
                this.placesInfoList = list;

                notifyItemRangeRemoved(0, placesInfoList.size());

                notifyDataSetChanged();
            }

        }

        public class PlaceHolder extends RecyclerView.ViewHolder {
            TextView tv_route;
            ImageButton im_location;
            Button bt_skip, bt_observation;


            public PlaceHolder(View itemView) {
                super(itemView);

                tv_route = itemView.findViewById(R.id.tv_route);
                im_location = itemView.findViewById(R.id.im_location);
                bt_skip = itemView.findViewById(R.id.bt_skip);
                bt_observation = itemView.findViewById(R.id.bt_observation);

                bt_observation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (!placesInfoList.get(getAdapterPosition()).isMapped) {

                            showObservationDialog();
                        } else {

                            showDialog();

                        }

                    }
                });


            }

            public void showObservationDialog() {
                actest.setVisibility(View.VISIBLE);
                et_pointer.setText("" + placesInfoList.get(getAdapterPosition()).getRouteNo());

                isObservationDialogOpen = true;
                itemPositionClicked = getAdapterPosition();

            }

            public void showDialog() {

                new AlertDialog.Builder(Walk_Activity.this)
                        .setTitle("Already observed?")
                        .setMessage("Route you are trying to access has been already observed. Would you like to observe it again?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                showObservationDialog();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }


    }
*/

    public class PlaceInfoAadapter extends RecyclerView.Adapter <PlaceInfoAadapter.PlaceHolder> {

        ArrayList <KmlItems> kmlItemsList = new ArrayList <> ( );

        LayoutInflater inflater;
        Context context;

        PlaceInfoAadapter(Context context) {
            inflater = LayoutInflater.from ( context );

            this.context = context;

        }

        @Override
        public PlaceInfoAadapter.PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate ( R.layout.route_info_single_item, parent, false );

            return new PlaceInfoAadapter.PlaceHolder ( view );
        }

        @Override
        public void onBindViewHolder(final PlaceInfoAadapter.PlaceHolder holder, final int position) {

            holder.tv_route.setText ( "" + kmlItemsList.get ( position ).getRouteNo ( ) );

            //   System.out.println ( " This is route " + kmlItemsList.get ( position ).getRouteNo ( ) );

            //  System.out.println("This is the mapped status " + kmlItemsList.get(position).isMapped());
            if (kmlItemsList.get ( position ).isMapped ( )) {

                holder.im_location.setImageDrawable ( getResources ( ).getDrawable ( R.drawable.btn_loc_actv ) );
            } else {
                holder.im_location.setImageDrawable ( getResources ( ).getDrawable ( R.drawable.btn_loc ) );


            }

            holder.im_location.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {

                    routeNo = holder.getAdapterPosition ( );
                    isMapDialogOpen = true;
                    mapbox.setVisibility ( View.VISIBLE );

                    holder.im_location.setImageDrawable ( getResources ( ).getDrawable ( R.drawable.btn_loc_actv ) );

                    onMapReady ( googleMap );

                }

            } );


            holder.im_info.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {

                    showInfoDialog ( kmlItemsList.get ( position ).getDescription ( ) );

                }
            } );
        }


        public void showInfoDialog(String message) {

            new AlertDialog.Builder ( Walk_Activity.this )
                    .setTitle ( "Information" )
                    .setMessage ( message )

                    .setNegativeButton ( "Close", new DialogInterface.OnClickListener ( ) {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss ( );
                        }
                    } )
                    .show ( );
        }

        @Override
        public int getItemCount() {

            // System.out.println("This is the size " + kmlItemsList.size());
            return kmlItemsList.size ( );

        }

        public void setSource(ArrayList <KmlItems> list) {
            if (list.size ( ) != 0) {
                this.kmlItemsList = list;

                notifyItemRangeRemoved ( 0, kmlItemsList.size ( ) );

                notifyDataSetChanged ( );
            }

        }


        public class PlaceHolder extends RecyclerView.ViewHolder {
            TextView tv_route;
            ImageButton im_location;
            ImageView im_info;
            Button bt_skip, bt_observation;


            public PlaceHolder(View itemView) {
                super ( itemView );

                tv_route = itemView.findViewById ( R.id.tv_route );
                im_location = itemView.findViewById ( R.id.im_location );
                bt_skip = itemView.findViewById ( R.id.bt_skip );
                bt_observation = itemView.findViewById ( R.id.bt_observation );
                im_info = itemView.findViewById ( R.id.im_info );

                bt_observation.setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View v) {


                        getRouteNumbers ( );
                        /*if (!kmlItemsList.get ( getAdapterPosition ( ) ).isMapped ( )) {


                        } else {

                            showDialog ( );

                        }*/

                    }
                } );


            }


            public void getRouteNumbers() {
                int currentRoute = kmlItemsList.get ( getAdapterPosition ( ) ).getRouteNo ( );
                List <Double> list;
                System.out.println ( "This is the current Route number " + currentRoute );

                if (currentRoute == 1) {
                    list = UserCoordinatesDB.getInstance ( context ).myDao ( ).getFirstRouteNoList ( currentRoute );
                } else {
                    list = UserCoordinatesDB.getInstance ( context ).myDao ( ).getrouteNoList ( (currentRoute - 1), currentRoute );
                }
                System.out.println ( "This is list size " + list.size ( ) );
                if (list.size ( ) != 0) {
                    for (double route : list) {
                        System.out.println ( "This is route " + route );
                    }

                    showObservationDialog ( getProperRouteNo ( list ) );


                } else {

                    showObservationDialog ( (currentRoute + 0.1) );

                }


            }

            public double getProperRouteNo(List <Double> list) {

                return ((Collections.max ( list )) + 0.1);
            }

            public void showObservationDialog(double route) {

                System.out.println ( "This is the final route " + route );
                // getRouteNumbers ( );
                actest.setVisibility ( View.VISIBLE );
                et_pointer.setText ( " " + Math.round ( route * 10 ) / 10.0 );

                isObservationDialogOpen = true;
                itemPositionClicked = getAdapterPosition ( );

            }


        }


    }

    //this class plots the obstacle obtain from the server and then fetches the data from db later on.
    public class ObstacleAadapter extends RecyclerView.Adapter <ObstacleAadapter.ObstacleHolder> {

        ArrayList <ObstaclePojo> obstacleInfoList = new ArrayList <> ( );

        LayoutInflater inflater;
        Context context;

        ObstacleAadapter(Context context) {
            inflater = LayoutInflater.from ( context );

            this.context = context;

        }

        @Override
        public ObstacleAadapter.ObstacleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate ( R.layout.checkbox_single_item, parent, false );

            return new ObstacleAadapter.ObstacleHolder ( view );
        }

        @Override
        public void onBindViewHolder(final ObstacleAadapter.ObstacleHolder holder, final int position) {

            holder.cb_obstacle.setText ( obstacleInfoList.get ( position ).getObstacleName ( ) );


            holder.cb_obstacle.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener ( ) {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        obstacleId = obstacleInfoList.get ( position ).getObstacleId ( );

                        getStructureData ( );

                    }
                }
            } );


        }

        public void getStructureData() {

            structureAdapter.setSource ( (ArrayList <StructurePojo>) ObstacleDB.getInstance ( context ).obstacleDao ( ).getStructureItems ( obstacleId ) );
            struct_box.setVisibility ( View.VISIBLE );
            actest.setVisibility ( View.GONE );


        }

        @Override
        public int getItemCount() {

            //  System.out.println("This is the size " + placesInfoList.size());
            return obstacleInfoList.size ( );

        }

        public void setSource(ArrayList <ObstaclePojo> list) {
            if (list.size ( ) != 0) {
                this.obstacleInfoList = list;

                notifyItemRangeRemoved ( 0, obstacleInfoList.size ( ) );

                notifyDataSetChanged ( );
            }

        }

        public class ObstacleHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_obstacle;

            public ObstacleHolder(View itemView) {
                super ( itemView );

                cb_obstacle = itemView.findViewById ( R.id.cb_obstacle );


            }


        }

    }

    public class StructureAdapter extends RecyclerView.Adapter <StructureAdapter.StructureHolder> {

        ArrayList <StructurePojo> structureList = new ArrayList <> ( );

        LayoutInflater inflater;
        Context context;


        StructureAdapter(Context context) {
            inflater = LayoutInflater.from ( context );

            this.context = context;

        }

        @Override
        public StructureAdapter.StructureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate ( R.layout.structure_single_item, parent, false );

            return new StructureAdapter.StructureHolder ( view );
        }

        @Override
        public void onBindViewHolder(final StructureAdapter.StructureHolder holder, final int position) {

            holder.bt_structure.setText ( structureList.get ( position ).getStructureName ( ) );

            holder.bt_structure.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {

                    structureId = structureList.get ( position ).getStructureId ( );

                    getParameterData ( );
                }
            } );


        }

        public void getParameterData() {

            parameterAdapter.setSource ( (ArrayList <ParameterPojo>) ObstacleDB.getInstance ( context ).obstacleDao ( ).getParameterItems ( structureId ) );
            struct_param_box.setVisibility ( View.VISIBLE );
            struct_box.setVisibility ( View.GONE );


        }

        @Override
        public int getItemCount() {

            return structureList.size ( );

        }

        public void setSource(ArrayList <StructurePojo> list) {
            if (list.size ( ) != 0) {
                this.structureList = list;

                notifyItemRangeRemoved ( 0, structureList.size ( ) );

                notifyDataSetChanged ( );
            }

        }

        public class StructureHolder extends RecyclerView.ViewHolder {

            private Button bt_structure;

            public StructureHolder(View itemView) {
                super ( itemView );

                bt_structure = itemView.findViewById ( R.id.bt_structure );


            }


        }

    }

    public class ParameterAdapter extends RecyclerView.Adapter <ParameterAdapter.ParameterHolder> {

        ArrayList <ParameterPojo> parameterList = new ArrayList <> ( );

        LayoutInflater inflater;
        Context context;

        ParameterAdapter(Context context) {
            inflater = LayoutInflater.from ( context );

            this.context = context;

        }

        @Override
        public ParameterAdapter.ParameterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate ( R.layout.parameter_single_item, parent, false );

            return new ParameterAdapter.ParameterHolder ( view );
        }

        @Override
        public void onBindViewHolder(final ParameterAdapter.ParameterHolder holder, final int position) {

            holder.tv_parameter.setText ( parameterList.get ( position ).getParaName ( ) );




           /* holder.bt_structure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    structureId = parameterList.get(position).getStructureId();

                    getParameterData();

                }
            });*/


        }


        @Override
        public int getItemCount() {

            return parameterList.size ( );

        }

        public void setSource(ArrayList <ParameterPojo> list) {
            if (list.size ( ) != 0) {
                this.parameterList = list;

                notifyItemRangeRemoved ( 0, parameterList.size ( ) );

                notifyDataSetChanged ( );

            }

        }

        public class ParameterHolder extends RecyclerView.ViewHolder {
            ArrayList <Para_DropDown> paraDropDownList;
            private TextView tv_parameter;
            Spinner sp_items;
            CheckBox cb_check;
            ImageButton im_remarks;

            public ParameterHolder(View itemView) {
                super ( itemView );

                tv_parameter = itemView.findViewById ( R.id.tv_parameter );
                sp_items = itemView.findViewById ( R.id.sp_parameter );
                cb_check = itemView.findViewById ( R.id.cb_check );
                im_remarks = itemView.findViewById ( R.id.im_remarks );


                sp_items.setOnTouchListener ( new View.OnTouchListener ( ) {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction ( ) == MotionEvent.ACTION_DOWN) {

                            parameterId = parameterList.get ( getAdapterPosition ( ) ).getParaId ( );

                            showParameterDropDown ( parameterList.get ( getAdapterPosition ( ) ).getParaId ( ) );

                            //showSpinner ( );
                            return true;

                        }
                        return false;
                    }
                } );

                im_remarks.setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View v) {
                        remark_pop.setVisibility ( View.VISIBLE );
                    }
                } );
                /*rem_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remark_pop.setVisibility(View.GONE);
                    }
                });
                rem_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remark_pop.setVisibility(View.GONE);
                    }
                });*/

            }

            public void showParameterDropDown(String id) {

                String[] singleChoiceItems = getParaDropDownName ( id ).toArray ( new String[0] );

                //if(singleChoiceItems.length>0){
                int itemSelected = 0;
                new AlertDialog.Builder ( context )
                        .setTitle ( "Select your Observations" )
                        .setSingleChoiceItems ( singleChoiceItems, itemSelected, new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int selectedIndex) {

                                paraDropId = paraDropDownList.get ( selectedIndex ).getParaDropId ( );

                                System.out.println ( "THIS IS para drop id " + paraDropId );
                            }
                        } )
                        .setPositiveButton ( "Ok", null )
                        .setNegativeButton ( "Cancel", null )
                        .show ( );
                // }
                /*else {
                    Toast.makeText ( context, "No Items to show", Toast.LENGTH_SHORT ).show ( );
                }*/

            }

            public void showSpinner() {

                String[] city_list = new String[3];
                city_list[0] = "New York";
                city_list[1] = "San Francisco";
                city_list[2] = "Washington DC";

                ArrayAdapter <String> aa = new ArrayAdapter <String> ( getApplicationContext ( ),
                        R.layout.spinner_single_item, city_list );

                sp_items = (Spinner) findViewById ( R.id.sp_parameter );
                sp_items.setAdapter ( aa );
            }

            public void setItemsToSpinner(String id) {

                ArrayList <String> list = getParaDropDownName ( id );

                System.out.println ( "THis is the final list size " + list.size ( ) );
                ArrayAdapter <String> adp1 = new ArrayAdapter <> ( context,
                        android.R.layout.simple_spinner_item, list );
                adp1.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
                sp_items.setAdapter ( adp1 );

                sp_items.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ( ) {
                    @Override
                    public void onItemSelected(AdapterView <?> adapterView, View view, int i, long l) {

                        System.out.println ( "Item selected is  " + adapterView.getItemAtPosition ( i ) );

                    }

                    @Override
                    public void onNothingSelected(AdapterView <?> adapterView) {

                    }
                } );

            }

            public ArrayList <Para_DropDown> getDropDownParameters(String parameterId) {


                paraDropDownList = (ArrayList <Para_DropDown>) ObstacleDB.getInstance ( context ).obstacleDao ( ).getParameterDropItems ( parameterId );

                System.out.println ( "This is the list of size return " + paraDropDownList.size ( ) );
                return paraDropDownList;


            }

            public ArrayList <String> getParaDropDownName(String parameterId) {

                ArrayList <Para_DropDown> list = getDropDownParameters ( parameterId );
                System.out.println ( "This is the list size  " + list.size ( ) );

                ArrayList <String> items = new ArrayList <> ( );
                for (Para_DropDown para : list) {

                    System.out.println ( "This is the name " + para.getParaDropName ( ) );
                    items.add ( para.getParaDropName ( ) );
                }

                return items;

            }


        }

    }
}
