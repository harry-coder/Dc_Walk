package com.dc_walk.CustomInterfaces;

import org.json.JSONException;
import org.json.JSONObject;

public interface RequestListener {

    public void onPreRequest();
    public void onPostRequest();
    public void isRequestSuccessful(boolean result);

    public void getJsonResponse(JSONObject response) throws JSONException;

}
