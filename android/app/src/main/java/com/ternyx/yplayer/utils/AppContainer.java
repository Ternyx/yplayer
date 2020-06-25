package com.ternyx.yplayer.utils;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.ternyx.yplayer.R;
import com.ternyx.yplayer.data.db.AppDatabase;
import com.ternyx.yplayer.data.db.model.User;
import com.ternyx.yplayer.data.net.SubscriptionRepo;
import com.ternyx.yplayer.utils.AuthManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class AppContainer {

    public AppDatabase database;
    public SubscriptionRepo subRepo;
    public OkHttpClient serverClient;
    public ObjectMapper jsonObjectMapper = new ObjectMapper();
    public AuthManager authManager;

    public AppContainer(Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, "main-db").build();
        authManager = new AuthManager(context, jsonObjectMapper, database);
        serverClient = createClient(context);
        subRepo = new SubscriptionRepo(serverClient, jsonObjectMapper);
    }

    private OkHttpClient createClient(Context context) {
        HttpUrl url = HttpUrl.parse(context.getResources().getString(R.string.base_url));

        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String sessionToken = authManager.getSessionToken();

                    HttpUrl newUrl = chain.request().url().newBuilder()
                            .scheme(url.scheme())
                            .host(url.host())
                            .port(url.port())
                            .build();

                    Request newReq = chain.request().newBuilder()
                            .url(newUrl)
                            .header("X-Auth-Token", sessionToken)
                            .build();

                    Response res = chain.proceed(newReq);

                    if (res.code() == 401) {
                        User user = authManager.signInSync();
                        if (user == null) {
                            return res;
                        }

                        Request newSessionRequest = newReq.newBuilder()
                                .header("X-Auth-Token", user.getSessionToken())
                                .build();

                        return chain.proceed(newSessionRequest);
                    }
                    return res;
                })
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

}
