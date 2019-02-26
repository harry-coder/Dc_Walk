package com.dc_walk.walk;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.dc_walk.CustomClasses.LocationItem;
import com.dc_walk.CustomClasses.MediaPojo;
import com.dc_walk.CustomClasses.UserData;
import com.dc_walk.CustomInterfaces.RequestListener;
import com.dc_walk.Home_Activity;
import com.dc_walk.KmlDatabase.KmlDB;
import com.dc_walk.KmlDatabase.KmlItems;
import com.dc_walk.Model.ObstaclePojo;
import com.dc_walk.Model.Para_DropDown;
import com.dc_walk.Model.ParameterPojo;
import com.dc_walk.Model.PlaceInfoPojo;
import com.dc_walk.Model.StructurePojo;
import com.dc_walk.Model.UserCoordinatesPojo;
import com.dc_walk.Model.UserEntriesPojo;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Walk_Activity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {


    Button raid_btn, locobs_btn, struct_param_done_btn, structure_btn, bt_cancel, bt_ok;
    ImageButton pointer_btn, mapclose_btn, actestclose_btn, actest_btn, struct_param_close_btn, structure_close_btn, remBtn;
    FrameLayout mapbox, struct_param_box;
    ScrollView actest, struct_box;
    //AppCompatRadioButton checkStruct_btn, checkEnv_btn, checkBuilt_btn, checkUtil_btn;
    LinearLayout remark_pop;

    int selectedRoute;

    boolean isObservationDialogOpen, isMapDialogOpen, isStructureDialogOpen, isParameterDialogOpen, isPictureDialogOpen;

    public static String obstacleId, structureId;

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

    private AlertDialog otpDialog;


    private static final int PLACE_PICKER_REQUEST = 1000;
    private static final int IMAGE_TAKEN_REQUEST = 2000;
    private static final int REQUEST_VIDEO_CAPTURE = 3000;


    private GoogleApiClient mClient;
    double lat, lon;
    RequestQueue queue;
    Button bt_picture, bt_itemObserver;
    ArrayList <UserCoordinatesPojo> userCoordinatesArrayList;

    ArrayList <MediaPojo> mediaList;
    ArrayList <String> parameterIdList;
    ArrayList <String> paraDropIdList;
    ArrayList <String> remarksIdList;

    ArrayList <UserData> userDataList;

    ArrayList <PlaceInfoPojo> kmlLatLonList;

    Uri fileUri;

    RadioGroup rg_radioGroup;

    Bitmap combileBitmap, bitmapTest;
    Handler handler;
    int itemPositionClicked;

    private GoogleMap googleMap;
    private FrameLayout fl_pictureLayout;

    RadioButton rb_lhs, rb_rhs;

    private Button bt_moreType, bt_moreFeature, bt_finish;

    private Button bt_takePicture1, bt_takePicture2, bt_takePicture3, bt_takePicture4, bt_takePicture5, bt_takePicture6, bt_takeVideo1, bt_takeVideo2, bt_takeVideo3;

    private String lhs = "unselected", rhs = "unselected";

    private String obsRemark;

    EditText et_parameterRemark;

    String paraRemark;

    int[] side;

    TextView tv_environmentTitle, tv_structureTitle;

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
        bt_itemObserver = findViewById ( R.id.bt_itemObserver );

        bt_moreType = findViewById ( R.id.bt_moreType );
        bt_moreFeature = findViewById ( R.id.bt_moreFeature );
        bt_finish = findViewById ( R.id.bt_finish );


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


        bt_ok = findViewById ( R.id.bt_ok );
        bt_cancel = findViewById ( R.id.bt_cancel );


        mediaList = new ArrayList <> ( );
        parameterIdList = new ArrayList <> ( );
        paraDropIdList = new ArrayList <> ( );
        remarksIdList = new ArrayList <> ( );

        kmlLatLonList = new ArrayList <> ( );


        rb_lhs = findViewById ( R.id.rb_lhs );
        rb_rhs = findViewById ( R.id.rb_rhs );


        et_parameterRemark = findViewById ( R.id.et_parameterRemark );


        queue = vollySingleton.getInstance ( ).getRequestQueue ( );
        handler = new android.os.Handler ( );


        tv_environmentTitle = findViewById ( R.id.tv_environmentTitle );
        tv_structureTitle = findViewById ( R.id.tv_structureTitle );

        rg_radioGroup = findViewById ( R.id.rg_radioGroup );

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


        bt_moreFeature.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                System.out.println ( "Inside more feature" );
                actest.setVisibility ( View.VISIBLE );
                fl_pictureLayout.setVisibility ( View.GONE );

            }
        } );

        bt_moreType.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                System.out.println ( "Inside more types" );

                fl_pictureLayout.setVisibility ( View.GONE );

                struct_box.setVisibility ( View.VISIBLE );
            }
        } );
        bt_picture.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {


                //   rb_lhs.setSelected ( false );
                // rb_rhs.setSelected ( false );
                lhs = "unselected";
                rhs = "unselected";
                fl_pictureLayout.setVisibility ( View.VISIBLE );

                struct_param_box.setVisibility ( View.GONE );


                isPictureDialogOpen = true;


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
        //struct_param_done_btn = findViewById ( R.id.struct_param_done_id );
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

       /* actest_btn.setOnClickListener ( new View.OnClickListener ( ) {
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

        } );*/

        actestclose_btn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                //this is to close the obstacle part
                System.out.println ( "This is ac test" );

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

                //this is to close structure button.
                System.out.println ( "This is struct close" );

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

                //this is to close the param
                System.out.println ( "This is param close" );

                struct_param_box.setVisibility ( View.GONE );

                //struct_param_box.inv
            }
        } );


        bt_finish.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                try {

                    //Here we are saving data to sqlite instead to sending data directly and then taking all data of the day and then sending all together all data at once
                    //sendImageToServer ( );
                    saveUserEntriesDataToDb ( );

                    updateServerLatLonDb ( );
                    tv_location.setText ( "" );
                    lat = 0;
                    lon = 0;


                    struct_param_box.setVisibility ( View.GONE );
                    struct_box.setVisibility ( View.GONE );
                    actest.setVisibility ( View.GONE );
                    fl_pictureLayout.setVisibility ( View.GONE );


                    isPictureDialogOpen = false;
                    isMapDialogOpen = false;
                    isObservationDialogOpen = false;
                    isParameterDialogOpen = false;
                    isStructureDialogOpen = false;

                    enableCameraButtons ( );


                    rg_radioGroup.clearCheck ( );

                    if (rb_rhs.isSelected ( )) {

                        System.out.println ( "Inside this rhs" );
                        rb_rhs.toggle ( );
                    } else if (rb_lhs.isSelected ( )) {
                        System.out.println ( "Inside this lhs" );

                        rb_lhs.toggle ( );

                    }

                    finish ( );
                    handler.postDelayed ( new Runnable ( ) {
                        @Override
                        public void run() {
                            Intent i = new Intent ( Walk_Activity.this, Walk_Activity.class );
                            startActivity ( i );
                            overridePendingTransition ( R.anim.right_in, R.anim.left_out );

                        }
                    }, 500 );




                } catch (Exception e) {
                    e.printStackTrace ( );
                }
            }
        } );


        rb_lhs.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener ( ) {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    System.out.println ( "On lhs selected" );

                    side[0] = 2;

                } else {

                    side[0] = 0;

                    System.out.println ( "On  not lhs selected" );


                }
            }
        } );

        rb_rhs.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener ( ) {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    side[0] = 1;

                } else {

                    side[0] = 0;

                }

            }
        } );

        bt_itemObserver.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {

                showRemarkDialog ( Walk_Activity.this );
            }
        } );

        side = new int[1];

    }



    public void enableCameraButtons() {
        bt_takePicture1.setEnabled ( true );
        bt_takePicture2.setEnabled ( true );
        bt_takePicture3.setEnabled ( true );
        bt_takePicture4.setEnabled ( true );
        bt_takePicture5.setEnabled ( true );
        bt_takePicture6.setEnabled ( true );
        bt_takeVideo1.setEnabled ( true );
        bt_takeVideo2.setEnabled ( true );
        bt_takeVideo3.setEnabled ( true );
    }

    @Override
    public void onClick(View view) {

        int id = view.getId ( );
        switch (id) {

            case R.id.bt_takePicture1: {
                invokeCamera ( bt_takePicture1 );
                bt_takePicture1.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;

            }
            case R.id.bt_takePicture2: {
                invokeCamera ( bt_takePicture2 );
                bt_takePicture2.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_takePicture3: {
                invokeCamera ( bt_takePicture3 );
                bt_takePicture3.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_takePicture4: {
                invokeCamera ( bt_takePicture4 );
                bt_takePicture4.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_takePicture5: {
                invokeCamera ( bt_takePicture5 );
                bt_takePicture4.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_takePicture6: {
                invokeCamera ( bt_takePicture6 );
                bt_takePicture4.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_video1: {
                invokeVideoRecorder ( bt_takeVideo1 );
                bt_takeVideo1.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_video2: {
                invokeVideoRecorder ( bt_takeVideo2 );
                bt_takeVideo2.setTextColor ( Color.parseColor ( "#c3c3c3" ) );
                break;
            }
            case R.id.bt_video3: {
                invokeVideoRecorder ( bt_takeVideo3 );
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

                // if (action == KeyEvent.ACTION_DOWN) {

                mapbox.setVisibility ( View.GONE );
                isMapDialogOpen = false;
                return true;
                // }

            }
        }
        if (isObservationDialogOpen) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                // if (action == KeyEvent.ACTION_DOWN) {

                tv_location.setText ( "" );
                isObservationDialogOpen = false;
                actest.setVisibility ( View.GONE );
                return true;
                // }

            }
        }
        if (isStructureDialogOpen) {
            System.out.println ( "Inside struct" );
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                // if (action == KeyEvent.ACTION_DOWN) {
                System.out.println ( "Inside struct1" );

                isStructureDialogOpen = false;
                struct_box.setVisibility ( View.GONE );

                //}

            }
        }
        if (isParameterDialogOpen) {
            System.out.println ( "Inside param" );

            if (keyCode == KeyEvent.KEYCODE_BACK) {

                //if (action == KeyEvent.ACTION_DOWN) {

                isParameterDialogOpen = false;
                struct_param_box.setVisibility ( View.GONE );

                rg_radioGroup.clearCheck ( );

                return true;
                //}

            }
        }
        if (isPictureDialogOpen) {
            System.out.println ( "Inside pic" );

            if (keyCode == KeyEvent.KEYCODE_BACK) {

                //     if (action == KeyEvent.ACTION_DOWN) {

                isPictureDialogOpen = false;
                fl_pictureLayout.setVisibility ( View.GONE );

                return true;
                //   }

            }
        }

        return super.dispatchKeyEvent ( event );


    }


    /* < ----------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE BEGINNING OF METHODS REQUIRED FOR CAMARE TO OPEN AND PREPARE IMAGE TO SEND TO SERVER*/


    private void invokeVideoRecorder(Button button) {
        Intent takeVideoIntent = new Intent ( MediaStore.ACTION_VIDEO_CAPTURE );
        takeVideoIntent.putExtra ( android.provider.MediaStore.EXTRA_VIDEO_QUALITY, 0 );
        if (takeVideoIntent.resolveActivity ( getPackageManager ( ) ) != null) {

            startActivityForResult ( takeVideoIntent, REQUEST_VIDEO_CAPTURE );
            button.setEnabled ( false );
        }
    }

    private void invokeCamera(Button button) {

        Intent camIntent = new Intent ( "android.media.action.IMAGE_CAPTURE" );
        fileUri = Uri.fromFile ( getOutputMediaFile ( ) );


        camIntent.putExtra ( android.provider.MediaStore.EXTRA_OUTPUT, fileUri );

        camIntent.putExtra ( "return-data", true );

        camIntent.putExtra ( MediaStore.EXTRA_OUTPUT, fileUri );

        startActivityForResult ( camIntent, IMAGE_TAKEN_REQUEST );

        button.setEnabled ( false );

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


                System.out.println ( "This is lat " + lat );
                System.out.println ( "This is lon " + lon );

                String address = Objects.requireNonNull ( place.getAddress ( ) ).toString ( );


                UserCoordinatesPojo userCoordinatesPojo = new UserCoordinatesPojo ( );
                userCoordinatesPojo.setLat ( lat );
                userCoordinatesPojo.setLon ( lon );
                userCoordinatesPojo.setRoute ( Double.parseDouble ( et_pointer.getText ( ).toString ( ) ) );


                userCoordinatesArrayList.add ( userCoordinatesPojo );


                if (TextUtils.isEmpty ( address )) {
                    tv_location.setText ( " " + lat + " , " + lon );
                } else {
                    tv_location.setText ( address );
                }

                insertUserCoordinatesToDatabase ( userCoordinatesPojo );

            }
        } else if (requestCode == IMAGE_TAKEN_REQUEST) {
            if (resultCode == RESULT_OK) {


                Bitmap background = null;


                try {
                    background = MediaStore.Images.Media.getBitmap ( getContentResolver ( ), fileUri );
                } catch (IOException e) {
                    e.printStackTrace ( );
                }
                System.out.println ( "This is file uri " + background );

                Bitmap bitmapTest = combineBitMap ( background, getCurrentDate ( ), "" + lat + ", " + lon );
                //     Bitmap bitmapTest = combineBitMap ( background, "4-12-108", "28.123 78.5425" );


                Toast.makeText ( this, "Image ready to send", Toast.LENGTH_SHORT ).show ( );

                setMediaUri ( getImageUri ( this, bitmapTest ), "image" );

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

    public String getCurrentDate() {

        SimpleDateFormat formatter = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss", Locale.getDefault ( ) );
        Date date = new Date ( );

        return formatter.format ( date );
    }


    public void setMediaUri(Uri uri, String mediaType) {
        MediaPojo mediaPojo = new MediaPojo ( );
        mediaPojo.setMediaUri ( String.valueOf ( uri ) );
        mediaPojo.setMediaType ( mediaType );

        mediaList.add ( mediaPojo );

    }


    public Bitmap combineBitMap(Bitmap src, String Date, String latlon) {

        // Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.yourimage); // the original file yourimage.jpg i added in resources
        Bitmap dest = Bitmap.createBitmap ( src.getWidth ( ), src.getHeight ( ), Bitmap.Config.ARGB_8888 );
        // Bitmap dest = Bitmap.createScaledBitmap ( src,src.getWidth ( ), src.getHeight ( ),false );

        String date = "Date " + Date;

        String loc = "Location " + latlon;
        Canvas cs = new Canvas ( dest );
        Paint tPaint = new Paint ( );
        tPaint.setTextSize ( 100 );
        tPaint.setColor ( Color.WHITE );
        tPaint.setStyle ( Paint.Style.FILL_AND_STROKE );
        cs.drawBitmap ( src, 0f, 100f, null );
        float height = tPaint.measureText ( "yY" );
        float width = tPaint.measureText ( date );
        float locWidth = tPaint.measureText ( loc );


        float x_coord = 100;

        float y_coord = (src.getHeight ( ) - 300);
        // cs.drawText ( yourText, x_coord, height + 30f, tPaint ); // 15f is to put space between top edge and the text, if you want to change it, you can
        cs.drawText ( date, x_coord, y_coord, tPaint ); // 15f is to put space between top edge and the text, if you want to change it, you can
        cs.drawText ( loc, x_coord, y_coord + 100, tPaint ); // 15f is to put space between top edge and the text, if you want to change it, you can

        /*float x_coord = (src.getWidth ( ) - (locWidth + 150));
        float y_coord = (src.getHeight ( ) - 300);
        // cs.drawText ( yourText, x_coord, height + 30f, tPaint ); // 15f is to put space between top edge and the text, if you want to change it, you can
        cs.drawText ( date, x_coord, y_coord, tPaint ); // 15f is to put space between top edge and the text, if you want to change it, you can
        cs.drawText ( loc, x_coord, y_coord + 200, tPaint ); // 15f is to put space between top edge and the text, if you want to change it, you can
*/
        try {
            dest.compress ( Bitmap.CompressFormat.JPEG, 100, new FileOutputStream ( getOutputMediaFile ( ) ) );
            // dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ( );
        }
        return dest;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream ( );
        inImage.compress ( Bitmap.CompressFormat.JPEG, 100, bytes );
        String path = MediaStore.Images.Media.insertImage ( inContext.getContentResolver ( ), inImage, "Converted Image", null );


        return Uri.parse ( path );
    }


    class FetchCoordinatesFromDB extends AsyncTask <Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            placeInfoAadapter.setSource ( getKmlPointsFromServer ( ) );

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            initialiseMap ( );
            progressDialog.dismiss ( );

        }
    }

    /* < ----------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE BEGINNING OF ACTIVITY LIFECYCLE METHODS*/

    @Override
    protected void onStart() {
        super.onStart ( );


        progressDialog.setMessage ( "Fetching data" );
        progressDialog.show ( );
        new FetchCoordinatesFromDB ( ).execute ( );



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


    public ArrayList <PlaceInfoPojo> getKmlPointsFromServer() {

        return kmlLatLonList = (ArrayList <PlaceInfoPojo>) LocationDB.getInstance ( this ).myDao ( ).getUserList ( );

    }



    /* < ----------------------------------------------------------------------------------------------------------->*/
    /* THIS IS THE BEGINNING OF METHODS REQUIRED FOR VARIOUS SERVER REQUESTS*/


    public void sendImageToServer() throws InterruptedException, IOException {

        String url = "http://monitorpm.feedbackinfra.com/dcnine_highways/embc_app/insert_map";


        Map <String, String> map = prepareMapToSend ( );

        request.setOnRequestListner ( new RequestListener ( ) {
            @Override
            public void onPreRequest() {

                progressDialog.setMessage ( "Sending Data to server.." );
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

                mediaList.clear ( );

            }
        } );


        // request.uploadMedia ( this, "video", url, uri, map );
        request.sendMultipleMediaToServer ( mediaList, url, map );

    }

    public Map <String, String> prepareMapToSend() {

        JSONArray parameterArray = new JSONArray ( );
        JSONArray paraDropArray = new JSONArray ( );
        JSONArray remarkArray = new JSONArray ( );

        Map <String, String> map = new HashMap <> ( );

        map.put ( "lat", "" + lat );
        map.put ( "longg", "" + lon );
        map.put ( "route_no", et_pointer.getText ( ).toString ( ) );

        map.put ( "obstacle_type", obstacleId );
        map.put ( "structure_type", structureId );

        if (lhs.equalsIgnoreCase ( "selected" )) {

            System.out.println ( "Inside side lhs" );
            map.put ( "side", "2" );
        } else if (rhs.equalsIgnoreCase ( "selected" )) {
            System.out.println ( "Inside side rhs" );

            map.put ( "side", "1" );
        }


        if (parameterIdList.size ( ) != 0) {
            for (int i = 0; i < parameterIdList.size ( ); i++) {
                parameterArray.put ( parameterIdList.get ( i ) );

            }

        }
        if (paraDropIdList.size ( ) != 0) {
            for (int i = 0; i < paraDropIdList.size ( ); i++) {
                parameterArray.put ( paraDropIdList.get ( i ) );

            }

        }
        if (remarksIdList.size ( ) != 0) {
            for (int i = 0; i < remarksIdList.size ( ); i++) {
                parameterArray.put ( remarksIdList.get ( i ) );

            }

        }

        map.put ( "parameter_type", "" + parameterArray );
        map.put ( "paraDrop_type", "" + paraDropArray );
        map.put ( "paraRemark", "" + remarkArray );
        //   map.put ( "obs", remark );


        //map.put ( "imageName1", fileUri.getLastPathSegment ( ) );
        //  map.put ( "videoName", uri.getLastPathSegment ( ) );


        return map;

    }


    public void saveUserEntriesDataToDb() throws IOException, InterruptedException {

        progressDialog.setMessage ( "Saving Data" );
        progressDialog.show ( );

        UserEntriesPojo userEntries = new UserEntriesPojo ( );
        userEntries.setLat ( "" + lat );
        userEntries.setLon ( "" + lon );
        userEntries.setRoute_no ( et_pointer.getText ( ).toString ( ) );
        userEntries.setObstacleTypeId ( obstacleId );
        userEntries.setStructureId ( structureId );

        userEntries.setObsRemark ( obsRemark );

        System.out.println ( "This is the side " + side[0] );
        userEntries.setSide ( "" + side[0] );
        /*if (lhs.equalsIgnoreCase ( "selected" )) {
            userEntries.setSide ( "2" );
        } else if (rhs.equalsIgnoreCase ( "selected" )) {
            userEntries.setSide ( "1" );
        }
*/
        //  userEntries.setParaList ( parameterIdList );
        //userEntries.setParaDropList ( paraDropIdList );
        userEntries.setUriList ( mediaList );
        // userEntries.setParaRemarkList ( remarksIdList );


        userEntries.setUserDataList ( userDataList );


        ObstacleDB.getInstance ( this ).obstacleDao ( ).insertUserEntries ( userEntries );


        progressDialog.dismiss ( );


        userDataList.clear ( );

//        getUserEntries ( );

    }


    public void getUserEntries() throws IOException, InterruptedException {

        ArrayList <UserEntriesPojo> userEntryList = (ArrayList <UserEntriesPojo>) ObstacleDB.getInstance ( this ).obstacleDao ( ).getAllUserEntries ( );

        System.out.println ( "This is the size " + userEntryList.size ( ) );


        for (UserEntriesPojo pojo : userEntryList) {

            System.out.println ( pojo.getUriList ( ).get ( 0 ).getMediaUri ( ) );

        }


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

    /* THIS IS THE BEGINNING OF Dialog section */


    public void showRemarkDialog(final Context context) {
        final AlertDialog.Builder malert = new AlertDialog.Builder ( context );
        LayoutInflater inflater = LayoutInflater.from ( context );
        View view1 = inflater.inflate ( R.layout.remarks_dialog, null );
        TextView tv_cancel = view1.findViewById ( R.id.tv_cancel );
        TextView tv_submit = view1.findViewById ( R.id.tv_submit );
        final EditText et_remark = view1.findViewById ( R.id.et_remark );


        tv_cancel.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                otpDialog.dismiss ( );
            }
        } );


        tv_submit.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                obsRemark = et_remark.getText ( ).toString ( );
                if (TextUtils.isEmpty ( obsRemark )) {
                    et_remark.setError ( "Please enter remark" );
                } else otpDialog.dismiss ( );
            }
        } );


        malert.setView ( view1 );

        otpDialog = malert.create ( );
        otpDialog.setCanceledOnTouchOutside ( false );
        otpDialog.show ( );

    }




    /* < ----------------------------------------------------------------------------------------------------------->*/

    /* THIS IS THE BEGINNING OF MAP RELATED METHODS FOR PARSING KML AND PLOTTING COORDINATES*/

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


        final PatternItem DOT = new Dot ( );
        final PatternItem GAP = new Gap ( 5 );

        Marker marker = null;
        MarkerOptions markerOptions = null;
       // ClusterManager <LocationItem> mClusterManager = new ClusterManager <LocationItem> ( this, googleMap );


        // Create a stroke pattern of a gap followed by a dot.
        // final List <PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList ( GAP, DOT );


        ArrayList <LatLng> latLngArrayList = getArrayListOfLatLon ( kmlLatLonList );

        Polyline polyline;
        PolylineOptions polylineOptions = new PolylineOptions ( ).width ( 8 ).color ( Color.BLUE ).geodesic ( true );
        for (int i = 0; i < latLngArrayList.size ( ); i++) {

            String type = kmlLatLonList.get ( i ).getType ( );

            markerOptions = new MarkerOptions ( );
            markerOptions.position ( latLngArrayList.get ( i ) );
            markerOptions.title ( kmlLatLonList.get ( i ).getDescription ( ) );



            if (type.equalsIgnoreCase ( "linestring" )) {
                polylineOptions.add ( latLngArrayList.get ( i ) );
                Objects.requireNonNull ( markerOptions ).icon ( bitmapDescriptorFromVector ( this, R.drawable.up_arrow_icon ) );


            }
            else if (type.equalsIgnoreCase ( "point" )) {

                  Objects.requireNonNull ( markerOptions ).icon ( bitmapDescriptorFromVector ( this, R.drawable.line_marker_icon ) );

//                Objects.requireNonNull ( markerOptions ).icon ( bitmapDescriptorFromVector ( this, R.drawable.marker_icon ) );

            }




            // markerOptions.snippet("Blah");




           /* if (i == 0) {

                markerOptions.icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_BLUE ) );
                markerOptions.title ( "Route Starting" );
                // googleMap.addMarker(new MarkerOptions().position(latLngArrayList.get(i)).title("Route " + i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


            } else if (i == latLngArrayList.size ( ) - 1) {

                //   googleMap.addMarker(new MarkerOptions().position(latLngArrayList.get(i)).title("Route " + i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                markerOptions.icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_GREEN ) );
                markerOptions.title ( "Route Ending" );

            }*/

           if(markerOptions!=null){
               marker = googleMap.addMarker ( markerOptions );

           }



        }


        polyline = googleMap.addPolyline ( polylineOptions );
        // polyline.setPattern ( PATTERN_POLYLINE_DOTTED );


        googleMap.setMapType ( GoogleMap.MAP_TYPE_HYBRID );


        googleMap.moveCamera ( CameraUpdateFactory.newLatLngZoom ( new LatLng ( Double.parseDouble ( kmlLatLonList.get ( routeNo ).getLng ( ) ), Double.parseDouble ( kmlLatLonList.get ( routeNo ).getLat ( ) ) ), 17 ) );


        readUserCoordinates ( googleMap );


    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable ( context, vectorResId );
        vectorDrawable.setBounds ( 0, 0, Objects.requireNonNull ( vectorDrawable ).getIntrinsicWidth ( ), vectorDrawable.getIntrinsicHeight ( ) );
        Bitmap bitmap = Bitmap.createBitmap ( vectorDrawable.getIntrinsicWidth ( ), vectorDrawable.getIntrinsicHeight ( ), Bitmap.Config.ARGB_8888 );
        Canvas canvas = new Canvas ( bitmap );
        vectorDrawable.draw ( canvas );
        return BitmapDescriptorFactory.fromBitmap ( bitmap );
    }



    public void drawUserCoordinatesPolyLineOnMap(GoogleMap googleMap, ArrayList <UserCoordinatesPojo> coordinatesList) {


        ArrayList <LatLng> list = getUserCoordinateList ( coordinatesList );


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

    public ArrayList <LatLng> getArrayListOfLatLon(ArrayList <PlaceInfoPojo> kmlItems) {

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
                System.out.println ( "This is item clicked " + selectedRoute );

                LocationDB.getInstance ( Walk_Activity.this ).myDao ( ).getUpdatedList ( true, selectedRoute );

                // placeInfoAadapter.notifyDataSetChanged ( );


                // LocationDB.getInstance ( Walk_Activity.this ).myDao ( ).getUpdatedList ( true, (itemPositionClicked + 1) );

                //      System.out.println("This is route no. "+pojo.getRouteNo());
                //    System.out.println("This is isMapped. "+pojo.isMapped);

                placeInfoAadapter.setSource ( (ArrayList <PlaceInfoPojo>) LocationDB.getInstance ( Walk_Activity.this ).myDao ( ).getUserList ( ) );


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


    @SuppressLint("StaticFieldLeak")
    public void readUserCoordinates(final GoogleMap googleMap) {

        new AsyncTask <Void, Void, ArrayList <UserCoordinatesPojo>> ( ) {
            @Override
            protected ArrayList <UserCoordinatesPojo> doInBackground(Void... voids) {
                return (ArrayList <UserCoordinatesPojo>) UserCoordinatesDB.getInstance ( Walk_Activity.this ).myDao ( ).getUserCoordinatesList ( );

            }

            @Override
            protected void onPostExecute(ArrayList <UserCoordinatesPojo> userCoordinatesPojos) {


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

        ArrayList <PlaceInfoPojo> kmlItemsList = new ArrayList <> ( );

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

              System.out.println("This is the mapped status " + kmlItemsList.get(position).isMapped());
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
           // return kmlItemsList.size ( );
            return 10;

        }

        public void setSource(ArrayList <PlaceInfoPojo> list) {
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

                        selectedRoute=kmlItemsList.get (getAdapterPosition ()).getRouteNo ( );


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
                    list = UserCoordinatesDB.getInstance ( context ).myDao ( ).getrouteNoList ( currentRoute, (currentRoute + 1) );
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
                        holder.cb_obstacle.toggle ( );

                        tv_environmentTitle.setText ( obstacleInfoList.get ( position ).getObstacleName ( ) );

                        getStructureData ( );


                    }
                }
            } );


        }

        public void getStructureData() {

            structureAdapter.setSource ( (ArrayList <StructurePojo>) ObstacleDB.getInstance ( context ).obstacleDao ( ).getStructureItems ( obstacleId ) );
            struct_box.setVisibility ( View.VISIBLE );
            actest.setVisibility ( View.GONE );

            isStructureDialogOpen = true;

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

                    tv_structureTitle.setText ( structureList.get ( position ).getStructureName ( ) );

                    getParameterData ( );
                }
            } );


        }

        public void getParameterData() {

            parameterAdapter.setSource ( (ArrayList <ParameterPojo>) ObstacleDB.getInstance ( context ).obstacleDao ( ).getParameterItems ( structureId ) );
            struct_param_box.setVisibility ( View.VISIBLE );
            struct_box.setVisibility ( View.GONE );

            isParameterDialogOpen = true;

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


            String paraId, paraDropDownId;

            Button bt_add;


            TextView tv_noParameter;

            public ParameterHolder(View itemView) {
                super ( itemView );

                tv_parameter = itemView.findViewById ( R.id.tv_parameter );
                sp_items = itemView.findViewById ( R.id.sp_parameter );
                cb_check = itemView.findViewById ( R.id.cb_check );
                im_remarks = itemView.findViewById ( R.id.im_remarks );
                bt_add = itemView.findViewById ( R.id.bt_add );
                tv_noParameter = itemView.findViewById ( R.id.tv_noParameter );


                userDataList = new ArrayList <> ( );

                cb_check.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener ( ) {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {


                            bt_add.setVisibility ( View.VISIBLE );
                            paraId = parameterList.get ( getAdapterPosition ( ) ).getParaId ( );


                        } else {
                            bt_add.setVisibility ( View.GONE );
                        }
                    }
                } );

                sp_items.setOnTouchListener ( new View.OnTouchListener ( ) {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction ( ) == MotionEvent.ACTION_DOWN) {


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

                bt_ok.setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View v) {


                        paraRemark = et_parameterRemark.getText ( ).toString ( );

                        System.out.println ( "This is the remark added " + paraRemark );

                        if (!TextUtils.isEmpty ( paraRemark )) {


                            et_parameterRemark.setText ( "" );
                            remark_pop.setVisibility ( View.GONE );


                        } else {
                            et_parameterRemark.setError ( "Please Provide a remark" );
                        }

                    }
                } );
                bt_cancel.setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View v) {
                        remark_pop.setVisibility ( View.GONE );
                    }
                } );


                bt_add.setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View view) {


                        UserData userData = new UserData ( );

                        userData.setParameterId ( paraId );
                        userData.setParaDropId ( paraDropDownId );


                        userData.setParameterRemark ( paraRemark );


                        userDataList.add ( userData );


                        bt_add.setVisibility ( View.GONE );


                    }
                } );
            }


            public void showParameterDropDown(String id) {

                String[] singleChoiceItems = getParaDropDownName ( id ).toArray ( new String[0] );

                //if(singleChoiceItems.length>0){
                int itemSelected = 0;
                new AlertDialog.Builder ( context )
                        .setTitle ( "Select Options" )
                        .setSingleChoiceItems ( singleChoiceItems, itemSelected, new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int selectedIndex) {

                                paraDropDownId = paraDropDownList.get ( selectedIndex ).getParaDropId ( );

                                //  paraDropIdList.add ( paraDropId );


                                //this is the place to get para drop down..
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


            public ArrayList <Para_DropDown> getDropDownParameters(String parameterId) {


                paraDropDownList = (ArrayList <Para_DropDown>) ObstacleDB.getInstance ( context ).obstacleDao ( ).getParameterDropItems ( parameterId );

                return paraDropDownList;


            }

            public ArrayList <String> getParaDropDownName(String parameterId) {

                ArrayList <Para_DropDown> list = getDropDownParameters ( parameterId );

                ArrayList <String> items = new ArrayList <> ( );
                for (Para_DropDown para : list) {

                    items.add ( para.getParaDropName ( ) );
                }

                return items;

            }


        }

    }
}
