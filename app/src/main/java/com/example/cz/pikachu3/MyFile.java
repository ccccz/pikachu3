package com.example.cz.pikachu3;

import android.graphics.Bitmap;
import android.os.Environment;

import com.amap.api.maps.model.Polyline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;
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


    public boolean save(Deque<Polyline> polylines, Bitmap bitmap){
        try {
            fileName=Environment.getExternalStorageDirectory() +"/store"+ sdf.format(new Date()) + ".txt";
            file=new File(fileName);
            if (!file.exists()){
                file.mkdir();
            }
            fos=new FileOutputStream(file);
            oos=new ObjectOutputStream(fos);
            oos.writeObject(bitmap);
            oos.writeObject(polylines);
            oos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
