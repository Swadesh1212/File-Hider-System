package in.sk.dao;

import in.sk.db.MyConnection;
import in.sk.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class UserDAO {

    // ✅ Check if user exists (email stored in Base64 in DB)
    public static boolean isExists(String email) throws SQLException {
        Connection connection = MyConnection.getConnection();

        // Encode input email for DB comparison
        String encodedEmail = Base64.getEncoder().encodeToString(email.getBytes());

        PreparedStatement ps = connection.prepareStatement("select 1 from users where email=?");
        ps.setString(1, encodedEmail);
        ResultSet rs = ps.executeQuery();

        return rs.next(); // true if any row exists
    }

    // ✅ Save user (store email in Base64)
    public static int saveUser(User user) throws SQLException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("insert into users(name, email) values(?, ?)");

        ps.setString(1, user.getName());

        // Encode email before saving
        String encodedEmail = Base64.getEncoder().encodeToString(user.getEmail().getBytes());
        ps.setString(2, encodedEmail);

        return ps.executeUpdate();
    }

    // ✅ Fetch user by email (decode back to plain text)
    public static User getUserByEmail(String email) throws SQLException {
        Connection connection = MyConnection.getConnection();

        // Encode input email for DB lookup
        String encodedEmail = Base64.getEncoder().encodeToString(email.getBytes());

        PreparedStatement ps = connection.prepareStatement("select name, email from users where email=?");
        ps.setString(1, encodedEmail);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String name = rs.getString("name");

            // Decode email from DB back to real
            String decodedEmail = new String(Base64.getDecoder().decode(rs.getString("email")));

            return new User(name, decodedEmail);
        }
        return null;
    }
}
