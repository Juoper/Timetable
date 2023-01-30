package org.timeTable.TimeTableScraper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class WebScraper {

    private CookieManager cm;
    private HttpClient client;

    public WebScraper() throws IOException, InterruptedException {
        //https://nessa.webuntis.com/WebUntis/j_spring_security_check?school=gym-ottobrunn&j_username=q11&j_password=GO%23webuntis01&token=
        cm = new CookieManager();
        CookieHandler.setDefault(cm);
        client = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(cm.getCookieStore(), CookiePolicy.ACCEPT_ALL))
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        getBearerToken();

    }

    public String getTimetable(LocalDate date) throws IOException, InterruptedException {

        //https://nessa.webuntis.com/WebUntis/api/public/timetable/weekly/data?elementType=1&elementId=1052&date=
        List<HttpCookie> cookieList = cm.getCookieStore().getCookies();

        StringBuilder cookieHeader = new StringBuilder();
        for (HttpCookie cookie : cookieList) {
            cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        }


        Map<String, String> parameters = new HashMap<>();
        parameters.put("elementType", "1");
        parameters.put("elementId", "1052");
        parameters.put("date", date.toString());                          //LocalDate.now().toString()

        String params = getParamsString(parameters);

        String requestUrl = "https://nessa.webuntis.com/WebUntis/api/public/timetable/weekly/data?" + params;

        var request = HttpRequest.newBuilder(
                        URI.create(requestUrl))
                .header("accept", "*/*")
                .header("Cookie", "")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public void getBearerToken() throws IOException, InterruptedException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("school", "gym-ottobrunn");
        parameters.put("j_username", "q11");
        parameters.put("j_password", "GO#webuntis01");
        parameters.put("token", "");

        String params = getParamsString(parameters);
        String requestUrl = "https://nessa.webuntis.com/WebUntis/j_spring_security_check?" + params;

        var request = HttpRequest.newBuilder(
                        URI.create(requestUrl))
                .header("accept", "*/*")
                .header("Cookie", "")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        CookieStore cookieStore = cm.getCookieStore();
        List<HttpCookie> cookieList = cookieStore.getCookies();

        StringBuilder cookieHeader = new StringBuilder();
        for (HttpCookie cookie : cookieList) {
            cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        }

        requestUrl = "https://nessa.webuntis.com/WebUntis/api/token/new";

        request = HttpRequest.newBuilder(
                        URI.create(requestUrl))
                .header("Content-Type", "application/text")
                .header("Cookie", cookieHeader.toString())
                .build();


        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String bearerToken = response.body();
    }

    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}
