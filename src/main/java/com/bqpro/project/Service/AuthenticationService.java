package com.bqpro.project.Service;

import com.bqpro.project.Exceptions.HmacCalculationException;
import com.bqpro.project.Exceptions.InvalidCookieValueException;
import com.bqpro.project.Exceptions.InvalidLoginException;
import com.bqpro.project.Exceptions.InvalidPasswordException;
import com.bqpro.project.Model.Credentials;
import com.bqpro.project.Model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

@Service
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;

    @Value("${auth.cookie.hmac-key:secret-key}")
    private String secretKey;

    public AuthenticationService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(Credentials credentialsDto) {
        String encodedMasterPassword = passwordEncoder.encode(CharBuffer.wrap("1234"));
      /*  System.out.println(credentialsDto.getPassword());
        System.out.println(credentialsDto.getLogin());*/
        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), encodedMasterPassword)) {
            return new User();
        }
        throw new InvalidPasswordException("Invalid password");
    }

    public User findByLogin(String login) {
        if ("login".equals(login)) {
            return new User();
        }
        throw new InvalidLoginException("Invalid login");
    }

    public String createToken(User user) {
        return user.getId() + "&" + user.getUsername() + "&" + calculateHmac(user);
    }

    public User findByToken(String token) {
        String[] parts = token.split("&");

        Long userId = Long.valueOf(parts[0]);
        String login = parts[1];
        String hmac = parts[2];

        User user = findByLogin(login);

        if (!hmac.equals(calculateHmac(user)) || !userId.equals(user.getId())) {
            throw new InvalidCookieValueException("Invalid Cookie value");
        }

        return user;
    }


    private String calculateHmac(User user) {
        byte[] secretKeyBytes = Objects.requireNonNull(secretKey).getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = Objects.requireNonNull(user.getId() + "&" + user.getUsername()).getBytes(StandardCharsets.UTF_8);

        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(valueBytes);
            return Base64.getEncoder().encodeToString(hmacBytes);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new HmacCalculationException("Error calculating Hmac",e);
        }
    }
}


