package com.cafe24.as8794.schoolbuscliker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ReservationGoSchoolListFragment extends Fragment
{

    MainActivity main;
    Button bt_D1;

    public ReservationGoSchoolListFragment()
    {
        super();
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        main = (MainActivity)context;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        main = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_reservation_go_school_list_fragment, container, false);

        bt_D1 = view.findViewById(R.id.bt_D1);

        bt_D1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(main, "D1", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}