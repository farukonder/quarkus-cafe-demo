package com.redhat.quarkus.cafe.web.infrastructure;

import com.redhat.quarkus.cafe.web.domain.CreateOrderCommand;
import com.redhat.quarkus.cafe.web.domain.DashboardUpdate;
import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.vertx.core.eventbus.EventBus;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Path("/api")
public class RestResource {

    @Inject
    EventBus bus;

    @Inject
    @RestClient
    OrderService orderService;

    Jsonb jsonb = JsonbBuilder.create();

    @POST
    @Path("/order")
    public Response orderIn(CreateOrderCommand createOrderCommand) {

        System.out.println("\norder in\n");
        System.out.println("\n"+ createOrderCommand +"\n");
        orderService.orderIn(createOrderCommand);
        System.out.println("\nsent\n");
        return Response.accepted().entity(createOrderCommand).build();
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDashboard(List<DashboardUpdate> dashboardUpdates) {

        System.out.println("updates received");
        dashboardUpdates.forEach( dashboardUpdate -> {
            bus.<String>send("updates", jsonb.toJson(dashboardUpdate).toString());
        });
        return Response.ok().build();
    }

}
