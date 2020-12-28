package services;

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class PetServiceRestAssuredTest {

    private static String baseUrl = "https://petstore.swagger.io/";

    @Test
    public void getPetsByStatusAvailableShouldOnlyContainAvailablePets(){

        given()
                .queryParam("status", "available").
        when()
                .get(baseUrl+"v2/pet/findByStatus").
        then()
                .assertThat()
                .statusCode(200)
                .body("status", hasItem("available"))
                .body("status", not(hasItem("sold")))
                .body("status", not(hasItem("pending")));
    }
}
