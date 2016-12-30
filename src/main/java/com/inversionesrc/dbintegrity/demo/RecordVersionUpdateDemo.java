package com.inversionesrc.dbintegrity.demo;

import com.inversionesrc.dbintegrity.ProtectionStringBuilder;
import com.inversionesrc.dbintegrity.RecordProtection;
import com.inversionesrc.dbintegrity.RecordProtectionUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecordVersionUpdateDemo extends DemoBase {
    public static void main(String[] args) throws SQLException {
        insertOneRecordAtVersion2();
        verifyAllRecordsIncludingPreviousVersions();
    }

    private static void insertOneRecordAtVersion2() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO RecordVersionUpdate (Version1Field, Version2Field, RecordProtection) VALUES (?, ?, ?);");
        String version1Field = "version 1 field in new record";
        String version2Field = "version 2 field in new record";
        preparedStatement.setString(1, version1Field);
        preparedStatement.setString(2, version2Field);
        preparedStatement.setString(3, new MyRecordProtectionAtVersion2(version1Field, version2Field).calculateProtection());
        preparedStatement.executeUpdate();
    }

    private static void verifyAllRecordsIncludingPreviousVersions() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT Version1Field, Version2Field, RecordProtection FROM RecordVersionUpdate");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String version1Field = resultSet.getString(1);
            String version2Field = resultSet.getString(2);
            String recordProtection = resultSet.getString(3);
            new MyRecordProtectionAtVersion2(version1Field, version2Field).verifyProtection(recordProtection);
            int recordVersion = RecordProtectionUtil.getRecordVersion(recordProtection);
            System.out.println(String.format("Record at version %s successfully verified, displaying data...", recordVersion));
            System.out.println("version1Field = " + version1Field);
            System.out.println("version2Field = " + version2Field);
            System.out.println();
        }
    }

    private static class MyRecordProtectionAtVersion2 extends RecordProtection {
        private final String version1Field;
        private final String version2Field;

        MyRecordProtectionAtVersion2(String version1Field, String version2Field) {
            this.version1Field = version1Field;
            this.version2Field = version2Field;
        }

        @Override
        protected String getStringToProtect(int recordVersion) {
            ProtectionStringBuilder protectionStringBuilder = new ProtectionStringBuilder();
            protectionStringBuilder.append(version1Field);
            if (recordVersion >= 2) { // We just include the version2Field for records at version 2 or later.
                protectionStringBuilder.append(version2Field);
            }
            return protectionStringBuilder.toString();
        }

        @Override
        protected int getRecordVersion() {
            return 2;
        }
    }
}
