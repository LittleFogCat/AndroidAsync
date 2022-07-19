package com.koushikdutta.async.http.body;

import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.DataSink;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.parser.JSONArrayParser;
import com.koushikdutta.async.parser.JSONObjectParser;
import com.koushikdutta.async.parser.StringParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectBody implements AsyncHttpRequestBody<Object> {
    public JSONObjectBody() {
    }

    byte[] mBodyBytes;
    Object json;

    public JSONObjectBody(JSONObject json) {
        this();
        this.json = json;
    }

    @Override
    public void parse(DataEmitter emitter, final CompletedCallback completed) {
        new StringParser().parse(emitter).setCallback((e, result) -> {
            if (e == null && result != null) {
                if (result.startsWith("[")) {
                    try {
                        json = new JSONArray(result);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                } else  {
                    try {
                        json = new JSONObject(result);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            completed.onCompleted(e);
        });
    }

    @Override
    public void write(AsyncHttpRequest request, DataSink sink, final CompletedCallback completed) {
        Util.writeAll(sink, mBodyBytes, completed);
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public boolean readFullyOnRequest() {
        return true;
    }

    @Override
    public int length() {
        mBodyBytes = json.toString().getBytes();
        return mBodyBytes.length;
    }

    public static final String CONTENT_TYPE = "application/json";

    @Override
    public Object get() {
        return json;
    }
}

