package com.ternyx.yplayer.utils;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.ternyx.yplayer.App;
import com.ternyx.yplayer.AppActivity;
import com.ternyx.yplayer.R;
import com.ternyx.yplayer.data.db.AppDatabase;
import com.ternyx.yplayer.data.db.model.User;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthManager {
    private Context context;

    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private ObjectMapper objectMapper;
    private AppDatabase database;

    public AuthManager(Context context, ObjectMapper objectMapper, AppDatabase database) {
        this.context = context;
        this.objectMapper = objectMapper;
        this.database = database;

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(context.getResources().getString(R.string.youtube_default_scope)))
                .requestServerAuthCode(context.getResources().getString(R.string.youtube_server_client_id))
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public void signOut() {
        googleSignInClient.signOut();
    }

    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(context) != null;
    }

    public void signIn(GoogleSignInAccount account, Consumer<Boolean> callback) {
        String serverAuthCode = account.getServerAuthCode();

        ObjectNode node = objectMapper.createObjectNode();
        node.put("code", serverAuthCode);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), node.toString());

        Request req = new Request.Builder()
                .url(context.getResources().getString(R.string.base_url) + "/auth/code")
                .post(body)
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.accept(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String xauthToken = response.header("X-Auth-Token");

                    User user = objectMapper.readValue(response.body().string(), User.class);
                    response.close();

                    user.setGoogleId(account.getId());
                    user.setSessionToken(xauthToken);

                    database.userDao().insertUser(user);
                    callback.accept( true);
                }
            }
        });
    }

    public User signInSync() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account == null) {
            Task<GoogleSignInAccount> taskAcc = googleSignInClient.silentSignIn();
            account = taskAcc.getResult();
        }

        String serverAuthCode = account.getServerAuthCode();

        ObjectNode node = objectMapper.createObjectNode();
        node.put("code", serverAuthCode);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), node.toString());

        Request req = new Request.Builder()
                .url(context.getResources().getString(R.string.base_url) + "/auth/code")
                .post(body)
                .build();

        try {
            Response response =  client.newCall(req).execute();
            if (!response.isSuccessful()) {
                return null;
            }

            String xauthToken = response.header("X-Auth-Token");

            User user = objectMapper.readValue(response.body().string(), User.class);
            response.close();

            user.setGoogleId(account.getId());
            user.setSessionToken(xauthToken);

            database.userDao().insertUser(user);
            return user;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSessionToken() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account == null) {
            return null;
        }
        Log.d("HAHA", "getSessionToken: " + database);
        Log.d("HAHA", "getSessionToken: " + database.userDao());
        Log.d("HAHA", "getSessionToken: " + database.userDao());
        return database.userDao().findByGoogleId(account.getId()).getSessionToken();
    }

    public void signIn(Intent data, Consumer<Boolean> callback) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            signIn(account, callback);
        } catch (ApiException e) {
            callback.accept(false);
        }
    }
}
