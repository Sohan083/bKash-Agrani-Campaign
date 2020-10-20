package com.example.bkashagranicampaign;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bkashagranicampaign.databinding.ActivityFormLayoutBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FormActivity extends AppCompatActivity {

    ActivityFormLayoutBinding binding;

    public static String presentLat = "", presentLon = "", presentAcc = "";
    private static final int MY_PERMISSIONS_REQUEST = 0;
    public LocationManager locationManager;
    public GPSLocationListener listener;
    public Location previousBestLocation = null;
    private static final int TWO_MINUTES = 1000 * 60 * 1;
    public static final String BROADCAST_ACTION = "gps_data";
    String code = "", message = "";
    Intent intent;
    static Bitmap bitmap;
    SweetAlertDialog pDialog;
    JSONObject jsonObject;

    TextView txtName, txtTeam, txtBranch, txtTodayCount, txtTotalCount, imageStatus;
    String consumerName = "", consumerPhone = "", consumerAccount = "", remarks = "";

    ImageButton logoutBtn, imageBtn;
    Button submitBtn;

    EditText edtConsumerName, edtConsumerPhone, edtConsumerAccount, edtRemark;
    CheckBox chk1,chk2, chk3, chk4, chk5, chk6;
    RadioGroup smartPhoneRadioGroup, bkashRadioGroup;

    String area = "", userId = "";

    String photoName = "", imageString = "";
    Boolean photoFlag = false;

    Uri photoURI;
    static final int REQUEST_IMAGE_CAPTURE = 99;
    String currentPhotoPath = "";

    SharedPreferences sharedPreferences;

    boolean network = false;

    String[] operatorList = {"017", "013", "019", "014", "016", "018", "015"};


    SweetAlertDialog sweetAlertDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormLayoutBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        userId = sharedPreferences.getString("id",null);

        txtName = binding.name;
        txtTeam = binding.team;
        txtBranch = binding.branch;
        txtTodayCount = binding.todayCount;
        txtTotalCount = binding.totalCount;

        logoutBtn = binding.logoutBtn;
        submitBtn = binding.submitBtn;
        imageBtn = binding.imageBtn;

        edtConsumerName = binding.consumerName;
        edtConsumerPhone = binding.consumerPhone;
        edtConsumerAccount = binding.consumerBank;
        edtRemark = binding.remark;

        smartPhoneRadioGroup  = binding.smartPhoneRadioGroup;
        bkashRadioGroup = binding.bkashRadioGroup;

        chk1 = binding.chkbox1;
        chk2 = binding.chkbox2;
        chk3 = binding.chkbox3;
        chk4 = binding.chkbox4;
        chk5 = binding.chkbox5;
        chk6 = binding.chkbox6;


        imageStatus = findViewById(R.id.imageStatus);

        chk1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                }
                else
                {

                }
            }
        });
        chk2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                }
                else
                {

                }
            }
        });
        chk3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                }
                else
                {

                }
            }
        });
        chk4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                }
                else
                {

                }
            }
        });
        chk5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                }
                else
                {

                }
            }
        });
        chk6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                }
                else
                {

                }
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SweetAlertDialog log = new SweetAlertDialog(FormActivity.this, SweetAlertDialog.WARNING_TYPE);
                log.setTitleText("Are you sure to Sign Out?");
                log.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        log.dismissWithAnimation();
                        finish();
                    }
                });
                log.setCancelText("No");
                log.setConfirmText("Ok");
                log.show();
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                photoName = CustomUtility.getDeviceDate()+"image.jpeg";
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                       CustomUtility.showAlert(FormActivity.this, ex.getMessage(), "Creating Image");
                        return;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "com.example.bkashagranicampaign.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                network = CustomUtility.haveNetworkConnection(FormActivity.this);
                consumerName = edtConsumerName.getText().toString();
                consumerPhone = edtConsumerPhone.getText().toString();
                consumerAccount = edtConsumerAccount.getText().toString();
                remarks = edtRemark.getText().toString();
                boolean flag = chekFeilds();
                if(flag)
                {
                    Log.e("checking","Okkk");
                    upload();
                }
            }
        });


        // for getting gps value
        intent = new Intent(BROADCAST_ACTION);
        GPS_Start();

        txtName.setText(sharedPreferences.getString("name", (String) null));
        txtTeam.setText(sharedPreferences.getString("team", (String) null));
        getStatus();
    }



    private boolean isCorrectPhoneNumber(String phone) {
        if (phone.equals("") || (phone.length() != 11)) {
            return false;
        }
        String code2 = phone.substring(0, 3);
        for (String op : operatorList) {
            if (op.equals(code2)) {
                return true;
            }
        }
        return false;
    }



    //after finishing camera intent whether the picture was save or not
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoFlag = true;
            imageStatus.setText(R.string.take_image_done);
        }
    }

    private File createImageFile() throws IOException {

        File storageDir = getExternalFilesDir("Routes/Photos");

        File image = new File(storageDir.getAbsolutePath() + File.separator + photoName);
        try {
            image.createNewFile();
        } catch (IOException e) {
            CustomUtility.showAlert(this, "Image Creation Failed. Please contact administrator", "Error");
        }
        currentPhotoPath = image.getAbsolutePath();
        Log.e("image path",currentPhotoPath);
        return image;
    }


    private boolean chekFeilds()
    {
        if (!network)
        {
           CustomUtility.showError(FormActivity.this,"Please turn on internet connection","No inerternet connection!");
            return false;
        }
        else if(consumerName.equals(""))
        {
            CustomUtility.showWarning(FormActivity.this,"Please enter consumer name","Required fields");
            return false;
        }
        else if(consumerPhone.equals(""))
        {
            CustomUtility.showWarning(FormActivity.this,"Please enter consumer phone number","Required fields");
            return false;
        }
        else if(!isCorrectPhoneNumber(consumerPhone))
        {
            CustomUtility.showWarning(FormActivity.this,"Please enter correct phone number","Required fields");
            return false;
        }
        else if(consumerAccount.equals(""))
        {
            CustomUtility.showWarning(FormActivity.this,"Please enter consumer bank account number","Required fields");
            return false;
        }
        else if(!photoFlag)
        {
            CustomUtility.showWarning(FormActivity.this,"Please take a selfie","Required fields");
            return false;
        }
        else if(presentAcc.equals(""))
        {
            CustomUtility.showWarning(FormActivity.this,"Please wait for the gps","Required fields");
            return false;
        }
        return true;
    }

    public void getStatus() {
        sweetAlertDialog = new SweetAlertDialog(this, 5);
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.show();
        MySingleton.getInstance(this).addToRequestQue(new StringRequest(1, "https://bkash.imslpro.com/api/consumer/user_status.php", new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    sweetAlertDialog.dismiss();
                    Log.e("response", response);
                    jsonObject = new JSONObject(response);
                    code = jsonObject.getString("success");
                    if (code.equals("true")) {
                        txtTodayCount.setText(jsonObject.getString("todayCount"));
                        txtTotalCount.setText(jsonObject.getString("totalCount"));
                        return;
                    }
                    CustomUtility.showError(FormActivity.this, "No data found", "Failed");
                } catch (JSONException e) {
                    CustomUtility.showError(FormActivity.this, e.getMessage(), "Getting Response");
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                sweetAlertDialog.dismiss();
               CustomUtility.showError(FormActivity.this, "Network Error, try again!", "Failed");
                final SweetAlertDialog s = new SweetAlertDialog(FormActivity.this, SweetAlertDialog.ERROR_TYPE);
                s.setConfirmText("Ok");
                s.setTitleText("Network Error, try again!");
                s.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        s.dismissWithAnimation();
                        startActivity(getIntent());
                        finish();
                    }
                });
                s.show();
            }
        }) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId", userId);
                return params;
            }
        });
    }


    private void upload()
    {

        pDialog = new SweetAlertDialog(FormActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.show();
        Uri uri = Uri.fromFile(new File(currentPhotoPath));
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            pDialog.dismiss();
            String err = e.getMessage() + " May be storage full please uninstall then install the app again";
           CustomUtility.showAlert(this, e.getMessage(), "Problem Creating Bitmap at Submit");
            return;
        }
        imageString =CustomUtility.imageToString(bitmap);
        String upLoadServerUri = "https://sec.imslpro.com/api/android/insert_attendance.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, upLoadServerUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Log.e("response",response);
                        try {
                            jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if(code.equals("true"))
                            {
                                File fdelete = new File(currentPhotoPath);
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        System.out.println("file Deleted :" + currentPhotoPath);
                                    } else {
                                        System.out.println("file not Deleted :" + currentPhotoPath);
                                    }
                                }
                                code = "Successful";
                                new SweetAlertDialog(FormActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Successful")
                                        .setContentText("")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                startActivity(getIntent());
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                            else
                            {
                                code = "Failed";
                                CustomUtility.showError(FormActivity.this,message,code);
                                //CustomUtility.showError(AttendanceActivity.this,"You allready submitted in",code);
                            }


                        } catch (JSONException e) {
                            CustomUtility.showError(FormActivity.this, e.getMessage(), "Failed");
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("response","onerrorResponse");
                CustomUtility.showError(FormActivity.this, "Network slow, try again", "Failed");

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId",userId);
                params.put("InTimeLat",presentLat);
                params.put("InTimeLon",presentLon);
                params.put("InTimeAccuracy",presentAcc);
                params.put("InTimePictureName",photoName);
                params.put("PictureData",imageString);
                return params;
            }
        };
        MySingleton.getInstance(FormActivity.this).addToRequestQue(stringRequest);
    }



    private void GPS_Start() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new GPSLocationListener();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        } catch (Exception ex) {

        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public class GPSLocationListener implements LocationListener {
        public void onLocationChanged(final Location loc) {
            Log.i("**********", "Location changed");
            if (isBetterLocation(loc, previousBestLocation)) {


                loc.getAccuracy();
                //location.setText(" " + loc.getAccuracy());

                presentLat = String.valueOf(loc.getLatitude());
                presentLon = String.valueOf(loc.getLongitude());
                presentAcc = String.valueOf(loc.getAccuracy());


//                Toast.makeText(context, "Latitude" + loc.getLatitude() + "\nLongitude" + loc.getLongitude(), Toast.LENGTH_SHORT).show();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);
            }
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Toast.makeText(getApplicationContext(), "Status Changed", Toast.LENGTH_SHORT).show();
        }
    }

}
