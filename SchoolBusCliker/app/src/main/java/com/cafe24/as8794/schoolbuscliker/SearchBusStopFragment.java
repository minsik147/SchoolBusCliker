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
    Button bt_location;
    String[] str_busList = {"---선택---", "등교버스1번", "등교버스2번", "등교버스3번", "등교버스4번", "등교버스5번", "등교버스6번", "등교버스7번",
            "하교버스1번", "하교버스2번", "하교버스3번", "하교버스4번", "하교버스5번"};

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
    PathOverlay path;
    Marker[] marker_D1;
    Marker[] marker_D2;

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

        bt_location = view.findViewById(R.id.btn_location);
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

        bt_location.setOnClickListener(new View.OnClickListener()
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

        // 버스 노선 선택 콤보박스
        ArrayAdapter spAdapter = new ArrayAdapter(main, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, str_busList);
        spinner.setAdapter(spAdapter);

        // 경로, 포지션
        path = new PathOverlay();

        marker_D1 = new Marker[14];
        for (int i=0; i<marker_D1.length; i++)
        {
            marker_D1[i] = new Marker();
        }

        marker_D2 = new Marker[12];
        for (int i=0; i<marker_D2.length; i++)
        {
            marker_D2[i] = new Marker();
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
        CameraUpdate cameraUpdate;

        for(int j=0; j<marker_D1.length; j++)
        {
            marker_D1[j].setMap(null);
        }
        for(int j=0; j<marker_D2.length; j++)
        {
            marker_D2[j].setMap(null);
        }
        path.setWidth(18);
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

                cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(36.32277307200287, 127.4027866167433), 11).animate(CameraAnimation.Fly, 1000);;
                naverMap.moveCamera(cameraUpdate);

                break;
            case "등교버스2번" :
                path.setCoords(Arrays.asList(
                        // 법동우체국 앞 -> 동춘당
                        new LatLng(36.36748490152211, 127.43118032265701),
                        new LatLng( 36.36816144263912, 127.43472748729957),
                        new LatLng( 36.36814227320804, 127.43566059543625),
                        new LatLng( 36.36787840777136, 127.43635276521317),
                        new LatLng( 36.36744371681849, 127.43693533298591),
                        new LatLng( 36.36484250650533, 127.44036657410311), // 동춘당 버스승강장

                        // 동춘당 -> 웰니스요양병원
                        new LatLng(36.36380208766047, 127.44207940255139),
                        new LatLng(36.36252336329172, 127.444256013225),
                        new LatLng(36.35916309124644, 127.44878546418579),
                        new LatLng(36.35842530077192, 127.44787319847981),
                        new LatLng(36.35725340543112, 127.4467690658369),
                        new LatLng(36.355351357904276, 127.44571650932755),
                        new LatLng(36.35571346797295, 127.44467131164882),
                        new LatLng(36.35488159006224, 127.44421258565313), // 웰니스요양병원

                        // 웰니스 -> 대전복합터미널 버스승강장
                        new LatLng(36.35100795811146, 127.44200984173412),
                        new LatLng(36.348592699758, 127.43528986425684), // 대전복합터미널

                        // 대전복합터미널 -> 성남사거리 지앤지마트 앞
                        new LatLng(36.347435134214045, 127.43206398627603),
                        new LatLng(36.34069589603182, 127.43598108666299),
                        new LatLng(36.340308916860785, 127.43521870011261), // 성남사거리 지앤지마트 앞

                        // 성남사거리 -> 목동 금호한사랑아파트 건너 신협
                        new LatLng(36.33923147819407, 127.43288470815301),
                        new LatLng(36.33830675184654, 127.43004759864613),
                        new LatLng(36.33385633756935, 127.420154883677),
                        new LatLng(36.33388486922744, 127.4197290088269),
                        new LatLng(36.33396307057817, 127.41927277034432),
                        new LatLng(36.336578815037065, 127.4160260295573),
                        new LatLng(36.33460564508309, 127.41331176509172),
                        new LatLng(36.33227033029939, 127.41106630785131), // 목동 금호한사랑아파트 건너 신협

                        // 목동 -> 용문동 치안센터
                        new LatLng(36.33048246849195, 127.40946982078565),
                        new LatLng(36.3291966658014, 127.40730802009774),
                        new LatLng(36.328984385291555, 127.40613193041342),
                        new LatLng(36.328754359307005, 127.4055348929903),
                        new LatLng(36.32883255362179, 127.40506474831021),
                        new LatLng(36.3289015365385, 127.40465302580871),
                        new LatLng(36.336285247314734, 127.39566080515974), // 용문동 치안센터

                        // 용문 -> 풍전 삼계탕 앞 버스 승강장
                        new LatLng(36.33827127419957, 127.39325376835063),
                        new LatLng(36.34168243358088, 127.38906033365686),
                        new LatLng(36.3421187568452, 127.38790402984269),
                        new LatLng(36.342326737855565, 127.38768506200417),
                        new LatLng(36.342379304227535, 127.38745418391191), // 풍전 삼계탕 앞 버스 승강장

                        // 풍전 -> 큰마을4거리 버스승강장
                        new LatLng(36.3429623695062, 127.38480313515049),
                        new LatLng(36.343401642996014, 127.38271388172724),
                        new LatLng(36.34371112876777, 127.38174905286381),
                        new LatLng(36.344669247361644, 127.38014128096947),
                        new LatLng(36.34533797488704, 127.37955691556155),
                        new LatLng(36.348034417870856, 127.37752580883182),
                        new LatLng(36.34876198909548, 127.37685255876242),
                        new LatLng(36.34924625164683, 127.37617534069209), // 큰마을4거리 버스승강장

                        // 큰마을4거리 -> 갈마서부농협
                        new LatLng(36.35051846533921, 127.37352448132414),
                        new LatLng(36.35129843346201, 127.37193511747023),
                        new LatLng(36.353788718548444, 127.36804209804906), // 갈마서부농협

                        // 갈마서부 -> 대전일보 지나 육교 밑 버스승강장
                        new LatLng(36.354305756841285, 127.36400037843016),
                        new LatLng(36.353929934237506, 127.36163676503077), // 대전일보

                        // 대전일보 -> 목원대학교
                        new LatLng(36.35291976250793, 127.35667447055143),
                        new LatLng(36.35186706527443, 127.35535508063509),
                        new LatLng(36.34900368751171, 127.35150985044778),
                        new LatLng(36.34846815342205, 127.34974730856023),
                        new LatLng(36.34848687425469, 127.34796497693381),
                        new LatLng(36.34864177989999, 127.34348176826178),
                        new LatLng(36.348653060864294, 127.3403263782303),
                        new LatLng(36.3455733595687, 127.34030183663405),
                        new LatLng(36.34283634931423, 127.33939878456987),
                        new LatLng(36.33606581222207, 127.33395618154455),
                        new LatLng(36.33389213323056, 127.33300017155379),
                        new LatLng(36.33220728981613, 127.33287048549157),
                        new LatLng(36.330170710869645, 127.33283954710951),
                        new LatLng(36.33013787001504, 127.33811857663959),
                        new LatLng(36.33013787001504, 127.33811857663959),
                        new LatLng(36.329180441683185, 127.33809216512734),
                        new LatLng(36.32668980512917, 127.3384906899361) // 목원대학교
                ));
                path.setColor(0xFFA72B43);
                path.setMap(naverMap);

                marker_D2[0].setPosition(new LatLng(36.36748490152211, 127.43118032265701));
                marker_D2[0].setCaptionText("법동우체국 앞");
                marker_D2[1].setPosition(new LatLng( 36.36484250650533, 127.44036657410311));
                marker_D2[1].setCaptionText("동춘당 버스승강장");
                marker_D2[2].setPosition(new LatLng(36.35488159006224, 127.44421258565313));
                marker_D2[2].setCaptionText("웰니스요양병원");
                marker_D2[3].setPosition(new LatLng(36.348592699758, 127.43528986425684));
                marker_D2[3].setCaptionText("대전복합터미널 버스승강장");
                marker_D2[4].setPosition(new LatLng(36.340308916860785, 127.43521870011261));
                marker_D2[4].setCaptionText("성남사거리 지앤지마트 앞");
                marker_D2[5].setPosition(new LatLng(36.33227033029939, 127.41106630785131));
                marker_D2[5].setCaptionText("목동 금호한사랑아파트 건너 신협");
                marker_D2[6].setPosition(new LatLng(36.336285247314734, 127.39566080515974));
                marker_D2[6].setCaptionText("용문동 치안센터");
                marker_D2[7].setPosition(new LatLng(36.342379304227535, 127.38745418391191));
                marker_D2[7].setCaptionText("풍전 삼계탕 앞 버스 승강장");
                marker_D2[8].setPosition(new LatLng(36.34924625164683, 127.37617534069209));
                marker_D2[8].setCaptionText("큰마을4거리 버스승강장");
                marker_D2[9].setPosition(new LatLng(36.353788718548444, 127.36804209804906));
                marker_D2[9].setCaptionText("갈마서부농협");
                marker_D2[10].setPosition(new LatLng(36.353929934237506, 127.36163676503077));
                marker_D2[10].setCaptionText("대전일보 지나 육교 밑 버스승강장");
                marker_D2[11].setPosition(new LatLng(36.32668980512917, 127.3384906899361));
                marker_D2[11].setCaptionText("목원대학교");

                for(int j=0; j< marker_D2.length; j++)
                {
                    marker_D2[j].setWidth(80);
                    marker_D2[j].setHeight(110);
                    marker_D2[j].setMap(naverMap);
                }

                cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(36.34512080487731, 127.39101294313369), 11).animate(CameraAnimation.Fly, 1000);;
                naverMap.moveCamera(cameraUpdate);
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