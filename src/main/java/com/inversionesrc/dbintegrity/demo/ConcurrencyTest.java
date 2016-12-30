package com.inversionesrc.dbintegrity.demo;

import com.inversionesrc.dbintegrity.RecordProtection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static com.inversionesrc.dbintegrity.demo.DemoBase.getConnection;

// TODO evaluate to create a unit test similar to this class in the library module, or at least move this class to the src/test/java folder of the library module, maybe without the JDBC dependency.
public class ConcurrencyTest {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    Connection connection = getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Person (Name, LastName, RowProtection) VALUES (?,?,?)");
                    for (int j = 0; j < 100; j++) {
                        String name = "John " + new Random().nextInt();
                        String lastName = "Doe " + new Random().nextInt();
                        preparedStatement.setString(1, name);
                        preparedStatement.setString(2, lastName);
                        RecordProtection recordProtection = new BasicDemo.PersonProtection(name, lastName);
                        preparedStatement.setString(3, recordProtection.calculateProtection());
                        preparedStatement.executeUpdate();
                        System.out.print("+");
                    }
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    Connection connection = getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT Name, LastName, RowProtection FROM Person");
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        String name = resultSet.getString(1);
                        String lastName = resultSet.getString(2);
                        String rowProtection = resultSet.getString(3);
                        new BasicDemo.PersonProtection(name, lastName).verifyProtection(rowProtection);
                        System.out.print("?");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

}
