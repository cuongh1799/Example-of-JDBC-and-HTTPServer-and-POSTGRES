package org.example;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    public static void handleGetUsers(HttpExchange exchange, Connection conn) throws IOException {
        List<User> users = new ArrayList<>();
        Gson gson = new Gson();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                users.add(
                        new User(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("created_at"))
                );
            }

            String json = gson.toJson(users);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }
}
