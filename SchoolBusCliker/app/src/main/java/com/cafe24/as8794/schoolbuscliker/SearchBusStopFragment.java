package com.cafe24.as8794.schoolbuscliker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SearchBusStopFragment extends Fragment implements OnMapReadyCallback
{
    MainActivity main;
    Spinner spinner;

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
    PathOverlay path;

    Button btn_location;

    String[] str_busList = {"---선택---", "등교버스1번", "등교버스2번", "등교버스3번", "등교버스4번", "등교버스5번", "등교버스6번", "등교버스7번",
            "하교버스1번", "하교버스2번", "하교버스3번", "하교버스4번", "하교버스5번", "하교버스6번", "하교버스7번"};

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
        spinner = view.findViewById(R.id.sp_busList);

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



        ArrayAdapter spAdapter = new ArrayAdapter(main, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, str_busList);
        spinner.setAdapter(spAdapter);

        path = new PathOverlay();

        // 경로 그리기
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                path.setMap(null);
                DrawPath(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

//        DrawPath("등교버스1번");

        return view;
    }

    void setLocation(double longitude, double latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
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
//        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(36.30755055344865, 127.36541690338018));
        naverMap.moveCamera(cameraUpdate);
    }

    void LocationPoint()
    {
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(new LatLng(latitude, longitude));
    }

    void DrawPath(int i)
    {
        path.setWidth(15);
//        path.setPatternImage(OverlayImage.fromResource(R.drawable.baseline_arrow_drop_up_24));
//        path.setPatternInterval(10);
        switch (str_busList[i])
        {
            case "등교버스1번" :
                path.setCoords(Arrays.asList(
                        new LatLng(36.28183624070405, 127.46737007186375), // 산내파출소
                        new LatLng( 36.28450532788157, 127.46580817454968),
                        new LatLng( 36.28820140412254, 127.4643329583286)
                ));
                path.setColor(Color.RED);
                path.setMap(naverMap);
                break;
            case "등교버스2번" :
                path.setCoords(Arrays.asList(
                        new LatLng(37.57152, 126.97714),
                        new LatLng(37.56607, 126.98268),
                        new LatLng(37.56445, 126.97707),
                        new LatLng(37.55855, 126.97822)
                ));
                path.setColor(Color.RED);
                path.setMap(naverMap);
                break;
            case "등교버스3번" :
                path.setCoords(Arrays.asList(
                        new LatLng(36.28183624070405, 127.46737007186375), // 산내소방서
                        new LatLng(36.30162446034211, 127.45673604591207) // 은어송초등학교
                ));
                path.setColor(Color.BLUE);
                path.setMap(naverMap);
                break;
            default:
                path.setMap(null);
                break;
        }
    }
}