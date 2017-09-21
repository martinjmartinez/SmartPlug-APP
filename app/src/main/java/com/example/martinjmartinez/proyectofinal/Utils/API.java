package com.example.martinjmartinez.proyectofinal.Utils;


import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/12/2017.
 */

public class API {

    private OkHttpClient client;

    public API() {
        client = new OkHttpClient();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public Device getDevice(Response response) {
        try {
            JSONObject deviceData = new JSONObject(response.body().string());
            Log.e("DATA", deviceData.toString());
            Device device = new Device();

            device.set_id(deviceData.getString("_id"));
            device.setName(deviceData.getString("name"));
            device.setIp_address(deviceData.getString("ip_address"));
            device.setStatus(deviceData.getBoolean("status"));

            if (deviceData.has("space")) {
                if (deviceData.get("space") instanceof JSONObject) {
                    Space space = new Space();

                    space.set_id(deviceData.getJSONObject("space").getString("_id"));
                    space.setName(deviceData.getJSONObject("space").getString("name"));

                    device.setSpace(space);
                }
            }
            if (deviceData.has("building")) {
                if (deviceData.get("building") instanceof JSONObject) {
                    Building building = new Building();
                    Log.e("entro", "klkkk");
                    building.set_id(deviceData.getJSONObject("building").getString("_id"));
                    building.setName(deviceData.getJSONObject("building").getString("name"));

                    device.setBuilding(building);
                }
            }

            return device;

        } catch (JSONException | IOException e) {
            Log.e("getDevice", e.getMessage());
        }

        return null;
    }

    public List<Device> getDeviceList(Response response) {
        List<Device> devicesList = new ArrayList<>();

        try {
            JSONArray devices = new JSONArray(response.body().string());

            for (int i = 0; i < devices.length(); i++) {
                Device device = new Device();

                device.set_id(devices.getJSONObject(i).getString("_id"));
                device.setName(devices.getJSONObject(i).getString("name"));
                device.setIp_address(devices.getJSONObject(i).getString("ip_address"));
                device.setStatus(devices.getJSONObject(i).getBoolean("status"));

                if (devices.getJSONObject(i).has("space")) {
                    if (devices.getJSONObject(i).get("space") instanceof JSONObject) {
                        Space space = new Space();

                        space.set_id(devices.getJSONObject(i).getJSONObject("space").getString("_id"));
                        space.setName(devices.getJSONObject(i).getJSONObject("space").getString("name"));

                        device.setSpace(space);
                    }
                }
                devicesList.add(device);
            }

            return devicesList;

        } catch (JSONException | IOException e) {
            Log.e("getDeviceList", e.getMessage());
        }

        return null;
    }

    public Space getSpace(Response response) {
        try {
            JSONObject spaceData = new JSONObject(response.body().string());
            Space space = new Space();

            space.set_id(spaceData.getString("_id"));
            space.setName(spaceData.getString("name"));

            if (spaceData.has("building")) {
                if (spaceData.get("building") instanceof JSONObject) {
                    JSONObject buildingData = spaceData.getJSONObject("building");
                    Building building = new Building();

                    building.set_id(buildingData.getString("_id"));
                    building.setName(buildingData.getString("name"));

                    space.setBuilding(building);
                }
            }
            if (spaceData.has("devices")) {
                if (spaceData.get("devices") instanceof JSONArray) {
                    JSONArray devices = spaceData.getJSONArray("devices");
                    List<Device> devicesList = new ArrayList<>();

                    for (int j = 0; j < devices.length(); j++) {
                        Device device = new Device();

                        device.set_id(devices.getJSONObject(j).getString("_id"));
                        device.setName(devices.getJSONObject(j).getString("name"));
                        device.setStatus(devices.getJSONObject(j).getBoolean("status"));
                        device.setIp_address(devices.getJSONObject(j).getString("ip_address"));

                        devicesList.add(device);
                    }
                    space.setDevices(devicesList);
                }
            }

            return space;

        } catch (JSONException | IOException e) {
            Log.e("getSpace", e.getMessage());
        }

        return null;
    }

    public double getPower(Response response) {
        try {
            JSONObject deviceData = new JSONObject(response.body().string());
            double power = 0;
            power = deviceData.getJSONObject("variables").getDouble("potencia");
            Log.e("power", power + "");

            return power;

        } catch (JSONException | IOException e) {
            Log.e("getPower", e.getMessage());
        }

        return 0;
    }

    public int getStatus(Response response) {
        try {
            JSONObject deviceData = new JSONObject(response.body().string());
            int status = 0;
            status = deviceData.getJSONObject("variables").getInt("status");

            return status;

        } catch (JSONException | IOException e) {
            Log.e("getStatus", e.getMessage());
        }

        return 0;
    }

    public List<Space> getSpaceList(Response response) {
        List<Space> spacesList = new ArrayList<>();

        try {
            JSONArray spaces = new JSONArray(response.body().string());

            for (int i = 0; i < spaces.length(); i++) {
                Space space = new Space();

                space.set_id(spaces.getJSONObject(i).getString("_id"));
                space.setName(spaces.getJSONObject(i).getString("name"));

                if (spaces.getJSONObject(i).has("devices")) {
                    if (spaces.getJSONObject(i).get("devices") instanceof JSONArray) {
                        JSONArray devices = spaces.getJSONObject(i).getJSONArray("devices");
                        List<Device> devicesList = new ArrayList<>();

                        for (int j = 0; j < devices.length(); j++) {
                            Device device = new Device();

                            device.set_id(devices.getJSONObject(j).getString("_id"));
                            device.setName(devices.getJSONObject(j).getString("name"));
                            device.setStatus(devices.getJSONObject(j).getBoolean("status"));
                            device.setIp_address(devices.getJSONObject(j).getString("ip_address"));

                            devicesList.add(device);
                        }
                        space.setDevices(devicesList);
                    }
                }
                spacesList.add(space);
            }

            return spacesList;

        } catch (JSONException | IOException e) {
            Log.e("getSpaceList", e.getMessage());
        }

        return null;
    }

    public List<Building> getBuildingList(Response response) {
        List<Building> buildingList = new ArrayList<>();

        try {
            JSONArray buildings = new JSONArray(response.body().string());

            for (int i = 0; i < buildings.length(); i++) {
                Building building = new Building();

                building.set_id(buildings.getJSONObject(i).getString("_id"));
                building.setName(buildings.getJSONObject(i).getString("name"));

                if (buildings.getJSONObject(i).has("spaces")) {
                    if (buildings.getJSONObject(i).get("spaces") instanceof JSONArray) {
                        JSONArray spaces = buildings.getJSONObject(i).getJSONArray("spaces");
                        List<Space> spacesList = new ArrayList<>();

                        for (int j = 0; j < spaces.length(); j++) {
                            Space space = new Space();

                            space.set_id(spaces.getJSONObject(j).getString("_id"));
                            space.setName(spaces.getJSONObject(j).getString("name"));

                            spacesList.add(space);
                        }

                        building.setSpaces(spacesList);
                    }
                }

                buildingList.add(building);
            }

            return buildingList;

        } catch (JSONException | IOException e) {
            Log.e("getBuildingList", e.getMessage());
        }

        return null;
    }

    public Building getBuilding(Response response) {
        try {
            JSONObject buildingData = new JSONObject(response.body().string());
            Building building = new Building();

            building.set_id(buildingData.getString("_id"));
            building.setName(buildingData.getString("name"));

            if (buildingData.has("spaces")) {
                if (buildingData.get("spaces") instanceof JSONArray) {
                    JSONArray spaces = buildingData.getJSONArray("spaces");
                    List<Space> spacesList = new ArrayList<>();

                    for (int j = 0; j < spaces.length(); j++) {
                        Space space = new Space();
                        JSONObject spaceData = spaces.getJSONObject(j);

                        space.set_id(spaceData.getString("_id"));
                        space.setName(spaceData.getString("name"));

                        if (spaceData.has("devices")) {
                            if (spaceData.get("devices") instanceof JSONArray) {
                                JSONArray devices = spaceData.getJSONArray("devices");
                                List<Device> buildingDevices = new ArrayList<>();

                                for (int z = 0; z < devices.length(); z++) {
                                    Device device = new Device();

                                    device.set_id(devices.getJSONObject(z).getString("_id"));
                                    device.setName(devices.getJSONObject(z).getString("name"));
                                    device.setStatus(devices.getJSONObject(z).getBoolean("status"));
                                    device.setIp_address(devices.getJSONObject(z).getString("ip_address"));

                                    buildingDevices.add(device);
                                }
                                space.setDevices(buildingDevices);
                            }
                        }
                        spacesList.add(space);
                    }
                    building.setSpaces(spacesList);
                }
            }

            return building;

        } catch (JSONException | IOException e) {
            Log.e("getBuilding", e.getMessage());
        }

        return null;
    }
}
