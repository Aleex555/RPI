package com.project;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
             TimeUnit.SECONDS.sleep(5);
            // el matem si encara no ha acabat
            if( process.isAlive() ) process.destroy();
            process.waitFor();

     
            
            System.out.println("Se ha creado la carpeta");

           

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
