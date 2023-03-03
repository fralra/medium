package com.geocoding;

import java.net.URI;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.micronaut.http.HttpRequest.POST;

@Singleton
public class GeocodingClient {

    private static Logger log = LoggerFactory.getLogger(GeocodingClient.class);

    HttpClient httpClient;

    public GeocodingClient(@Client("${oracle.map-server.url}") HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String query(String street, String city, String region, String zipcode, String country) {

        URI uri = UriBuilder.of("/elocation/lbs")
                // .queryParam("address", address)
                .build();
        String data = "xml_request=<?xml version=\"1.0\" standalone=\"yes\"?>"
                + "<geocode_request vendor=\"elocation\">"
                + "  <address_list>"
                + "    <input_location id=\"0\">"
                + "      <input_address match_mode=\"relax_street_type\">"
                + "        <gen_form country=\"" + country + "\" street=\"" + street + "\" region=\"" + region + "\""
                + "            postal_code=\"" + zipcode + "\" city=\"" + city + "\"/>"
                + "      </input_address>"
                + "    </input_location>"
                + "  </address_list>"
                + "</geocode_request>&format=JSON";

        try {
            log.info("POST {}", uri);
            HttpResponse<String> response = httpClient.toBlocking().exchange(
                    POST(uri, data)
                            .contentType("application/x-www-form-urlencoded")
                            .accept(MediaType.APPLICATION_JSON),
                    String.class);
            log.info("received response - reading body");
            return response.getBody()
                    .orElseThrow(() -> new InternalError("response body could not be converted to string"));
        } catch (HttpClientResponseException e) {
            Object body = e.getResponse().body();
            if (e.getStatus() == HttpStatus.FORBIDDEN || e.getStatus() == HttpStatus.UNAUTHORIZED) {
                log.info("hit 401, invalidating cookie and retrying");
            }
            if (e.getStatus() == HttpStatus.BAD_REQUEST) {
                throw new IllegalArgumentException(body != null ? body.toString() : "bad request");
            }
            log.warn("address failed. Response = {}", body);
            throw new InternalError("address failed");
        }
    }

}
