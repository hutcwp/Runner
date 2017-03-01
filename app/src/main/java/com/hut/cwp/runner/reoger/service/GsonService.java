package com.hut.cwp.runner.reoger.service;

import com.google.gson.Gson;

/**
 * Created by 24540 on 2017/2/24.
 */

public class GsonService {

    public static <T> T parseJson(String jsonString, Class<T> clazz) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, clazz);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("解析json失败");
        }
        return t;

    }
}
