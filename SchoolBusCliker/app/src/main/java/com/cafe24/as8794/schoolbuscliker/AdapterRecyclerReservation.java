package com.cafe24.as8794.schoolbuscliker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterRecyclerReservation extends RecyclerView.Adapter<AdapterRecyclerReservation.ViewHolder>
{
    MainActivity main;
    int ID;
    private ArrayList<itemReservationCheck> data = null;

    Button btn_cancel, btn_OK;
    LinearLayout lin_1, lin_2;

    public AdapterRecyclerReservation(ArrayList<itemReservationCheck> data)
    {
        this.data = data;
    }

    // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_reservation, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        itemReservationCheck item = data.get(position);

        holder.start.setText(item.getStart());
        holder.end.setText(item.getEnd());
        holder.bus.setText(item.getBus());
        holder.date.setText(item.getDate());
        holder.id.setText(item.getId()+"");


        String sta = item.getIsBoarding()+"";
        if (sta.equals("미탑승"))
        {
            holder.state.setTextColor(Color.RED);
//            btn_cancel.setVisibility(View.INVISIBLE);
//            btn_OK.setVisibility(View.INVISIBLE);
            lin_1.setVisibility(View.GONE);
            lin_2.setVisibility(View.GONE);
        }

        if (sta.equals("탑승 완료"))
        {
            holder.state.setTextColor(Color.GREEN);
//            btn_cancel.setVisibility(View.INVISIBLE);
//            btn_OK.setVisibility(View.INVISIBLE);
            lin_1.setVisibility(View.GONE);
            lin_2.setVisibility(View.GONE);
        }

        holder.state.setText(item.getIsBoarding()+"");
    }

    // getItemCount : 전체 데이터의 개수를 리턴
    @Override
    public int getItemCount()
    {
        return data.size();
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        EditText start, end, bus, date;
        TextView id, state;

        ViewHolder(View view)
        {
            super(view);
            start = view.findViewById(R.id.et_start);
            end = view.findViewById(R.id.et_end);
            bus = view.findViewById(R.id.et_bus);
            date = view.findViewById(R.id.et_date);
            id = view.findViewById(R.id.tv_id);
            state = view.findViewById(R.id.tv_state);
            btn_cancel = view.findViewById(R.id.btn_Cancel);
            btn_OK = view.findViewById(R.id.btn_OK);

            lin_1 = view.findViewById(R.id.lin_1);
            lin_2 = view.findViewById(R.id.lin_2);
        }
    }
}
