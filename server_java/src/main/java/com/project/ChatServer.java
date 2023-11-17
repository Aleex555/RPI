package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import java.util.concurrent.ConcurrentHashMap;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

public class ChatServer extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    //private Displaymensaje displayMensaje = new Displaymensaje();
    private CommandExecutor commandExecutor = new CommandExecutor();

    private ConcurrentHashMap<WebSocket, String> connectionTypes = new ConcurrentHashMap<>();


    public ChatServer (int port) {
        super(new InetSocketAddress(port));
    }
    
    

    @Override
    public void onStart() {
        // Quan el servidor s'inicia
        String host = getAddress().getAddress().getHostAddress();
        int port = getAddress().getPort();
        System.out.println("Type 'exit' to stop and exit server.");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);

        String displayIP = "";
        try {
            displayIP = Main.getLocalIPAddress();
        } catch (Exception e) {
        e.printStackTrace();
    }
        
        commandExecutor.onOpen(displayIP);
    }

    
    

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Quan un client es connecta
        String clientId = getConnectionId(conn);

        try {
            CommandExecutor.detenerProceso();
        } catch (Exception e) {
            
        }

        String clientType = handshake.getResourceDescriptor();

        // Asignar el tipo de cliente a la conexión
        connectionTypes.put(conn, clientType);
        
        //pruebaaaaaaaaaaaaaaaa

        // Saludem personalment al nou client
        JSONObject objWlc = new JSONObject("{}");
        objWlc.put("type", "private");
        objWlc.put("from", "server");
        objWlc.put("value", "Welcome to the chat server");
        conn.send(objWlc.toString()); 


        // Li enviem el seu identificador
        JSONObject objId = new JSONObject("{}");
        objId.put("type", "id");
        objId.put("from", "server");
        objId.put("value", clientId);
        conn.send(objId.toString()); 

        // Enviem al client la llista amb tots els clients connectats
        sendList(conn);

        // Enviem la direcció URI del nou client a tothom 
        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "connected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        broadcast(objCln.toString());

        // Mostrem per pantalla (servidor) la nova connexió
        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println("New client (" + clientId + "): " + host);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Quan un client es desconnecta
        String clientId = getConnectionId(conn);

        // Informem a tothom que el client s'ha desconnectat
        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "disconnected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        broadcast(objCln.toString());

        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println("Client disconnected '" + clientId + "'");
        
    }

    @Override

