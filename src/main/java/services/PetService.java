package services;

import enums.PetStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

public class PetService {

    private String baseUrl;
    private final String findByStatusPath = "/v2/pet/findByStatus";

    public PetService(String baseUrl){
        this.baseUrl = baseUrl;
    }

    public HttpResponse getPetsByStatus(PetStatus status) throws IOException, URISyntaxException {


        URIBuilder uriBuilder = new URIBuilder(baseUrl);
        uriBuilder
                .setPath(findByStatusPath)
                .addParameter("status", status.name().toLowerCase());

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        return httpClient.execute(httpGet);
    }
}
