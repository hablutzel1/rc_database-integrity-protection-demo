package com.inversionesrc.dbintegrity.demo;

import com.inversionesrc.dbintegrity.RecordProtectionConfiguration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

abstract class DemoBase {

    static {
        try {
            RecordProtectionConfiguration.init(new FileInputStream("src/main/resources/databaseintegrity.properties"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected static final Connection connection = getConnection();

    static Connection getConnection()  {
        try {
            // TODO try to use Windows authentication without requiring the user to configure any additional DLL. Try with the alternative JDBC driver for SQL Server.
            return DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=DatabaseIntegrityProtectionDemo", "sa", "sqlserver");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
