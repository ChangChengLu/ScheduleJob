package com.cclu.api.dto.timer;

import java.util.Map;

/**
 * @author ChangCheng Lu
 * @description 回调上下文
 */
public class NotifyHttpParam {

    private String method;
    private String url;
    private Map<String,String> header;
    private String body;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
