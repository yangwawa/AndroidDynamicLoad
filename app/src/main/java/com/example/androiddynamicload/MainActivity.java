package com.example.androiddynamicload;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView mTv = null;
    Button mBtn = null;
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
//                String pluginName = "myplugin.jar";
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
    }
}