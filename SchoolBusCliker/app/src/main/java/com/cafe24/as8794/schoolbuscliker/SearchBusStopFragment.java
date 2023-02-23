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
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
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
    Marker[] marker_D1;

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

        // 경로, 포지션
        path = new PathOverlay();
        marker_D1 = new Marker[14];
        for (int i=0; i<marker_D1.length; i++)
        {
            marker_D1[i] = new Marker();
        }

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
        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(latitude, longitude), 15).animate(CameraAnimation.Fly, 1000);;
//        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(36.30755055344865, 127.36541690338018));
        naverMap.moveCamera(cameraUpdate);
    }

    void LocationPoint()
    {
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(new LatLng(latitude, longitude));
    }

    // 버스 번호 선택시 정류장 경로 그리기
    void DrawPath(int i)
    {
        for(int j=0; j<marker_D1.length; j++)
        {
            marker_D1[j].setMap(null);
        }
        path.setWidth(15);
        switch (str_busList[i])
        {
            case "등교버스1번" :
                path.setCoords(Arrays.asList(
                        // 산내파출소 -> 은어송초등학교
                        new LatLng(36.28183624070405, 127.46737007186375), // 산내파출소
                        new LatLng(36.28450532788157, 127.46580817454968),
                        new LatLng(36.28820140412254, 127.4643329583286),
                        new LatLng(36.289255174669286, 127.46332900705322),
                        new LatLng( 36.29104328826666, 127.46293604853754),
                        new LatLng( 36.29327515275369, 127.46198073311609),
                        new LatLng( 36.29733460953294, 127.4597363670063),
                        new LatLng( 36.297913768724236, 127.4591107592909),
                        new LatLng( 36.30114782228246, 127.4570672663014),
                        new LatLng(36.30158193168874, 127.45666343099215), // 은어송초등학교

                        // 은어송초등학교 -> 효동현대아파트
                        new LatLng(36.3066888507901, 127.4514601775376),
                        new LatLng(36.30791852947595, 127.45097735771459),
                        new LatLng(36.309781854812414, 127.44914532072025),
                        new LatLng(36.310965142571206, 127.44841443274758),
                        new LatLng(36.311863315166235, 127.44499839547547),
                        new LatLng(36.31160737894712, 127.4435438485553),
                        new LatLng( 36.312130269262774, 127.44227186627899),
                        new LatLng(36.31277807574104, 127.44133461979858),
                        new LatLng(36.31757431340484, 127.43957716685759),

                        // 효동현대아파트 -> 대전역 대성미용 갤러리 앞
                        new LatLng(36.321710118075195, 127.43789932937672),
                        new LatLng(36.33118277656171, 127.43259515019896),
                        new LatLng(36.33079018225903, 127.43150985374912), // 대성 미용 갤러리

                        // 대전역 대성미용 갤러리 앞 -> 교보생명빌딩
                        new LatLng(36.327226262696975, 127.42197917193401),
                        new LatLng(36.32642762296918, 127.4216518949061),
                        new LatLng(36.32467816734136, 127.41942348679284), // 교보생명

                        // 교보생명빌딩 -> 오류동 임문택약국
                        new LatLng(36.32303507035383, 127.41704818749211),
                        new LatLng(36.322775319489615, 127.4139982114473),
                        new LatLng(36.32075951156468, 127.40660986745162), // 임문택약국

                        // 임문택약국 -> 유천동 로이젠
                        new LatLng(36.31880047748452, 127.39894936363694), // 유천동 로이젠

                        // 유천동 로이젠 -> 버드내아파트 버스승강장
                        new LatLng(36.315861774797895, 127.38756789341281), // 버드내아파트 버스승강장

                        // 버드내 -> 신원상가 연산타일
                        new LatLng(36.3150922088947, 127.38448516565795),
                        new LatLng(36.31118146565379, 127.37586434970615), // 신원상가 연산타일

                        // 신원상가 -> 정림동 고개 버스승강장
                        new LatLng(36.31007471611358, 127.37244070937659), // 정림동 고개 버스승강장

                        // 정림동 고개 -> 정림동 GS주유소
                        new LatLng(36.307927055221874, 127.36533236913465), // 정림동 GS주유소

                        // 정림동 -> 가수원 4거리 현대자동차
                        new LatLng(36.30731091609318, 127.36347007853881),
                        new LatLng(36.30700311368496, 127.36095789081399),
                        new LatLng(36.30526252057788, 127.35386033174561),
                        new LatLng(36.3054372474073, 127.3534380303692),
                        new LatLng(36.3056410388587, 127.35309101258551),
                        new LatLng(36.30609668881986, 127.35290378769179), // 가수원 4거리 현대자동차

                        // 가수원 4거리 현대자동차 -> 수목토아파트 버스승강장 앞
                        new LatLng(36.31755596600299, 127.3480362437681),
                        new LatLng(36.31870067772346, 127.34796338178732), // 수목토아파트 버스승강장 앞

                        // 수목토아파트 -> 목원대학교
                        new LatLng(36.33218194866398, 127.34809293442125),
                        new LatLng(36.33218579026018, 127.3381218569649),
                        new LatLng(36.329180441683185, 127.33809216512734),
                        new LatLng(36.32668980512917, 127.3384906899361) // 목원대학교
                ));
                path.setColor(0xFFA72B43);
                path.setMap(naverMap);

                marker_D1[0].setPosition(new LatLng(36.28183624070405, 127.46737007186375));
                marker_D1[0].setCaptionText("산내소방서 앞");
                marker_D1[1].setPosition(new LatLng(36.30158193168874, 127.45666343099215));
                marker_D1[1].setCaptionText("은어송초등학교 앞");
                marker_D1[2].setPosition(new LatLng(36.31757431340484, 127.43957716685759));
                marker_D1[2].setCaptionText("효동 현대 아파트 106동 앞");
                marker_D1[3].setPosition(new LatLng(36.33079018225903, 127.43150985374912));
                marker_D1[3].setCaptionText("대전역 대성미용갤러리 앞");
                marker_D1[4].setPosition(new LatLng(36.32467816734136, 127.41942348679284));
                marker_D1[4].setCaptionText("교보생명빌딩 앞");
                marker_D1[5].setPosition(new LatLng(36.32075951156468, 127.40660986745162));
                marker_D1[5].setCaptionText("오류동 임문택약국 앞");
                marker_D1[6].setPosition(new LatLng(36.31880047748452, 127.39894936363694));
                marker_D1[6].setCaptionText("유천동 로이젠 앞");
                marker_D1[7].setPosition(new LatLng(36.315861774797895, 127.38756789341281));
                marker_D1[7].setCaptionText("버드내아파트 버스승강장");
                marker_D1[8].setPosition(new LatLng(36.31118146565379, 127.37586434970615));
                marker_D1[8].setCaptionText("신원상가 연산타일");
                marker_D1[9].setPosition(new LatLng(36.31007471611358, 127.37244070937659));
                marker_D1[9].setCaptionText("정림동 고개 버스승강장");
                marker_D1[10].setPosition(new LatLng(36.307927055221874, 127.36533236913465));
                marker_D1[10].setCaptionText("정림동 GS주유소");
                marker_D1[11].setPosition(new LatLng(36.30609668881986, 127.35290378769179));
                marker_D1[11].setCaptionText("가수원 4거리 현대자동차");
                marker_D1[12].setPosition(new LatLng(36.31870067772346, 127.34796338178732));
                marker_D1[12].setCaptionText("수목토아파트 버스승강장 앞");
                marker_D1[13].setPosition(new LatLng(36.32668980512917, 127.3384906899361));
                marker_D1[13].setCaptionText("목원대학교");

                for(int j=0; j<marker_D1.length; j++)
                {
                    marker_D1[j].setWidth(80);
                    marker_D1[j].setHeight(110);
                    marker_D1[j].setMap(naverMap);
                }

                CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(36.32277307200287, 127.4027866167433), 11).animate(CameraAnimation.Fly, 1000);;
                naverMap.moveCamera(cameraUpdate);

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