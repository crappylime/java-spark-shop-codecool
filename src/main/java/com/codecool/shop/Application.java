package com.codecool.shop;

import spark.Request;
import spark.Response;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static spark.Spark.*;

public class Application {
    private static Connection connection;
    private static Application app;

    public Application(String[] args) {
        System.out.println("Initializing application...");

        try {
            this.connectToDb();
            app=this;
            if (Objects.equals(args[0], "--init-db")) {
                System.out.println(args[0]);
                dropTables();
                createTables();
            } else if (Objects.equals(args[0], "--migrate-db")) {
                System.out.println(args[0]);
                createTables();
            }
            exception(Exception.class, (e, req, res) -> e.printStackTrace());
            staticFileLocation("/public");
            port(8888);
            routes();
        } catch (SQLException e) {
            System.out.println("Application initialization failed...");
            e.printStackTrace();
        }
    }

    private void connectToDb() throws SQLException {
        System.out.println("Connection to DB...");
        this.connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
    }

    private void dropTables() throws SQLException {
        Statement statement = connection.createStatement();
        List<String> tables = new ArrayList<>();

        ResultSet rs = statement.executeQuery("" +
                "SELECT name FROM sqlite_master WHERE type='table' AND name!='sqlite_sequence'");
        while (rs.next()) {
            tables.add(rs.getString("name"));
        }
        for (String table: tables) {
            statement.execute("DROP TABLE '" + table + "'");
        }
    }
    
    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(prepareQuery("products.sql"));
        statement.execute(prepareQuery("categories.sql"));
        statement.execute(prepareQuery("suppliers.sql"));
    }

    private String prepareQuery(String fileName) {
        StringBuilder sb = new StringBuilder();
        try{
            BufferedReader bufferedReader = new BufferedReader(
                    new FileReader("src/main/resources/sql/" + fileName)
            );
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void routes() {
        get("/", (Request req, Response res) -> {
            return "hello world";
        });
    }
}
