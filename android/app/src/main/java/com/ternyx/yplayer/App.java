package com.ternyx.yplayer;

import android.app.Application;

import com.ternyx.yplayer.utils.AppContainer;

public class App extends Application {
    public AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer(this);
    }
}
