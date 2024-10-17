package edu.school21.sockets.app;

import edu.school21.sockets.config.ApplicationConfig;
import edu.school21.sockets.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.net.BindException;

public class Main {

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0].substring(args[0].indexOf('=') + 1));

            ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
            Server server = context.getBean(Server.class);
            server.start(port);
        } catch (NumberFormatException e) {
            System.out.println("argument \"--port=<integer>\" required");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
