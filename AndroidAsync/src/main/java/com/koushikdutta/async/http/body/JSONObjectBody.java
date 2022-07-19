package com.koushikdutta.async.http.body;

import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.DataSink;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.parser.JSONArrayParser;
import com.koushikdutta.async.parser.JSONObjectParser;

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
        new JSONObjectParser().parse(emitter).setCallback((e, result) -> {
            if (e != null) {
                new JSONArrayParser().parse(emitter).setCallback((e1, result1) -> {
                    json = result1;
                    completed.onCompleted(e1);
                });
            } else {
                json = result;
                completed.onCompleted(null);
            }
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

