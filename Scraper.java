import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Scraper {
    public static HttpResponse<String> scrapeWebsite(String link) throws Exception {
        // client
        HttpClient client = HttpClient.newHttpClient();

        // req
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .build();

        // response
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
