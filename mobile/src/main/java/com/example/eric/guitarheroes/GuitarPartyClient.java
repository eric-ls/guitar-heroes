package com.example.eric.guitarheroes;

import com.loopj.android.http.*;

public class GuitarPartyClient {
    private static String BASE_URL = "http://api.guitarparty.com/v2/";
    private static String API_KEY = "e2c90b469a80bf1057cea6dfe898f9f62a00d2fe";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public GuitarPartyClient() {
        client.addHeader("Guitarparty-Api-Key", API_KEY);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}