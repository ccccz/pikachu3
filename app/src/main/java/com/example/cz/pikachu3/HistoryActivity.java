package com.example.cz.pikachu3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by CZ on 2019/4/28.
 */

public class HistoryActivity extends Activity{

    private MapView mMapView;
    MyLocationStyle myLocationStyle;
    AMap aMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationListener mLocationListener;
    int isOK=0;
    private ArrayList<LatLng> latLngs=new ArrayList<>();
    private AMapLocationClientOption mLocationClientOption;
    private Polyline polyline;
    private LatLng latLng;
    private UiSettings uiSettings;
    private MyFile myFile;
    private Deque<Polyline> polylines=new ArrayDeque<>();
    private ImageButton mButton;
    boolean isStart=true,isDraw=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("history create!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mMapView = (MapView) findViewById(R.id.hisMap);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        myFile=new MyFile(getApplication());
        mButton=(ImageButton)findViewById(R.id.mButton2);

        String path=this.getIntent().getStringExtra("miao");
        System.out.println(path+"!!!!!!!!!!!!!!!!");

        if(aMap==null){
            aMap=mMapView.getMap();
        }

        /**
         * 定位蓝点
         */
        myLocationStyle=new MyLocationStyle();
        myLocationStyle.interval(2000);
        myLocationStyle.showMyLocation(true);
        myLocationStyle.strokeWidth(0);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);


        /**
         * 定位SDK,画线
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
                                latLngs.add(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
                                polyline.setPoints(latLngs);
                            }
                        }
                        latLng=new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
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

        /**
         * 地图的初始设置
         */
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        uiSettings=aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);

        /**
         * 拆包
         */
        Map<String,Object> map=myFile.openFile(path);
        Deque<Polyline> polylines2=(ArrayDeque<Polyline>)map.get("poilline");
        if (polylines2!=null){
            for (int i=0;i<polylines2.size();i++){
                polyline=aMap.addPolyline(new PolylineOptions().addAll(polylines2.pop().getPoints()).width(15).color(R.color.qiuxiang).lineJoinType(PolylineOptions.LineJoinType.LineJoinRound));
            }
        }


        /**
         * 点击
         */
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStart){
                    mButton.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                    System.out.println("click>>>>>>>>>>>>>>>>>>>");
                    isDraw=true;
                    polyline=aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(13).color(R.color.qiandan).lineJoinType(PolylineOptions.LineJoinType.LineJoinRound));
                }else{
                    mButton.setImageDrawable(getResources().getDrawable(R.drawable.start));
                    System.out.println("click<<<<<<<<<<<<<<<<<<");
                    isDraw=false;
                    polylines.push(polyline);
                    polyline=null;
                    latLngs.clear();
                }
                isStart=!isStart;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }

}
