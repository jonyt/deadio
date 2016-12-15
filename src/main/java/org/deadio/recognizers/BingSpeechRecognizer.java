package org.deadio.recognizers;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;

/**
 * Created by yoni on 15/12/16.
 */
public class BingSpeechRecognizer implements SpeechRecognizer {
    private final String key;
    private final String instanceId;
    private String token;
    private JWT jwt;

    public BingSpeechRecognizer(String key, String instanceId){
        this.key = key;
        this.instanceId = instanceId;
    }

    public String recognize(File audioFilepath) throws Exception {
        HttpResponse<String> response = Unirest
                .post("https://speech.platform.bing.com/recognize")
                .header("Authorization", "Bearer " + getToken())
                .queryString("Version", "3.0")
                .queryString("requestid", UUID.randomUUID().toString())
                .queryString("appID", "D4D52672-91D7-4C74-8AD8-42B1D98141A5")
                .queryString("format", "json")
                .queryString("locale", "en-US")
                .queryString("scenarios", "ulm")
                .queryString("instanceid", instanceId)
                .queryString("device.os", "Linux")
                .body(Files.readAllBytes(audioFilepath.toPath()))
                .asString(); // When there's an error the response is returned as string, so we can't use asJson()

        if (response.getStatus() != 200)
            throw new Exception("Response error: " + response.getStatusText());

        RecognitionResult recognitionResult = parseResponse(response.getBody());
        if (!recognitionResult.isSuccess())
            throw new Exception("Request failed");

        return recognitionResult.getText();
    }

    private String getToken() throws Exception {
        Date now = new Date();
        if (jwt == null || now.after(jwt.getExpiresAt())){
            HttpResponse<String> response = Unirest
                    .post("https://api.cognitive.microsoft.com/sts/v1.0/issueToken")
                    .header("Ocp-Apim-Subscription-Key", key)
                    .asString();
            if (response.getStatus() != 200)
                throw new Exception("Response error: " + response.getStatusText());
            token = response.getBody();
            jwt = JWT.decode(token);
        }

        return token;
    }

    private RecognitionResult parseResponse(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode headerNode = rootNode.get("header");
        boolean isSuccess = headerNode.get("status").asBoolean();
        String text = headerNode.get("lexical").asText();

        return new RecognitionResult(isSuccess, text);
    }

    private class RecognitionResult {
        private final boolean isSuccess;
        private final String text;

        public RecognitionResult(boolean isSuccess, String text){
            this.isSuccess = isSuccess;
            this.text = text;
        }

        public boolean isSuccess(){
            return isSuccess;
        }

        public String getText(){
            return text;
        }
    }
}
