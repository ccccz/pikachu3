package com.example.cz.pikachu3;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceStatusListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    MapView mMapView = null;
    AMap aMap=null;
    MyLocationStyle myLocationStyle;
    Polyline polyline;
    Location mlocation;
    Double lat=0.0;
    Double lon=0.0;
    public AMapLocationClient mLocationClient=null;
    public AMapLocationListener mLocationListener;
    public AMapLocationClientOption mLocationClientOption=null;  //用于设置发起定位的模式和相关参数
    private Context appContext;
    List<LatLng> latLngs=new ArrayList<LatLng>();
    int isOK=0;
    Marker marker;
    ImageButton mButton;
    boolean isStart=true,isDraw=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        mButton=(ImageButton)findViewById(R.id.mButton);

        if(aMap==null){
            aMap=mMapView.getMap();
        }

        //设置地图
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));


        /**
         * 定位蓝点
         */

        myLocationStyle=new MyLocationStyle();
        myLocationStyle.interval(2000);
        myLocationStyle.showMyLocation(true);
        myLocationStyle.strokeWidth(0);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);

        /**
         * button点击处理
         */
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStart){
                    mButton.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                    System.out.println("click>>>>>>>>>>>>>>>>>>>");
                    isDraw=true;
                }else{
                    mButton.setImageDrawable(getResources().getDrawable(R.drawable.start));
                    System.out.println("click<<<<<<<<<<<<<<<<<<");
                    isDraw=false;
                }
                isStart=!isStart;
            }
        });

        /**
         * 定位SDK
         */

        //声明AMapLocationClient类对象
        mLocationClient=new AMapLocationClient(getApplicationContext());
        //声明定位回调监听器
        mLocationListener=new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation!=null){
                    if (aMapLocation.getErrorCode()==0) {
                        if (isOK<3){
                            isOK++;
                        }else{
                            if (isDraw){
                                latLngs.add(new LatLng(lat,lon));
                                latLngs.add(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
                                aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(13).color(R.color.qiandan));
                                latLngs.clear();
                                System.out.println("okk");
                            }
                        }
                        lat=aMapLocation.getLatitude();
                        lon=aMapLocation.getLongitude();
                    }else{
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        mLocationClient.setLocationListener(mLocationListener);
        //声明AMapLocationClientOption对象，AMapLocationClientOption用来设置发起定位的模式和相关参数
        mLocationClientOption=new AMapLocationClientOption();
        /**
         * AMapLocationClientOption设置
         */
        //设置定位场景，此处选择运动
        mLocationClientOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        //设置定位模式
        mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationClientOption.setInterval(5000);
//        mLocationClientOption.setOnceLocation(true);
        if (mLocationClient!=null){
            //应用于AMapLocationClient
            mLocationClient.setLocationOption(mLocationClientOption);
            //类似于重启的操作，保证场景生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }



        //隐藏状态栏，导航栏，actionbar
        View decorView=getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);

        /**
         * searchView设置
         */
        SearchView searchView=(SearchView) findViewById(R.id.mySearch);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(false);

        //监听searchview的内容
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("搜索       "+query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


}


/**
 * 废弃代码
 */
//          /**
 //         * 绘制轨迹
 //         */
//        LBSTraceClient lbsTraceClient=LBSTraceClient.getInstance(getApplicationContext());
//        lbsTraceClient.startTrace(new TraceStatusListener() {
//            @Override
//            public void onTraceStatus(List<TraceLocation> list, List<LatLng> list1, String s) {
//                if (list1!=null){
//                    System.out.println(s+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
////                    text.setText(s);
//                    for (int i=0;i<list1.size();i++){
//                        latLngs.add(new LatLng(list1.get(i).latitude,list1.get(i).longitude));
//                        System.out.println(list1.get(i).latitude+"       "+list1.get(i).longitude);
//                    }
//                    aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
//                    latLngs.clear();
//                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
//                }
//            }
//        });

//        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(android.location.Location location) {
//                latLngs.add(new LatLng(location.getLatitude(),location.getLongitude()));
//                polyline =aMap.addPolyline(new PolylineOptions().
//                        addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
//            }
//        });

//        /**
//         * 获取定位
//         */
//        mlocation=new Location(getApplicationContext());
//        LatLng ll=mlocation.getLocation(aMap);
//        if (ll!=null){
//            aMap.addMarker(new MarkerOptions().position(ll));
//        }

//                        if (isFirst){
//                            isFirst=false;
//                            lat=aMapLocation.getLatitude();
//                            lon=aMapLocation.getLongitude();
//                        }else{
//                            latLngs.add(new LatLng(lat,lon));
//                            latLngs.add(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
//                            lat=aMapLocation.getLatitude();
//                            lon=aMapLocation.getLongitude();
//                            aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
//                            latLngs.clear();
//                        }

//        /**
//         * 画线
//         */
//        List<LatLng> latLngs = new ArrayList<LatLng>();
//        latLngs.add(new LatLng(32.115456,118.953837));
//        latLngs.add(new LatLng(32.114373,118.954379));
//        latLngs.add(new LatLng(32.114895,118.955768));
//        latLngs.add(new LatLng(32.113687,118.956444));
//        latLngs.add(new LatLng(32.113082,118.957233));
////        mlocation.printLine(latLngs);
//        polyline =aMap.addPolyline(new PolylineOptions().
//                addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));