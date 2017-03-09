package kdh.com.map;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import net.daum.android.map.openapi.search.Item;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.HashMap;


public class MainActivity extends Activity {
 EditText mEditTextQuery;
    Button mButtonSearch;
    MapView mMapView;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        final MapView mapView = new MapView(this);
        mapView.setDaumMapApiKey(getString(R.string.API_KEY));

        RelativeLayout container = (RelativeLayout) findViewById(R.id.mapView);
        container.addView(mapView);
         //container.addView(mMapView);
         mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.581032, 126.925893), -3, true);

        //mapView.zoomIn(true); //zoom-in 1번
        //   mapView.zoomOut(true);// zoom-out 1번

       /* MapPOIItem marker = new MapPOIItem();
        marker.setItemName("미스터 피자");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(37.581032, 126.925893));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);
         //default Marker*/

        MapPOIItem customMarker = new MapPOIItem();
        customMarker.setItemName("미스터 피자");
        customMarker.setTag(1);
        customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(37.581032, 126.925893));
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker.setCustomImageResourceId(R.drawable.map_pin_red); // 마커 이미지.
        customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        //x축 작으면 오른쪽, 크면 왼쪽으로 마커 이동, y축 작으면 밑으로 크면 위로 마커 위치 이동
        mapView.addPOIItem(customMarker);
         //custom Marker

    }
}
