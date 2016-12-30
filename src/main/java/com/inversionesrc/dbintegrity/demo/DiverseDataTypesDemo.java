package com.inversionesrc.dbintegrity.demo;

import com.inversionesrc.dbintegrity.ProtectionStringBuilder;
import com.inversionesrc.dbintegrity.RecordProtection;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

public class DiverseDataTypesDemo extends DemoBase {

    public static void main(String[] args) throws SQLException, IOException {
        int pictureId = insertAndProtect();
        selectAndVerify(pictureId);
    }

    private static int insertAndProtect() throws SQLException, IOException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Gallery (Name, Picture, CreationDate, NumberOfViews, Price, RowProtection) VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
        String name = "Apple";
        byte[] picture = IOUtils.toByteArray(DiverseDataTypesDemo.class.getResourceAsStream("/apple_picture.png"));
        // TODO check if this is persisting GMT 0 or local timezone.
        Timestamp creationDate = new Timestamp(System.currentTimeMillis());
        // FIXME temporarily clearing out the fractional seconds component because it has been observed that when a timestamp like this '2016-12-11 07:48:31.481' (with a fractional with three digits) is sent to the database, its value is stored different, e.g. '2016-12-11 07:48:31.480' which produces a signature verification error, here it is required to identify who is responsible for this behaviour, it is, the JDBC driver, the SQL Server engine, etc. Check too if this problem happens with other JDBC driver for SQL Server or with another DBMS.
        creationDate.setNanos(0);
        int numberOfVisits = new Random().nextInt();
        // We create a random decimal from 0 to 1000 (exclusive) with two fractional digits.
        double price = new Random().nextDouble() * 1000;
        price = Math.round(price * 100) / 100.0;
        preparedStatement.setString(1, name);
        preparedStatement.setBytes(2, picture);
        preparedStatement.setTimestamp(3, creationDate);
        preparedStatement.setInt(4, numberOfVisits);
        preparedStatement.setDouble(5, price);
        String rowProtection = new GalleryProtection(name, picture, creationDate, numberOfVisits, price).calculateProtection();
        preparedStatement.setString(6, rowProtection);
        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getInt(1);
    }

    private static void selectAndVerify(int insert) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT Name, Picture, CreationDate, NumberOfViews, Price, RowProtection FROM Gallery WHERE PictureId = ?");
        preparedStatement.setInt(1, insert);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String name = resultSet.getString(1);
        byte[] picture = resultSet.getBytes(2);
        Timestamp creationDate = resultSet.getTimestamp(3);
        int numberOfViews = resultSet.getInt(4);
        double price = resultSet.getDouble(5);
        String rowProtection = resultSet.getString(6);
        GalleryProtection galleryProtection = new GalleryProtection(name, picture, creationDate, numberOfViews, price);
        galleryProtection.verifyProtection(rowProtection);
        System.out.println("name = " + name);
        System.out.println("picture = " + picture);
        System.out.println("creationDate = " + creationDate);
        System.out.println("numberOfViews = " + numberOfViews);
        System.out.println("price = " + price);
    }

    public static class GalleryProtection extends RecordProtection {

        private final String name;
        private final byte[] picture;
        private final Timestamp creationDate;
        private final int numberOfVisits;
        private final double price;

        GalleryProtection(String name, byte[] picture, Timestamp creationDate, int numberOfVisits, double price) {
            this.name = name;
            this.picture = picture;
            this.creationDate = creationDate;
            this.numberOfVisits = numberOfVisits;
            this.price = price;
        }

        @Override
        protected String getStringToProtect(int recordVersion) {
            // TODO check the form of the produced string, check at the date.
            return new ProtectionStringBuilder().append(name).append(picture).append(creationDate).append(numberOfVisits).append(price).toString();
        }
    }
}
