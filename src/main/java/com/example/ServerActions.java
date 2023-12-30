package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.ArrayList;

public class ServerActions {
    public ServerSocket server;
    public Socket connected;
    public BufferedReader inDalClient;
    public static DataOutputStream outVersoIlClient;
    public boolean exit;
    public String receivedString;
    public File searchFile;
    public String arrayString[];

    public ServerActions() {
        try {
            this.server = new ServerSocket(8080);
            System.out.println("Avvio del server socket...");
        } catch (IOException e) {
            System.out.println("Errore ServerSocket");
        }
        this.exit = false;
    }

    public void start() {
        try {
            while (!exit) {
                this.connected = server.accept();
                this.inDalClient = new BufferedReader(new InputStreamReader(this.connected.getInputStream()));
                ServerActions.outVersoIlClient = new DataOutputStream(new DataOutputStream(connected.getOutputStream()));
                try {
                    receivedString = inDalClient.readLine();
                    System.out.println(receivedString);
                    if (receivedString != null && !receivedString.isEmpty()) {
                        this.arrayString = receivedString.split(" ");
                        if (arrayString.length == 3 && arrayString[2].contains("HTTP")) {
                            searchFile = new File("htdocs/" + arrayString[1]);
                            if (searchFile.exists()) {
                                if (arrayString[1].split("\\.")[1].equals("png")) {
                                    sendImage(searchFile);
                                } else {
                                    Response response = new Response();
                                    String file = readFile(searchFile, arrayString[1].split("\\.")[1]);
                                    response.setContentType(searchFile);
                                    response.setBody(file);
                                    sendResponse(response);
                                }
                            } else {
                                Response response = new Response();
                                File errorFile = new File("fileErr.html");
                                String fileErr = readFile(errorFile, "html");
                                response.setResponseCode("404");
                                response.setContentType(errorFile);
                                response.setBody(fileErr);
                                sendResponse(response);
                                System.out.println("Errore: file non trovato");
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Errore generico interno " + e.getMessage());
                    connected.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Errore generico " + e.getMessage());
        }
        try {
            connected.close();
        } catch (IOException e) {
            System.out.println("Errore nella chiusura del socket");
        }
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public boolean isExit() {
        return this.exit;
    }

    public String readFile(File searchFile, String extension) throws IOException {
        String textFile = "";
        if (extension.equals("html") || extension.equals("htm") || extension.equals("css")) {
            try {
                Scanner myReader = new Scanner(searchFile);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    textFile += data;
                    System.out.println(data);
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("File non trovato");
            }
        }
        return textFile;
    }

    public void sendResponse(Response response) {
        ArrayList<String> copyArray = response.getResponseData();
        for (int i = 0; i < copyArray.size(); i++) {
            try {
                System.out.println(copyArray.get(i));
                outVersoIlClient.writeBytes(copyArray.get(i));
            } catch (IOException e) {
                System.out.println("Errore nell'invio della risposta");
                e.printStackTrace();
            }
        }
    }

    private static void sendImage(File searchFile) throws IOException {
        outVersoIlClient.writeBytes("HTTP/1.1 200 OK\n");
        System.out.println("HTTP/1.1 200 OK\n");
        outVersoIlClient.writeBytes("Date: " + LocalDateTime.now().toString() + "\n");
        System.out.println("Date: " + LocalDateTime.now().toString() + "\n");
        outVersoIlClient.writeBytes("Server: Lodde-server" + "\n");
        System.out.println("Server: Lodde-server\n");
        outVersoIlClient.writeBytes("Content-Type: " + "image/png" + "\n");
        System.out.println("Content-Type: " + "image/png\n");
        outVersoIlClient.writeBytes("Content-Length: " + searchFile.length() + "\n");
        System.out.println("Content-Length: " + searchFile.length());
        outVersoIlClient.writeBytes("\n");
        InputStream input = new FileInputStream(searchFile);
        byte[] buf = new byte[8192];
        int n;
        while ((n = input.read(buf)) != -1) {
            outVersoIlClient.write(buf, 0, n);
        }
        input.close();
    }
}