package com.inversionesrc.dbintegrity.demo;

import com.google.common.base.Stopwatch;
import com.inversionesrc.dbintegrity.RecordProtection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static com.inversionesrc.dbintegrity.demo.DemoBase.getConnection;

public class LoadTest {
    public static void main(String[] args) throws SQLException {
        performTest(false, 100);
        performTest(true, 100);
        performTest(false, 1000);
        performTest(true, 1000);
        performTest(false, 10000);
        performTest(true, 10000);
    }

    private static void performTest(boolean withRecordProtection, int numberOfRecords) throws SQLException {
        System.out.println("========================================");
        System.out.println("Performing test " + (withRecordProtection ? "with" : "without") + " integrity protection and " + numberOfRecords + " records.");
        System.out.println("========================================");
        Connection connection = getConnection();
        System.out.println("Deleting all records in table...");
        connection.prepareStatement("DELETE FROM Person").executeUpdate();
        System.out.println("All records deleted.");
        System.out.println("Inserting " + numberOfRecords + " random records...");
        Stopwatch stopwatch = Stopwatch.createStarted();
        PreparedStatement insertPS = connection.prepareStatement("INSERT INTO Person (Name, LastName, RowProtection) VALUES (?,?,?)");
        for (int j = 0; j < numberOfRecords; j++) {
            String name = "John " + new Random().nextInt();
            String lastName = "Doe " + new Random().nextInt();
            insertPS.setString(1, name);
            insertPS.setString(2, lastName);
            if (withRecordProtection) {
                RecordProtection recordProtection = new BasicDemo.PersonProtection(name, lastName);
                insertPS.setString(3, recordProtection.calculateProtection());
            } else {
                insertPS.setString(3, null);
            }
            insertPS.executeUpdate();
        }
        System.out.println("All records inserted correctly, it took " + stopwatch.toString() + ".");
        System.out.println("Querying all previously inserted records...");
        stopwatch.reset().start();
        PreparedStatement selectPS = connection.prepareStatement("SELECT Name, LastName, RowProtection FROM Person");
        ResultSet resultSet = selectPS.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString(1);
            String lastName = resultSet.getString(2);
            String rowProtection = resultSet.getString(3);
            if (withRecordProtection) {
                new BasicDemo.PersonProtection(name, lastName).verifyProtection(rowProtection);
            }
        }
        System.out.println("Querying done, it took " + stopwatch.toString() + ".");
        System.out.println();
        connection.close();
    }
}
