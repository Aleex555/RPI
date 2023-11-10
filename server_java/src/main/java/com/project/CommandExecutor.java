package com.project;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class CommandExecutor {
    

    public void executeCommand(String mensaje) {

        String displayText = mensaje;

        // Directorio de trabajo basado en el directorio de inicio del usuario

        String workingDirectory = "/home/ieti/dev/rpi-rgb-led-matrix";
        //String workingDirectory = "/home/super";
        // Comando a ejecutar (personalizado según tus necesidades)

        String command = "examples-api-use/scrolling-text-example -y 18 -f ~/dev/bitmap-fonts/bitmap/cherry/cherry-10-b.bdf --led-cols=64 --led-rows=64 --led-slowdown-gpio=4 --led-no-hardware-pulse "+mensaje;
        //String command = "mkdir "+mensaje;

        try {
            // Crear el proceso builder y configurar el directorio de trabajo
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.directory(new File(workingDirectory));

            // Redirigir la salida del proceso a la consola
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Temporizador para interrumpir el proceso después de 5 segundos
            

            // Leer la salida del proceso
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Imprimir la salida del comando
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            System.out.println("Se ha creado la carpeta");

            // Cancelar el temporizador después de la finalización del proceso
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
