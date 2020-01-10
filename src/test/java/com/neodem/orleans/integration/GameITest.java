package com.neodem.orleans.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.original.model.OriginalGameState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/10/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameITest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void initShouldFailInitAGameWithNoPlayers() {
        String url = createURLWithPort("/game/init");
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        Map<String, String> params = new HashMap<>();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class, params);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    public void initShouldSucceed() throws JsonProcessingException {
        GameState state = send("/game/init", "playerNames", "Bob,Tony");
        assertThat(state).isNotNull();
    }

    private GameState send(String uri, String... params) throws JsonProcessingException {
        String url = createURLWithPort(uri);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        if (params != null) {
            for (int i = 0; i < params.length - 1; i++) {
                builder.queryParam(params[i], params[i + 1]);
            }
        }
        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        String json = response.getBody();
        JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);

        return new OriginalGameState(jsonNode);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
