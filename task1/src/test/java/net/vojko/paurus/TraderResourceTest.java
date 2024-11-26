package net.vojko.paurus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TraderResourceTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    public void createTraderApi() {
        var response = given().when().with().header("Content-Type", "application/json").body("""
                {
                    "traderId": 5,
                    "taxationType": "GENERAL",
                    "taxationMethod": "RATE",
                    "taxationRate": 0.1
                }
                """).post("/v1/trader/").then().statusCode(RestResponse.StatusCode.CREATED).extract().body()
                .as(JsonNode.class);

        assertThat(response.get("id").asInt(), notNullValue());
        assertThat(response.get("traderId").asInt(), is(5));
        assertThat(response.get("taxationType").asText(), is("GENERAL"));
        assertThat(response.get("taxationMethod").asText(), is("RATE"));
        assertThat(response.get("taxationRate").asDouble(), is(0.1));
    }

    @Test
    public void createInvalidTrader_NoAmountApi() {
        var response = given().when().with().header("Content-Type", "application/json").body("""
                {
                    "traderId": 10,
                    "taxationType": "GENERAL",
                    "taxationMethod": "AMOUNT"
                }
                """).post("/v1/trader/").then().statusCode(RestResponse.StatusCode.BAD_REQUEST).extract().body()
                .as(JsonNode.class);

        assertThat(response.get("message").asText(), is(
                "createTrader.traderRequest.taxationAmount: Taxation amount is required for AMOUNT taxation method"));
    }

    @Test
    public void createInvalidTrader_NegativeAmout() {
        var response = given().when().with().header("Content-Type", "application/json").body("""
                {
                    "traderId": 10,
                    "taxationType": "GENERAL",
                    "taxationMethod": "AMOUNT",
                    "taxationAmount": -5.0
                }
                """).post("/v1/trader/").then().statusCode(RestResponse.StatusCode.BAD_REQUEST).extract().body()
                .as(JsonNode.class);

        assertThat(response.get("message").asText(),
                is("createTrader.traderRequest.taxationAmount: Taxation amount must be greater than 0"));
    }

    @Test
    public void deleteTrader() {
        var traderId = 9;
        given().when().with().delete("/v1/trader/" + traderId).then().statusCode(RestResponse.StatusCode.NO_CONTENT);
    }

    @Test
    public void getTrader_Found() {
        var traderId = 1;
        var response = given().when().with().get("/v1/trader/" + traderId).then().statusCode(RestResponse.StatusCode.OK)
                .extract().body().as(JsonNode.class);

        assertThat(response.get("id").asInt(), is(1));
        assertThat(response.get("traderId").asInt(), is(traderId));
        assertThat(response.get("taxationType").asText(), is("GENERAL"));
        assertThat(response.get("taxationMethod").asText(), is("RATE"));
        assertThat(response.get("taxationRate").asDouble(), is(0.1));
    }

    @Test
    public void getTrader_NotFound() {
        var response2 = given().when().with().get("/v1/trader/" + 9).then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND).extract().body().as(JsonNode.class);
        assertThat(response2.get("message").asText(), is("Trader not found"));

    }

    @Test
    public void updateTrader() throws JsonProcessingException {

        var traderId = 11;
        // create trader for update operations
        given().when().with().header("Content-Type", "application/json").body("""
                {
                    "traderId": 11,
                    "taxationType": "GENERAL",
                    "taxationMethod": "RATE",
                    "taxationRate": 0.1
                }
                """).post("/v1/trader/").then().statusCode(RestResponse.StatusCode.CREATED);

        var foundTraderResponse = given().when().with().get("/v1/trader/" + traderId).then()
                .statusCode(RestResponse.StatusCode.OK).extract().body().as(JsonNode.class);

        var expectedTraderResponse = objectMapper.readTree(String.format("""
                {
                            "id": %s,
                            "traderId": 11,
                            "taxationType": "GENERAL",
                            "taxationMethod": "RATE",
                            "taxationRate": 0.1
                        }""", foundTraderResponse.get("id").asInt()));

        assertThat(foundTraderResponse, is(expectedTraderResponse));

        var updatedTraderResponse = given().when().with().header("Content-Type", "application/json").body("""
                {
                    "traderId": 11,
                    "taxationType": "GENERAL",
                    "taxationMethod": "AMOUNT",
                    "taxationAmount": 5
                }
                """).put("/v1/trader/" + traderId).then().statusCode(RestResponse.StatusCode.OK).extract().body()
                .as(JsonNode.class);

        var expectedUpdatedTraderResponse = objectMapper.readTree(String.format("""
                {
                    "id": %s,
                    "traderId": 11,
                    "taxationType": "GENERAL",
                    "taxationMethod": "AMOUNT",
                    "taxationAmount": 5
                }""", foundTraderResponse.get("id").asInt()));

        assertThat(updatedTraderResponse, is(expectedUpdatedTraderResponse));
        assertThat(foundTraderResponse, not(expectedUpdatedTraderResponse));
    }
}
