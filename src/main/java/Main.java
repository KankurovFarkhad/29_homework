import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        System.out.print("Enter user name: ");
        String name = new Scanner(System.in).nextLine();

        JSONObject json = new JSONObject();
        json.put("name", name);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://dummy.restapiexample.com/api/v1/create"))
                .timeout(Duration.of(15, SECONDS))
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        HttpClient client = HttpClient.newBuilder().build();

        while (true) {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseCode = response.headers().firstValue("response").get();

            if (responseCode.equals("429")) {

                int retryAfter = Integer.parseInt(response.headers().firstValue("retry-after").get()) + 5;
                System.out.println(
                        "A reply with a status code was returned " + responseCode +
                        ". Expected to be resent in " + retryAfter  + " seconds..........."
                );
                Thread.sleep(retryAfter * 1000);

            } else {

                System.out.println(response.body());
                break;
            }

        }
    }
}
