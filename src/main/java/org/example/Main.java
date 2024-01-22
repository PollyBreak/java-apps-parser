package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static int id = 0;

    public static void main(String[] args) {

        String filePath = "links.txt";
        List<String> links = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                links.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONArray apps = new JSONArray();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("work4.txt",
                true))) {
            for (String link : links) {  //links of pages to parse
                try {
                    Document document = Jsoup.connect(link)
                            .timeout(5000)
                            .get();
                    String str = document.select("script").get(0).toString();
                    str = likeJson(str); //removes all unnecessary from html and results in a json view
                    JSONObject jsonObject = convertToJson(str); //from Microsoft objects take attributes
                    // and create our own json objects
                    apps.put(jsonObject);
                    //write ready json to file
                    bufferedWriter.write(jsonObject.toString(4) + ",\n");

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
//                System.out.println("Parsing again");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        try (FileWriter fileWriter = new FileWriter("output.json")) {
            fileWriter.write(apps.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static JSONObject convertToJson(String string) {
        JSONObject jsonObject = new JSONObject(string);

        String title = jsonObject.optString("title");
        String description = jsonObject.optString("description");
        JSONObject productDetails = jsonObject.optJSONObject("productDetails");
        String category = productDetails.optString("categoryId");
        double price = productDetails.optDouble("price");
        String publisher = productDetails.optString("publisherName");
        JSONArray images = productDetails.optJSONArray("images");
        JSONObject requirements = productDetails.optJSONObject("systemRequirements");
        JSONObject minRequirements = requirements.optJSONObject("minimum");
        String cpu = "";
        String os = "";

        if (minRequirements != null) {
            JSONArray minRequirementsItems = minRequirements.optJSONArray("items");
            cpu = minRequirementsItems.optJSONObject(0).optString("description");
            os = minRequirementsItems.optJSONObject(1).optString("description");
        }

        Integer size = jsonObject.optInt("maxInstallSizeInBytes");
//        int i1 = size != null ? size : 0;
        String version = jsonObject.optString("version");
        int downloads = productDetails.optInt("ratingCount");
        Boolean availability = productDetails.optBoolean("isDownloadable");
//                getBoolean("isDownloadable");

        LoadAction loadAction = new LoadAction(availability);
        JSONObject loadActionJsonObject = new JSONObject();
        loadActionJsonObject.put("availability", loadAction.availability);
        loadActionJsonObject.put("region_exclusion", loadAction.regionsExclusion);
        loadActionJsonObject.put("host", loadAction.host);
        loadActionJsonObject.put("secret", loadAction.secret);


        Requirements requirements1 = new Requirements(cpu, os, size);
        JSONObject requirementsJsonObject = new JSONObject();
        requirementsJsonObject.put("os", requirements1.os);
        requirementsJsonObject.put("cpu", requirements1.cpu);
        requirementsJsonObject.put("gpu", requirements1.gpu);
        requirementsJsonObject.put("ram", requirements1.ram);
        requirementsJsonObject.put("size", requirements1.size);


        String[] images1;
        List<String> images2 = new ArrayList<>();
        for (int i = 0; i < images.length(); i++) {
            JSONObject imagesJSONObject = images.getJSONObject(i);
            String url = imagesJSONObject.getString("url");
            images2.add(url);
        }
        images1 = new String[images2.size()];
        for (int i = 0; i < images2.size(); i++) {
            images1[i] = images2.get(i);
        }

        id++;

        JSONObject newApp = new JSONObject();
        newApp.put("_id", id);
        newApp.put("title", title);
        newApp.put("rating", 0);
        newApp.put("description", description);
        newApp.put("category", category);
        newApp.put("price", price);
        newApp.put("publisher", publisher);
        newApp.put("images", images1);
        newApp.put("requirements", requirementsJsonObject);
        newApp.put("version", version);
        newApp.put("downloads", downloads);
        newApp.put("loadAction", loadActionJsonObject);

        return newApp;
    }

    public static String likeJson(String string) {
        string = string.replaceAll("<script>", "");
        string = string.replaceAll("</script>", "");
        string = string.replaceAll("window\\.storeViewModel\\s*=\\s*", "");
        string = string.trim();
        return string;
    }

}