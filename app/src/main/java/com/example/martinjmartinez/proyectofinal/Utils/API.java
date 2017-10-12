package com.example.martinjmartinez.proyectofinal.Utils;


import android.util.Log;

import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class API {

    private OkHttpClient client;
    private Realm realm = Realm.getDefaultInstance();
    private BuildingService buildingService = new BuildingService(realm);
    private SpaceService spaceService = new SpaceService(realm);
    private DeviceService deviceService = new DeviceService(realm);

    public API() {
        this.client = new OkHttpClient();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public void getBuildingFromCloud(Response response) {
        try {
            JSONObject buildingData = new JSONObject(response.body().string());
            String _id = buildingData.getString("_id");
            String name = buildingData.getString("name");

            buildingService.createBuilding(_id, name);

        } catch (JSONException | IOException e) {
            Log.e("getBuildingFromCloud", e.getMessage());
        }
    }

    public void getDeviceFromCloud(Response response, String mSpaceId, String mBuildingId) {
        try {
            JSONObject deviceData = new JSONObject(response.body().string());
            String _id  = deviceData.getString("_id");
            String name = deviceData.getString("name");
            String ip_address = deviceData.getString("ip_address");
            Boolean isOn = deviceData.getBoolean("status");

            deviceService.createDevice(_id, name, isOn, ip_address, mSpaceId, mBuildingId);

        } catch (JSONException | IOException e) {
            Log.e("getBuildingFromCloud", e.getMessage());
        }
    }

    public void getSpaceFromCloud(Response response, String mBuildingId) {
        try {
            JSONObject spaceData = new JSONObject(response.body().string());
            String _id = spaceData.getString("_id");
            String name = spaceData.getString("name");

            spaceService.createSpace(_id, name, mBuildingId);

        } catch (JSONException | IOException e) {
            Log.e("getSpaceFromCloud", e.getMessage());
        }
    }
    public void getDevicesFromCloud(Response response) {
        try {
            JSONArray devices = new JSONArray(response.body().string());

            for (int i = 0; i < devices.length(); i++) {
                String _id = devices.getJSONObject(i).getString("_id");
                String name = devices.getJSONObject(i).getString("name");
                String ip_address = devices.getJSONObject(i).getString("ip_address");
                Boolean isOn = devices.getJSONObject(i).getBoolean("status");
                String spaceId = devices.getJSONObject(i).getString("space");
                String buildingId = devices.getJSONObject(i).getString("building");

                deviceService.updateOrCreateDevice(_id, name, isOn, ip_address, spaceId, buildingId);

                if (devices.getJSONObject(i).has("lastHistoryId")) {
                    String lastHistoryId = devices.getJSONObject(i).getString("lastHistoryId");

                    deviceService.updateDeviceLastHistoryId(_id, lastHistoryId);
                }
            }
        } catch (JSONException | IOException e) {
            Log.e("getDevicesFromCloud", e.getMessage());
        }
    }

    public String[] getArduinoInfo(Response response) {
        String[] data = new String[2];
        try {
            JSONObject deviceData = new JSONObject(response.body().string());
            double power = 0;
            int status = 0;
            power = deviceData.getJSONObject("variables").getDouble("potencia");
            status = deviceData.getJSONObject("variables").getInt("status");

            Log.e("power", power + "");
            Log.e("status", status + "");

            data[0] = String.valueOf(power);
            data[1] = String.valueOf(status);

            return data;

        } catch (JSONException | IOException e) {
            Log.e("getArduinoInfo", e.getMessage());
        }

        return data;
    }

    public void getSpacesFromCloud(Response response) {
        try {
            JSONArray spaces = new JSONArray(response.body().string());

            for (int i = 0; i < spaces.length(); i++) {
                String _id = spaces.getJSONObject(i).getString("_id");
                String name = spaces.getJSONObject(i).getString("name");
                String buildingId = spaces.getJSONObject(i).getString("building");

                spaceService.updateOrCreateSpace(_id, name, buildingId);
            }
        } catch (JSONException | IOException e) {
            Log.e("getSpacesFromCloud", e.getMessage());
        }
    }

    public void getBuildingsFromCloud(Response response) {
        try {
            JSONArray buildings = new JSONArray(response.body().string());

            for (int i = 0; i < buildings.length(); i++) {
                String _id = buildings.getJSONObject(i).getString("_id");
                String name = buildings.getJSONObject(i).getString("name");

                buildingService.updateOrCreateBuilding(_id, name);
            }

        } catch (JSONException | IOException e) {
            Log.e("getBuildingsFromCloud", e.getMessage());
        }
    }
}
