package com.feicuiedu.treasure.treasure.home.map;

import com.feicuiedu.treasure.net.NetClient;
import com.feicuiedu.treasure.treasure.Area;
import com.feicuiedu.treasure.treasure.Treasure;
import com.feicuiedu.treasure.treasure.TreasureApi;
import com.feicuiedu.treasure.treasure.TreasureRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/11/11.
 */
//宝藏数据获取业务类
public class MapPresenter {
    /**
     * 业务请求
     * @param area
     */
    private MapMvpView mapMvpView;
    private Area area;

    public MapPresenter(MapMvpView mapMvpView){
        this.mapMvpView=mapMvpView;
    }
    public void getTreasure(Area area){

        if (TreasureRepo.getInstance().isCached(area)){
            return;
        }
       this.area=area;

        // 来去进行宝藏数据的获取
        TreasureApi treasureApi = NetClient.getInstance().getTreasureApi();
        Call<List<Treasure>> treasureCall = treasureApi.getTreasureInArea(area);
        treasureCall.enqueue(callback);
    }

    private Callback<List<Treasure>> callback = new Callback<List<Treasure>>() {
        // 请求成功的时候
        @Override
        public void onResponse(Call<List<Treasure>> call, Response<List<Treasure>> response) {
            if (response.isSuccessful()){
                List<Treasure> treasureList = response.body();
                if (treasureList==null){
                    // 友好的弹出吐司说明一下
                    mapMvpView.showMessage("发生了未知的错误");
                    return;
                }
                //添加宝物
                TreasureRepo.getInstance().addTreasure(treasureList);
                TreasureRepo.getInstance().cache(area); //隐藏区域

                mapMvpView.setData(treasureList);
            }
    }

        // 请求失败的时候
        @Override
        public void onFailure(Call<List<Treasure>> call, Throwable t) {
            mapMvpView.showMessage(t.getMessage());
        }
    };
}
