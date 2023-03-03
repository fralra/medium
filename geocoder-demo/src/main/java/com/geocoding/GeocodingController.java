package com.geocoding;


import java.util.Arrays;
import java.util.stream.Collectors;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.web.router.exceptions.UnsatisfiedQueryValueRouteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller("/geocode")
public class GeocodingController {
    
    private static Logger log = LoggerFactory.getLogger(GeocodingController.class);

    GeocodingClient geocodingClient;
  
    public GeocodingController(GeocodingClient geocodingClient) {
      this.geocodingClient = geocodingClient;
    }
  
    @Get("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDirects(@QueryValue String street, @QueryValue String city, 
                             @QueryValue String region, @QueryValue String zipcode, 
                             @QueryValue String country) {

      log.info("running {}", street+" "+city+" "+region+" "+zipcode+" "+country);
      return geocodingClient.query(street,city,region,zipcode, country);
    }
  
  
    @Error(global = true)
    public HttpResponse<JsonError> error(HttpRequest request, Throwable e) {
      log.error("processing exception {}", e.getMessage(), e);
      JsonError error = new JsonError(e.getMessage()).link(Link.SELF, Link.of(request.getUri()));
      if (e.getClass() == IllegalArgumentException.class || e.getClass() == UnsatisfiedQueryValueRouteException.class) {
        return HttpResponse.<JsonError>badRequest().body(error);
      }
      return HttpResponse.<JsonError>serverError().body(error);
    }


}
