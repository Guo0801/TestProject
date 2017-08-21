package com.test.alisa.baidumaptest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;


public class MainActivity extends Activity implements View.OnClickListener {


    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MapStatus mMapStatus;
    private ImageButton mIbSmall;
    private ImageButton mIbLarge;
    private ImageButton mIbMode;
    private ImageButton mIbTraffice;
    private ImageButton mIbLocation;

    //模式切换，正常模式
    private boolean modeFlag=true;
    //当前地图的缩放比例
    private float zoomLevel;
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    //是否为第一次定位【若是第一次定位需要将自己的位置显示在地图中间】
    private boolean isFirstLocation=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 百度地图的SDK的初始化
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.fragment_map);

        //初始化控件
        initView();

        //初始化地图
        initMap();

        //初始化定位
        initLocation();

    }

    /**
     * 定位初始化
     */
    private void initLocation() {
        //定位客户端设置
        mLocationClient=new LocationClient(this);
        mLocationListener=new MyLocationListener();

        //注册监听
        mLocationClient.registerLocationListener(mLocationListener);

        //定位
        LocationClientOption option=new LocationClientOption();
        //坐标位置
        option.setCoorType("bd09ll");
        //【可选】设置是否需要地址信息【默认不需要】
        option.setIsNeedAddress(true);
        //打开GPS
        option.setOpenGps(true);
        //每1000毫秒定位一次
        option.setScanSpan(1000);


        mLocationClient.setLocOption(option);


    }

    /**
     * 地图初始化
     */
    private void initMap() {
        //获取地图控件
        mMapView=findViewById(R.id.map_view);
        //不显示缩放比例尺
        mMapView.showZoomControls(false);
//        //不显示百度地图logo
//        mMapView.removeViewAt(1);

        //百度地图
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        //改变地图状态
        mMapStatus = new MapStatus.Builder()
                                  .zoom(15)
                                  .build();
        MapStatusUpdate mMapStatusUpdate=MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        //设置地图状态改变的监听
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            //开始改变地图状态
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            //当地图状态改变时
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            //完成地图状态的改变
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                //当地图状态改变时，获取放大级别
                zoomLevel=mapStatus.zoom;

            }
        });
    }

    /**
     * 控件初始化
     */
    private void initView() {
        //地图控制按钮
        mIbSmall=findViewById(R.id.ib_small);
        mIbSmall.setOnClickListener(this);
        mIbLarge=findViewById(R.id.ib_large);
        mIbLarge.setOnClickListener(this);
        mIbMode=findViewById(R.id.ib_mode);
        mIbMode.setOnClickListener(this);
        mIbLocation=findViewById(R.id.ib_loc);
        mIbLocation.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_large:
                if(zoomLevel<18){
                   mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                    mIbSmall.setEnabled(true);
                }else {
//                    Toast.makeText(this,"地图已放至最大，可继续滑动操作", Toast.LENGTH_SHORT).show();
                      showInfo("地图已放至最大，可继续滑动操作");
                    mIbLarge.setEnabled(false);
                }
                break;
            case R.id.ib_small:
                if(zoomLevel>6){
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                    mIbLarge.setEnabled(true);
                }else{
//                    Toast.makeText(this, "地图已放至最小，可继续滑动操作", Toast.LENGTH_SHORT).show();
                    showInfo("地图已放至最小，可继续滑动操作");
                    mIbSmall.setEnabled(false);
                }
                break;
            case R.id.ib_mode://卫星模式和普通模式
                if(modeFlag){
                    modeFlag=false;
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
//                    Toast.makeText(this, "开启卫星模式", Toast.LENGTH_SHORT).show();
                    showInfo("开启卫星模式");
                }else{
                    modeFlag=true;
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//                    Toast.makeText(this, "开启普通模式", Toast.LENGTH_SHORT).show();
                    showInfo("开启普通模式");
                }
                break;
            case R.id.ib_loc:
                isFirstLocation=true;
//                Toast.makeText(this, "返回自己位置", Toast.LENGTH_SHORT).show();

                showInfo("返回自己位置");
                break;
            case R.id.ib_traffic://是否开启交通地图
                if(mBaiduMap.isTrafficEnabled()){
                    mBaiduMap.setTrafficEnabled(false);
                    mIbTraffice.setBackgroundResource(R.drawable.offtraffic);
//                    Toast.makeText(this, "关闭实时交通图", Toast.LENGTH_SHORT).show();
                    showInfo("关闭实时交通图");
                }else {
                   mBaiduMap.setTrafficEnabled(true);
                    mIbTraffice.setBackgroundResource(R.drawable.ontraffic);
//                    Toast.makeText(this, "开启实时交通图", Toast.LENGTH_SHORT).show();
                    showInfo("开启实时交通图");
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭定位
        mBaiduMap.setMyLocationEnabled(false);
        if (mLocationClient.isStarted()){
            mLocationClient.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当activity执行onDestory时执行的 mMapView.onDestroy();实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //当activity执行onDestory时执行的 mMapView.onResume();实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //当activity执行onDestory时执行的 mMapView.onPause();实现地图生命周期管理
        mMapView.onPause();
    }

    /**
     * 显示消息
     * @param s
     */
    private void showInfo(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * 自定义定位监听
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //将获取的location信息给百度地图
            MyLocationData data=new MyLocationData.Builder()
                     .accuracy(bdLocation.getRadius())
                     .direction(100)
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(data);

            if(isFirstLocation){
                //获取经纬度
                LatLng latlng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(latlng);
//                //直接到中间
//                mBaiduMap.setMapStatus(update);
                //动画的方式到中间
                mBaiduMap.animateMapStatus(update);

                isFirstLocation=false;
                showInfo("位置： "+bdLocation.getAddrStr());

            }

        }
    }

}




