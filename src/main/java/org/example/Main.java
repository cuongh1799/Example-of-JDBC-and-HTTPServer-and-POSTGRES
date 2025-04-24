package org.example;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Connection connection;
        Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources/.env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        try{
            connection = DriverManager.getConnection(
                    dotenv.get("CONNECTION_STRING"),
                    dotenv.get("CONNECTION_USER"),
                    dotenv.get("CONNECTION_PASSWORD")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/api/users", new UserRoutes(connection));
        httpServer.setExecutor(null);
        httpServer.start();
        System.out.println("Server running at http://localhost:8080");
    }
}
