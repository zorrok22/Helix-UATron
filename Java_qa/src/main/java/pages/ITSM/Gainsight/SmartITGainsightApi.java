package pages.ITSM.Gainsight;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import util.SolQAConstants;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SmartITGainsightApi {

	private static final Pattern COOKIE_VALUE_PATTERN = Pattern.compile("(?i)(?:^|;\\s*)(%s)=([^;]+)");

	private SmartITGainsightApi() {}

	public static String baseUrlFromCurrentUrl(String currentUrl) {
		try {
			URL url = new URL(currentUrl);
			String portPart = url.getPort() > 0 ? ":" + url.getPort() : "";
			return url.getProtocol() + "://" + url.getHost() + portPart;
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to parse base URL from: " + currentUrl, e);
		}
	}

	public static JSONObject getAptrinsicConfigurationJsonForITSM(String baseUrl, String cookieHeader) {
		if (baseUrl == null || baseUrl.trim().isEmpty()) {
			throw new IllegalArgumentException("baseUrl must not be null/empty");
		}
		if (cookieHeader == null || cookieHeader.trim().isEmpty()) {
			throw new IllegalArgumentException("cookieHeader must not be null/empty");
		}

		String endpoint = baseUrl.replaceAll("/+$", "") + "/analytics/gainsight/configuration/ITSM";
		String xsrfToken = extractCookieValue(cookieHeader, "XSRF-TOKEN");

		Response response = RestAssured.given()
			.relaxedHTTPSValidation()
			.header(SolQAConstants.CONTENT_TYPE_HEADER, SolQAConstants.APPLICATION_JSON)
			.header("Cookie", cookieHeader)
			.header("cache-control", "no-cache")
			.header("X-XSRF-TOKEN", xsrfToken == null ? "" : xsrfToken)
			.when()
			.get(endpoint);

		int status = response.getStatusCode();
		if (status != 200) {
			throw new IllegalStateException("Failed to fetch Gainsight config JSON. Status: " + status + ", Body: " + response.getBody().asString());
		}

		String jsonData = response.getBody().asString();
		JSONArray wrapper = new JSONArray("[" + jsonData + "]");
		return wrapper.getJSONObject(0);
	}

	private static String extractCookieValue(String cookieHeader, String cookieName) {
		if (cookieHeader == null || cookieName == null) {
			return null;
		}
		Pattern p = Pattern.compile(String.format(COOKIE_VALUE_PATTERN.pattern(), Pattern.quote(cookieName)));
		Matcher m = p.matcher(cookieHeader);
		if (!m.find()) {
			return null;
		}
		return m.group(2);
	}
}

