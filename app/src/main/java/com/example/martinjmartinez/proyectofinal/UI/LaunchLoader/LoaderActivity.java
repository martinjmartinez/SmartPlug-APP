package com.example.martinjmartinez.proyectofinal.UI.LaunchLoader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;

import java.io.IOException;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoaderActivity extends AppCompatActivity {

    private API mAPI;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        mAPI = new API();
        realm = Realm.getDefaultInstance();

        getData(mAPI.getClient());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void getData(OkHttpClient client) {
        getBuildings(client);
    }

    public void getBuildings(final OkHttpClient client) {
        Request request = new Request.Builder()
                .url(Constants.BUILDING_QUERY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("getBuildings", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code at getBuildings" + response);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAPI.getBuildingsFromCloud(response);
                        }
                    });

                    getSpaces(client);
                }

            }
        });
    }

    public void getSpaces(final OkHttpClient client) {
        Request request = new Request.Builder()
                .url(Constants.SPACE_QUERY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("getSpaces", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code at getSpaces" + response);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAPI.getSpacesFromCloud(response);
                        }
                    });

                    getDevices(client);
                }
            }
        });
    }

    public void getDevices(final OkHttpClient client) {
        Request request = new Request.Builder()
                .url(Constants.DEVICE_QUERY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("getDevices", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code at getDevices" + response);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAPI.getDevicesFromCloud(response);
                        }
                    });

                    getHistorials(client);
                }
            }
        });
    }

    public void getHistorials(OkHttpClient client) {
        Request request = new Request.Builder()
                .url(Constants.HISTORY_QUERY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("getHistorials", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code at getHistorials" + response);
                } else {
                    final String string = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAPI.getHistorialsFromCloud(string);

                        }
                    });

                    loadFinished();
                }
            }
        });
    }

    public void loadFinished() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
