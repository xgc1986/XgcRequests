package com.xgc1986.xgcRequests;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xgc1986 on 1/04/16.
 */
public class Request {

    public static final int GET = 0;
    public static final int POST = 1;
    public static final int PUT = 2;
    public static final int DELETE = 3;

    protected int method;
    protected String url;
    protected AsyncTask<Request, Void, HttpResponse> task;
    protected List<RequestParameter> params = new ArrayList<RequestParameter>();
    protected HttpClient client = new DefaultHttpClient();
    protected OnSuccess onSuccess;
    protected OnFailure onFailure;


    public Request(int method, String url) {
        if (method < 0 || method > 3) {
            throw new Error("invalid method");
        }

        if (url == null) {
            throw new Error("url cannot be null");
        }

        this.method = method;

        this.url = url;
    }

    public Request addParam(String key, Object value) {
        params.add(new RequestParameter(key, value));
        return this;
    }

    public Request addParam(RequestParameter param) {
        params.add(param);
        return this;
    }

    // TODO send headers
    // TODO session?

    public void send() {

        HttpRequestBase request;

        switch (method) {
            case GET:
                request = new HttpGet(url + buildQuery());
                break;
            case POST:
                request = new HttpPost(url);
                ((HttpPost)request).setEntity(buildBody());
                break;
            case PUT:
                request = new HttpPut(url);
                ((HttpPut)request).setEntity(buildBody());
                break;
            case DELETE:
                request = new HttpDelete(url + buildQuery());
                break;
            default:
                throw new Error("Method not allowed");

        }

        sendRequest(request);
    }

    private void sendRequest(final HttpRequestBase request) {
        task = new AsyncTask<Request, Void, HttpResponse>() {
            @Override
            protected HttpResponse doInBackground(Request... params) {

                HttpResponse response = null;
                try {
                    response = client.execute(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return response;
            }

            @Override
            protected void onPostExecute(HttpResponse response) {
                if (response != null) {
                    int status = response.getStatusLine().getStatusCode();

                    if (status >= 200 && status < 400) {
                        onSuccess(response);
                    } else {
                        onFailure(response);
                    }
                } else {
                    // TODO
                    onFailure(response);
                }
            }
        };

        task.execute(this);
    }

    private String buildQuery() {

        String query = "";
        if (!params.isEmpty()) {
            RequestParameter firstParam = params.get(0);
            query = "?" + firstParam.getKey() + "=" + firstParam.getValue();

            for (RequestParameter param:params) {
                query = "&" + param.getKey() + "=" + param.getValue();
            }
        }

        return query;
    }

    private HttpEntity buildBody() {
        try {
            return new UrlEncodedFormEntity(params, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOnSuccess(OnSuccess onSuccess) {
        this.onSuccess = onSuccess;
    }

    public void setOnFailure(OnFailure onFailure) {
        this.onFailure = onFailure;
    }

    public void onSuccess(HttpResponse response) {
        if (onSuccess != null) {
            onSuccess.onSuccess(new Response(response));
        }
    }

    public void onFailure(HttpResponse response) {
        if(onFailure != null) {
            onFailure.onFailure(new Response(response));
        }
    }
}
