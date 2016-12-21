package com.zpauly.bind;

import android.app.Activity;
import android.app.ActivityManager;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Method;

/**
 * Created by zpauly on 2016/12/20.
 */

public class Bind {
    private static final String TAG = Bind.class.getName();

    private Object obj;

    private Source mSource;

    private Bind(Source source, Object obj) {
        this.mSource = source;
        this.obj = obj;
    }

    public static void bindView(View view, Object obj) {
        new Bind(new ViewSource(view), obj).bind();
    }

    public static void bindView(Activity activity) {
        new Bind(new ActivitySource(activity), activity).bind();
    }

    private void bind() {
        String objName = obj.getClass().getCanonicalName();
        Log.i(TAG, "obj name = " + objName);
        try {
            Class<?> binderClass = Class.forName(objName + "_Binding");
            Object instance = binderClass.newInstance();
            Method bindViewMethod = binderClass.getMethod("bindView", obj.getClass(), Source.class);
            bindViewMethod.setAccessible(true);
            bindViewMethod.invoke(instance, obj, mSource);
            Log.i(TAG, "method bind view invoked");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
