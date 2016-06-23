package org.pingpong;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class PingPongCommand extends HystrixCommand<String> {

    private WebTarget client;

    public PingPongCommand(WebTarget client) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("PingPong"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                    .withExecutionTimeoutInMilliseconds(10000)));
        this.client = client;
    }

    @Override
    protected String run() throws Exception {
        try {
            System.out.println("Starting Time: " + System.currentTimeMillis());
            return client.path("/").request(MediaType.APPLICATION_JSON).get(String.class);
        } finally {
            System.out.println("Finish Time: " + System.currentTimeMillis());
        }
    }
}
