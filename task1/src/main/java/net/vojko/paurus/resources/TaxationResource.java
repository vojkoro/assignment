package net.vojko.paurus.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.vojko.paurus.models.Bet;
import net.vojko.paurus.models.TaxationRequest;
import net.vojko.paurus.services.TaxService;

@Path("/v1/taxation")
public class TaxationResource {

    @Inject
    TaxService taxService;

    @POST
    @Path("/calculate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTaxResponse(TaxationRequest taxationRequest) {
        var bet = new Bet(taxationRequest.playedAmount(), taxationRequest.odd());
        var potentialPayout = taxService.calculateTax(taxationRequest.traderId(), bet);
        return Response.ok(potentialPayout).build();
    }
}
