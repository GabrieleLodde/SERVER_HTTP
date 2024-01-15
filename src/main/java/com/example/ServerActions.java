package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                ServerActions.outVersoIlClient = new DataOutputStream(
                        new DataOutputStream(connected.getOutputStream()));
                try {
                    receivedString = inDalClient.readLine();
                    System.out.println(receivedString);
                    if (receivedString != null && !receivedString.isEmpty()) {
                        this.arrayString = receivedString.split(" ");
                        if (arrayString.length == 3 && arrayString[2].contains("HTTP")) {
                            searchFile = new File("htdocs/" + arrayString[1]);
                            if (searchFile.isDirectory()) {
                                searchFile = new File("htdocs/mio_file.html");
                            }
                            Response response = new Response();
                            if (searchFile.exists()) {
                                response.setContentType(searchFile);
                                sendResponse(response, searchFile);
                            } else {
                                if (searchFile.getPath().equals("htdocs/classe.json")) {
                                    System.out.println("PUZZI DI GIOELE");
                                    serializationDeserialization(response);
                                } else {
                                    File errorFile = new File("fileErr.html");
                                    response.setResponseCode("404");
                                    response.setContentType(errorFile);
                                    sendResponse(response, errorFile);
                                    System.out.println("Errore: file non trovato\n");
                                }
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

    public void readFile(File searchFile) throws IOException {
        InputStream input = new FileInputStream(searchFile);
        byte[] buf = new byte[8192];
        int n;
        while ((n = input.read(buf)) != -1) {
            outVersoIlClient.write(buf, 0, n);
        }
        input.close();
    }

    public void sendResponse(Response response, File searchFile) {
        ArrayList<String> copyArray = response.getResponseData(searchFile);
        for (int i = 0; i < copyArray.size(); i++) {
            try {
                System.out.println(copyArray.get(i));
                outVersoIlClient.writeBytes(copyArray.get(i));
            } catch (IOException e) {
                System.out.println("Errore nell'invio della risposta");
                e.printStackTrace();
            }
        }
        try {
            readFile(searchFile);
            outVersoIlClient.writeBytes("" + "\n");
        } catch (IOException e) {
            System.out.println("Errore nell'invio della risposta");
            e.printStackTrace();
        }
        System.out.println("");
    }

    public void serializationDeserialization(Response response)
            throws StreamWriteException, DatabindException, IOException {
        Alunno a = new Alunno("Pippo", "Rossi", new Date(2006, 07, 12));
        Alunno b = new Alunno("Gioele", "Febbre", new Date(2005, 12, 03));
        Alunno c = new Alunno("DJ", "Yang", new Date(2003, 06, 12));
        Alunno d = new Alunno("Gabriele", "Lodde", new Date(2005, 06, 16));

        ArrayList<Alunno> arrayAlunni = new ArrayList<Alunno>();
        arrayAlunni.add(a);
        arrayAlunni.add(b);
        arrayAlunni.add(c);
        arrayAlunni.add(d);

        Classe c1 = new Classe(5, "BIA", "18-TW", arrayAlunni);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File("htdocs/classe.json"), c1);
        response.setContentType(new File("htdocs/classe.json"));
        sendResponse(response, new File("htdocs/classe.json"));
    }
}