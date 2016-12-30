package com.inversionesrc.dbintegrity.demo;

import com.inversionesrc.dbintegrity.ProtectionStringBuilder;
import com.inversionesrc.dbintegrity.RecordProtection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MultipleProtectionsDemo extends DemoBase {

    public static void main(String[] args) throws SQLException {
        String regular1 = "regular 1";
        String regular2 = "regular 2";
        String critical1 = "critical 1";
        String critical2 = "critical 2";
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO MultipleProtections (Regular1, Regular2, Critical1, Critical2, RegularFieldsProtection, CriticalFieldsProtection) VALUES (?, ?, ?, ?, ?, ?)");
        preparedStatement.setString(1, regular1);
        preparedStatement.setString(2, regular2);
        preparedStatement.setString(3, critical1);
        preparedStatement.setString(4, critical2);
        preparedStatement.setString(5, new RegularFieldsProtection(regular1, regular2).calculateProtection());
        preparedStatement.setString(6, new CriticalFieldsProtection(critical1, critical2).calculateProtection());
        preparedStatement.executeUpdate();
    }

    private static class RegularFieldsProtection extends RecordProtection {
        private final String regular1;
        private final String regular2;

        RegularFieldsProtection(String regular1, String regular2) {
            this.regular1 = regular1;
            this.regular2 = regular2;
        }

        @Override
        protected String getStringToProtect(int recordVersion) {
            return new ProtectionStringBuilder().append(regular1).append(regular2).toString();
        }
    }

    private static class CriticalFieldsProtection extends RecordProtection {
        private final String critical1;
        private final String critical2;

        CriticalFieldsProtection(String critical1, String critical2) {
            this.critical1 = critical1;
            this.critical2 = critical2;
        }

        @Override
        protected String getStringToProtect(int recordVersion) {
            return new ProtectionStringBuilder().append(critical1).append(critical2).toString();
        }

        /**
         * This method is overriden to specify a custom key to be used. In this demo this key id would correspond to a
         * critical key, that eventually could be stored into an HSM.
         *
         * @return
         */
        @Override
        public int getKeyId() {
            return 432;
        }
    }
}
