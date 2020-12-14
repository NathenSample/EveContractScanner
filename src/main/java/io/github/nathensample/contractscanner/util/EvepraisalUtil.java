package io.github.nathensample.contractscanner.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import io.github.nathensample.contractscanner.model.evepraisal.EvepraisalResponse;
import io.gsonfire.GsonFireBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class EvepraisalUtil {

    private static final String BASE_STRING = "https://evepraisal.com/appraisal.json?market=jita&raw_textarea=";
    public EvepraisalResponse submitEvePraisal(String body)
    {
        try {
            body = URLEncoder.encode(body, StandardCharsets.UTF_8.toString());
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody reqbody = RequestBody.create(null, new byte[0]);
            Request request = new Request.Builder()
                    .url(BASE_STRING + body)
                    .post(reqbody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            JsonReader jsonReader = new JsonReader(new StringReader(response.body().string()));
            jsonReader.setLenient(true);
            GsonFireBuilder fireBuilder = new GsonFireBuilder();
            GsonBuilder builder = fireBuilder.createGsonBuilder();
            Gson gson = builder.create();
            return gson.fromJson(jsonReader, new TypeToken<EvepraisalResponse>(){}.getType());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //Never gonna happen
            //TODO: add a log for the sake of it
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: do something reasonable
        }
        //TODO: null is evil.
        return null;
    }
}
