package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import javax.swing.plaf.nimbus.State;
import javax.xml.transform.Result;
import java.beans.Statement;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    // **START** CREATING SQL functions for database

           // Inserting name and type into table
    static void insertBeer(Connection conn, String name, String type) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO beer (name, type) VALUES (?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, type);
        stmt.execute();
    }
          // Delete row from Table based off id #
    static void deleteBeer(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM beer WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    static ArrayList<Beer> selectBeers(Connection conn) throws SQLException {
        java.sql.Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT * FROM beer");
        ArrayList<Beer> beers = new ArrayList<>();
        while (results.next()) {
            String name = results.getString("name");
            String type = results.getString("type");
            int id = results.getInt("id");
            Beer beer = new Beer(id, name, type);
            beers.add(beer);
        }
        return beers;

    }
          // Editing name and type from "beer" table
    static void editBeer(Connection conn, String name, String type, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE beer SET name = ?, type = ? WHERE id = ?");
        stmt.setString(1, name);
        stmt.setString(2, type);
        stmt.setInt(3, id);
        stmt.execute();
    }

      /*  // Search Box
    static searchBeer(Connection conn, String name) throws SQLException {
        java.sql.Statement stmt = conn.createStatement();
        ResultSet searchResults = stmt.executeQuery("SELECT * FROM beer");

    }  */

    // **END**  SQL functions for database

    public static void main(String[] args) throws SQLException {
        // H2 database creation
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        java.sql.Statement stmt = conn.createStatement();
        // TABLE CREATED - beer
        stmt.execute("CREATE TABLE IF NOT EXISTS beer(name VARCHAR, type VARCHAR, id IDENTITY PRIMARY KEY)");


        // ArrayList<Beer> beers = new ArrayList();
        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        return new ModelAndView(new HashMap(), "not-logged-in.html");
                    }
                    HashMap m = new HashMap();
                    m.put("username", username);
                    m.put("beers", selectBeers(conn));
                    return new ModelAndView(m, "logged-in.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                ((request, response) -> {
                    String username = request.queryParams("username");
                    Session session = request.session();
                    session.attribute("username", username);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/create-beer",
                ((request, response) -> {
                    String name = request.queryParams("beername");
                    String type = request.queryParams("beertype");
                    insertBeer(conn, name, type);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/delete-beer",
                ((request, response) -> {
                    String id = request.queryParams("beerid");
                    try {
                        int idNum = Integer.valueOf(id);
                        deleteBeer(conn, idNum);
                    } catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/edit-beer",
                ((request, response) -> {
                    String nameEdit = request.queryParams("nameedit");
                    String idEdit = request.queryParams("beeridedit");
                    String typeEdit = request.queryParams("typeedit");

                    try {
                        int idEditNum = Integer.valueOf(idEdit);
                        editBeer(conn, nameEdit, typeEdit, idEditNum);
                    } catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                })
        );

       /* Spark.post(
                "/search-beer",
                ((request, response) -> {
                    String search = request.queryParams("search").toLowerCase();


                })
        ); */
    }
}
