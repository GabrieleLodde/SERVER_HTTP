package com.example;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Response {
    public String code;
    public String phrase;
    public String date;
    public ArrayList<String> responseData;
    public String protocol;
    public String contentType;

    public Response() {
        this.protocol = "HTTP/1.1";
        this.code = "200";
        this.phrase = "OK";
        this.date = LocalDateTime.now().toString();
        this.contentType = "";
        this.responseData = new ArrayList<String>();
    }

    public void setResponseCode(String code) {
        switch (code) {
            case "200":
                this.code = code;
                this.phrase = "OK";
                break;

            case "300":
                this.code = code;
                this.phrase = "Redirection";
                break;

            case "404":
                this.code = code;
                this.phrase = "Not found";
                break;

            case "500":
                this.code = code;
                this.phrase = "Internal Server Error";
                break;
        }
    }

    public ArrayList<String> getResponseData(File searchFile) {
        String addString;
        addString = protocol + " " + code + " " + phrase + "\n";
        responseData.add(addString);
        addString = "Date: " + date + "\n";
        responseData.add(addString);
        addString = "Server: Lodde-server" + "\n";
        responseData.add(addString);
        addString = "Content-Type: " + getContentType() + ";charset=UTF-8" + "\n";
        responseData.add(addString);
        addString = "Content-Length: " + searchFile.length() + "\n";
        responseData.add(addString);
        addString = "\n";
        responseData.add(addString);
        return responseData;
    }

    public void setContentType(File f){
        String [] type = f.getName().split("\\.");
        String exit = type[type.length-1];
        switch (exit) {
            case "html":
            case "htm":
                this.contentType = "text/html";
                break;
            case "png":
                this.contentType = "image/png";
                break;
            case "css":
                this.contentType = "text/css";
                break;
            case "json":
                this.contentType = "application/json";
                break;
        }
    }

    public String getContentType(){
        return this.contentType;
    }
}