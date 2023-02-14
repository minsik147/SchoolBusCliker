package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

        System.out.println("haha");
        tb_start = findViewById(R.id.tb_start);
        tb_start.setText("하하");
        tb_end = findViewById(R.id.tb_end);
        tb_count = findViewById(R.id.tb_count);

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

        itemCheck item = new itemCheck();

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

        itemCheck item = new itemCheck();

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

               try
               {
                   Thread.sleep(1000);
               }
               catch (InterruptedException e)
               {

               }
           }
        }
    }
}