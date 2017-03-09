package kdh.com.map;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;

import java.util.ArrayList;

/**
 * Created by chris on 2016-12-01.
 */
public class CustomAdapter  extends BaseAdapter {

    private Context mContext;
    private int mResource;
    private ArrayList<SearchActivity.ListItemInfo> datas;
    private LayoutInflater mInflater;
    //private Item item;





    public CustomAdapter(Context context, int layoutResource, ArrayList<SearchActivity.ListItemInfo> datas){

        this.datas = datas;
        this.mContext = context;
        this.mResource = layoutResource;
    }


    public int getCount(){
        return datas.size();
    }

    public Object getItem(int position){
        return datas.get(position);
    }
    public long getItemId(int position){
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent){



        if(convertView == null){
            this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.addr_info_list, parent, false);
        }

        if(datas != null){
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView oldaddr = (TextView) convertView.findViewById(R.id.old_addr);
            TextView newaddr = (TextView) convertView.findViewById(R.id.new_addr);

            name.setText(datas.get(position).getName());
            oldaddr.setText(datas.get(position).getOldaddr());
            newaddr.setText(datas.get(position).getNewaddr());

            convertView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    // 터치 시 해당 아이템 이름 출력
                    MapPOIItem[] poiItems = SearchActivity.mMapView.getPOIItems();

                    if (poiItems.length > 0) {
                        SearchActivity.mMapView.selectPOIItem(poiItems[position], false);
                    } else {
                        Toast.makeText(mContext, "아무것도 없다. ", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Button btn_set = (Button) convertView.findViewById(R.id.btn_set);

            btn_set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SearchActivity.dMessage.setText("\n" + datas.get(position).getName() + "\n" + datas.get(position).getOldaddr() + "\n" + datas.get(position).getNewaddr());
                    SearchActivity.dMessage.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    SearchActivity.dMessage.setTextColor(Color.MAGENTA);
                    SearchActivity.dMessage.setTextSize(25);
                    SearchActivity.builder.setView(SearchActivity.dMessage);

                    MapPOIItem[] poiItems = SearchActivity.mMapView.getPOIItems();

                    SearchActivity.mMapView.selectPOIItem(poiItems[position], false);//결정 눌렀을 때 해당 마커 띄워주기

                    SearchActivity.builder.setTitle("거주지로 결정하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("결정", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //AddInfo.home.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                                   // AddInfo.home.setText(datas.get(position).getName() + "\n" + datas.get(position).getOldaddr() + "\n" + datas.get(position).getNewaddr());
                                    SearchActivity.finish.finish();

                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    ((ViewGroup)SearchActivity.dMessage.getParent()).removeView(SearchActivity.dMessage);//커스텀으로 만든 다이얼로그뷰가 만들어 지면 상위 뷰그룹에 묶여있고 취소를 눌러도 계속 남아있는 상태여서 다시 다이얼로그를 호출하면 예외가 발생된다. 그래서 취소시 다이얼로그뷰를 지운다.
                                }
                            }).create().show();



                }
            });



        }

        return convertView;
    }





}
