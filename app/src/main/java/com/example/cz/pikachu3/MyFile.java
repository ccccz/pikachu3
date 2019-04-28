package com.example.cz.pikachu3;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.amap.api.maps.model.Polyline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by CZ on 2019/4/24.
 * 功能：负责与本地存储相关的处理
 */

public class MyFile {

    private String fileName="";
    private File file;
    private FileOutputStream fos;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private ObjectOutputStream oos;
    private String path;
    private FileInputStream fis;
    private ObjectInputStream ois;

    public MyFile(Context context) {
        path=context.getExternalFilesDir(null).getPath()+ "/pikapika" ;

        File temp=new File(path);
        temp.mkdir();


    }


    public boolean saveFile(Deque<Polyline> polylines, Bitmap bitmap){
        try {
            fileName=path + "/" + sdf.format(new Date()) + ".txt";
            file=new File(fileName);
            fos=new FileOutputStream(file);
            oos=new ObjectOutputStream(fos);
            oos.writeObject(bitmap);
            oos.writeObject(polylines);
            oos.close();
            fos.close();
            System.out.println(fileName+"##############################");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String,Object>> getfiles(){
        File temp=new File(path);
        List<Map<String,Object>> list=new ArrayList<>();
        if (temp.isDirectory()){
            File[] fileList=temp.listFiles();
            for (int i=0;i<fileList.length;i++){
                Map<String,Object> map=new HashMap<>();
                map.put("miao",fileList[i].getName());
                list.add(map);
            }
        }
        return list;
    }

    public Map<String,Object> openFile(String name){
        //TODO:拆包
        Map<String,Object> map=new HashMap<>();
        fileName=name;
        file=new File(path+fileName);
        try {
            fis=new FileInputStream(file);
            ois=new ObjectInputStream(fis);
            map.put("bitmap",ois.readObject());
            map.put("poilline",ois.readObject());
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return map;
    }

}
