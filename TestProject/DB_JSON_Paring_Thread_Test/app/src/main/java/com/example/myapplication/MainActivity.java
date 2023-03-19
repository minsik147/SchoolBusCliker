package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
{
    TextView tb_start;
    TextView tb_end;
    TextView tb_busStop;
    TextView tb_busStop_start;
    TextView tb_busStop_end;
    public TextView tb_count;
    static int GET_COUNT;
    boolean is;
    String str_start;
    String str_end;
    int int_start;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        tb_start = findViewById(R.id.tb_start);
        tb_end = findViewById(R.id.tb_end);
        tb_count = findViewById(R.id.tb_count);
        tb_busStop = findViewById(R.id.tb_busStop);

        str_start = "대전역";
        tb_busStop_start = findViewById(R.id.tb_busStop_start);
        tb_busStop_start.setText(str_start);

        str_end = "정림동";
        tb_busStop_end = findViewById(R.id.tb_busStop_end);
        tb_busStop_end.setText(str_end);

        int_start = 0;

        th t = new th();
        is = true;
        t.start();
    }

    void data()
    {
        String serverUrl = "https://as8794.cafe24.com/new_bus_clicker/get_json/get_json_bus_city.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, serverUrl, null, new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                try
                {
                    int int_start = 0;
                    int int_end = 0;
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String start = jsonObject.getString("start");
                        String end = jsonObject.getString("end");

                        if (str_start.equals(start))
                        {
                            int_start++;
                        }

                        if (str_end.equals(end))
                        {
                            int_end++;
                        }

                        tb_start.setText(int_start + "");
                        tb_end.setText(int_end + "");
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

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestQueue.add(jsonArrayRequest);
    }

    class th extends Thread
    {

        String serverUrl = "https://as8794.cafe24.com/new_bus_clicker/get_count_data/test.php";
        // String serverUrl = "https://as8794.cafe24.com/new_bus_clicker/get_json/get_json_bus_city.php";

        @Override
        public void run()
        {
            while(is)
            {
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, serverUrl, null, new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        try
                        {
                            for (int i = 0; i < response.length(); i++)
                            {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int count = Integer.parseInt(jsonObject.getString("count"));

                                if (GET_COUNT != count)
                                {
                                    tb_count.setText(count + "");
                                    data();
                                }
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

                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                requestQueue.add(jsonArrayRequest);

                System.out.println("1");
                try
                {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e)
                {

                }
            }
        }
    }

    // 뒤로가기 버튼
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
            is = false;
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}