# App.js (Main.java)

A bit different than in ExpressJS, in Java you need to 

- In `main`, create server `httpserver` by `Httpserver.create` (at least for the [localhost](http://localhost) case)

```java
HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
```

- Add `.env` file and write down your credentials, you will need `maven` for this

```java
Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources/.env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

```

- Create connection by using `Connection` and `DriverManager.getConnection()`, by declaring this in main and make it param in controllers we improve performance and prevent duplication.

```java
Connection connection;
    try{
        connection = DriverManager.getConnection(
                    dotenv.get("CONNECTION_STRING"),
                    dotenv.get("CONNECTION_USER"),
                    dotenv.get("CONNECTION_PASSWORD")
        );
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
```

- From that, create the routes by `createContext`

```java
httpServer.createContext("/api/users", new UserController(connection));
```

- The rest is copy and paste, the `setExecutor` is needed before `start()` and usually set to null for default setting

```java
httpServer.setExecutor(null);
httpServer.start();
System.out.println("Server running at http://localhost:8080");
```

# Controllers

as you can see in the above example, there is `new UserController`. We will create all the CRUD logic and connect to the database in it

You will need `Gson` for JSON converting 

We going to set `HttpExchange` is basically acting as the `req` and `res` at the same time

We need `Connection` to execute SQL queries

```java
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
```

# UserRoutes

We are going to switch case the `HttpExchange`  to see what CRUD methods is the user requesting 

```java
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

```
