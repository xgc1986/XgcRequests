package com.xgc1986.xgcRequests;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xgc1986 on 1/04/16.
 */
public class Response {

    HttpResponse httpResponse ;
    HttpEntity httpEntity;

    private String message;

    Response(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
        this.httpEntity = httpResponse.getEntity();
    }

    public HttpEntity getHttpEntity() {
        return httpEntity;
    }

    public String getMessage() {
        if (message == null) {
            InputStream inputStream = null;
            try {
                inputStream = httpEntity.getContent();
                message = IOUtils.toString(inputStream, "UTF-8");
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return message;
        }
        else {
            return "";
        }
    }

    public int getStatusCode() {
        return httpResponse.getStatusLine().getStatusCode();
    }
}
