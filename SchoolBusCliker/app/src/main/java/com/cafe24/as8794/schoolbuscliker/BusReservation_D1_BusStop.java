package com.cafe24.as8794.schoolbuscliker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BusReservation_D1_BusStop extends AppCompatActivity
{
    String userID, userPass, userName, email, tel, address;
    String date;
    TextView tv_string;
    TextView tv_start, tv_end;
    Spinner spinner_start, spinner_end;
    int int_select;
    Button bt_OK;
    String[] str_BusStop = new String[15];

    private void DefaultSetting()
    {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        userPass = intent.getStringExtra("userPass");
        userName = intent.getStringExtra("userName");
        email = intent.getStringExtra("email");
        tel = intent.getStringExtra("tel");
        address = intent.getStringExtra("address");
        date = intent.getStringExtra("date");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_reservation_d1_bus_stop);

        DefaultSetting();

        // 상단바 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("예약하기");

        bt_OK = findViewById(R.id.bt_next);
        tv_string = findViewById(R.id.tv_string);
        tv_start = findViewById(R.id.tv_start);
        tv_end = findViewById(R.id.tv_end);
        spinner_start = findViewById(R.id.sp_start);
        spinner_end = findViewById(R.id.sp_end);

        tv_string.setText(userName + "님, 예약해볼까요?");

        str_BusStop[0] = "--선택하세요--";
        BusStopLoad();

        // 버스 노선 선택 콤보박스
        ArrayAdapter spAdapter = new ArrayAdapter(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, str_BusStop);
        spinner_start.setAdapter(spAdapter);

        // 경로 그리기
        spinner_start.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (i == 0)
                {
                    return;
                }
                else
                {
                    bt_OK.setEnabled(true);
                    tv_start.setText(str_BusStop[i] + "");
                    spinner_start.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        bt_OK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String start = tv_start.getText().toString();

                if (start.equals("목원대학교"))
                {
                    Toast.makeText(BusReservation_D1_BusStop.this, "승차지점과 하차지점은 같을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void BusStopLoad()
    {
        String URL = "https://as8794.cafe24.com/new_bus_clicker/get_json/get_json_bus_city_stop_code_D1.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, null, new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                try
                {
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String busStop = jsonObject.getString("busStop");

                        str_BusStop[(i+1)] = busStop + "";
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), "에러", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestQueue.add(jsonArrayRequest);
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