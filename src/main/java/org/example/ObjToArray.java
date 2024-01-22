package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ObjToArray {
    public static void ok() {
        String filePath = "work2.json";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONArray apps = new JSONArray(content.toString());

            try (FileWriter fileWriter = new FileWriter("output.json")) {
                fileWriter.write(apps.toString(4));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
