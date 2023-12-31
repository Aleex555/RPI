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

        String command = "examples-api-use/scrolling-text-example -C 159,0,255 -y 18 -f ~/dev/bitmap-fonts/bitmap/cherry/cherry-13-b.bdf --led-cols=64 --led-rows=64 --led-slowdown-gpio=4 --led-no-hardware-pulse "+displayText;
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

        String command = "examples-api-use/scrolling-text-example -C 159,0,255 -y 18 -f ~/dev/bitmap-fonts/bitmap/cherry/cherry-13-b.bdf --led-cols=64 --led-rows=64 --led-slowdown-gpio=4 --led-no-hardware-pulse "+displayText;
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

   

    public static boolean verificarCredenciales(String usuario, String contra) {
        try {
     
            String jsonString = new String(Files.readAllBytes(Paths.get("data/usuarios.json")));

       
            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray usuariosArray = jsonObject.getJSONArray("usuarios");

         
            for (int i = 0; i < usuariosArray.length(); i++) {
                JSONObject usuarioObj = usuariosArray.getJSONObject(i);

                String usuarioGuardado = usuarioObj.getString("usuario");
                String contraGuardada = usuarioObj.getString("contrasena");

             
                if (usuario.equals(usuarioGuardado) && contra.equals(contraGuardada)) {
                    System.out.println("Se conecto un usuario");
                    return true;
                }
            }

            
            return false;

        } catch (IOException e) {
            
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
        } else {
            System.out.println("El archivo conectados.json no existe en la carpeta data.");
        }
    }

    public static void conectadosJSON(String nombre, String from , String id) {
        String rutaArchivo = "data/conectados.json";

        // Verificar si el archivo ya existe
        if (Files.exists(Paths.get(rutaArchivo))) {
            // Leer el contenido actual del archivo
            String contenidoActual = leerContenido(rutaArchivo);

            // Crear un objeto JSON con la nueva información
            JSONObject nuevoUsuario = new JSONObject();
            nuevoUsuario.put("id", id);
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

          
        } else {
            // Si el archivo no existe, crear uno nuevo con el nuevo usuario dentro de un arreglo "usuarios"
            JSONObject jsonObject = new JSONObject();
            JSONArray usuariosArray = new JSONArray();

            // Crear un objeto JSON con la nueva información
            JSONObject nuevoUsuario = new JSONObject();
            nuevoUsuario.put("id", id);
            nuevoUsuario.put("nombre", nombre);
            nuevoUsuario.put("from", from);

            // Agregar el nuevo usuario al arreglo
            usuariosArray.put(nuevoUsuario);

            // Agregar el arreglo de usuarios al objeto principal
            jsonObject.put("usuarios", usuariosArray);

            try (FileWriter fileWriter = new FileWriter(rutaArchivo)) {
                fileWriter.write(jsonObject.toString(4));

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

    public static void eliminarUsuarioPorId(String userId) {
        String jsonContent = "";
        try {
            jsonContent = new String(Files.readAllBytes(Paths.get("data/conectados.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        if (!jsonContent.isEmpty()) {
            JSONObject json = new JSONObject(jsonContent);
            JSONArray usuarios = json.getJSONArray("usuarios");
    
            Iterator<Object> iterator = usuarios.iterator();
            while (iterator.hasNext()) {
                JSONObject usuario = (JSONObject) iterator.next();
                String id = usuario.getString("id");
                if (id.equals(userId)) {
                    iterator.remove();
                    break;
                }
            }
    
            json.put("usuarios", usuarios);
    
            try {
                Files.write(Paths.get("data/conectados.json"), json.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }}

        public static Map<String, String> convertirJsonAHashMap() throws Exception {
        Map<String, String> hashMap = new HashMap<>();

        try (FileReader reader = new FileReader("data/conectados.json")) {
            // Parsear el archivo JSON
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));

            // Obtener la lista de usuarios
            JSONArray usuarios = jsonObject.getJSONArray("usuarios");

            // Iterar sobre la lista y agregar al HashMap
            for (int i = 0; i < usuarios.length(); i++) {
                JSONObject usuario = usuarios.getJSONObject(i);
                String nombre = usuario.getString("nombre");
                String from = usuario.getString("from");

                // Si ya existe un valor para la clave, concatenar los valores con un separador
                if (hashMap.containsKey(nombre)) {
                    String valorExistente = hashMap.get(nombre);
                    hashMap.put(nombre, valorExistente);
                } else {
                    hashMap.put(nombre, from);
                }
            }
        }
        System.out.println(hashMap);
        return hashMap;
    }
    
    public static Map<String, String> obtenerNombreYFromPorId(String id) throws Exception {
        Map<String, String> resultado = new HashMap<>();

        try (FileReader reader = new FileReader("data/conectados.json")) {
            // Parsear el archivo JSON
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));

            // Obtener la lista de usuarios
            JSONArray usuarios = jsonObject.getJSONArray("usuarios");

            // Buscar en la lista por ID
            for (int i = 0; i < usuarios.length(); i++) {
                JSONObject usuario = usuarios.getJSONObject(i);
                String idUsuario = usuario.getString("id");

                if (idUsuario.equals(id)) {
                    resultado.put("nombre", usuario.getString("nombre"));
                    resultado.put("from", usuario.getString("from"));
                    return resultado;
                }
            }
        }

        return null; // Retornar null si no se encuentra el ID
    }

    
}
