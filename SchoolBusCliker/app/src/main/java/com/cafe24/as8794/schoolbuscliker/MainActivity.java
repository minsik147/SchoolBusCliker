package com.cafe24.as8794.schoolbuscliker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity
{
    BottomNavigationView bottomNavigationView;

    ReservationFragment reservationFragment;
    SearchBusStopFragment searchBusStopFragment;
    ReservationInformation reservationInformation;
    MyInformation myInformation;

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