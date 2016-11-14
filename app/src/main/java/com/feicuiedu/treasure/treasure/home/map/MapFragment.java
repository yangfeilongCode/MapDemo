package com.feicuiedu.treasure.treasure.home.map;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.feicuiedu.treasure.R;
import com.feicuiedu.treasure.commons.ActivityUtils;
import com.feicuiedu.treasure.components.TreasureView;
import com.feicuiedu.treasure.treasure.Area;
import com.feicuiedu.treasure.treasure.Treasure;
import com.feicuiedu.treasure.treasure.TreasureRepo;
import com.feicuiedu.treasure.treasure.home.detail.TreasureDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 *
 */
public class MapFragment extends Fragment implements MapMvpView{

    @BindView(R.id.map_frame)
    FrameLayout mapFrame;
    @BindView(R.id.centerLayout)
    RelativeLayout centerLayout;
    @BindView(R.id.treasureView)
    TreasureView treasureView;
    @BindView(R.id.layout_bottom)
    FrameLayout layoutBottom;
    @BindView(R.id.hide_treasure)
    RelativeLayout hideTreasure;
    @BindView(R.id.btn_HideHere)
    Button btnHideHere;
    @BindView(R.id.tv_currentLocation)
    TextView tvCurrentLocation;
    @BindView(R.id.iv_located)
    ImageView ivLocated;
    @BindView(R.id.et_treasureTitle)
    EditText etTreasureTitle;

    private MapView mapView;
    private BaiduMap baiduMap;
    private Unbinder bind;
    private LocationClient locationClient;
    private static LatLng myLocation;
    private ActivityUtils activityUtils;

    private LatLng target;// 用来暂时保存一下当前地图的位置，方便我们判断地图的位置有没有变化

    private boolean isFirstLocate = true;// 这个主要是用来判断是不是第一进来的时候的定位
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        bind = ButterKnife.bind(this, view);
        activityUtils=new ActivityUtils(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化百度地图
        initBaiduMap();


        // 初始化定位相关
      initLocation();
    }

    private void initLocation() {
        /**
         * 1. 开启定位图层
         * 2. 定位类的实例化
         * 3. 定位进行一些相关的设置
         * 4. 设置定位的监听
         * 5. 开始定位（为了处理某些机型初始化定位不成功，需要重新请求定位）
         */
        baiduMap.setMyLocationEnabled(true);
        locationClient = new LocationClient(getContext());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setCoorType("bd09ll");// 设置百度坐标类型
        option.setIsNeedAddress(true);// 设置需要地址信息
        locationClient.setLocOption(option);// 要把我们做的设置给LocationClient
        locationClient.registerLocationListener(locationListener);// 设置百度地图的监听
        locationClient.start();// 开始定位
        locationClient.requestLocation();// 为了处理某些机型初始化定位不成功，需要重新请求定位
    }

    private BDLocationListener locationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 处理一下定位
            /**
             * 1. 判断有没有定位成功
             * 2. 获得定位信息(经纬度)
             * 3. 定位信息设置到地图上
             * 4. 移动到定位的位置去
             *      第一次进入：一进到项目里面就会移动到定位的位置去
             *      点击定位按钮：其他时候如果需要定位
             */
            if (bdLocation == null) {
                locationClient.requestLocation();
                return;
            }
            double lng = bdLocation.getLongitude();// 经度
            double lat = bdLocation.getLatitude();//纬度

            // 拿到定位的位置
            myLocation = new LatLng(lat, lng);

            MyLocationData myLocationData = new MyLocationData.Builder()
                    .longitude(lng)
                    .latitude(lat)
                    .accuracy(100f)// 精度，定位圈的大小
                    .build();

