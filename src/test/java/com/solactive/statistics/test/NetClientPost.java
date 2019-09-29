package com.solactive.statistics.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solactive.model.Tick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class NetClientPost {

    static Random r = new Random();

    static String[] instrumentIds = new String[]{"GOOGLE", "IBM.N", "MICROSOFT"};

    private static double getRandomValue() {
        return r.nextDouble();
    }

    private static String getRandomInstrumentId() {
        int index = r.nextInt(instrumentIds.length);
        return instrumentIds[index];
    }

    // http://localhost:8080/RESTfulExample/json/product/post
    public static void main(String[] args) throws IOException, InterruptedException {


        for (int i = 0; i < 100000; i++) {
            Thread.sleep(5);
            URL url = new URL("http://localhost:8080/app/tick");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            Tick tick = new Tick();
            tick.setInstrument(getRandomInstrumentId());
            tick.setPrice(getRandomValue());
            tick.setTimestamp(System.currentTimeMillis());
            ObjectMapper obj = new ObjectMapper();
            String s = obj.writeValueAsString(tick);


            OutputStream os = conn.getOutputStream();
            os.write(s.getBytes());
            os.flush();

/*
                if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }
*/

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();


        }
    }

}

