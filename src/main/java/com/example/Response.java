package com.example;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Response {
    public String protocol;
    public String code;
    public String phrase;
    public String date;
    protected ArrayList<String> responseData;
    public String server;
    public String content_type;
    public long content_length;
    public String body;
    public File textFile;

    public Response() {
        this.protocol = "HTTP/1.1";
        this.code = "200";
        this.phrase = "OK";
        this.date = LocalDateTime.now().toString();
        this.server = "meucci-server";
        this.content_type = "text/plain; charset=UTF-8";
        this.body = "";
    }

    public void setResponseCode(String code) {
        switch (code) {
            case "200":
                this.code = code;
                this.phrase = "OK";
                this.content_length = getTextFile().length();
                break;

            case "300":
                this.code = code;
                this.phrase = "Redirection";
                break;

            case "400":
                this.code = code;
                this.phrase = "Not found";
                this.body = "The resource was not found";
                break;

            case "500":
                this.code = code;
                this.phrase = "Internal Server Error";
                break;
        }

    }

    public void addArray() {
        this.responseData.add(getProtocol() + getResponseCode() + getPhrase());
        this.responseData.add("Date: " + getDate());
        this.responseData.add("Server " + getServer());
        this.responseData.add("Content-type " + getContent_type());
        this.responseData.add("Content-length " + getContent_length());
        this.responseData.add(getBody());
    }

    public ArrayList<String> getResponseData() {
        return this.responseData;
    }

    public String getResponseCode() {
        return code;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setDate(LocalDateTime d) {
        this.date = d.toString();
    }

    public String getProtocol() {
        return protocol;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getServer() {
        return server;
    }

    public String getContent_type() {
        return content_type;
    }

    public String getDate() {
        return date;
    }

    public File getTextFile() {
        return textFile;
    }

    public long getContent_length() {
        return content_length;
    }
}