public void onMessage(WebSocket conn, String message) {
    // Quan arriba un missatge

    
    String clientType = connectionTypes.get(conn);
    String clientId = getConnectionId(conn);
    try {
        JSONObject objRequest = new JSONObject(message);
        String type = objRequest.getString("type");

        if (type.equalsIgnoreCase("galeria")) {
            JSONObject objResponse = new JSONObject("{}");
            objResponse.put("type", "img");
            conn.send(objResponse.toString());

        }else if (type.equalsIgnoreCase("image")){
            // PARAMOS LOS PROCESOS EXISTETES
            boolean isaliveMensaje = commandExecutor.isProcesoAlive();
            boolean isliveImagen = commandExecutor.isProcesoImagenAlive();
            if (isaliveMensaje){
                try {
                    commandExecutor.detenerProcesoMensaje();
                } catch (Exception e) {
            
                }
            }
            if (isliveImagen){
                try {
                    commandExecutor.detenerProcesoImagen();
                } catch (Exception e) {
            
                }
            }

            String texto64 = objRequest.getString("value");
            String nombre_imagen = objRequest.getString("name");
          
            String path64 = "image64/imagenTexto.txt"; 
            commandExecutor.writeTextToFile(path64,texto64);
            System.out.println("Loading...");

            String imagen64 = commandExecutor.readTextFromFile(path64);
            commandExecutor.conversorImagen(imagen64);
            
            
            
            
            commandExecutor.executeImagen("imagenconvertida.jpg");
            System.out.println("IMAGEN enviada con exito");



        }else if (type.equalsIgnoreCase("registro")) {
            String usuario = objRequest.getString("user");
            String contra = objRequest.getString("password");
            boolean verificador = commandExecutor.verificarCredenciales(usuario,contra);
            if (verificador==true){
                JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "ok");
                conn.send(objResponse.toString());
                System.out.println("credenciales correctas");
            }else if (verificador==false){
                System.out.println("credenciales incorrectas");
            }

        }else if (type.equalsIgnoreCase("list")) {
            // El client demana la llista de tots els clients
            System.out.println("Client '" + clientId + "' with type '" + clientType + "' requests list of clients");
            sendList(conn);

        } else if (type.equalsIgnoreCase("private")) {
            // El client envia un missatge privat a un altre client
            System.out.println("Client '" + clientId + "'' sends a private message");

            JSONObject objResponse = new JSONObject("{}");
            objResponse.put("type", "private");
            objResponse.put("from", clientId);
            objResponse.put("value", objRequest.getString("value"));

            String destination = objRequest.getString("destination");
            WebSocket desti = getClientById(destination);

            

            if (desti != null) {
                desti.send(objResponse.toString());
            }

        } else if (type.equalsIgnoreCase("broadcast")) {
            //Detenemos los procesos
            boolean isaliveMensaje = commandExecutor.isProcesoAlive();
            boolean isliveImagen = commandExecutor.isProcesoImagenAlive();
            if (isaliveMensaje){
                try {
                    commandExecutor.detenerProcesoMensaje();
                    System.out.println("Se cerro proceso de  mensaje");
                } catch (Exception e) {
            
                }
            }
            if (isliveImagen){
                try {
                    commandExecutor.detenerProcesoImagen();
                    System.out.println("Se cerro proceso de  imagen");
                } catch (Exception e) {
            
                }
            }
            String from = objRequest.getString("from");
            // El client envia un missatge a tots els clients
            
            //displayMensaje.displayHolaMundo();
            if (from.equalsIgnoreCase("Flutter")) {
                System.out.println("Cliente Flutter "+clientId+" envio un mensaje-->");
            }else if (from.equalsIgnoreCase("Android")) {
                System.out.println("Cliente Android "+clientId+" envio un mensaje-->");
            }
     
            
                JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "broadcast");
                objResponse.put("from", clientId);
                objResponse.put("value", objRequest.getString("value"));
            
            broadcast(objResponse.toString());
            String textoenviado = objRequest.getString("value");

            

            commandExecutor.executeCommand(textoenviado);
            

            
        }
        

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // Quan hi ha un error
        ex.printStackTrace();
    }

    public void runServerBucle () {
        boolean running = true;
        try {
            System.out.println("Starting server");
            start();
            while (running) {
                String line;
                line = in.readLine();
                if (line.equals("exit")) {
                    running = false;
                }
            } 
            try {
            commandExecutor.detenerProceso();
            commandExecutor.detenerProcesoMensaje();
            commandExecutor.detenerProcesoImagen();
        } catch (Exception e) {
            
        }
            System.out.println("Stopping server");
            stop(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }  
    }
    

    private void sendList(WebSocket conn) {
    JSONObject objResponse = new JSONObject("{}");
    objResponse.put("type", "list");
    objResponse.put("from", "server");
    objResponse.put("list", getClients());
    

    conn.send(objResponse.toString());
}

    public String getConnectionId (WebSocket connection) {
        String name = connection.toString();
        return name.replaceAll("org.java_websocket.WebSocketImpl@", "");
    }

    public String[] getClients () {
        int length = getConnections().size();
        String[] clients = new String[length];
        int cnt = 0;

        for (WebSocket ws : getConnections()) {
            clients[cnt] = getConnectionId(ws);               
            cnt++;
        }
        return clients;
    }

    public WebSocket getClientById (String clientId) {
        for (WebSocket ws : getConnections()) {
            String wsId = getConnectionId(ws);
            if (clientId.compareTo(wsId) == 0) {
                return ws;
            }               
        }
        
        return null;
    }

    
    
}

