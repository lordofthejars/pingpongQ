package org.pingpong;

import org.arquillian.cube.HostIp;
import org.arquillian.cube.q.api.NetworkChaos;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.arquillian.cube.q.api.NetworkChaos.JitterType.jitter;
import static org.arquillian.cube.q.api.NetworkChaos.LatencyType.latencyInMillis;
import static org.arquillian.cube.q.api.NetworkChaos.ToxicityType.toxicity;
import static org.arquillian.cube.q.api.Q.DurationRunCondition.during;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class PingPongTest {

    @HostIp
    private String ip;

    @ArquillianResource
    private NetworkChaos networkChaos;

    @Test
    public void shouldPingPong() throws Exception {
        {
            //without Q
            URL url = new URL("http://" + ip + ":" + 8081 + "/pingpong/ping");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            assertThat(response.toString(), is("{  \"status\": \"OK\"}"));
        }

        {
            // With Q and no toxicity
            networkChaos.on("pingpong", 8080).exec(during(15, TimeUnit.SECONDS), () -> {
                URL url = new URL("http://" + ip + ":" + 8081 + "/pingpong/ping");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                assertThat(response.toString(), is("{  \"status\": \"OK\"}"));
            });
        }

        {
            // With Q and latency
            networkChaos.on("pingpong", 8080).latency(latencyInMillis(500), jitter(0),
                    toxicity(0.5f), NetworkChaos.ToxicDirectionStream.DOWNSTREAM)
                    .exec(during(15, TimeUnit.SECONDS), () -> {
                        URL url = new URL("http://" + ip + ":" + 8081 + "/pingpong/ping");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        assertThat(response.toString(), is("{  \"status\": \"OK\"}"));
                    });
        }

        {
            networkChaos.on("pingpong", 8080).down().exec(during(10, TimeUnit.SECONDS), () -> {
                URL url = new URL("http://" + ip + ":" + 8081 + "/pingpong/ping");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                try {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();
                    assertThat(response.toString(), is("{  \"status\": \"OK\"}"));
                } catch (Exception e) {
                }
            });
        }

    }

}
