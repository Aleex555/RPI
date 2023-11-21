package com.project;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.io.IOException;
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

    //PROCESOS DE LA IMAGEN
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


    //PROCESOS DE LA IP
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


    //PROCESOS DEL MENSAJE
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
    public static void conversorImagen(String base64Image) {
        try {
            // Decodificar Base64 a arreglo de bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Convertir bytes a BufferedImage
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bais);

            // Verificar si el archivo existe y eliminarlo si es necesario
            File outputFile = new File("data/imagenconvertida.jpg");
            if (outputFile.exists()) {
                if (outputFile.delete()) {
                    System.out.println("Archivo existente eliminado con éxito.");
                } else {
                    System.out.println("No se pudo eliminar el archivo existente.");
                }
            }

            // Escribir la nueva imagen en el archivo
            ImageIO.write(image, "jpg", outputFile);

            System.out.println("Imagen guardada con éxito.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void eliminarJSON() {
        String rutaArchivo = "data/conectados.json";
        File archivoJSON = new File(rutaArchivo);

        if (archivoJSON.exists()) {
            boolean eliminado = archivoJSON.delete();
            if (eliminado) {
                System.out.println("El archivo conectados.json fue eliminado correctamente.");
            } else {
                System.out.println("No se pudo eliminar el archivo conectados.json.");
            }
        } else {
            System.out.println("El archivo conectados.json no existe en la carpeta data.");
        }
    }

    public static void conectadosJSON(String nombre, String from) {
        String rutaArchivo = "data/conectados.json";

        // Verificar si el archivo ya existe
        if (Files.exists(Paths.get(rutaArchivo))) {
            // Leer el contenido actual del archivo
            String contenidoActual = leerContenido(rutaArchivo);

            // Crear un objeto JSON con la nueva información
            JSONObject nuevoUsuario = new JSONObject();
            nuevoUsuario.put("nombre", nombre);
            nuevoUsuario.put("from", from);

            // Verificar si ya hay usuarios en el archivo
            JSONObject jsonObject;
            if (!contenidoActual.isEmpty()) {
                // Si hay usuarios existentes, obtener el objeto JSON principal
                jsonObject = new JSONObject(contenidoActual);
            } else {
                // Si no hay usuarios existentes, crear un nuevo objeto JSON principal con un arreglo vacío
                jsonObject = new JSONObject();
                jsonObject.put("usuarios", new JSONArray());
            }

            // Obtener el arreglo de usuarios
            JSONArray usuariosArray = jsonObject.getJSONArray("usuarios");

            // Agregar el nuevo usuario al arreglo
            usuariosArray.put(nuevoUsuario);

            // Escribir el contenido actualizado en el archivo
            escribirContenido(rutaArchivo, jsonObject.toString(4));

            System.out.println("Usuario agregado al archivo JSON correctamente.");
        } else {
            // Si el archivo no existe, crear uno nuevo con el nuevo usuario dentro de un arreglo "usuarios"
            JSONObject jsonObject = new JSONObject();
            JSONArray usuariosArray = new JSONArray();

            // Crear un objeto JSON con la nueva información
            JSONObject nuevoUsuario = new JSONObject();
            nuevoUsuario.put("nombre", nombre);
            nuevoUsuario.put("from", from);

            // Agregar el nuevo usuario al arreglo
            usuariosArray.put(nuevoUsuario);

            // Agregar el arreglo de usuarios al objeto principal
            jsonObject.put("usuarios", usuariosArray);

            try (FileWriter fileWriter = new FileWriter(rutaArchivo)) {
                fileWriter.write(jsonObject.toString(4));
                System.out.println("Archivo JSON creado correctamente.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String leerContenido(String rutaArchivo) {
        try {
            return new String(Files.readAllBytes(Paths.get(rutaArchivo)));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static void escribirContenido(String rutaArchivo, String contenido) {
        try (FileWriter fileWriter = new FileWriter(rutaArchivo)) {
            fileWriter.write(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
