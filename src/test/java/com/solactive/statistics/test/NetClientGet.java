package com.solactive.statistics.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solactive.model.Statistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetClientGet {

    public static void main(String[] args) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        for (int i = 0; i < 10000; i++) {
            URL url = new URL("http://localhost:8080/app/statistics");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output = "";
            String s;
            System.out.println("Output from Server .... \n");
            while ((s = br.readLine()) != null) {
                output = output + s;
            }

            Statistics statistics = objectMapper.readValue(output, Statistics.class);

            System.out.println("Statistics : " + statistics);

            conn.disconnect();


        }

    }

}