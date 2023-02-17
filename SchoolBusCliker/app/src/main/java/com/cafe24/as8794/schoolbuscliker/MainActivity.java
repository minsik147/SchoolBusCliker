package com.cafe24.as8794.schoolbuscliker;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

public class MainActivity extends AppCompatActivity
{
    BottomNavigationView bottomNavigationView;

    ReservationFragment reservationFragment;
    SearchBusStopFragment searchBusStopFragment;
    ReservationInformation reservationInformation;
    MyInformation myInformation;

    // 네이버 맵 관련
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프래그먼트 설정
        reservationFragment = new ReservationFragment();
        searchBusStopFragment = new SearchBusStopFragment();
        reservationInformation = new ReservationInformation();
        myInformation = new MyInformation();

        // 네이버 맵 관련
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        final LocationListener gpsLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                String provider = location.getProvider();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();

                System.out.println("위치정보 : " + provider + "\n" +
                        "위도 : " + longitude + "\n" +
                        "경도 : " + latitude + "\n" +
                        "고도  : " + altitude);

                searchBusStopFragment.setLocation(longitude, latitude);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,
                1,
                gpsLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000,
                1,
                gpsLocationListener);



        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, reservationFragment).commit();

        // 상단바 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("예약하기");

        // 바텀네비게이션 설정
        bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.item1:
                        getSupportActionBar().setTitle("예약하기");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, reservationFragment).commit();
                        return true;
                    case R.id.item2:
                        getSupportActionBar().setTitle("정류장찾기");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, searchBusStopFragment).commit();
                        return true;
                    case R.id.item3:
                        getSupportActionBar().setTitle("예약정보");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, reservationInformation).commit();
                        return true;
                    case R.id.item4:
                        getSupportActionBar().setTitle("내정보");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, myInformation).commit();
                        return true;
                }
                return false;
            }
        });
    }

    // 뒤로가기
    private long tempTime = System.currentTimeMillis();
    @Override
    public void onBackPressed()
    {
//        super.onBackPressed();
        if (System.currentTimeMillis() > tempTime + 2000)
        {
            tempTime = System.currentTimeMillis();
            Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (System.currentTimeMillis() <= tempTime + 2000)
        {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}