package org.pingpong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

@Path("/ping")
public class PingPongResource {

    private WebTarget client = ClientBuilder.newClient()
            .property("http.receive.timeout", 30000L)
            .target("http://pingpong:8080");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String pingpong() {
        return new PingPongCommand(client).execute();
    }

}
