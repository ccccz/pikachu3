//package com.example.cz.pikachu3;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.support.v4.app.NotificationCompat;
//import android.view.View;
//import android.widget.RemoteViews;
//
///**
// * Created by CZ on 2019/4/4.
// */
//
//public class MyService extends Service {
////    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    //创建
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        //普通notification用到的视图
//        RemoteViews normalView = new RemoteViews(getPackageName(), R.layout.normal_notification);
////        Intent intent2=new Intent(this,MainActivity.class);
////        PendingIntent pi=PendingIntent.getActivity(this,0,intent2,0);
////        Notification notification=new NotificationCompat.Builder(this){
////
////        };
//        startForeground(1,notification);
//        System.out.println("OK!!!!");
//    }
//
//    //发送自定义视图通知
//    public void sendCustomViewNotification(View view) {
//        //普通notification用到的视图
//        RemoteViews normalView = new RemoteViews(getPackageName(), R.layout.normal_notification);
//
//        Notification notification = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setTicker("开始播放啦~~")
//                .setOngoing(true)
//                .setContent(normalView)//设置普通notification视图
//                .setPriority(NotificationCompat.PRIORITY_MAX)//设置最大优先级
//                .build();
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(22, notification);
//    }
//
//    //启动
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    //销毁
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    public void startFore(){
//
//    }
//}
