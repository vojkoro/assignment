package net.vojko.paurus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TaxationResourceTest {

    @Test
    void testStrategy_traderId_1_generalRateTaxation() {
        var response = given().when().with().header("Content-Type", "application/json").body("""
                 {
                       "traderId": 1,
                       "playedAmount": 5,
                       "odd": 1.5
                }
                 """).post("/v1/taxation/calculate").then().statusCode(RestResponse.StatusCode.OK).extract().body()
                .as(JsonNode.class);

        assertThat(response.get("possibleReturnAmount").asDouble(), is(6.75));
        assertThat(response.get("possibleReturnAmountBefTax").asDouble(), is(7.5));
        assertThat(response.get("possibleReturnAmountAfterTax").asDouble(), is(6.75));
        assertThat(response.get("taxRate").asDouble(), is(0.1));
    }

    @Test
    void testStrategy_traderId_2_generalAmountTaxation() {
        var response = given().when().with().header("Content-Type", "application/json").body("""
                 {
                       "traderId": 2,
                       "playedAmount": 5,
                       "odd": 1.5
                }
                 """).post("/v1/taxation/calculate").then().statusCode(RestResponse.StatusCode.OK).extract().body()
                .as(JsonNode.class);

        assertThat(response.get("possibleReturnAmount").asDouble(), is(5.5));
        assertThat(response.get("possibleReturnAmountBefTax").asDouble(), is(7.5));
        assertThat(response.get("possibleReturnAmountAfterTax").asDouble(), is(5.5));
        assertThat(response.get("taxAmount").asDouble(), is(2.0));
    }

    @Test
    void testStrategy_traderId_2_generalAmountTaxation_invalidReturnAmount() {
        var response = given().when().with().header("Content-Type", "application/json").body("""
                 {
                       "traderId": 2,
                       "playedAmount": 1,
                       "odd": 1.5
                }
                 """).post("/v1/taxation/calculate").then().statusCode(RestResponse.StatusCode.BAD_REQUEST).extract()
                .body().as(JsonNode.class);

        assertThat(response.get("message").asText(), is("Negative result where positive is expected"));
    }

    @Test
    void testStrategy_traderId_3_winningsRateTaxation() {
        var response = given().when().with().header("Content-Type", "application/json").body("""
                 {
                       "traderId": 3,
                       "playedAmount": 5,
                       "odd": 1.5
                }
                 """).post("/v1/taxation/calculate").then().statusCode(RestResponse.StatusCode.OK).extract().body()
                .as(JsonNode.class);

        assertThat(response.get("possibleReturnAmount").asDouble(), is(7.25));
        assertThat(response.get("possibleReturnAmountBefTax").asDouble(), is(7.5));
        assertThat(response.get("possibleReturnAmountAfterTax").asDouble(), is(7.25));
        assertThat(response.get("taxRate").asDouble(), is(0.1));
    }

    @Test
    void testStrategy_traderId_4_winningsAmountTaxation() {
        var response = given().when().with().header("Content-Type", "application/json").body("""
                 {
                       "traderId": 4,
                       "playedAmount": 5,
                       "odd": 1.5
                }
                 """).post("/v1/taxation/calculate").then().statusCode(RestResponse.StatusCode.OK).extract().body()
                .as(JsonNode.class);

        assertThat(response.get("possibleReturnAmount").asDouble(), is(6.0));
        assertThat(response.get("possibleReturnAmountBefTax").asDouble(), is(7.5));
        assertThat(response.get("possibleReturnAmountAfterTax").asDouble(), is(6.0));
        assertThat(response.get("taxAmount").asDouble(), is(1.0));
    }

}
