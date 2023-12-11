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

public class ServerActions {

    public ServerSocket server;
    public Socket connected;
    public BufferedReader inDalClient;
    public DataOutputStream outVersoIlClient;
    public boolean exit;
    public String receivedString;
    public Response response = new Response();

    public ServerActions() {
        try {
            this.server = new ServerSocket(8080);
            System.out.println("Avvio del server socket...");
        } catch (IOException e) {
            System.out.println("Errore ServerSocket");
        }
        try {
            this.connected = server.accept();
        } catch (IOException e) {
            System.out.println("Errore Socket");
        }
        try {
            this.inDalClient = new BufferedReader(new InputStreamReader(this.connected.getInputStream()));
        } catch (IOException e) {
            System.out.println("Errore BufferedReader");
        }
        try {
            this.outVersoIlClient = new DataOutputStream(new DataOutputStream(connected.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Errore DataOutputStream");
        }
        this.exit = false;
        this.receivedString = "";
    }

    public void start() {
        do {
            try {
                receivedString = inDalClient.readLine();
                System.out.println(receivedString);
                if (receivedString.equals(" ")) {
                    this.setExit(true);
                } else {
                    String arrayString[] = receivedString.split(" ");
                    if (arrayString.length != 3 || !arrayString[2].contains("HTTP")) {
                        // STRINGA RICEVUTA NON CORRETTA
                        getOutVersoIlClient().writeBytes("ERRORE SERVER" + "\n");
                        response.setResponseCode("500");
                    } else {
                        arrayString[1] = arrayString[1].replaceFirst("/", "");
                        File searchFile = new File("SERVERHTTP" + arrayString[1]);
                        if (searchFile.exists()) {
                            // FILE ESISTE
                            getOutVersoIlClient().writeBytes("FILE PRESENTE" + "\n");
                            readFile(searchFile);
                            response.setResponseCode("200");
                            response.setBody(getTextFile(searchFile));
                            sendResponse();
                        } else {
                            // FILE NON ESISTE
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Errore ricezione");
            }
        } while (!isExit());
        try {
            inDalClient.close();
            outVersoIlClient.close();
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

    public void readFile(File searchFile) {
        try {
            Scanner myReader = new Scanner(searchFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File non trovato");
        }
    }

    public String getTextFile(File searchFile) {
        String dataFile = "";
        Scanner myReader;
        try {
            myReader = new Scanner(searchFile);
            while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            dataFile += data;
        }
        } catch (FileNotFoundException e) {
            System.out.println("Errore nella ricezione del testo del file");
        }
        return dataFile;
    }

    public void sendResponse() {
        for (String s : response.getResponseData()) {
            System.out.println(s);
            try {
                getOutVersoIlClient().writeBytes(s + "\n");
            } catch (IOException e) {
                System.out.println("Errore nell'invio della risposta");
            }
        }
    }

    public DataOutputStream getOutVersoIlClient() {
        return outVersoIlClient;
    }
}