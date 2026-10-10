package pages.ITSM.Api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Author: Daniel
 * This Remedy API client can be used for getting Remedy API key before ANY ITSM API calls
 */

public class RemedyApiClient {
    private static final Logger logger = Logger.getLogger(RemedyApiClient.class.getName());
    private String itsmRestApiUrl;
    private String authToken;
    public String API_KEY_END_POINT = "/api/jwt/login";

    public RemedyApiClient(String itsmRestApiUrl) {
        this.itsmRestApiUrl = itsmRestApiUrl;
    }

    public String getRemedyApiKey(String remedyAdminUser, String remedyAdminPassword) throws Exception {
        String payload = "username=" + URLEncoder.encode(remedyAdminUser, "UTF-8")
                + "&password=" + URLEncoder.encode(remedyAdminPassword, "UTF-8");
        String urlStr = itsmRestApiUrl + API_KEY_END_POINT;

        logger.info("URL: " + urlStr);
        logger.info("Payload: " + payload);

        disableSSLCertValidation();

        URL url = new URL(urlStr);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        int status = connection.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Failed to login. Status code: " + status);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        authToken = response.toString();
        logger.info("Auth Token: " + authToken);
        return authToken;
    }

    private void disableSSLCertValidation() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
        };
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}