            baiduMap.setMyLocationData(myLocationData);
            if (isFirstLocate) {
                moveToMyLocation();
                isFirstLocate = false;
            }
        }
    };

    public static LatLng getMyLocation(){
        return myLocation;
    }

    private void initBaiduMap() {

        // 查看百度地图的ＡＰＩ

        // 百度地图状态
        MapStatus mapStatus = new MapStatus.Builder()
                .overlook(0)// 0--(-45) 地图的俯仰角度
                .zoom(15)// 3--21 缩放级别
                .build();

        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)// 设置地图的状态
                .compassEnabled(true)// 指南针
                .zoomGesturesEnabled(true)// 设置允许缩放手势
                .rotateGesturesEnabled(true)// 旋转
                .scaleControlEnabled(false)// 不显示比例尺控件
                .zoomControlsEnabled(false);// 不显示缩放控件

        // 创建一个MapView
        mapView = new MapView(getContext(), options);

        // 在当前的Layout上面添加MapView
        mapFrame.addView(mapView, 0);

        // MapView 的控制器
        baiduMap = mapView.getMap();

        // 怎么对地图状态进行监听？
        baiduMap.setOnMapStatusChangeListener(mapStatusChangeListener);
        //地图标的点击事件
        baiduMap.setOnMarkerClickListener(clickListener);

    }

    // 地图类型的切换（普通视图--卫星视图）
    @OnClick(R.id.tv_satellite)
    public void switchMapType() {
        // 先获得当前的类型
        int type = baiduMap.getMapType();
        type = type == BaiduMap.MAP_TYPE_NORMAL ? BaiduMap.MAP_TYPE_SATELLITE : BaiduMap.MAP_TYPE_NORMAL;
        baiduMap.setMapType(type);
    }

    // 定位实现
    @OnClick(R.id.tv_located)
    public void moveToMyLocation() {

        // 将地图位置设置成定位的位置
        MapStatus mapStatus = new MapStatus.Builder()
                .target(myLocation)
                .rotate(0)// 地图位置摆正
                .zoom(19)
                .build();
        // 更新地图状态
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.animateMapStatus(update);
    }
    //指南调正
    @OnClick(R.id.tv_compass)
    public void switchCompass() {
        /**
         * 指南针是地图视图的一个图标
         */
        boolean isCompass = baiduMap.getUiSettings().isCompassEnabled();
        baiduMap.getUiSettings().setCompassEnabled(!isCompass);
    }
   //放大缩小控件
    @OnClick({R.id.iv_scaleUp, R.id.iv_scaleDown})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_scaleUp:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());// 放大
                break;
            case R.id.iv_scaleDown:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());// 缩小
                break;
        }
    }
   //点击宝藏视图
    @OnClick(R.id.treasureView)
    public void clickTreasureView() {  //点击宝藏
        // 跳转到详情页面，宝藏传递过去
        int id = currentMarker.getExtraInfo().getInt("id");
        Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
        TreasureDetailActivity.open(getContext(),treasure);
    }





    // 百度地图状态的监听
    private BaiduMap.OnMapStatusChangeListener mapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            //当地图的状态发生变化时，动态的去获取某区宇德的宝藏数据

            //地图状态发生变化
           LatLng target=mapStatus.target;
            //判断位置有没有变化
            if (target!=MapFragment.this.target){

                // 位置发生变化了，去进行此位置周边的宝藏数据获取，提供方法来进行
                updateMapArea();

                //将位置更新为变化后的位置
                MapFragment.this.target=target;
            }
        }
    };
  //位置发生变化，区域也发生变化，更新区域，动态获取区域内的数据
    public void updateMapArea(){

        MapStatus mapStatus=baiduMap.getMapStatus();
        //获取经纬度
        double lng=mapStatus.target.longitude;
        double lat=mapStatus.target.latitude;

        Area area=new Area();
        //经纬度的向上取整
        area.setMaxLat(Math.ceil(lat));
        area.setMaxLng(Math.ceil(lng));
        //经纬度的向下取整
        area.setMinLat(Math.floor(lat));
        area.setMinLng(Math.floor(lng));

        //根据区域来进行获取数据
        new MapPresenter(this).getTreasure(area);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();  //解绑
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void setData(List<Treasure> list) {
        // TODO 我们要将拿到的宝藏数据以添加覆盖物的形式展示
        for (Treasure treasure : list) {
            //获取经纬度
            LatLng latLng = new LatLng(treasure.getLatitude(), treasure.getLongitude());
            // 要在经纬度处添加覆盖物Marker
            addMarker(latLng, treasure.getId());
        }
    }
    private BitmapDescriptor dot=BitmapDescriptorFactory.fromResource(R.drawable.treasure_dot);
    private BitmapDescriptor dot_click=BitmapDescriptorFactory.fromResource(R.drawable.treasure_expanded);
    //添加宝藏的覆盖物，每一个覆盖物中都包含各自的宝藏信息
    public void addMarker(LatLng latLng,int treasureId){

        MarkerOptions options=new MarkerOptions();
        options.position(latLng);//位置
        options.icon(dot);//图片
        options.anchor(0.5f,0.5f);//覆盖物锚点

        Bundle bundle=new Bundle();  //绑定
        bundle.putInt("id",treasureId); //传id
        options.extraInfo(bundle);  //覆盖物额外信息
        baiduMap.addOverlay(options);//添加覆盖物

    }

    private Marker currentMarker;//标记
    //标记点击监听事件
    private BaiduMap.OnMarkerClickListener clickListener=new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
          if (currentMarker!=null){ //标记为空
              currentMarker.setVisible(true); //展示宝藏数据弹框
          }
            currentMarker=marker; //重新设置标记
            currentMarker.setVisible(false); //隐藏宝物数据

            InfoWindow infoWindow=new InfoWindow(dot_click,currentMarker.getPosition(),0,infoWindowClickListener);
            baiduMap.showInfoWindow(infoWindow);

            //取出当前的Marker宝藏信息
            int id=marker.getExtraInfo().getInt("id");
            Log.e("aaaaa", "id --------"+id );
            Treasure treasure=TreasureRepo.getInstance().getTreasure(id);
            Log.e("aaaaa", "bindTreasure --------"+treasure );
            treasureView.bindTreasure(treasure);
            /**
             * 切换到宝藏选中视图
             */
            changeUIMode(UI_MODE_SECLECT);

            return false;
        }
    };
    //信息弹框点击事件
    private InfoWindow.OnInfoWindowClickListener infoWindowClickListener=new InfoWindow.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick() {
            //TODO  隐藏
       changeUIMode(UI_MODE_NORMAL); //切回普通视图

        }
    };



    private static final int UI_MODE_NORMAL=0;//普通视图
    private static final int UI_MODE_SECLECT=1;//宝藏选中视图
    private static final int UI_MODE_HIDE=2;//埋藏视图

    private int uiMode= UI_MODE_NORMAL;

    //提供一个方法：用来切换地图
    private void changeUIMode(int uiMode){
        if (this.uiMode==uiMode){
            return;
        }
        this.uiMode=uiMode;
        switch (uiMode){
            case UI_MODE_NORMAL:
                if (currentMarker!=null){
                    currentMarker.setVisible(true);
                }
                baiduMap.hideInfoWindow();
                layoutBottom.setVisibility(View.GONE);
                centerLayout.setVisibility(View.GONE);
                break;
            case UI_MODE_SECLECT:
                layoutBottom.setVisibility(View.VISIBLE); //宝藏信息展示
                treasureView.setVisibility(View.VISIBLE); //展示宝藏信息卡片
                centerLayout.setVisibility(View.GONE);  //埋藏宝藏地点
                hideTreasure.setVisibility(View.GONE);  //隐藏宝藏信息卡片
                break;
            case UI_MODE_HIDE:
                centerLayout.setVisibility(View.VISIBLE);
                layoutBottom.setVisibility(View.GONE);
                btnHideHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutBottom.setVisibility(View.VISIBLE);
                        hideTreasure.setVisibility(View.VISIBLE);
                        treasureView.setVisibility(View.GONE);
                    }
                });
                break;

        }
    }
}
