package net.daum.android.map.openapi.search;

import java.util.List;

/**
 * Created by chris on 2016-08-21.
 */
public interface OnFinishSearchListener {


    public void onSuccess(List<Item> itemList);

    public void onFail();
}
