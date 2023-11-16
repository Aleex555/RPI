package com.project;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

public class CommandExecutor {
    

    private static volatile boolean detenerProceso = false;
    private boolean variable;


    private static Process procesoIp;
    private static Process procesoMensaje;


    public void executeCommand(String mensaje) {
        
        String displayText = mensaje;

        // Directorio de trabajo basado en el directorio de inicio del usuario

        String workingDirectory = "/home/ieti/dev/rpi-rgb-led-matrix";
        //String workingDirectory = "/home/super";
        // Comando a ejecutar (personalizado según tus necesidades)

        String command = "examples-api-use/scrolling-text-example -y 18 -f ~/dev/bitmap-fonts/bitmap/cherry/cherry-13-b.bdf --led-cols=64 --led-rows=64 --led-slowdown-gpio=4 --led-no-hardware-pulse "+displayText;
        //String command = "mkdir "+mensaje;

        try {
            // Crear el proceso builder y configurar el directorio de trabajo
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.directory(new File(workingDirectory));

            // Redirigir la salida del proceso a la consola
            processBuilder.redirectErrorStream(true);
            procesoMensaje = processBuilder.start();

            
            if (variable) {
                procesoMensaje.waitFor();
            }
            
            System.out.println("INFORMACION ENVIADA CON EXITO");

           

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onOpen (String mensaje) {
        
        String displayText = mensaje;

        // Directorio de trabajo basado en el directorio de inicio del usuario

        String workingDirectory = "/home/ieti/dev/rpi-rgb-led-matrix";
        //String workingDirectory = "/home/super";
        // Comando a ejecutar (personalizado según tus necesidades)

        String command = "examples-api-use/scrolling-text-example -y 18 -f ~/dev/bitmap-fonts/bitmap/cherry/cherry-13-b.bdf --led-cols=64 --led-rows=64 --led-slowdown-gpio=4 --led-no-hardware-pulse "+displayText;
        //String command = "mkdir "+mensaje;

        try {
            // Crear el proceso builder y configurar el directorio de trabajo
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.directory(new File(workingDirectory));

            // Redirigir la salida del proceso a la consola
            processBuilder.redirectErrorStream(true);
            procesoIp = processBuilder.start();

            // Temporizador para interrumpir el proceso después de 5 segundos
           
           
            // Si aún está vivo, detener el proceso
            if (variable) {
                procesoIp.waitFor();
            }
            

     
            
            System.out.println("SERVER CREADO CON EXITO");

           

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void detenerProceso() throws InterruptedException {
        procesoIp.destroy();
        procesoIp.waitFor();
    }

    public static boolean isProcesoAlive() {
        if (procesoMensaje == null) {
            return false;
        }else {
            return true;
        }
    }

    public static void detenerProcesoMensaje() throws InterruptedException {
        procesoMensaje.destroy();
        procesoMensaje.waitFor();
    }

    //verificador de credenciales

    public static boolean verificarCredenciales(String usuario, String contra) {
        try {
            // Leer el contenido del archivo JSON
            String jsonString = new String(Files.readAllBytes(Paths.get("data/usuarios.json")));

            // Convertir la cadena JSON a un objeto JSON
            JSONObject jsonObject = new JSONObject(jsonString);

            // Obtener la matriz de usuarios
            JSONArray usuariosArray = jsonObject.getJSONArray("usuarios");

            // Iterar sobre los usuarios y verificar las credenciales
            for (int i = 0; i < usuariosArray.length(); i++) {
                JSONObject usuarioObj = usuariosArray.getJSONObject(i);

                String usuarioGuardado = usuarioObj.getString("usuario");
                String contraGuardada = usuarioObj.getString("contrasena");

                // Verificar si las credenciales coinciden
                if (usuario.equals(usuarioGuardado) && contra.equals(contraGuardada)) {
                    System.out.println("Credenciales válidas. Acceso permitido.");
                    return true;
                }
            }

            // Si el bucle termina sin encontrar coincidencias
            System.out.println("Credenciales inválidas. Acceso denegado.");
            return false;

        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON: " + e.getMessage());
        }
        return false;
    }
}
