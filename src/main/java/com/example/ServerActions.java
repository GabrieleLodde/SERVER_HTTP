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
            while (!exit) {
                this.connected = server.accept();
                this.inDalClient = new BufferedReader(new InputStreamReader(this.connected.getInputStream()));
                this.outVersoIlClient = new DataOutputStream(new DataOutputStream(connected.getOutputStream()));
                try {
                    receivedString = inDalClient.readLine();
                    System.out.println(receivedString);
                    if (receivedString != null && !receivedString.isEmpty()) {
                        this.arrayString = receivedString.split(" ");
                        if (arrayString.length == 3 && arrayString[2].contains("HTTP")) {
                            // STRINGA RICEVUTA CORRETTA
                            searchFile = new File("htdocs/" + arrayString[1]);
                            if (searchFile.exists()) {
                                // FILE ESISTE
                                Response response = new Response();
                                String file = readFile(searchFile, arrayString[1].split("\\.")[1]);
                                response.setContentType(searchFile);
                                response.setBody(file);
                                sendResponse(response);
                            } else {
                                // FILE NON ESISTE
                                Response response = new Response();
                                response.setResponseCode("404");
                                sendResponse(response);
                                System.out.println("Errore: file non trovato");
                            }
                        } /*
                           * else {
                           * // RICHIESTA ERRATA
                           * Response response = new Response();
                           * response.setResponseCode("500");
                           * sendResponse(response);
                           * System.out.println("Internal Server Error");
                           * setExit(true);
                           * }
                           */
                    }
                } catch (IOException e) {
                    System.out.println("Errore generico " + e.getMessage());
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

    public static String readFile(File searchFile, String extension) throws IOException {
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
        } else if (extension.equals("png")) {
            // serializzazione di un'immagine
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