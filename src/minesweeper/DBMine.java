/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dell
 */
public class DBMine {

    public String get(String name) {
        String print = new String();
        try {

            print = "Your history is : \nName   Score   Result\n";
            Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/minesweeper", "razi", "123456");
            PreparedStatement output = connection.prepareStatement("Select * from RAZI.record where name='" + name + "'");
            ResultSet resultset = output.executeQuery();
            String n;
            while (resultset.next()) {
                n = resultset.getString("name");
                if (name.equals(n)) {
                    String timer = resultset.getString("score");
                    String result = resultset.getString("status");
                    print = print + name + "   " + timer + "   " + result + "\n";
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBMine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return print;
    }

    public void insertLost(String time, String name) {

        try {
            Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/minesweeper", "razi", "123456");

            PreparedStatement insert = connection.prepareStatement("insert into RAZI.record values(?,?,?)");
            insert.setString(1, name);
            insert.setInt(2, Integer.parseInt(time));
            insert.setString(3, "Lose");
            insert.executeUpdate();

        } catch (Exception exp) {
            System.out.println("Connection exception : " + exp);
        }
    }

    public void insertWon(String time, String name) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/minesweeper", "razi", "123456");

            PreparedStatement insert = connection.prepareStatement("insert into RAZI.record values(?,?,?)");
            insert.setString(1, name);
            insert.setInt(2, Integer.parseInt(time));
            insert.setString(3, "Win");
            insert.executeUpdate();

        } catch (Exception exp) {
            System.out.println("Connection exception : " + exp);
        }
    }

    public ArrayList<Player> showLeaderboard() {
        String print = new String();
        ArrayList<Player> board = new ArrayList<>();
        print = "";
        try {
            Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/minesweeper", "razi", "123456");

            PreparedStatement output = connection.prepareStatement("Select * from RAZI.record where status='Win' order by score asc");
            ResultSet resultset = output.executeQuery();
            int i = 0;
            while (resultset.next() && i < 10) {
                if (i == 0) {
                    print = "Top winners are : \n";
                }
                i++;
                String name = resultset.getString("name");
                String timer = resultset.getString("score");
                Player p = new Player(name, timer, i);
                board.add(p);

            }
        } catch (Exception exp) {
            System.out.println("Connection exception : " + exp);
        }
        return board;
    }
}
