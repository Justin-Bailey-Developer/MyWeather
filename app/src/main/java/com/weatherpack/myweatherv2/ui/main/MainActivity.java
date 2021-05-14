package com.weatherpack.myweatherv2.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.weatherpack.myweatherv2.BuildConfig;
import com.weatherpack.myweatherv2.R;
import com.weatherpack.myweatherv2.ui.main.SectionsPagerAdapter;
import com.weatherpack.myweatherv2.ui.main.model.currentWeather.CurrentWeather;
import com.weatherpack.myweatherv2.ui.main.model.location.AccuWeatherLocation;
import com.weatherpack.myweatherv2.ui.main.retrofit.IAccuWeather;
import com.weatherpack.myweatherv2.ui.main.retrofit.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;

    //retrofit (RxJava)
    private IAccuWeather weatherService;
    private CompositeDisposable compositeDisposable;

    private Button btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this,
                getSupportFragmentManager(), location.getLocalizedName(), location.getKey());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);*/
        //FloatingActionButton fab = findViewById(R.id.fab);

        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this);
        checkPermissionsAndRequestLocation();

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        btnShare = findViewById(R.id.btnShare);

//        btnShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bitmap bitmap = getScreenShot();
//                saveScreenshot(bitmap);
//                shareImage();
//            }
//        });
    }

    private void checkPermissionsAndRequestLocation() {

        int hasFineLocationPermission = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCourseLocationPermission = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED &&
                hasCourseLocationPermission != PackageManager.PERMISSION_GRANTED){
            final String [] permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
            // Request permission - this is asynchronous
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
        else {
            // We have permission, so now ask for the location
            getLocationAndCreateUI();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        // is this for our request?
        if (requestCode == 0) {
            if (grantResults.length > 0 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                getLocationAndCreateUI();
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Location permission denied",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationAndCreateUI() {
        LocationRequest locationRequest = buildLocationRequest();
        LocationCallback locationCallback = buildLocationCallBack();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.myLooper());
    }

    private LocationRequest buildLocationRequest() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
        return locationRequest;
    }

    private LocationCallback buildLocationCallBack() {

        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getRetrofit();
        weatherService = retrofit.create(IAccuWeather.class);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                Log.i("LocationResult", "onLocationResult: " + location);
                // Make the call using Retrofit and RxJava
                compositeDisposable.add(weatherService.getLocationByPosition(
                        location.getLatitude() + "," +
                                location.getLongitude(),
                        BuildConfig.ACCUWEATHER_API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<AccuWeatherLocation>()
                        {
                            @Override
                            public void accept(AccuWeatherLocation location)
                                    throws Exception {
                                displayData(location);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception
                            {
                                Log.d("MYERROR", "accept: " + throwable.getMessage());
                            }
                        }));
            }
        };
        return locationCallback;
    }

    private void displayData(AccuWeatherLocation location) {

        SectionsPagerAdapter sectionsPagerAdapter =
                new SectionsPagerAdapter(MainActivity.this,
                        getSupportFragmentManager(),
                        location.getLocalizedName(),
                        location.getKey());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static void saveScreenshot(Bitmap bm, String fileName){
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareImage(File file){
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

}