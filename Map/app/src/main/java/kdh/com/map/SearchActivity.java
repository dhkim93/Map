package kdh.com.map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.android.map.openapi.search.Item;
import net.daum.android.map.openapi.search.OnFinishSearchListener;
import net.daum.android.map.openapi.search.Searcher;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chris on 2016-08-29.
 */
public class SearchActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener{
    EditText mEditTextQuery;
    Button mButtonSearch;
    static MapView mMapView;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
    private ListView mListView;
    private ArrayList<Item> mitem;
    private CustomAdapter mAdapter;
    private ArrayList<ListItemInfo> datas = new ArrayList<ListItemInfo>();
    private Runnable mRunnable;
    private Handler mHandler;
    private Context mContext = null;

    public static Activity finish;
    public static AlertDialog.Builder builder;
    public static TextView dMessage;
    public static AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        mMapView = (MapView)findViewById(R.id.map_view);

        mMapView.setDaumMapApiKey(getString(R.string.API_KEY));
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
       // mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

        finish = SearchActivity.this;

        builder = new AlertDialog.Builder(this);
        dMessage = new TextView(this);


        mListView = (ListView)findViewById(R.id.lv);
        mitem = new ArrayList<Item>();
        mAdapter = new CustomAdapter(getApplicationContext(), R.layout.addr_info_list, datas);
        mListView.setAdapter(mAdapter);

        mContext = this;


        mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        };

        //mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), 3, true);

        mEditTextQuery = (EditText) findViewById(R.id.editTextQuery); // 검색창

        mButtonSearch = (Button) findViewById(R.id.buttonSearch); // 검색버튼
        mButtonSearch.setOnClickListener(new View.OnClickListener() { // 검색버튼 클릭 이벤트 리스너
            @Override
            public void onClick(View v) {
                String query = mEditTextQuery.getText().toString();
                if (query == null || query.length() == 0) {
                    showToast("검색어를 입력하세요.");
                    return;
                }
                hideSoftKeyboard(); // 키보드 숨김
                MapPoint.GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
                double latitude = geoCoordinate.latitude; // 위도
                double longitude = geoCoordinate.longitude; // 경도
                int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
                int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
                String apikey = getString(R.string.API_KEY);

                Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
                searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
                    @Override
                    public void onSuccess(List<Item> itemList) {
                        datas.clear();//기존 저장되어있더 데이터 삭제
                        mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                        showResult(itemList); // 검색 결과 보여줌

                        for (int i = 0; i < itemList.size(); i++) {
                            Item item = itemList.get(i);

                            datas.add(new ListItemInfo(item.title, item.address, item.newAddress));

                        }

                        mHandler.post(mRunnable);
                    }


                    @Override
                    public void onFail() {
                        showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
                    }
                });
            }
        });
    }


    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextQuery.getWindowToken(), 0);
    }
    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SearchActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showResult(List<Item> itemList) {
        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mMapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
        }
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

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    public class ListItemInfo{

        String name;
        String oldaddr;
        String newaddr;
        MapPOIItem num;

        public ListItemInfo(String name, String oldaddr, String newaddr){
            this.name = name;
            this.oldaddr = oldaddr;
            this.newaddr = newaddr;

        }

        public void setName(String name){
            this.name = name;
        }
        public void setOldaddr(String oldaddr){
            this.oldaddr = oldaddr;
        }
        public void setNewaddr(String newaddr){
            this.newaddr = newaddr;
        }

        public String getName(){
            return name;
        }
        public String getOldaddr(){
            return oldaddr;
        }
        public String getNewaddr(){
            return newaddr;
        }

    }

}
