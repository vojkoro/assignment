package net.vojko.paurus.resources;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.vojko.paurus.models.TraderRequest;
import net.vojko.paurus.services.TraderService;

@Path("/v1/trader")
public class TraderResource {

    @Inject
    TraderService traderService;

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTrader(@Valid TraderRequest traderRequest) {
        var trader = traderService.createTrader(traderRequest);

        return Response.created(null).entity(trader).build(); // maybe we can return 201 CREATED
    }

    @PUT
    @Path("/{traderId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putTracer(@PathParam("traderId") Long traderId, @Valid TraderRequest traderUpdate) {
        var trader = traderService.updateTrader(traderId, traderUpdate);
        return Response.ok(trader).build();
    }

    @GET
    @Path("/{traderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTraderByTraderId(@PathParam("traderId") Long traderId) {
        var trader = traderService.getTraderById(traderId);
        return Response.ok(trader).build();
    }

    @DELETE
    @Path("/{traderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTraderByTraderId(@PathParam("traderId") Long traderId) {
        traderService.deleteTrader(traderId);
        return Response.noContent().build();
    }
}
