package com.cafe24.as8794.schoolbuscliker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.Manifest;
import android.widget.Button;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.Map;

public class SearchBusStopFragment extends Fragment implements OnMapReadyCallback
{
    MainActivity main;

    // 네이버 맵 관련
    private NaverMap naverMap;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String[] PERMISSIONS =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    double longitude;
    double latitude;
    int mapCheck;
    boolean isLocation;

    Button btn_location;

    public SearchBusStopFragment()
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {


        View view = inflater.inflate(R.layout.activity_serach_bus_stop_fragment, container, false);

        btn_location = view.findViewById(R.id.btn_location);

        // 현재 위치
        locationSource = new FusedLocationSource(main, LOCATION_PERMISSION_REQUEST_CODE);

        // 지도 생성
        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null)
        {
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        btn_location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
                {
                    Toast.makeText(main, "위치 권한을 허용하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                CameraUpdate();
            }
        });

        return view;
    }

    void setLocation(double longitude, double latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
//
//        if (isLocation == false)
//        {
//            Toast.makeText(main, "사용자의 현 위치를 찾았습니다.", Toast.LENGTH_SHORT).show();
//            isLocation = true;
//        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        this.naverMap = naverMap;

        LocationPoint();
        naverMap.setLocationSource(locationSource);
        CameraUpdate();
    }

    void CameraUpdate()
    {
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude));
        naverMap.moveCamera(cameraUpdate);
    }

    void LocationPoint()
    {
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(new LatLng(latitude, longitude));
    }
}