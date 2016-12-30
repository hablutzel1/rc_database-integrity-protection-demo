package com.inversionesrc.dbintegrity.demo;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MaliciousUpdateDemo extends DemoBase {
    public static void main(String[] args) throws SQLException {
        int personId = BasicDemo.insertAndProtect("John", "Doe");
        // This call shows a malicious update to the database, for example, made by someone with direct access to the database.
        maliciousUpdate(personId, "Trudy", "Mallory");
        BasicDemo.selectAndVerify(personId);
    }

    private static void maliciousUpdate(int personId, String maliciousNewName, String maliciousNewLastname) throws SQLException {
        // The attacker can't create a valid row protection string because he doesn't possess the signing key so he just updates the record data.
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Person SET Name=?, LastName=? WHERE PersonId=?");
        preparedStatement.setString(1, maliciousNewName);
        preparedStatement.setString(2, maliciousNewLastname);
        preparedStatement.setInt(3, personId);
        preparedStatement.executeUpdate();
    }
}
