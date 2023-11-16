package com.project;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

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
    private static Process procesoImagen;


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

    public void executeImagen(String imagen) {
        
        String displayImage = imagen;

        // Directorio de trabajo basado en el directorio de inicio del usuario

        String workingDirectory = "/home/ieti/dev/rpi-rgb-led-matrix";
        //String workingDirectory = "/home/super";
        // Comando a ejecutar (personalizado según tus necesidades)
        
        System.out.println(displayImage);
        String command = "utils/led-image-viewer -C --led-cols=64 --led-rows=64 --led-slowdown-gpio=4 --led-no-hardware-pulse ~/Baixades/RPI/server_java/data/"+displayImage;
        //String command = "mkdir "+mensaje;

        try {
            // Crear el proceso builder y configurar el directorio de trabajo
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.directory(new File(workingDirectory));

            // Redirigir la salida del proceso a la consola
            processBuilder.redirectErrorStream(true);
            procesoImagen = processBuilder.start();

            
            if (variable) {
                procesoImagen.waitFor();
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

    public static void detenerProcesoImagen() throws InterruptedException {
        procesoImagen.destroy();
        procesoImagen.waitFor();
    }
    public static boolean isProcesoImagenAlive() {
        if (procesoImagen == null) {
            return false;
        }else {
            return true;
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

    //conversor de imagenes
    public static void conversorImagen(String base64Image){
        //Recibe un str de base64 y convierte en jpg
        try {

            // Decodificar Base64 a arreglo de bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Convertir bytes a BufferedImage
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bais);

            // Guardar la imagen 
            String path
            File outputFile = new File("data/2copy.jpg");
            ImageIO.write(image, "jpg", outputFile);
            //
            System.out.println("Imagen guardada con éxito.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public static String readTextFromFile(String filePath) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();  // Manejo básico de excepciones, puedes personalizarlo según tus necesidades
        }

        return content.toString();
    }

    public static void writeTextToFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();  // Manejo básico de excepciones, puedes personalizarlo según tus necesidades
        }
    }
}
