package com.example.androiddynamicload;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView mTv = null;
    Button mBtn = null;
    Button mBtnLoadApk = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv = findViewById(R.id.text);
        mBtn = findViewById(R.id.button);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File sdcardPath = Environment.getExternalStorageDirectory().getAbsoluteFile();
                String pluginName = "myplugin_dex.jar";
                String pluginPath = sdcardPath.getAbsolutePath() + File.separator + pluginName;
//                PathClassLoader classLoader = new PathClassLoader(pluginPath, getClass().getClassLoader());
//                ClassLoader classLoader = new BaseDexClassLoader(pluginPath, sdcardPath, null, getClass().getClassLoader());
                DexClassLoader classLoader = new DexClassLoader(pluginPath, sdcardPath.getAbsolutePath(), null, getClass().getClassLoader());
                try {
                    Class mainClazz = classLoader.loadClass("com.example.myplugin.PluginMain");
                    Object pluginO = mainClazz.getConstructor(null).newInstance();

//                    IPlugin plugin = (IPlugin) pluginO;
//                    String version = plugin.getVersion();

                    Method method = mainClazz.getMethod("getVersion");
                    String version = (String) method.invoke(pluginO, null);

                    Log.v(TAG, "getVersion=" + version);
                    mTv.setText("verson=" + version);
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        });

        mBtnLoadApk = findViewById(R.id.button2);
        mBtnLoadApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File sdcardPath = Environment.getExternalStorageDirectory().getAbsoluteFile();
                    String pluginApk = "mypluginapk-debug.apk";
                    String pluginPath = sdcardPath.getAbsolutePath() + File.separator + pluginApk;

                    int resId = getAppNameResId(pluginPath);
                    Resources resources = getResourcesObject(MainActivity.this, pluginPath);
                    String apkName = resources.getText(resId).toString();

                    Log.v(TAG, "apkName=" + apkName);
                    mTv.setText("apkName=" + apkName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public int getAppNameResId(String apkPath){
        PackageManager packageManager = this.getPackageManager();
        PackageInfo info = packageManager.getPackageArchiveInfo(apkPath, 0);
        return info.applicationInfo.labelRes;

    }

    /**
     * 获取res中text
     */
    public static String loadTextFromRes(Context context, Resources resources, int resId){
        if(context == null || resources == null || resId == 0){
            return null;
        }
        CharSequence appName = null;
        try {
            appName = resources.getText(resId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return appName != null ? appName.toString() : null;
    }

    /**
     * 获去apk的res
     */
    public static Resources getResourcesObject(Context context, String apkPath) throws Exception {
        Resources res = context.getResources();

        Class<?> assertClass = Class.forName("android.content.res.AssetManager");
        Object assetMag  = assertClass.getConstructor(null).newInstance(null);
        Method method = assertClass.getMethod("addAssetPath", String.class);
        method.invoke(assetMag, apkPath);

        Class<?> resClass = Class.forName("android.content.res.Resources");
        res = (Resources) resClass.getConstructor(assertClass, res.getDisplayMetrics().getClass(), res.getConfiguration().getClass())
                .newInstance(assetMag, res.getDisplayMetrics(), res.getConfiguration());
        return res;
    }

}