package org.example;
import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import java.io.*;
import java.sql.*;

public class UserRoutes implements HttpHandler {
    private final Gson gson = new Gson();
    private final Connection conn;

    public UserRoutes(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                UserController.handleGetUsers(exchange, conn);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // Method not allowed
        }
    }
}
