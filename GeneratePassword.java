import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hashed = encoder.encode(password);
        System.out.println("BCrypt hash for 'admin123': " + hashed);
    }
}
