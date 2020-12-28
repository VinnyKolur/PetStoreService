package services;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.GsonBuilder;
import enums.PetStatus;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import models.PetsResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class PetServiceTest {

    @Rule
    public WireMockRule wireMock = new WireMockRule(options().dynamicPort());

    private static String baseUrl = "https://petstore.swagger.io/";
    private PetService petService;

    private Boolean mockTest = false;
    private String mockBaseUrl = "http://localhost:";


    @Before
    public void setUp() {
        petService = new PetService(baseUrl);
    }

    @Test
    public void getPetsByAvailableStatusAndVerifyNumberOfDoggies() throws IOException, URISyntaxException {
        //Arrange
        int expectedStatusCode = 200;
        int expectedNumberOfDoggies = 143;

        mockTest = true;  //*****Switch to use WireMock*****
        if(mockTest) {
            stubFor(WireMock.get(urlPathEqualTo("/v2/pet/findByStatus"))
                    .willReturn(okJson(new String(Files.readAllBytes(Paths.get("src/test/resources/pets.json"))))));

            expectedNumberOfDoggies = 6;
            petService = new PetService(mockBaseUrl + wireMock.port());
        }

        //Act
        var response = petService.getPetsByStatus(PetStatus.AVAILABLE);

        //Assert
        assertEquals(expectedStatusCode, response.getStatusLine().getStatusCode());
        List<PetsResponse> pets = Arrays.asList(new GsonBuilder().create().fromJson(EntityUtils.toString(response.getEntity()), PetsResponse[].class));

                //Checks all pets in the result have the status available
        assertEquals("All pets should only have the status 'available'", pets.size(), pets.stream().filter(pet-> pet.getStatus().equalsIgnoreCase("available")).collect(Collectors.toList()).size());

                //Optional : there should be no pets with status other than 'available'
        assertEquals("The pets with status other than 'available' should be zero",0, pets.stream().filter(pet-> !pet.getStatus().equalsIgnoreCase("available")).collect(Collectors.toList()).size());

                //above we have made sure the pets are filtered by available, now count the doggies
        int actualNumberOfDoggies = pets.stream().filter(pet-> pet.getName().equalsIgnoreCase("doggie")).collect(Collectors.toList()).size();
        assertEquals(MessageFormat.format("Last recorded number of pets with the name doggie was {0} but now it is {1}",expectedNumberOfDoggies,  actualNumberOfDoggies) ,expectedNumberOfDoggies,actualNumberOfDoggies);

    }

    @Test
    @Parameters({"SOLD", "PENDING" })
    public void getPetsByNonAvailableStatusAndVerifyAvailablePetsAreExcluded(PetStatus status) throws IOException, URISyntaxException {
        //Arrange
        int expectedStatusCode = 200;

        //Act
        var response = petService.getPetsByStatus(status);

        //Assert
        assertEquals(expectedStatusCode, response.getStatusLine().getStatusCode());
        List<PetsResponse> pets = Arrays.asList(new GsonBuilder().create().fromJson(EntityUtils.toString(response.getEntity()), PetsResponse[].class));

        assertEquals("There should be zero pets with available status",0, pets.stream().filter(pet-> pet.getStatus().equalsIgnoreCase("available")).collect(Collectors.toList()).size());//, "There should be zero pets with available status");
    }

}
