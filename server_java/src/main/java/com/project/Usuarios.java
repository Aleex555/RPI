package com.project;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Usuarios {
    
    public static void crearUsuariosJSON() {
        // Crear una lista de usuarios
        List<JSONObject> usuarios = new ArrayList<>();

        usuarios.add(crearUsuario("Alex", "1234"));
        usuarios.add(crearUsuario("Cristian", "1234"));
        usuarios.add(crearUsuario("Yuhi", "1234"));
        usuarios.add(crearUsuario("Susana", "1234"));
        usuarios.add(crearUsuario("Maria", "1234"));
        usuarios.add(crearUsuario("Manuel", "1234"));

        // Crear el objeto JSON principal
        JSONObject json = new JSONObject();
        json.put("usuarios", new JSONArray(usuarios));

       
        try (FileWriter fileWriter = new FileWriter("data/usuarios.json", false)) {
            fileWriter.write(json.toString(2)); // El segundo argumento indica la cantidad de espacios de sangría para la salida
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static JSONObject crearUsuario(String usuario, String contrasena) {
        JSONObject usuarioJSON = new JSONObject();
        usuarioJSON.put("usuario", usuario);
        usuarioJSON.put("contrasena", contrasena);
        return usuarioJSON;
    }

    public static void eliminarUsuarioPorNombre(String nombreUsuario) {
        try {
            // Leer el contenido del archivo JSON
            JSONObject json = leerJSON();

            if (json != null) {
                // Obtener la lista de usuarios
                JSONArray usuarios = json.getJSONArray("usuarios");

                // Buscar y eliminar el usuario con el nombre dado
                for (int i = 0; i < usuarios.length(); i++) {
                    JSONObject usuario = usuarios.getJSONObject(i);
                    String nombre = usuario.getString("usuario");

                    if (nombre.equals(nombreUsuario)) {
                        usuarios.remove(i);
                    
                        break;
                    }
                }

                // Actualizar y guardar el JSON
                json.put("usuarios", usuarios);
                escribirJSON(json);
            } else {
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject leerJSON() {
        try (FileReader reader = new FileReader("data/usuarios.json")) {
            return new JSONObject(new JSONTokener(reader));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void escribirJSON(JSONObject json) {
        try (FileWriter fileWriter = new FileWriter("data/usuarios.json", false)) {
            fileWriter.write(json.toString(2));
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
