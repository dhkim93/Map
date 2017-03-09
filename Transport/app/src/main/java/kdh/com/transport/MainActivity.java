package kdh.com.transport;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.CurrentLocationEventListener{
    Button callD;
    final Context context = this;
    RelativeLayout container;
    private MapView mapTest;
    int itemId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callD = (Button) findViewById(R.id.callD);

    }
    public void callD(View v) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_custom);
        dialog.setTitle("Title...");




        // set the custom dialog components - text, image and button
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        container = (RelativeLayout)dialog.findViewById(R.id.mapTest);
        container.removeAllViews();

        mapTest = new MapView(this);
        mapTest.setDaumMapApiKey(getString(R.string.API_KEY));


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

       // RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button large = new Button(this);
        container.addView(mapTest);
        container.addView(large);

        large.setLayoutParams(params);

        large.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch(itemId){
                    case 0 :
                    {

                        mapTest.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                        Log.e("hiii", "item=0");
                        itemId=1;


                    }
                    break;
                    case 1 :
                    {
                        mapTest.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                        Log.e("hiii", "item=1");
                        itemId=2;
                    }
                    break;
                    case 2 :
                    {
                        mapTest.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                        mapTest.setShowCurrentLocationMarker(false);
                        mapTest.setShowCurrentLocationMarker(true);
                        Log.e("hiii", "item=2");
                        itemId=0;
                    }
                    break;
                }

            }


        });

        mapTest.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.544646, 126.8329898), -1, true);
        MapPOIItem customMarker = new MapPOIItem();
        customMarker.setItemName("호떡떡");
        customMarker.setTag(1);
        customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(37.544646, 126.8329898));
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker.setCustomImageResourceId(R.drawable.map_pin_red); // 마커 이미지.
        customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        //x축 작으면 오른쪽, 크면 왼쪽으로 마커 이동, y축 작으면 밑으로 크면 위로 마커 위치 이동
        mapTest.setCurrentLocationEventListener(this);
        mapTest.setShowCurrentLocationMarker(true);
        //mapTest.setOpenAPIKeyAuthenticationResultListener(this);
        mapTest.setMapViewEventListener(this);
        mapTest.setMapType(MapView.MapType.Standard);
        mapTest.addPOIItem(customMarker);

        MapCircle circle1 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(37.544646, 126.8329898), // center
                15 , // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 255, 255, 0) // fillColor
        );
        circle1.setTag(5678);
        mapTest.addCircle(circle1);

// 지도뷰의 중심좌표와 줌레벨을 Circle이 모두 나오도록 조정.
        MapPointBounds[] mapPointBoundsArray = { circle1.getBound() };
        MapPointBounds mapPointBounds = new MapPointBounds(mapPointBoundsArray);
        int padding = 50; // px
        mapTest.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));

        // TextView text = (TextView) dialog.findViewById(R.id.text);
        //text.setText("Android custom dialog example!");

        Button dialogButton = (Button) dialog.findViewById(R.id.btnTest);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        chkGpsService();
    }
    //GPS 설정 체크
    private boolean chkGpsService() {

        String gps = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        Log.d(gps, "aaaa");

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}
