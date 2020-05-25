package com.example.myplugin;

import com.example.androiddynamicload.IPlugin;

public class PluginMain implements IPlugin {

    public String getVersion(){
        return "plugin_version_1.0";
    }

    @Override
    public void main() {
        //do everythig
    }

}
