package com.example.cz.pikachu3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    MapView mMapView = null;
    AMap aMap=null;
    MyLocationStyle myLocationStyle;
    Polyline polyline;
    Deque<Polyline> polylines=new ArrayDeque<>();  //存储本次轨迹记录
    Location mlocation;
    Double lat=0.0;
    Double lon=0.0;
    public AMapLocationClient mLocationClient=null;
    public AMapLocationListener mLocationListener;
    public AMapLocationClientOption mLocationClientOption=null;  //用于设置发起定位的模式和相关参数
    private Context appContext;
    List<LatLng> latLngs=new ArrayList<LatLng>();
    LatLng latLng;
    int isOK=0;
    Marker marker;
    ImageButton mButton, delButton,locaButton,saveButton,hisButton;
    boolean isStart=true,isDraw=false;
    LocationManager locationManager;
    UiSettings uiSettings;
    ListView listView;
    MyFile myFile;
    Bitmap mbitMap;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        myFile=new MyFile(getApplicationContext());
        mButton=(ImageButton)findViewById(R.id.mButton);
        delButton =(ImageButton)findViewById(R.id.updataButton);
        saveButton=(ImageButton)findViewById(R.id.saveButton);
        hisButton=(ImageButton)findViewById(R.id.hisButton);
        locaButton=(ImageButton)findViewById(R.id.locaButton);
        mListView=(ListView)findViewById(R.id.mlistView);

        if(aMap==null){
            aMap=mMapView.getMap();
        }

        //设置地图
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));

        /**
         * 设置控件样式
         */
        uiSettings=aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);


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
         * button点击处理
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

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (polylines!=null){
                    Polyline temp=polylines.pop();
                    temp.remove();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shotPhoto();
                myFile.saveFile(polylines,mbitMap);
            }
        });

        hisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /**
         * 定位按钮
         */
        locaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
            }
        });

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



        //隐藏状态栏，导航栏，actionbar
        View decorView=getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
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


                //TODO:待删
                System.out.println("搜索       "+query);
                List<LatLng> lll = new ArrayList<LatLng>();
                lll.add(new LatLng(32.113912,118.953266));
                lll.add(new LatLng(32.114384,118.954371));
                lll.add(new LatLng(32.113144,118.954993));
                lll.add(new LatLng(32.112726,118.953813));
                lll.add(new LatLng(32.111563,118.954398));
                lll.add(new LatLng(32.111985,118.955556));

                printLine(lll);

                lll.clear();
                lll.add(new LatLng(32.114384,118.954419));
                lll.add(new LatLng(32.114911,118.955739));
                lll.add(new LatLng(32.113675,118.956442));
                lll.add(new LatLng(32.113148,118.954988));
                lll.add(new LatLng(32.113675,118.956442));
                lll.add(new LatLng(32.113053,118.957236));
                lll.add(new LatLng(32.112285,118.956656));
                lll.add(new LatLng(32.112031,118.955975));

                printLine(lll);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

//        /***
//         * 设置服务,状态栏
//         */
//        Intent intent=new Intent(this,MyService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//        }else{
//            startService(intent);
//        }

        /**
        * 关于GPS打开的设置
        */
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        boolean isOK=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isOK){
            gpsDialog();
        }
        /**
         * 测试画线功能，通过
         */

//        List<LatLng> lll = new ArrayList<LatLng>();
//        lll.add(new LatLng(32.115456,118.953837));
//        lll.add(new LatLng(32.114373,118.954379));
//        lll.add(new LatLng(32.114895,118.955768));
//        lll.add(new LatLng(32.113687,118.956444));
//        lll.add(new LatLng(32.113082,118.957233));
//
//        printLine(lll);


        /**
         * 新建文件夹
         */
        File temp=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/pikapika" );
        temp.mkdir();

        /**
         * ListView适配器
         */
        List<Map<String,Object>> mlist=myFile.getfiles();
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,mlist,R.layout.list_layout,new String[]{"miao"},new int[]{R.id.testText});
        mListView.setAdapter(simpleAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map<String,Object>> mlist=myFile.getfiles();
                Intent intent=new Intent(MainActivity.this,HistoryActivity.class);
                intent.putExtra("miao",(String) mlist.get(position).get("miao"));
                startActivity(intent);
            }
        });
    }

    /**
     * Destroy
     */

    @Override
    protected void onDestroy() {
        System.out.println("DES");
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();

//        /**
//         * 停止服务
//         */
//        Intent stopIntent=new Intent(this,MyService.class);
//        stopService(stopIntent);

    }

    /**
     * Resume
     */

    @Override
    protected void onResume() {
        System.out.println("RES");
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));

    }

    /**
     * Pause
     */

    @Override
    protected void onPause() {
        System.out.println("PAU");
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    /**
     * SaveInstanceState
     * @param outState
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * GPS弹出框
     */

    private void gpsDialog(){
        final AlertDialog.Builder myDialog=new AlertDialog.Builder(MainActivity.this);
        myDialog.setTitle("GPS设置");
        myDialog.setMessage("请求GPS权限");
        myDialog.setPositiveButton("打开GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent intent2 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent2);
            }
        });

        myDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        myDialog.show();
    }

    /**
     * 画线
     */
    private Polyline printLine(List<LatLng> ls){
        polyline=aMap.addPolyline(new PolylineOptions().addAll(ls).width(15).color(R.color.qiuxiang).lineJoinType(PolylineOptions.LineJoinType.LineJoinRound));
        System.out.println("printLine success");
        return polyline;
    }

    /**
     * 侧边栏初始化
     */
    private void initView(){
        listView=(ListView)findViewById(R.id.mlistView);
    }

    /**
     * 截图功能
     */
    private void shotPhoto(){

        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {

            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int status) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                if(null == bitmap){
                    return;
                }
                try {
                    File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/pikapika" + "/test_"
                            + sdf.format(new Date()) + ".png");
                    System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath()+"*********");
                    FileOutputStream fos = new FileOutputStream(file);
                    boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    mbitMap=bitmap;
                    //发送广播通知
                    Uri uri=Uri.fromFile(file);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri));

                    try {
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    StringBuffer buffer = new StringBuffer();
                    if (b)
                        buffer.append("截屏成功 ");
                    else {
                        buffer.append("截屏失败 ");
                    }
                    if (status != 0)
                        buffer.append("地图渲染完成，截屏无网格");
                    else {
                        buffer.append( "地图未渲染完成，截屏有网格");
                    }
                    System.out.println(buffer.toString());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
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