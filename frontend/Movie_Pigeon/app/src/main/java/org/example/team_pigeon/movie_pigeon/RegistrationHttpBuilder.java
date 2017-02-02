package org.example.team_pigeon.movie_pigeon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Guo Mingxuan on 1/2/2017.
 */

class RegistrationHttpBuilder {
    String url = "128.199.231.190";
    String registrationURL = "128.199.231.190/api/users";
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String param1, param2, param3;
    HttpURLConnection connection = null;

    RegistrationHttpBuilder(String email, String username, String password) {
        String query = formQuery(email, username, password);
        // type 0 for sign in, type 1 for registration
       request(query);
    }

    private void request(String query) {
        // build registration request here
        try {
            connection = (HttpURLConnection) new URL(registrationURL).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(query.getBytes(charset));
            }

            InputStream response = connection.getInputStream();
            // TODO process the response



            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("Unable to connect to server");
        }
    }

    private String formQuery(String email, String username, String password) {
        param1 = "email=" + email;
        param2 = "username=" + username;
        param3 = "password=" + password;
        try {
            return String.format("param1=%s&param2=%s&param3=%s",
                    URLEncoder.encode(param1, charset),
                    URLEncoder.encode(param2, charset),
                    URLEncoder.encode(param3, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("Unable to encode message");
            return "";
        }
    }
}
