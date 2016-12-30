package com.inversionesrc.dbintegrity.demo;

import com.inversionesrc.dbintegrity.ProtectionStringBuilder;
import com.inversionesrc.dbintegrity.RecordProtection;
import com.inversionesrc.dbintegrity.RecordProtectionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BasicDemo extends DemoBase {

    public static void main(String[] args) throws SQLException {
        System.out.println("Inserting Person \"John Doe\"...");
        int personId = insertAndProtect("John", "Doe");
        System.out.println("Person saved succesfully with PersonId " + personId + ".");
        System.out.println();

        System.out.println("Querying Person with PersonId " + personId + "...");
        selectAndVerify(personId);
        System.out.println();

        System.out.println("Updating Person with PersonId " + personId + " to \"Jane Doe\"...");
        updateAndProtect(personId, "Jane", "Doe");
        System.out.println();

        System.out.println("Querying and verifying Person with PersonId " + personId + "...");
        selectAndVerify(personId);

    }

    static int insertAndProtect(String name, String lastName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Person (Name, LastName, RowProtection) VALUES (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, lastName);
        System.out.println("Generating row protection...");
        RecordProtection personProtection = new PersonProtection(name, lastName);
        String rowProtection = personProtection.calculateProtection();
        System.out.println("Row protection generated succesfully.");
        preparedStatement.setString(3, rowProtection);
        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getInt(1);
    }

    static void selectAndVerify(int personId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT Name, LastName, RowProtection FROM Person WHERE PersonId = ?");
        preparedStatement.setInt(1, personId);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String name = resultSet.getString(1);
        String lastName = resultSet.getString(2);
        String rowProtection = resultSet.getString(3);
        try {
            System.out.println("Verifying row protection...");
            RecordProtection recordProtection = new PersonProtection(name, lastName);
            recordProtection.verifyProtection(rowProtection);
            System.out.println("Row protection verified succesfully.");
            System.out.println("Displaying verified information...");
            System.out.println("name = " + name);
            System.out.println("lastName = " + lastName);
        } catch (RecordProtectionException e) {
            System.err.println("Row protection verification failed, not displaying the information because it is not considered trustworthy: " + e.getMessage());
        }
    }

    private static void updateAndProtect(int personId, String newName, String newLastName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Person SET Name=?, LastName=?, RowProtection=? WHERE PersonId=?");
        preparedStatement.setString(1, newName);
        preparedStatement.setString(2, newLastName);
        System.out.println("Generating row protection...");
        PersonProtection personProtection = new PersonProtection(newName, newLastName);
        String rowProtection = personProtection.calculateProtection();
        System.out.println("Row protection generated succesfully.");
        preparedStatement.setString(3, rowProtection);
        preparedStatement.setInt(4, personId);
        preparedStatement.executeUpdate();
    }

    static class PersonProtection extends RecordProtection {
        private final String name;
        private final String lastName;

        PersonProtection(String name, String lastName) {
            this.name = name;
            this.lastName = lastName;
        }

        @Override
        protected String getStringToProtect(int recordVersion) {
            return new ProtectionStringBuilder().append(name).append(lastName).toString();
        }
    }
}
