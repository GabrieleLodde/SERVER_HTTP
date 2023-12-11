package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.ArrayList;

public class ServerActions {

    public ServerSocket server;
    public Socket connected;
    public BufferedReader inDalClient;
    public DataOutputStream outVersoIlClient;
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
            this.connected = server.accept();
            this.inDalClient = new BufferedReader(new InputStreamReader(this.connected.getInputStream()));
            this.outVersoIlClient = new DataOutputStream(new DataOutputStream(connected.getOutputStream()));
            while (!exit) {
                receivedString = inDalClient.readLine();
                System.out.println(receivedString);
                if (receivedString.isEmpty()) {
                    break;
                } else {
                    this.arrayString = receivedString.split(" ");
                    if (arrayString.length == 3 && arrayString[2].contains("HTTP")) {
                        // STRINGA RICEVUTA CORRETTA
                        searchFile = new File("." + arrayString[1]);
                        if (searchFile.exists()) {
                            // FILE ESISTE
                            Response response = new Response();
                            String textFile = readFile(searchFile);
                            response.setBody(textFile);
                            sendResponse(response);
                            setExit(true);
                        } else {
                            // FILE NON ESISTE
                            Response response = new Response();
                            response.setResponseCode("404");
                            sendResponse(response);
                            System.out.println("Errore: file non trovato");
                            setExit(true);
                        }
                    } else {
                        // RICHIESTA ERRATA
                        Response response = new Response();
                        response.setResponseCode("500");
                        sendResponse(response);
                        System.out.println("Internal Server Error");
                        setExit(true);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Errore generico");
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

    public static String readFile(File searchFile) {
        String textFile = "";
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
            }
        }
    }
}