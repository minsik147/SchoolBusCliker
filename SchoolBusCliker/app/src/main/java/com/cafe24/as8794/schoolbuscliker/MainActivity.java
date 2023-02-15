package com.cafe24.as8794.schoolbuscliker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    Button bt_goSchool;
    Button bt_goHome;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 상단바 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("예약하기");

        // 아이디 값 설정
        bt_goSchool = findViewById(R.id.bt_goSchool);
        bt_goHome = findViewById(R.id.bt_goHome);

        // 버튼 클릭 리스너
        bt_goSchool.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bt_goSchool.setBackgroundResource(R.drawable.selector_button2);
                bt_goHome.setBackgroundResource(R.drawable.selector_button);
            }
        });

        bt_goHome.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bt_goSchool.setBackgroundResource(R.drawable.selector_button);
                bt_goHome.setBackgroundResource(R.drawable.selector_button2);
            }
        });
    }

    // 뒤로가기
    @Override
    public void onBackPressed()
    {
//        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
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