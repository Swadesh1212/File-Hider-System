package in.sk.dao;

import in.sk.db.MyConnection;
import in.sk.model.Data;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DataDao {
    public static List<Data> getAllFiles(String email) throws SQLException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("select * from data where email = ?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        List<Data> files = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String path = rs.getString(3);
            files.add(new Data(id, name, path));
        }
        return files;
    }

    public static int hideFile(Data file) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "insert into data(name, path, email, bin_data) values (?, ?, ?, ?)");

        ps.setString(1, file.getFileName());
        ps.setString(2, file.getPath());
        ps.setString(3, file.getEmail());

        // Read file as bytes
        File f = new File(file.getPath());
        byte[] fileBytes = Files.readAllBytes(f.toPath());

        // Encode to Base64
        String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);

        ps.setString(4, base64Encoded);

        int ans = ps.executeUpdate();

        // Delete original file
        f.delete();

        return ans;
    }

    public static void unhide(int id) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("select path, bin_data from data where id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String path = rs.getString("path");
            String base64Encoded = rs.getString("bin_data");

            // Decode Base64 back to bytes
            byte[] fileBytes = Base64.getDecoder().decode(base64Encoded);

            // Write bytes back to file
            try (FileOutputStream fos = new FileOutputStream(path)) {
                fos.write(fileBytes);
            }

            // Delete record from DB
            ps = connection.prepareStatement("delete from data where id=?");
            ps.setInt(1, id);
            ps.executeUpdate();

            System.out.println("Successfully Unhidden");
        }
    }
}
