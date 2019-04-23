package com.example.cz.pikachu3;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CZ on 2019/3/31.
 */

public class Location {

    public AMapLocationClient mLocationClient=null;
    public AMapLocationListener mLocationListener;
    public AMapLocationClientOption mLocationClientOption=null;  //用于设置发起定位的模式和相关参数
    private Context appContext;
    List<LatLng> latLngs=new ArrayList<LatLng>();
    Double lat=0.0;
    Double lon=0.0;


    public Location(Context appContext){
        this.appContext=appContext;
    }

    public LatLng getLocation(AMap aMap){


        /**
         * 定位SDK
         */

        //声明AMapLocationClient类对象
        mLocationClient=new AMapLocationClient(appContext);
        //声明定位回调监听器
        mLocationListener=new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation!=null){
                    if (aMapLocation.getErrorCode()==0){
                        System.out.println(aMapLocation.getLatitude()+"      "+aMapLocation.getLongitude());
                        lat=aMapLocation.getLatitude();
                        lon=aMapLocation.getLongitude();


//                        latLngs.add(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
//                        aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
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
//        mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        mLocationClientOption.setInterval(2000);
//        mLocationClientOption.setOnceLocation(true);
        if (mLocationClient!=null){
            //应用于AMapLocationClient
            mLocationClient.setLocationOption(mLocationClientOption);
            //类似于重启的操作，保证场景生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }

        if (lat==0.0 && lon==0.0){
            return null;
        }
        //TODO:未实现的返回
        LatLng result=new LatLng(lat,lon);
        return result;
    }

    public List<LatLng> printLine(final List<LatLng> latLngs){

        //TODO:显示行动轨迹
        mLocationClientOption.setOnceLocationLatest(false);
        mLocationClientOption.setOnceLocation(false);
        mLocationClientOption.setInterval(100000);

        mLocationListener=new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation!=null){
                    if (aMapLocation.getErrorCode()==0){
                        latLngs.add(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
                    }else{
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        return null;
    }
}
