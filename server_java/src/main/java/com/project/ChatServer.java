package com.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
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

        // Crear un mapa con la información que deseas incluir en el JSON
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("nombre", "NombreDelJuego");
        jsonData.put("usuario", "NombreDeUsuario");
        jsonData.put("from", displayIP);
        


        
         
    }
        CommandExecutor.eliminarJSON();
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

   


        // Li enviem el seu identificador
        JSONObject objId = new JSONObject("{}");
        objId.put("type", "id");
        objId.put("from", "server");
        objId.put("value", clientId);
        conn.send(objId.toString()); 

        // Enviem al client la llista amb tots els clients connectats

     
 
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Quan un client es desconnecta
        String clientId = getConnectionId(conn);

        // Informem a tothom que el client s'ha desconnectat
        /*/
        Map<String, String> resultado = new Map ();
        try {
            resultado = CommandExecutor.obtenerNombreYFromPorId(clientId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "disconnected");
        objCln.put("from", "server");
        objCln.put("usuario", resultado.get("nombre"));
        objCln.put("from", resultado.get("from"));
        broadcast(objCln.toString());
        */
        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println("Client disconnected '" + clientId + "'");
        /* 
        JSONObject objResponse1 = new JSONObject("{}");
                objResponse1.put("type", "connected");
                objResponse1.put("usuario", objRequest.getString("user"));
                objResponse1.put("from", objRequest.getString("from"));
                objResponse1.put("id", clientId);
                broadcast(objResponse1.toString());
        */
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

        }if (type.equalsIgnoreCase("connected")) {
            JSONObject objResponse = new JSONObject("{}");
            objResponse.put("type", "ok");
            conn.send(objResponse.toString());

        }if (type.equalsIgnoreCase("list")) {
            // El client demana la llista de tots els clients
            
            CommandExecutor.convertirJsonAHashMap();
            sendList(conn);

        } else if (type.equalsIgnoreCase("image")){
            // PARAMOS LOS PROCESOS EXISTETES
            boolean isaliveMensaje = CommandExecutor.isProcesoAlive();
            boolean isliveImagen = CommandExecutor.isProcesoImagenAlive();
            if (isaliveMensaje){
                try {
                    CommandExecutor.detenerProcesoMensaje();
                } catch (Exception e) {
            
                }
            }
            if (isliveImagen){
                try {
                    CommandExecutor.detenerProcesoImagen();
                } catch (Exception e) {
            
                }
            }

            String texto64 = objRequest.getString("value");
            String nombre_imagen = objRequest.getString("name");
          
            String path64 = "image64/imagenTexto.txt"; 
            CommandExecutor.writeTextToFile(path64,texto64);
            System.out.println("Loading...");

            String imagen64 = CommandExecutor.readTextFromFile(path64);
            CommandExecutor.conversorImagen(imagen64);
            
            
            
            
            commandExecutor.executeImagen("imagenconvertida.jpg");
            System.out.println("IMAGEN enviada con exito");



        }else if (type.equalsIgnoreCase("registro")) {
            String usuario = objRequest.getString("user");
            String contra = objRequest.getString("password");
            String from = objRequest.getString("from");
            boolean verificador = CommandExecutor.verificarCredenciales(usuario,contra);
            if (verificador==true){
                JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "ok");
                conn.send(objResponse.toString());
                System.out.println("credenciales correctas");
                CommandExecutor.conectadosJSON(usuario,from,clientId);
                
                JSONObject objResponse1 = new JSONObject("{}");
                objResponse1.put("type", "connected");
                objResponse1.put("usuario", objRequest.getString("user"));
                objResponse1.put("from", objRequest.getString("from"));
                objResponse1.put("id", clientId);
                broadcast(objResponse1.toString());
                
                

            }else if (verificador==false){
                JSONObject objResponse = new JSONObject("{}");
                System.out.println("credenciales incorrectas");
                objResponse.put("type", "no");
                conn.send(objResponse.toString());
            }

        } else if (type.equalsIgnoreCase("broadcast")) {
            //Detenemos los procesos
            boolean isaliveMensaje = CommandExecutor.isProcesoAlive();
            boolean isliveImagen = CommandExecutor.isProcesoImagenAlive();
            if (isaliveMensaje){
                try {
                    CommandExecutor.detenerProcesoMensaje();
                    System.out.println("Se cerro proceso de  mensaje");
                } catch (Exception e) {
            
                }
            }
            if (isliveImagen){
                try {
                    CommandExecutor.detenerProcesoImagen();
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
     
            String textoenviado = objRequest.getString("value");

            String usuarioSendMessage = objRequest.getString("usuario");
            String usuarioFrom = objRequest.getString("from");


            JSONObject objResponse1 = new JSONObject("{}");
            objResponse1.put("type", "infomensaje");
            objResponse1.put("usuario", usuarioSendMessage);
            objResponse1.put("from", usuarioFrom);
            objResponse1.put("id", clientId);
            broadcast(objResponse1.toString());
            
            

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
            CommandExecutor.detenerProceso();
            CommandExecutor.detenerProcesoMensaje();
            CommandExecutor.detenerProcesoImagen();
        } catch (Exception e) {
            
        }
            System.out.println("Stopping server");
            stop(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }  
    }
    

    private void sendList(WebSocket conn) throws JSONException, Exception {
    JSONObject objResponse = new JSONObject("{}");
    objResponse.put("type", "list");
    objResponse.put("from", "server");
    objResponse.put("list", CommandExecutor.convertirJsonAHashMap());
    

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

