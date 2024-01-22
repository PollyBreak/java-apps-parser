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
    public static int id=0;
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

                }
                catch (IOException | JSONException e) {
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

    public static JSONObject convertToJson(String string){
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


        Requirements requirements1 = new Requirements(cpu,os, size);
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
        for (int i = 0; i<images2.size(); i++){
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
        newApp.put("requirements",requirementsJsonObject);
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


    public static String getExample() {
        return "{\n" +
                "  \"productDetails\": {\n" +
                "    \"previews\": [],\n" +
                "    \"subtitleNarratorText\": null,\n" +
                "    \"typeTag\": null,\n" +
                "    \"ratingCountFormatted\": \"26K\",\n" +
                "    \"iconUrl\": \"https://store-images.s-microsoft.com/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477\",\n" +
                "    \"pdpImageUrl\": \"https://store-images.s-microsoft.com/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477\",\n" +
                "    \"largePromotionImage\": null,\n" +
                "    \"posterArtUrl\": \"https://store-images.s-microsoft.com/image/apps.6876.14127333176902609.4e70d082-f75d-42e0-8cd1-46056ce8778f.1fe24d41-48a7-4b5f-a3f3-bf549de6e506\",\n" +
                "    \"boxArtUrl\": \"https://store-images.s-microsoft.com/image/apps.6876.14127333176902609.4e70d082-f75d-42e0-8cd1-46056ce8778f.1fe24d41-48a7-4b5f-a3f3-bf549de6e506\",\n" +
                "    \"iconUrlBackground\": \"transparent\",\n" +
                "    \"trailers\": [],\n" +
                "    \"screenshots\": [\n" +
                "      {\n" +
                "        \"imageType\": \"screenshot\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"Desktop/0\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.63781.14127333176902609.fa1c97a7-6019-4921-b6eb-d0f87dd6a68c.09c92d5c-0944-4630-b261-213a099e1b88\",\n" +
                "        \"height\": 1539,\n" +
                "        \"width\": 2736\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"screenshot\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"Desktop/1\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.18816.14127333176902609.fa1c97a7-6019-4921-b6eb-d0f87dd6a68c.bd65a784-12ae-49b0-bcba-b0442884208f\",\n" +
                "        \"height\": 1539,\n" +
                "        \"width\": 2736\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"screenshot\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"Desktop/2\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.20367.14127333176902609.fa1c97a7-6019-4921-b6eb-d0f87dd6a68c.0c0e496f-24d2-4d34-8dd5-f7a1c55bc4ff\",\n" +
                "        \"height\": 1539,\n" +
                "        \"width\": 2736\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"screenshot\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"Desktop/3\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.34300.14127333176902609.fa1c97a7-6019-4921-b6eb-d0f87dd6a68c.d9c1b18f-a60e-4ed8-ae2c-fca83ae5aef6\",\n" +
                "        \"height\": 1539,\n" +
                "        \"width\": 2736\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"screenshot\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"Desktop/4\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.8044.14127333176902609.fa1c97a7-6019-4921-b6eb-d0f87dd6a68c.dd5077c1-3228-4c6a-a95e-a635dbac7632\",\n" +
                "        \"height\": 1539,\n" +
                "        \"width\": 2736\n" +
                "      }\n" +
                "    ],\n" +
                "    \"encodedTitle\": \"itunes\",\n" +
                "    \"isMovie\": false,\n" +
                "    \"isApplication\": true,\n" +
                "    \"isGame\": false,\n" +
                "    \"isTvSeries\": false,\n" +
                "    \"isMoviesOrTVs\": false,\n" +
                "    \"isPwa\": false,\n" +
                "    \"isCoreGame\": false,\n" +
                "    \"supportsWindowsDesktop\": true,\n" +
                "    \"isAllowed\": false,\n" +
                "    \"isBrowsable\": true,\n" +
                "    \"isAd\": false,\n" +
                "    \"isPrimeVideo\": false,\n" +
                "    \"isSparkProduct\": false,\n" +
                "    \"isAndroid\": false,\n" +
                "    \"redirectUrl\": null,\n" +
                "    \"platforms\": [\n" +
                "      \"x86\",\n" +
                "      \"x64\"\n" +
                "    ],\n" +
                "    \"privacyUrl\": \"https://www.apple.com/legal/privacy/\",\n" +
                "    \"additionalTermLinks\": null,\n" +
                "    \"legalUrl\": null,\n" +
                "    \"accessible\": false,\n" +
                "    \"isDeviceCompanionApp\": false,\n" +
                "    \"supportUris\": [\n" +
                "      {\n" +
                "        \"uri\": \"https://support.apple.com/itunes/\",\n" +
                "        \"purpose\": null\n" +
                "      }\n" +
                "    ],\n" +
                "    \"features\": [],\n" +
                "    \"notes\": null,\n" +
                "    \"supportedLanguages\": [\n" +
                "      \"Arabic\",\n" +
                "      \"Catalan\",\n" +
                "      \"Chinese (Simplified)\",\n" +
                "      \"Chinese (Traditional)\",\n" +
                "      \"Chinese (Traditional, Hong Kong SAR)\",\n" +
                "      \"Croatian\",\n" +
                "      \"Czech\",\n" +
                "      \"Danish\",\n" +
                "      \"Dutch\",\n" +
                "      \"English (Australia)\",\n" +
                "      \"English (United Kingdom)\",\n" +
                "      \"English (United States)\",\n" +
                "      \"Finnish\",\n" +
                "      \"French\",\n" +
                "      \"French (Canada)\",\n" +
                "      \"German\",\n" +
                "      \"Greek\",\n" +
                "      \"Hebrew\",\n" +
                "      \"Hindi\",\n" +
                "      \"Hungarian\",\n" +
                "      \"Indonesian\",\n" +
                "      \"Italian\",\n" +
                "      \"Japanese\",\n" +
                "      \"Korean \",\n" +
                "      \"Malay\",\n" +
                "      \"Norwegian (Bokmål)\",\n" +
                "      \"Polish \",\n" +
                "      \"Portuguese (Brazil)\",\n" +
                "      \"Portuguese (Portugal)\",\n" +
                "      \"Romanian\",\n" +
                "      \"Russian\",\n" +
                "      \"Slovak\",\n" +
                "      \"Spanish (Latin America)\",\n" +
                "      \"Spanish (Spain)\",\n" +
                "      \"Swedish\",\n" +
                "      \"Thai\",\n" +
                "      \"Turkish\",\n" +
                "      \"Ukrainian\",\n" +
                "      \"Vietnamese\"\n" +
                "    ],\n" +
                "    \"publisherCopyrightInformation\": \"\",\n" +
                "    \"publisherAddress\": null,\n" +
                "    \"publisherPhoneNumber\": null,\n" +
                "    \"additionalLicenseTerms\": \"https://www.apple.com/legal/sla/docs/iTunesWindows.pdf\",\n" +
                "    \"appWebsiteUrl\": \"https://www.apple.com/itunes/\",\n" +
                "    \"productRatings\": [\n" +
                "      {\n" +
                "        \"ratingSystem\": \"Entertainment Software Rating Board\",\n" +
                "        \"ratingSystemShortName\": \"ESRB\",\n" +
                "        \"ratingSystemId\": \"ESRB\",\n" +
                "        \"ratingSystemUrl\": \"https://www.esrb.org/ratings-guide/\",\n" +
                "        \"ratingValue\": \"TEEN\",\n" +
                "        \"ratingId\": \"ESRB:T\",\n" +
                "        \"ratingValueLogoUrl\": \"https://store-images.microsoft.com/image/global.17268.image.4cc004ee-a56d-4f11-ae99-67a89379b743.13d51d69-d3ba-4760-8fdf-f996abafa50a\",\n" +
                "        \"ratingAge\": 13,\n" +
                "        \"restrictMetadata\": false,\n" +
                "        \"restrictPurchase\": false,\n" +
                "        \"ratingDescriptors\": [\n" +
                "          \"Diverse Content: Discretion Advised\"\n" +
                "        ],\n" +
                "        \"ratingDescriptorLogoUrls\": [],\n" +
                "        \"ratingDisclaimers\": [],\n" +
                "        \"interactiveElements\": [\n" +
                "          \"In-App Purchases\"\n" +
                "        ],\n" +
                "        \"longName\": \"TEEN\",\n" +
                "        \"shortName\": \"T\",\n" +
                "        \"description\": \"For ages 13 and up\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"requiredHardware\": null,\n" +
                "    \"recommendedHardware\": null,\n" +
                "    \"hardwareWarnings\": null,\n" +
                "    \"permissionsRequired\": [\n" +
                "      \"Uses all system resources\"\n" +
                "    ],\n" +
                "    \"packageAndDeviceCapabilities\": [\n" +
                "      \"runFullTrust\",\n" +
                "      \"Microsoft.storeFilter.core.notSupported_8wekyb3d8bbwe\"\n" +
                "    ],\n" +
                "    \"version\": \"\",\n" +
                "    \"lastUpdateDateUtc\": \"2023-12-14T21:41:15Z\",\n" +
                "    \"skus\": [\n" +
                "      {\n" +
                "        \"actions\": [\n" +
                "          \"Details\",\n" +
                "          \"Fulfill\",\n" +
                "          \"Purchase\",\n" +
                "          \"Browse\",\n" +
                "          \"Curate\",\n" +
                "          \"Redeem\"\n" +
                "        ],\n" +
                "        \"availabilityId\": \"B0SX201CMC8L\",\n" +
                "        \"skuType\": \"full\",\n" +
                "        \"price\": 0.0,\n" +
                "        \"displayPrice\": \"Free\",\n" +
                "        \"buyActionTitleOverride\": null,\n" +
                "        \"buyActionSubtitle\": null,\n" +
                "        \"currencyMismatch\": false,\n" +
                "        \"strikethroughPrice\": null,\n" +
                "        \"promoMessage\": null,\n" +
                "        \"promoEndDateUtc\": null,\n" +
                "        \"currencyCode\": \"USD\",\n" +
                "        \"currencySymbol\": \"$\",\n" +
                "        \"resourceSetId\": \"1\",\n" +
                "        \"isPaymentInstrumentRequired\": false,\n" +
                "        \"fulfillmentData\": \"{\\\"ProductId\\\":\\\"9PB2MZ1ZMB1S\\\",\\\"WuBundleId\\\":\\\"a15f155e-99ac-428e-b4e7-0b106fe0a5ef\\\",\\\"WuCategoryId\\\":\\\"7e8dd96b-6ed8-48d7-a6d9-c48a656183ed\\\",\\\"PackageFamilyName\\\":\\\"AppleInc.iTunes_nzyj5cx40ttqa\\\",\\\"SkuId\\\":\\\"0010\\\",\\\"Content\\\":null,\\\"PackageFeatures\\\":null}\",\n" +
                "        \"msaPurchaseType\": \"PurchaseActionMsaAllowBypass\",\n" +
                "        \"packageRequirements\": [\n" +
                "          {\n" +
                "            \"hardwareRequirements\": [],\n" +
                "            \"hardwareDependencies\": null,\n" +
                "            \"supportedArchitectures\": [\n" +
                "              \"x86\",\n" +
                "              \"x64\"\n" +
                "            ],\n" +
                "            \"platformDependencies\": [\n" +
                "              {\n" +
                "                \"platformName\": \"Windows.Desktop\",\n" +
                "                \"minVersion\": 2814750835277824,\n" +
                "                \"maxTested\": 2814751208898560\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ],\n" +
                "        \"timedTrialMessage\": null,\n" +
                "        \"remainingDaysInTrial\": 0,\n" +
                "        \"durationInSeconds\": null,\n" +
                "        \"hardwareRequirements\": [],\n" +
                "        \"hardwareWarnings\": [],\n" +
                "        \"operatingSystemType\": null,\n" +
                "        \"startDate\": \"1753-01-01T00:00:00Z\",\n" +
                "        \"endDate\": \"9998-12-30T00:00:00Z\",\n" +
                "        \"requiredEntitlementKeys\": null,\n" +
                "        \"contentIds\": null,\n" +
                "        \"upgradeToProductId\": null,\n" +
                "        \"unlockProductId\": null,\n" +
                "        \"eligibleUpgrades\": null,\n" +
                "        \"upsellLinks\": null,\n" +
                "        \"skuButtonTitle\": \"\",\n" +
                "        \"recurrencePolicyId\": null,\n" +
                "        \"recurrencePolicyTitle\": null,\n" +
                "        \"consumableQuantity\": null,\n" +
                "        \"availabilities\": [\n" +
                "          {\n" +
                "            \"availabilityId\": \"B0SX201CMC8L\",\n" +
                "            \"price\": 0.0,\n" +
                "            \"displayPrice\": \"Free\",\n" +
                "            \"recurrencePrice\": null,\n" +
                "            \"displayRecurrencePrice\": null,\n" +
                "            \"strikethroughPrice\": null,\n" +
                "            \"buyActionTitleOverride\": null,\n" +
                "            \"buyActionSubtitle\": null,\n" +
                "            \"currencyMismatch\": false,\n" +
                "            \"promoMessage\": null,\n" +
                "            \"remediations\": null,\n" +
                "            \"remediationRequired\": false,\n" +
                "            \"availabilityEndDate\": \"9998-12-30T00:00:00Z\",\n" +
                "            \"preorderReleaseDate\": \"0001-01-01T00:00:00Z\",\n" +
                "            \"displayRank\": 0,\n" +
                "            \"displayGroup\": null,\n" +
                "            \"affirmation\": null,\n" +
                "            \"conditions\": {\n" +
                "              \"endDate\": \"9998-12-30 00:00:00Z\",\n" +
                "              \"startDate\": \"1753-01-01 00:00:00Z\",\n" +
                "              \"resourceSetIds\": [\n" +
                "                \"1\"\n" +
                "              ],\n" +
                "              \"clientConditions\": {\n" +
                "                \"allowedPlatforms\": [\n" +
                "                  {\n" +
                "                    \"maxVersion\": 2147483647,\n" +
                "                    \"minVersion\": 0,\n" +
                "                    \"platformName\": \"Windows.Desktop\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            },\n" +
                "            \"actions\": [\n" +
                "              \"Details\",\n" +
                "              \"Fulfill\",\n" +
                "              \"Purchase\",\n" +
                "              \"Browse\",\n" +
                "              \"Curate\",\n" +
                "              \"Redeem\"\n" +
                "            ],\n" +
                "            \"isGamesWithGold\": false,\n" +
                "            \"promoMessageDescription\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"availabilityId\": \"9W2XV5XHP383\",\n" +
                "            \"price\": 0.0,\n" +
                "            \"displayPrice\": \"\",\n" +
                "            \"recurrencePrice\": null,\n" +
                "            \"displayRecurrencePrice\": null,\n" +
                "            \"strikethroughPrice\": \"\",\n" +
                "            \"buyActionTitleOverride\": null,\n" +
                "            \"buyActionSubtitle\": null,\n" +
                "            \"currencyMismatch\": false,\n" +
                "            \"promoMessage\": \"\",\n" +
                "            \"remediations\": null,\n" +
                "            \"remediationRequired\": false,\n" +
                "            \"availabilityEndDate\": \"9998-12-30T00:00:00Z\",\n" +
                "            \"preorderReleaseDate\": \"0001-01-01T00:00:00Z\",\n" +
                "            \"displayRank\": 1,\n" +
                "            \"displayGroup\": null,\n" +
                "            \"affirmation\": null,\n" +
                "            \"conditions\": {\n" +
                "              \"endDate\": \"9998-12-30 00:00:00Z\",\n" +
                "              \"startDate\": \"1753-01-01 00:00:00Z\",\n" +
                "              \"resourceSetIds\": [\n" +
                "                \"1\"\n" +
                "              ],\n" +
                "              \"clientConditions\": {\n" +
                "                \"allowedPlatforms\": [\n" +
                "                  {\n" +
                "                    \"maxVersion\": 2147483647,\n" +
                "                    \"minVersion\": 0,\n" +
                "                    \"platformName\": \"Windows.Desktop\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            },\n" +
                "            \"actions\": [\n" +
                "              \"License\",\n" +
                "              \"Browse\",\n" +
                "              \"Details\"\n" +
                "            ],\n" +
                "            \"isGamesWithGold\": false,\n" +
                "            \"promoMessageDescription\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"availabilityId\": \"9X4ZH89V3GZB\",\n" +
                "            \"price\": 0.0,\n" +
                "            \"displayPrice\": \"\",\n" +
                "            \"recurrencePrice\": null,\n" +
                "            \"displayRecurrencePrice\": null,\n" +
                "            \"strikethroughPrice\": \"\",\n" +
                "            \"buyActionTitleOverride\": null,\n" +
                "            \"buyActionSubtitle\": null,\n" +
                "            \"currencyMismatch\": false,\n" +
                "            \"promoMessage\": \"\",\n" +
                "            \"remediations\": null,\n" +
                "            \"remediationRequired\": false,\n" +
                "            \"availabilityEndDate\": \"9998-12-30T00:00:00Z\",\n" +
                "            \"preorderReleaseDate\": \"0001-01-01T00:00:00Z\",\n" +
                "            \"displayRank\": 2,\n" +
                "            \"displayGroup\": null,\n" +
                "            \"affirmation\": null,\n" +
                "            \"conditions\": {\n" +
                "              \"endDate\": \"9998-12-30 00:00:00Z\",\n" +
                "              \"startDate\": \"1753-01-01 00:00:00Z\",\n" +
                "              \"resourceSetIds\": [\n" +
                "                \"1\"\n" +
                "              ],\n" +
                "              \"clientConditions\": {\n" +
                "                \"allowedPlatforms\": [\n" +
                "                  {\n" +
                "                    \"maxVersion\": 2147483647,\n" +
                "                    \"minVersion\": 0,\n" +
                "                    \"platformName\": \"Windows.Mobile\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"maxVersion\": 2147483647,\n" +
                "                    \"minVersion\": 0,\n" +
                "                    \"platformName\": \"Windows.Team\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"maxVersion\": 2147483647,\n" +
                "                    \"minVersion\": 0,\n" +
                "                    \"platformName\": \"Windows.Xbox\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"maxVersion\": 2147483647,\n" +
                "                    \"minVersion\": 0,\n" +
                "                    \"platformName\": \"Windows.Holographic\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            },\n" +
                "            \"actions\": [\n" +
                "              \"License\",\n" +
                "              \"Details\"\n" +
                "            ],\n" +
                "            \"isGamesWithGold\": false,\n" +
                "            \"promoMessageDescription\": \"\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"bundledSkus\": null,\n" +
                "        \"isPreorder\": false,\n" +
                "        \"isRental\": false,\n" +
                "        \"rentalDurationInDays\": null,\n" +
                "        \"firstAvailableDate\": \"2018-04-26T20:00:00Z\",\n" +
                "        \"isRepurchasable\": false,\n" +
                "        \"skuInformationChart\": null,\n" +
                "        \"videoInstances\": null,\n" +
                "        \"skuId\": \"0010\",\n" +
                "        \"skuDisplayRanks\": [\n" +
                "          {\n" +
                "            \"dimension\": null,\n" +
                "            \"rank\": 0\n" +
                "          }\n" +
                "        ],\n" +
                "        \"skuTitle\": \"iTunes\",\n" +
                "        \"description\": \"iTunes is the easiest way to enjoy everything you need to be entertained - music, movies, and TV shows - and keep it all easily organized. Rent or buy movies, download your favorite TV shows, and more.\\n\\niTunes is also home to Apple Music, where you can listen to millions of songs and your entire music library - ad-free with zero commercials. Plus, download your favorite music to listen without Wi-Fi. Try it free with no commitment, and cancel anytime.\",\n" +
                "        \"images\": null,\n" +
                "        \"badges\": null,\n" +
                "        \"colorHexCode\": null,\n" +
                "        \"colorDisplayName\": null,\n" +
                "        \"msrp\": 0.0,\n" +
                "        \"displayMSRP\": \"Free\",\n" +
                "        \"salePrices\": [\n" +
                "          {\n" +
                "            \"conditions\": null,\n" +
                "            \"price\": 0.0,\n" +
                "            \"displayPrice\": \"Free\",\n" +
                "            \"badgeId\": \"default\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"includedWith\": null,\n" +
                "        \"streamingService\": null,\n" +
                "        \"jsonExtensionData\": {}\n" +
                "      }\n" +
                "    ],\n" +
                "    \"osProductInformation\": null,\n" +
                "    \"categoryId\": \"Music\",\n" +
                "    \"subcategoryId\": \"\",\n" +
                "    \"navItemId\": \"apps\",\n" +
                "    \"navId\": \"Apps\",\n" +
                "    \"addOnPriceRange\": null,\n" +
                "    \"recurrencePolicy\": null,\n" +
                "    \"deviceFamilyDisallowedReason\": null,\n" +
                "    \"builtFor\": \"Built for Windows 10\",\n" +
                "    \"revisionId\": \"2023-12-14T21:43:42.7659952Z\",\n" +
                "    \"pdpBackgroundColor\": \"#FFFFFF\",\n" +
                "    \"containsDownloadPackage\": true,\n" +
                "    \"systemRequirements\": {\n" +
                "      \"minimum\": {\n" +
                "        \"title\": \"Minimum\",\n" +
                "        \"items\": [\n" +
                "          {\n" +
                "            \"level\": 3,\n" +
                "            \"itemCode\": \"dvc\",\n" +
                "            \"name\": \"Available on\",\n" +
                "            \"description\": \"PC\",\n" +
                "            \"validationHint\": null,\n" +
                "            \"isValidationPassed\": true,\n" +
                "            \"helpLink\": null,\n" +
                "            \"helpTitle\": null,\n" +
                "            \"priority\": 1,\n" +
                "            \"jsonExtensionData\": {}\n" +
                "          },\n" +
                "          {\n" +
                "            \"level\": 3,\n" +
                "            \"itemCode\": \"ops\",\n" +
                "            \"name\": \"OS\",\n" +
                "            \"description\": \"Windows 10 version 16299.0 or higher\",\n" +
                "            \"validationHint\": \"required\",\n" +
                "            \"isValidationPassed\": true,\n" +
                "            \"helpLink\": null,\n" +
                "            \"helpTitle\": null,\n" +
                "            \"priority\": 1,\n" +
                "            \"jsonExtensionData\": {}\n" +
                "          },\n" +
                "          {\n" +
                "            \"level\": 3,\n" +
                "            \"itemCode\": \"X86\",\n" +
                "            \"name\": \"Architecture\",\n" +
                "            \"description\": \"x86, x64\",\n" +
                "            \"validationHint\": \"required\",\n" +
                "            \"isValidationPassed\": true,\n" +
                "            \"helpLink\": null,\n" +
                "            \"helpTitle\": null,\n" +
                "            \"priority\": 1,\n" +
                "            \"jsonExtensionData\": {}\n" +
                "          }\n" +
                "        ],\n" +
                "        \"emptySectionMessage\": null\n" +
                "      },\n" +
                "      \"recommended\": {\n" +
                "        \"title\": \"Recommended\",\n" +
                "        \"items\": [\n" +
                "          {\n" +
                "            \"level\": 3,\n" +
                "            \"itemCode\": \"dvc\",\n" +
                "            \"name\": \"Available on\",\n" +
                "            \"description\": \"PC\",\n" +
                "            \"validationHint\": null,\n" +
                "            \"isValidationPassed\": true,\n" +
                "            \"helpLink\": null,\n" +
                "            \"helpTitle\": null,\n" +
                "            \"priority\": 1,\n" +
                "            \"jsonExtensionData\": {}\n" +
                "          },\n" +
                "          {\n" +
                "            \"level\": 3,\n" +
                "            \"itemCode\": \"ops\",\n" +
                "            \"name\": \"OS\",\n" +
                "            \"description\": \"Windows 10 version 16299.0 or higher\",\n" +
                "            \"validationHint\": \"required\",\n" +
                "            \"isValidationPassed\": true,\n" +
                "            \"helpLink\": null,\n" +
                "            \"helpTitle\": null,\n" +
                "            \"priority\": 1,\n" +
                "            \"jsonExtensionData\": {}\n" +
                "          },\n" +
                "          {\n" +
                "            \"level\": 3,\n" +
                "            \"itemCode\": \"X86\",\n" +
                "            \"name\": \"Architecture\",\n" +
                "            \"description\": \"x86, x64\",\n" +
                "            \"validationHint\": \"required\",\n" +
                "            \"isValidationPassed\": true,\n" +
                "            \"helpLink\": null,\n" +
                "            \"helpTitle\": null,\n" +
                "            \"priority\": 1,\n" +
                "            \"jsonExtensionData\": {}\n" +
                "          }\n" +
                "        ],\n" +
                "        \"emptySectionMessage\": null\n" +
                "      }\n" +
                "    },\n" +
                "    \"keyIds\": [\n" +
                "      \"9572a6c0-2ecb-955f-3a4b-692272b2555e\"\n" +
                "    ],\n" +
                "    \"allowedPlatforms\": [\n" +
                "      \"Windows.Desktop\"\n" +
                "    ],\n" +
                "    \"xbox360ContentMediaId\": null,\n" +
                "    \"earlyAdopterEnrollmentUrl\": null,\n" +
                "    \"installationTerms\": \"Get this app while signed in to your Microsoft account and install on up to ten Windows devices.\",\n" +
                "    \"skuDisplayGroups\": null,\n" +
                "    \"xboxXpa\": false,\n" +
                "    \"relatedPackageIdentities\": null,\n" +
                "    \"primaryPackageIdentity\": null,\n" +
                "    \"detailsDisplayConfiguration\": {\n" +
                "      \"descriptionConfiguration\": null,\n" +
                "      \"ratingsAndReviewsConfiguration\": null,\n" +
                "      \"jsonExtensionData\": {}\n" +
                "    },\n" +
                "    \"ownershipType\": null,\n" +
                "    \"offerExpirationDate\": null,\n" +
                "    \"warningMessages\": [\n" +
                "      {\n" +
                "        \"header\": \"Seizure warnings\",\n" +
                "        \"body\": null,\n" +
                "        \"text\": \"Photosensitive seizure warning\",\n" +
                "        \"target\": \"https://support.xbox.com/xbox-one/console/photosensitive-seizure-warning\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"deviceQualified\": null,\n" +
                "    \"isMicrosoftProduct\": false,\n" +
                "    \"productFamilyLicenseTerms\": null,\n" +
                "    \"hasParentBundles\": false,\n" +
                "    \"hasAlternateEditions\": false,\n" +
                "    \"isLtidCompatible\": null,\n" +
                "    \"productPartD\": {\n" +
                "      \"pdp\": \"pdpapplayout1\",\n" +
                "      \"moduleTags\": [\n" +
                "        \"AppsDefaultTabs\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"videoProductType\": 0,\n" +
                "    \"isMsixvc\": false,\n" +
                "    \"position\": null,\n" +
                "    \"parentId\": null,\n" +
                "    \"categoryIds\": [\n" +
                "      \"Music\"\n" +
                "    ],\n" +
                "    \"mediaBadges\": null,\n" +
                "    \"isEligibleForMoviesAnywhere\": null,\n" +
                "    \"copyrightInformation\": null,\n" +
                "    \"installer\": {\n" +
                "      \"type\": 1,\n" +
                "      \"id\": \"9PB2MZ1ZMB1S\",\n" +
                "      \"extras\": null,\n" +
                "      \"installerErrorUrl\": null,\n" +
                "      \"installerErrors\": null\n" +
                "    },\n" +
                "    \"catalogSource\": \"BigCat\",\n" +
                "    \"webAppStartUrl\": null,\n" +
                "    \"categories\": [\n" +
                "      \"Music\"\n" +
                "    ],\n" +
                "    \"images\": [\n" +
                "      {\n" +
                "        \"imageType\": \"logo\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477\",\n" +
                "        \"height\": 300,\n" +
                "        \"width\": 300\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"BoxArt\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.6876.14127333176902609.4e70d082-f75d-42e0-8cd1-46056ce8778f.1fe24d41-48a7-4b5f-a3f3-bf549de6e506\",\n" +
                "        \"height\": 1080,\n" +
                "        \"width\": 1080\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"hero\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.17190.14127333176902609.89767926-caf9-4d6c-b8d6-1b7212c2aa8a.d65253a8-93d9-4f23-90ce-72103b1af6ea\",\n" +
                "        \"height\": 1080,\n" +
                "        \"width\": 1920\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"screenshot\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"Desktop/0\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.63781.14127333176902609.fa1c97a7-6019-4921-b6eb-d0f87dd6a68c.09c92d5c-0944-4630-b261-213a099e1b88\",\n" +
                "        \"height\": 1539,\n" +
                "        \"width\": 2736\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"screenshot\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"Desktop/1\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.18816.14127333176902609.fa1c97a7-6019-4921-b6eb-d0f87dd6a68c.bd65a784-12ae-49b0-bcba-b0442884208f\",\n" +
                "        \"height\": 1539,\n" +
                "        \"width\": 2736\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"screenshot\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"Desktop/2\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.20367.14127333176902609.fa1c97a7-6019-4921-b6eb-d0f87dd6a68c.0c0e496f-24d2-4d34-8dd5-f7a1c55bc4ff\",\n" +
                "        \"height\": 1539,\n" +
                "        \"width\": 2736\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"screenshot\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"Desktop/3\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.34300.14127333176902609.fa1c97a7-6019-4921-b6eb-d0f87dd6a68c.d9c1b18f-a60e-4ed8-ae2c-fca83ae5aef6\",\n" +
                "        \"height\": 1539,\n" +
                "        \"width\": 2736\n" +
                "      },\n" +
                "      {\n" +
                "        \"imageType\": \"screenshot\",\n" +
                "        \"backgroundColor\": \"\",\n" +
                "        \"foregroundColor\": \"\",\n" +
                "        \"caption\": \"\",\n" +
                "        \"imagePositionInfo\": \"Desktop/4\",\n" +
                "        \"productColor\": null,\n" +
                "        \"url\": \"https://store-images.s-microsoft.com/image/apps.8044.14127333176902609.fa1c97a7-6019-4921-b6eb-d0f87dd6a68c.dd5077c1-3228-4c6a-a95e-a635dbac7632\",\n" +
                "        \"height\": 1539,\n" +
                "        \"width\": 2736\n" +
                "      }\n" +
                "    ],\n" +
                "    \"productId\": \"9PB2MZ1ZMB1S\",\n" +
                "    \"title\": \"iTunes\",\n" +
                "    \"shortTitle\": \"iTunes\",\n" +
                "    \"subtitle\": \"Apple Inc.\",\n" +
                "    \"description\": \"iTunes is the easiest way to enjoy everything you need to be entertained - music, movies, and TV shows - and keep it all easily organized. Rent or buy movies, download your favorite TV shows, and more.\\n\\niTunes is also home to Apple Music, where you can listen to millions of songs and your entire music library - ad-free with zero commercials. Plus, download your favorite music to listen without Wi-Fi. Try it free with no commitment, and cancel anytime.\",\n" +
                "    \"shortDescription\": \"\",\n" +
                "    \"developerName\": \"\",\n" +
                "    \"publisherName\": \"Apple Inc.\",\n" +
                "    \"publisherId\": \"45897260\",\n" +
                "    \"isUniversal\": false,\n" +
                "    \"language\": \"en-us\",\n" +
                "    \"bgColor\": null,\n" +
                "    \"fgColor\": null,\n" +
                "    \"price\": 0.0,\n" +
                "    \"displayPrice\": \"Free\",\n" +
                "    \"strikethroughPrice\": null,\n" +
                "    \"recentLowestPriceMessage\": null,\n" +
                "    \"displayPricePrefix\": null,\n" +
                "    \"buyActionTitleOverride\": null,\n" +
                "    \"buyActionSubtitle\": null,\n" +
                "    \"currencyMismatch\": false,\n" +
                "    \"promoMessage\": null,\n" +
                "    \"promoEndDateUtc\": null,\n" +
                "    \"averageRating\": 2.5,\n" +
                "    \"ratingCount\": 26007,\n" +
                "    \"hasFreeTrial\": false,\n" +
                "    \"productType\": \"Application\",\n" +
                "    \"productFamilyName\": \"Apps\",\n" +
                "    \"mediaType\": \"Apps\",\n" +
                "    \"contentIds\": [\n" +
                "      \"9572a6c0-2ecb-955f-3a4b-692272b2555e\"\n" +
                "    ],\n" +
                "    \"packageFamilyNames\": [\n" +
                "      \"AppleInc.iTunes_nzyj5cx40ttqa\"\n" +
                "    ],\n" +
                "    \"recommendationReason\": null,\n" +
                "    \"releaseNotes\": null,\n" +
                "    \"subcategoryName\": \"\",\n" +
                "    \"alternateId\": null,\n" +
                "    \"alternateIds\": [\n" +
                "      {\n" +
                "        \"alternateIdType\": 2,\n" +
                "        \"alternateIdValue\": \"3341375f-cbac-4501-9ff5-2f9b9459fe79\",\n" +
                "        \"alternatedIdTypeString\": \"LegacyWindowsStoreProductId\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"alternateIdType\": 1,\n" +
                "        \"alternateIdValue\": \"6bac2737-f33c-4366-8f3a-68a0f452e95a\",\n" +
                "        \"alternatedIdTypeString\": \"LegacyWindowsPhoneProductId\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"alternateIdType\": 9,\n" +
                "        \"alternateIdValue\": \"1707222538\",\n" +
                "        \"alternatedIdTypeString\": \"XboxTitleId\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"curatedBGColor\": null,\n" +
                "    \"curatedFGColor\": null,\n" +
                "    \"curatedImageUrl\": null,\n" +
                "    \"curatedTitle\": null,\n" +
                "    \"curatedDescription\": null,\n" +
                "    \"doNotFilter\": null,\n" +
                "    \"collectionItemType\": \"Product\",\n" +
                "    \"curatedVideoUri\": null,\n" +
                "    \"creativeId\": null,\n" +
                "    \"payloadId\": null,\n" +
                "    \"contentType\": null,\n" +
                "    \"artistName\": null,\n" +
                "    \"artistId\": null,\n" +
                "    \"albumTitle\": null,\n" +
                "    \"albumProductId\": null,\n" +
                "    \"isExplicit\": null,\n" +
                "    \"numberOfSeasons\": 0,\n" +
                "    \"releaseDateUtc\": \"2018-04-26T20:00:00Z\",\n" +
                "    \"durationInSeconds\": 0,\n" +
                "    \"isCompatible\": true,\n" +
                "    \"isSoftBlocked\": null,\n" +
                "    \"isPurchaseEnabled\": true,\n" +
                "    \"incompatibleReason\": null,\n" +
                "    \"developerOptOutOfSDCardInstall\": true,\n" +
                "    \"hasAddOns\": true,\n" +
                "    \"hasThirdPartyIAPs\": true,\n" +
                "    \"externalUri\": null,\n" +
                "    \"autosuggestSubtitle\": null,\n" +
                "    \"merchandizedProductType\": null,\n" +
                "    \"voiceTitle\": \"\",\n" +
                "    \"phraseCustomPronunciation\": null,\n" +
                "    \"plaintextPassName\": null,\n" +
                "    \"glyphTextPassName\": null,\n" +
                "    \"subscriptionDiscountMessageTemplate\": null,\n" +
                "    \"capabilitiesTable\": [],\n" +
                "    \"capabilities\": [],\n" +
                "    \"hideFromCollections\": false,\n" +
                "    \"isDownloadable\": true,\n" +
                "    \"hideFromDownloadsAndUpdates\": false,\n" +
                "    \"incompatibleLink\": null,\n" +
                "    \"incompatibleLabel\": null,\n" +
                "    \"gamingOptionsXboxLive\": false,\n" +
                "    \"actionOverrides\": [\n" +
                "      {\n" +
                "        \"actionType\": \"InstallOnDevices\",\n" +
                "        \"cases\": [\n" +
                "          {\n" +
                "            \"conditions\": {\n" +
                "              \"classicAppKeys\": [],\n" +
                "              \"fullSkuLicenseSatisfied\": null,\n" +
                "              \"fullSkuOwned\": null,\n" +
                "              \"platform\": {\n" +
                "                \"maxVersion\": null,\n" +
                "                \"minVersion\": null,\n" +
                "                \"platformName\": \"Windows.Xbox\",\n" +
                "                \"architecture\": null\n" +
                "              }\n" +
                "            },\n" +
                "            \"visibility\": false,\n" +
                "            \"uri\": null,\n" +
                "            \"targetApplication\": null\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"actionType\": \"share\",\n" +
                "        \"cases\": [\n" +
                "          {\n" +
                "            \"conditions\": {\n" +
                "              \"classicAppKeys\": [],\n" +
                "              \"fullSkuLicenseSatisfied\": null,\n" +
                "              \"fullSkuOwned\": null,\n" +
                "              \"platform\": {\n" +
                "                \"maxVersion\": null,\n" +
                "                \"minVersion\": null,\n" +
                "                \"platformName\": \"Windows.Xbox\",\n" +
                "                \"architecture\": null\n" +
                "              }\n" +
                "            },\n" +
                "            \"visibility\": false,\n" +
                "            \"uri\": null,\n" +
                "            \"targetApplication\": null\n" +
                "          },\n" +
                "          {\n" +
                "            \"conditions\": {\n" +
                "              \"classicAppKeys\": [],\n" +
                "              \"fullSkuLicenseSatisfied\": null,\n" +
                "              \"fullSkuOwned\": null,\n" +
                "              \"platform\": {\n" +
                "                \"maxVersion\": null,\n" +
                "                \"minVersion\": null,\n" +
                "                \"platformName\": \"Windows.Holographic\",\n" +
                "                \"architecture\": null\n" +
                "              }\n" +
                "            },\n" +
                "            \"visibility\": false,\n" +
                "            \"uri\": null,\n" +
                "            \"targetApplication\": null\n" +
                "          },\n" +
                "          {\n" +
                "            \"conditions\": null,\n" +
                "            \"visibility\": true,\n" +
                "            \"uri\": \"https://www.microsoft.com/store/productId/{productId}?ocid=pdpshare\",\n" +
                "            \"targetApplication\": null\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"availableDevicesDisplayText\": \"\uEC4E\",\n" +
                "    \"availableDevicesNarratorText\": \"PC\",\n" +
                "    \"models\": null,\n" +
                "    \"capabilityXboxEnhanced\": null,\n" +
                "    \"badges\": [],\n" +
                "    \"catalogId\": null,\n" +
                "    \"hasParentBundle\": null,\n" +
                "    \"acquiredDateUtc\": null,\n" +
                "    \"approximateSizeInBytes\": 440812730,\n" +
                "    \"maxInstallSizeInBytes\": 525361152,\n" +
                "    \"bundleIds\": null,\n" +
                "    \"productActionsList\": null,\n" +
                "    \"skusSummary\": [\n" +
                "      {\n" +
                "        \"skuId\": \"0010\",\n" +
                "        \"skuDisplayRanks\": null,\n" +
                "        \"skuTitle\": null,\n" +
                "        \"description\": null,\n" +
                "        \"images\": null,\n" +
                "        \"badges\": null,\n" +
                "        \"colorHexCode\": null,\n" +
                "        \"colorDisplayName\": null,\n" +
                "        \"msrp\": 0.0,\n" +
                "        \"displayMSRP\": \"Free\",\n" +
                "        \"salePrices\": [\n" +
                "          {\n" +
                "            \"conditions\": null,\n" +
                "            \"price\": 0.0,\n" +
                "            \"displayPrice\": \"Free\",\n" +
                "            \"badgeId\": \"default\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"includedWith\": null,\n" +
                "        \"streamingService\": null,\n" +
                "        \"jsonExtensionData\": {}\n" +
                "      }\n" +
                "    ],\n" +
                "    \"colorPicker\": null,\n" +
                "    \"bundlePackageIdentities\": null,\n" +
                "    \"pcgaShortDescription\": null,\n" +
                "    \"pcgaTrailer\": null,\n" +
                "    \"pcgaMinimumUserAge\": null,\n" +
                "    \"isGamingAppOnly\": false,\n" +
                "    \"installerType\": null,\n" +
                "    \"appExtension\": null,\n" +
                "    \"supportsInstantGaming\": false,\n" +
                "    \"schema\": null,\n" +
                "    \"jsonExtensionData\": {}\n" +
                "  },\n" +
                "  \"averageRating\": 2.0,\n" +
                "  \"legacyWindowsPhoneProductId\": \"6bac2737-f33c-4366-8f3a-68a0f452e95a\",\n" +
                "  \"productImageUrl\": \"https://store-images.s-microsoft.com/image/apps.6876.14127333176902609.4e70d082-f75d-42e0-8cd1-46056ce8778f.1fe24d41-48a7-4b5f-a3f3-bf549de6e506\",\n" +
                "  \"preloadedImageSourceSets\": [\n" +
                "    {\n" +
                "      \"source\": \"https://store-images.s-microsoft.com:443/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477?h=210\",\n" +
                "      \"sourceSet\": \"https://store-images.s-microsoft.com/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477?h=210 1x, https://store-images.s-microsoft.com/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477?h=253 1.125x, https://store-images.s-microsoft.com/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477?h=307 1.375x, https://store-images.s-microsoft.com/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477?h=380 1.625x, https://store-images.s-microsoft.com/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477?h=464 1.875x, https://store-images.s-microsoft.com/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477?h=576 2.5x\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"chrome\": [\n" +
                "    \"Home\",\n" +
                "    \"Productivity\",\n" +
                "    \"Gaming\",\n" +
                "    \"Entertainment\",\n" +
                "    \"lob\"\n" +
                "  ],\n" +
                "  \"keywords\": \"Microsoft,Microsoft Store,applications,apps,Windows apps, Music\",\n" +
                "  \"description\": \"iTunes is the easiest way to enjoy everything you need to be entertained - music, movies, and TV shows - and keep it all easily organized. Rent or buy movies, download your favorite TV shows, and more.\\n\\niTunes is also home to Apple Music, where you can listen to millions of songs and your entire music library - ad-free with zero commercials. Plus, download your favorite music to listen without Wi-Fi. Try it free with no commitment, and cancel anytime.\",\n" +
                "  \"title\": \"iTunes - Official app in the Microsoft Store\",\n" +
                "  \"siteName\": \"Microsoft Apps\",\n" +
                "  \"marketDetails\": {\n" +
                "    \"market\": \"US\",\n" +
                "    \"tier\": 1\n" +
                "  },\n" +
                "  \"hl\": \"en-us\",\n" +
                "  \"gl\": \"US\",\n" +
                "  \"telemetry\": {\n" +
                "    \"appInsightsKey\": \"InstrumentationKey=842996e3-e971-4020-8066-87a5bcbc2b0e;IngestionEndpoint=https://centralus-2.in.applicationinsights.azure.com/;LiveEndpoint=https://centralus.livediagnostics.monitor.azure.com/\",\n" +
                "    \"adobeKey\": \"be61a02d4c674edfb65d61bd30fb65d5-ee4b86c1-d9a7-4f97-8d4b-124dd301b180-7729\",\n" +
                "    \"role\": \"web-store-browser\"\n" +
                "  },\n" +
                "  \"previewImageUrl\": \"https://store-images.s-microsoft.com/image/apps.40518.14127333176902609.7be7b901-15fe-4c27-863c-7c0dbfc26c5c.5c278f58-912b-4af9-88f8-a65fff2da477\",\n" +
                "  \"previewImageWidth\": 300,\n" +
                "  \"previewImageHeight\": 300,\n" +
                "  \"version\": \"2024.1.19.28\",\n" +
                "  \"environmentName\": \"Production\",\n" +
                "  \"supportedLocaleHashes\": {\n" +
                "    \"af-za\": \"24b33d6b26\",\n" +
                "    \"am-et\": \"e8c6e57a0c\",\n" +
                "    \"ar-sa\": \"8b38ea894e\",\n" +
                "    \"as-in\": \"a5df646683\",\n" +
                "    \"az-latn-az\": \"4e8ec0c9b4\",\n" +
                "    \"bg-bg\": \"bd4a028903\",\n" +
                "    \"bn-in\": \"1e058f300e\",\n" +
                "    \"bs-latn-ba\": \"8ce081a209\",\n" +
                "    \"ca-es\": \"b25efd3150\",\n" +
                "    \"ca-es-valencia\": \"d6cbcb1d29\",\n" +
                "    \"cs-cz\": \"e75f0ac53e\",\n" +
                "    \"cy-gb\": \"3da02e1a48\",\n" +
                "    \"da-dk\": \"36da5d7190\",\n" +
                "    \"de-de\": \"56fba3fcce\",\n" +
                "    \"el-gr\": \"21a0fcc48d\",\n" +
                "    \"en-gb\": \"1439c58725\",\n" +
                "    \"en-us\": \"faeb896f30\",\n" +
                "    \"es-es\": \"6845d49ce4\",\n" +
                "    \"es-mx\": \"326cfe1597\",\n" +
                "    \"et-ee\": \"11439a0d2d\",\n" +
                "    \"eu-es\": \"97163c00e1\",\n" +
                "    \"fa-ir\": \"2e4af6938e\",\n" +
                "    \"fi-fi\": \"98707cc2ee\",\n" +
                "    \"fil-ph\": \"36baf23a13\",\n" +
                "    \"fr-ca\": \"3f3f9a1138\",\n" +
                "    \"fr-fr\": \"3bc11c6ebf\",\n" +
                "    \"ga-ie\": \"a6c7e59fb1\",\n" +
                "    \"gd-gb\": \"846db35d01\",\n" +
                "    \"gl-es\": \"71b48b6cf3\",\n" +
                "    \"gu-in\": \"23c2ec654e\",\n" +
                "    \"he-il\": \"2c96bf118d\",\n" +
                "    \"hi-in\": \"01ae056e56\",\n" +
                "    \"hr-hr\": \"42de48a897\",\n" +
                "    \"hu-hu\": \"6f5f0f89f5\",\n" +
                "    \"hy-am\": \"8cfc7deb3e\",\n" +
                "    \"id-id\": \"6ba6482d10\",\n" +
                "    \"is-is\": \"8c8f2164dd\",\n" +
                "    \"it-it\": \"a34786f2bb\",\n" +
                "    \"ja-jp\": \"500ccfc7bb\",\n" +
                "    \"ka-ge\": \"ba27a3f1ab\",\n" +
                "    \"kk-kz\": \"a28cf7bb0a\",\n" +
                "    \"km-kh\": \"a2d5f8d0fa\",\n" +
                "    \"kn-in\": \"9c78414aaa\",\n" +
                "    \"ko-kr\": \"9c0ea0f039\",\n" +
                "    \"kok-in\": \"7f39a38e69\",\n" +
                "    \"lb-lu\": \"2185897f0b\",\n" +
                "    \"lo-la\": \"8bcfcbab02\",\n" +
                "    \"lt-lt\": \"5730946394\",\n" +
                "    \"lv-lv\": \"2e4e193ff5\",\n" +
                "    \"mi-nz\": \"26e77a9718\",\n" +
                "    \"mk-mk\": \"e3016ccd83\",\n" +
                "    \"ml-in\": \"b040fcec72\",\n" +
                "    \"mr-in\": \"54e0082fc6\",\n" +
                "    \"ms-my\": \"bb49d64ebb\",\n" +
                "    \"mt-mt\": \"19050a84cb\",\n" +
                "    \"nb-no\": \"bfac64e05a\",\n" +
                "    \"ne-np\": \"35d7c3c472\",\n" +
                "    \"nl-nl\": \"29e0152e47\",\n" +
                "    \"nn-no\": \"02764b1bc3\",\n" +
                "    \"or-in\": \"5767b38246\",\n" +
                "    \"pa-in\": \"ffb55a3af7\",\n" +
                "    \"pl-pl\": \"5328ab475d\",\n" +
                "    \"pt-br\": \"f1a1ff99f3\",\n" +
                "    \"pt-pt\": \"2c0698fb1c\",\n" +
                "    \"quz-pe\": \"0fa3e2d7fc\",\n" +
                "    \"ro-ro\": \"4e19d691ea\",\n" +
                "    \"ru-ru\": \"324e7e0035\",\n" +
                "    \"sk-sk\": \"4878636f34\",\n" +
                "    \"sl-si\": \"105265036a\",\n" +
                "    \"sq-al\": \"0d21345e61\",\n" +
                "    \"sr-cyrl-ba\": \"1c4293011c\",\n" +
                "    \"sr-cyrl-rs\": \"690e017dd2\",\n" +
                "    \"sr-latn-rs\": \"49c9a9b7f2\",\n" +
                "    \"sv-se\": \"4fbc78f771\",\n" +
                "    \"ta-in\": \"6a1a6d4824\",\n" +
                "    \"te-in\": \"2c5183fa0b\",\n" +
                "    \"th-th\": \"9820e86935\",\n" +
                "    \"tr-tr\": \"7e13810b73\",\n" +
                "    \"tt-ru\": \"24bd355ddb\",\n" +
                "    \"ug-cn\": \"97c6ec18f5\",\n" +
                "    \"uk-ua\": \"f71be911aa\",\n" +
                "    \"ur-pk\": \"15abe7009c\",\n" +
                "    \"uz-latn-uz\": \"8e24c1941f\",\n" +
                "    \"vi-vn\": \"dc294c739f\",\n" +
                "    \"zh-cn\": \"89ce5cbe2f\",\n" +
                "    \"zh-tw\": \"c530cc6b8c\"\n" +
                "  },\n" +
                "  \"localeStrings\": {\n" +
                "    \"BadgePage.AppID.Header\": \"App ID\",\n" +
                "    \"BadgePage.AppID.Invalid.Tooltip\": \"Product ID should be 12 characters, unless ID begins with XP, in which case it should be 14 characters.\",\n" +
                "    \"BadgePage.AppID.Tooltip\": \"The ID of your app. You can find this in Microsoft Partner Center, or as the end of the URL of your app on this site, e.g. https://apps.microsoft.com/store/detail/xbox/9MV0B5HZVK9Z\",\n" +
                "    \"BadgePage.ButtonAriaLabel\": \"{0} button\",\n" +
                "    \"BadgePage.CID.Header\": \"Campaign ID\",\n" +
                "    \"BadgePage.CID.Tooltip\": \"The campaign code of your app. You can find this in Microsoft Partner Center.\",\n" +
                "    \"BadgePage.Code.Tab\": \"Code Snippet\",\n" +
                "    \"BadgePage.Description\": \"The Microsoft Store app badge is the best way for you to display, direct, and track traffic from your assets to the Microsoft Store listing.\",\n" +
                "    \"BadgePage.Design.Tab\": \"Design Preview\",\n" +
                "    \"BadgePage.Interaction.Header\": \"Interaction Animation\",\n" +
                "    \"BadgePage.Interaction.Tooltip\": \"The badge animation on hover and pressed states.\",\n" +
                "    \"BadgePage.Language.Header\": \"Language\",\n" +
                "    \"BadgePage.Language.Tooltip\": \"Auto detect (recommended) renders the badge in the language of the user's browser. Alternately, choose a language to always render the badge in that language.\",\n" +
                "    \"BadgePage.Launch.Header\": \"Launch Mode\",\n" +
                "    \"BadgePage.Launch.Tooltip\": \"The appearance of the Store app when users click your app badge. Full mode launches the Store with your app's entire page loaded. Popup mode launches the Store in mini mode, showing just your app itself.\",\n" +
                "    \"BadgePage.NonJS.Tab\": \"Non-JavaScript Badge\",\n" +
                "    \"BadgePage.TabAriaLabel\": \"{0} tab\",\n" +
                "    \"BadgePage.TabSelectedAriaLabel\": \"Selected {0} tab\",\n" +
                "    \"BadgePage.Theme.Header\": \"Theme\",\n" +
                "    \"BadgePage.Theme.Tooltip\": \"Auto Mode will detect browser's settings and display inverted badge theme.\",\n" +
                "    \"BadgePage.Title\": \"Build your app badge\",\n" +
                "    \"Common.AppName\": \"Microsoft Apps\",\n" +
                "    \"Common.CollectionsTitle\": \"Collections\",\n" +
                "    \"Common.PageTitle\": \"{0} - Microsoft Apps\",\n" +
                "    \"Common.PageAppsTitle\": \"{0} - Official app in the Microsoft Store\",\n" +
                "    \"Common.PageGameTitle\": \"{0} - Official game in the Microsoft Store\",\n" +
                "    \"Common.PageMoviesTitle\": \"{0} - Buy, watch, or rent from the Microsoft Store\",\n" +
                "    \"Common.ScrollLeft.ButtonTitle\": \"Scroll left\",\n" +
                "    \"Common.ScrollRight.ButtonTitle\": \"Scroll right\",\n" +
                "    \"Common.ScrollToTop.ButtonTitle\": \"Scroll to top\",\n" +
                "    \"Common.ScrollToTop.ButtonTitle.comment\": \"The accessible title for the scroll to top button\",\n" +
                "    \"Common.SeeAll\": \"See all\",\n" +
                "    \"Common.SeeAllTitle\": \"See all {0}\",\n" +
                "    \"Common.SeeDetails\": \"See details\",\n" +
                "    \"Common.AdBadge\": \"Ad\",\n" +
                "    \"Common.Description\": \"Description\",\n" +
                "    \"Common.Ellipsis\": \"{0}...\",\n" +
                "    \"Common.LearnMore\": \"Learn more\",\n" +
                "    \"Common.SpecialOffers\": \"Special offers that won't last forever\",\n" +
                "    \"Common.UnableToConnect\": \"Unable to connect\",\n" +
                "    \"Common.UnableToConnectDetails\": \"There was a problem loading data. Refreshing the page might help.\",\n" +
                "    \"Common.IncludedWithOption\": \"Included with {0}\",\n" +
                "    \"Footer.ColumnHeaderWhatsNew\": \"What's New\",\n" +
                "    \"Footer.ColumnWhatsNewLinkMoreContent\": \"More content in the Microsoft Store\",\n" +
                "    \"Footer.ColumnWhatsNewLinkApps\": \"Apps\",\n" +
                "    \"Footer.ColumnWhatsNewLinkGames\": \"Games\",\n" +
                "    \"Footer.ColumnWhatsNewLinkMMTV\": \"Movies & TV\",\n" +
                "    \"Footer.ColumnWhatsNewLinkWinExpBlog\": \"Windows Experience Blog\",\n" +
                "    \"Footer.ColumnHeaderMSStore\": \"Microsoft Store\",\n" +
                "    \"Footer.ColumnMSStoreLinkAccountProfile\": \"Microsoft account\",\n" +
                "    \"Footer.ColumnMSStoreLinkSupport\": \"Microsoft Store support\",\n" +
                "    \"Footer.ColumnMSStoreLinkAccountReturns\": \"Returns\",\n" +
                "    \"Footer.ColumnMSStoreLinkFlexPay\": \"Flexible payments\",\n" +
                "    \"Footer.ColumnMSStoreLinkAccountStorePolicy\": \"Policies and Code of Conduct\",\n" +
                "    \"Footer.ColumnHeaderForDevelopers\": \"For Developers\",\n" +
                "    \"Footer.ColumnForDevsLinkPublishApp\": \"Publish your app\",\n" +
                "    \"Footer.ColumnForDevsLinkAdvWithUs\": \"Advertise with us\",\n" +
                "    \"Footer.ColumnForDevsLinkGenBadge\": \"Generate your app badge\",\n" +
                "    \"Footer.ColumnHeaderWindows\": \"Windows\",\n" +
                "    \"Footer.ColumnWindowsLinkCareers\": \"Careers\",\n" +
                "    \"Footer.ColumnWindowsLinkAbout\": \"About Microsoft\",\n" +
                "    \"Footer.ColumnWindowsLinkNews\": \"Company news\",\n" +
                "    \"Footer.ColumnWindowsLinkInvest\": \"Investors\",\n" +
                "    \"Footer.ColumnWindowsLinkDI\": \"Diversity & inclusion\",\n" +
                "    \"Footer.ColumnWindowsLinkAccess\": \"Accessibility\",\n" +
                "    \"Footer.ColumnWindowsLinkSustain\": \"Sustainability\",\n" +
                "    \"Footer.LegalNoticeLink\": \"Legal notices and consumer information\",\n" +
                "    \"Footer.PrivacyChoicesLink\": \"Your Privacy Choices\",\n" +
                "    \"Footer.PrivacyChoicesIcon\": \"An icon that represents privacy choices\",\n" +
                "    \"Footer.SecondaryMenuLinkPrivacy\": \"Privacy\",\n" +
                "    \"Footer.SecondaryMenuLinkTerms\": \"Terms of use\",\n" +
                "    \"Footer.SecondaryMenuLinkTrademarks\": \"Trademarks\",\n" +
                "    \"Footer.SecondaryMenuLinkSafety\": \"Safety & eco\",\n" +
                "    \"Footer.SecondaryMenuLinkRecycling\": \"Recycling\",\n" +
                "    \"Footer.SecondaryMenuLinkAds\": \"About our ads\",\n" +
                "    \"Footer.SecondaryMenuLinkContact\": \"Contact Microsoft\",\n" +
                "    \"Footer.SecondaryMenuLinkAPS\": \"APS\",\n" +
                "    \"Footer.SecondaryMenuLinkAustralianConsumerLaw\": \"Australian Consumer Law\",\n" +
                "    \"Footer.SecondaryMenuLinkCancelSubscription\": \"Cancel your subscription\",\n" +
                "    \"Footer.SecondaryMenuLinkManageOrCancelSubscription\": \"Manage or cancel subscription\",\n" +
                "    \"Footer.SecondaryMenuLinkEUComplianceDoCs\": \"EU Compliance DoCs\",\n" +
                "    \"Footer.MicrosoftCopyright\": \"© Microsoft 2023\",\n" +
                "    \"NavigationBar.Apps\": \"Apps\",\n" +
                "    \"NavigationBar.Games\": \"Games\",\n" +
                "    \"NavigationBar.Home\": \"Home\",\n" +
                "    \"NavigationBar.MoviesAndTV\": \"Movies & TV\",\n" +
                "    \"NavigationBar.Search\": \"Search\",\n" +
                "    \"NavigationBar.Cancel\": \"Cancel\",\n" +
                "    \"NavigationBar.OpenInStore\": \"Open Store app\",\n" +
                "    \"NavigationBar.SkipToMain\": \"Skip to main content\",\n" +
                "    \"ProductDetails.AdditionalInfo\": \"Additional information\",\n" +
                "    \"ProductDetails.AdditionalTerms\": \"Additional terms\",\n" +
                "    \"ProductDetails.AgeRating\": \"Age rating\",\n" +
                "    \"ProductDetails.AppBadge\": \"App badge\",\n" +
                "    \"ProductDetails.AppLicenseTerms\": \"License terms\",\n" +
                "    \"ProductDetails.AppPrivacyPolicy\": \"Privacy policy\",\n" +
                "    \"ProductDetails.AppSupport\": \"Support\",\n" +
                "    \"ProductDetails.AppWebsite\": \"Website\",\n" +
                "    \"ProductDetails.ApproximateSize\": \"Approximate size\",\n" +
                "    \"ProductDetails.Audio\": \"Audio\",\n" +
                "    \"ProductDetails.Average\": \"Average\",\n" +
                "    \"ProductDetails.Buy\": \"{0} Buy\",\n" +
                "    \"ProductDetails.BuyBoxFreeAriaLabel\": \"Install {0}\",\n" +
                "    \"ProductDetails.BuyBoxDownloadAriaLabel\": \"Download {0}\",\n" +
                "    \"ProductDetails.BuyBoxFreeAriaRole\": \"Install {0} link\",\n" +
                "    \"ProductDetails.BuyBoxPaidAriaLabel\": \"Purchase {0} for {1}\",\n" +
                "    \"ProductDetails.BuyBoxPaidAriaRole\": \"Purchase {0} link\",\n" +
                "    \"ProductDetails.BuyBoxSpecialAriaLabel\": \"Purchase {0} discounted from {1} for\",\n" +
                "    \"ProductDetails.BuyFrom\": \"Buy from {0}\",\n" +
                "    \"ProductDetails.BuyFrom.Comment\": \"The Buy from label represents the price to buy a movie or TV product and is displayed on our product price button. The product price button is displayed on the product details page; '{0}' represents the calculated product price.\",\n" +
                "    \"ProductDetails.SecondaryBuy\": \"Or buy from {0}\",\n" +
                "    \"ProductDetails.SecondaryBuy.Comment\": \"The Or buy from label represents a movie or TV products' buy price which is separate from rent price. This is only used if products also have a rent price, and the text will be displayed on the second row of the product price button. '{0}' represents the calculated product price.\",\n" +
                "    \"ProductDetails.Category\": \"Category\",\n" +
                "    \"ProductDetails.Capabilities\": \"This app can\",\n" +
                "    \"ProductDetails.CollectionProduct\": \"Product {0} of {1} selected\",\n" +
                "    \"ProductDetails.ConsolidateCategory\": \"+ {0}\",\n" +
                "    \"ProductDetails.CreateAppBadge\": \"Create app badge\",\n" +
                "    \"ProductDetails.DefaultImage.label\": \"Product image\",\n" +
                "    \"ProductDetails.DevelopedBy\": \"Developed by\",\n" +
                "    \"ProductDetails.Directors\": \"Directors\",\n" +
                "    \"ProductDetails.Duration\": \"Duration\",\n" +
                "    \"ProductDetails.FeaturesHeader\": \"Features\",\n" +
                "    \"ProductDetails.Free\": \"Free\",\n" +
                "    \"ProductDetails.Free.comment\": \"The free comparison string for the product details page.\",\n" +
                "    \"ProductDetails.FromPrice\": \"From {0}\",\n" +
                "    \"ProductDetails.SelectedDialogSlide\": \"Dialog carousel selected current slide: {0} out of {1}\",\n" +
                "    \"ProductDetails.SelectedSlide\": \"Current screenshot: {0} out of {1}\",\n" +
                "    \"ProductDetails.GeneralHeader\": \"{0}:\",\n" +
                "    \"ProductDetails.Genre\": \"Genre\",\n" +
                "    \"ProductDetails.Genres\": \"Genres\",\n" +
                "    \"ProductDetails.Get\": \"Get\",\n" +
                "    \"ProductDetails.Download\": \"Download\",\n" +
                "    \"ProductDetails.GetLogo.label\": \"Get logo image for button\",\n" +
                "    \"ProductDetails.ShowAll\": \"Show All\",\n" +
                "    \"ProductDetails.HasThirdPartyIAPs\": \"Offers in-app purchases\",\n" +
                "    \"ProductDetails.Included\": \"Included\",\n" +
                "    \"ProductDetails.Installation\": \"Installation\",\n" +
                "    \"ProductDetails.Install\": \"Install\",\n" +
                "    \"ProductDetails.MultipleLanguageAudio\": \"Multiple languages available\",\n" +
                "    \"ProductDetails.Networks\": \"Networks\",\n" +
                "    \"ProductDetails.OneLanguageAudio\": \"English audio\",\n" +
                "    \"ProductDetails.OrRegularPrice\": \"or\",\n" +
                "    \"ProductDetails.PeopleAlsoLike.Title\": \"Related apps\",\n" +
                "    \"ProductDetails.PeopleAlsoView\": \"People also view\",\n" +
                "    \"ProductDetails.PeopleCheckout\": \"You should also check out\",\n" +
                "    \"ProductDetails.PublishedBy\": \"Published by\",\n" +
                "    \"ProductDetails.PublisherInfo\": \"Publisher info\",\n" +
                "    \"ProductDetails.Ratings\": \"Ratings\",\n" +
                "    \"ProductDetails.RatingsAndReviews\": \"Ratings and reviews\",\n" +
                "    \"ProductDetails.RatingsFromAmazon\": \"Ratings from Amazon Appstore\",\n" +
                "    \"ProductDetails.RatingsCount\": \"{0} ratings\",\n" +
                "    \"ProductDetails.ReleaseDate\": \"Release date\",\n" +
                "    \"ProductDetails.ReleasedYear\": \"Released year\",\n" +
                "    \"ProductDetails.Rent\": \"Rent from {0}\",\n" +
                "    \"ProductDetails.LegalDisclaimer\": \"Legal Disclaimer\",\n" +
                "    \"ProductDetails.LegalDisclaimerNote\": \"This seller has certified that it will only offer products or services that comply with all applicable laws.\",\n" +
                "    \"ProductDetails.ReportIllegalContent\": \"Report this product for illegal content\",\n" +
                "    \"ProductDetails.IllegalContentTooltip\": \"Use the share button on this page to get the product link to report for illegal content\",\n" +
                "    \"ProductDetails.PotentialViolation\": \"Potential violation\",\n" +
                "    \"ProductDetails.ReportTextAreaLabel\": \"Tell us how you found the violation and any other info you think is useful.\",\n" +
                "    \"ProductDetails.Submit\": \"Submit\",\n" +
                "    \"ProductDetails.ReportThisProduct\": \"Report this product\",\n" +
                "    \"ProductDetails.ReportToMicrosoft\": \"Report this product for violating Microsoft Store Policy\",\n" +
                "    \"ProductDetails.ReportProductHeader\": \"Report this product to Microsoft\",\n" +
                "    \"ProductDetails.PermissionsInfo\": \"Permissions info\",\n" +
                "    \"ProductDetails.Screenshot\": \"Screenshot {0}\",\n" +
                "    \"ProductDetails.ScreenshotLabel\": \"Screenshots\",\n" +
                "    \"ProductDetails.ScreenshotCount\": \"{0} / {1}\",\n" +
                "    \"ProductDetails.SeeFromAmazon\": \"See all reviews from Amazon\",\n" +
                "    \"ProductDetails.SeizureWarning\": \"Seizure warning\",\n" +
                "    \"ProductDetails.SeizureWarningPhotosensitive\": \"Photosensitive seizure warning\",\n" +
                "    \"ProductDetails.Share\": \"Share\",\n" +
                "    \"ProductDetails.Trailer\": \"Trailer {0}\",\n" +
                "    \"ProductDetails.ReadLess\": \"Read less\",\n" +
                "    \"ProductDetails.ReadLessAriaLabel\": \"{0} read less\",\n" +
                "    \"ProductDetails.ReadLessAriaRole\": \"Read less button\",\n" +
                "    \"ProductDetails.ReadMore\": \"Read more\",\n" +
                "    \"ProductDetails.ReadMoreAriaLabel\": \"{0} read more\",\n" +
                "    \"ProductDetails.ReadMoreAriaRole\": \"Read more button\",\n" +
                "    \"ProductDetails.Size\": \"Size\",\n" +
                "    \"ProductDetails.Studio\": \"Studio\",\n" +
                "    \"ProductDetails.Subtitles\": \"Subtitles\",\n" +
                "    \"ProductDetails.SupportedLanguages\": \"Supported languages\",\n" +
                "    \"ProductDetails.SystemRequirementHeader\": \"System Requirements\",\n" +
                "    \"ProductDetails.TotalRatings\": \"Rated {0} out of 5.\",\n" +
                "    \"ProductDetails.TransactionTerms\": \"Terms of transaction\",\n" +
                "    \"ProductDetails.Writers\": \"Writers\",\n" +
                "    \"ProductDetails.PlayTrailer\": \"Play Trailer\",\n" +
                "    \"ProductDetails.Play\": \"Play Icon for Videos\",\n" +
                "    \"ProductDetails.ScreenshotTab\": \"Next Screenshot Button\",\n" +
                "    \"ProductDetails.MetaTitle\": \"Get {0} from the Microsoft Store\",\n" +
                "    \"ProductDetails.HourAbbreviation\": \" h \",\n" +
                "    \"ProductDetails.MinAbbreviation\": \" min\",\n" +
                "    \"ProductDetails.StreamOnService\": \"Stream on {0}\",\n" +
                "    \"ProductDetails.WithGamePass\": \"{0} with Game Pass\",\n" +
                "    \"ProductDetails.Year\": \"Year {0}.\",\n" +
                "    \"ProductDetails.ZeroRatings\": \"No ratings available.\",\n" +
                "    \"RatingReview.AriaLabel.Users\": \"Rated {0} out of {1} stars by {2} users.\",\n" +
                "    \"RatingReview.AriaLabel\": \"Rated {0} out of {1} stars.\",\n" +
                "    \"RatingReview.About\": \"About ratings & reviews\",\n" +
                "    \"RatingReview.AddAReview\": \"Add a review\",\n" +
                "    \"RatingReview.AriaMetaInfoForReview\": \"{0} rated this {1} stars on {2}\",\n" +
                "    \"RatingReview.Flag.label\": \"The flag logo to report current product review\",\n" +
                "    \"RatingReview.HighestRated\": \"Highest rated\",\n" +
                "    \"RatingReview.LowestRated\": \"Lowest rated\",\n" +
                "    \"RatingReview.MostHelpful\": \"Most helpful\",\n" +
                "    \"RatingReview.MostRecent\": \"Most recent\",\n" +
                "    \"RatingReview.NoReviewText\": \"There aren't any reviews yet.\",\n" +
                "    \"RatingReview.ReportIssueAriaLabel\": \"{0} report an issue\",\n" +
                "    \"RatingReview.ThumbsUp.label\": \"Thumbs up logo for product review module\",\n" +
                "    \"RatingReview.ThumbsDown.label\": \"Thumbs down logo for product review module\",\n" +
                "    \"Search.AnnounceSuggestion\": \"{0} suggestions available for {1}.\",\n" +
                "    \"Search.Placeholder\": \"Search apps, games, movies, and more\",\n" +
                "    \"Search.Filter.Button\": \"Filters\",\n" +
                "    \"Search.Filter.Reset\": \"Reset all\",\n" +
                "    \"Search.Filter.All\": \"All departments\",\n" +
                "    \"Search.Filter.Apps\": \"Apps\",\n" +
                "    \"Search.Filter.Games\": \"Games\",\n" +
                "    \"Search.Filter.Movies\": \"Movies\",\n" +
                "    \"Search.Filter.TV\": \"TV Shows\",\n" +
                "    \"Search.Filter.Devices\": \"Devices\",\n" +
                "    \"Search.Filter.Memberships\": \"Memberships\",\n" +
                "    \"Search.Filter.Fonts\": \"Fonts\",\n" +
                "    \"Search.Filter.Themes\": \"Themes\",\n" +
                "    \"Search.Filter.AllAges\": \"All ages\",\n" +
                "    \"Search.Filter.Three\": \"3 and under\",\n" +
                "    \"Search.Filter.AllPrices\": \"All types\",\n" +
                "    \"Search.Filter.Free\": \"Free\",\n" +
                "    \"Search.Filter.Paid\": \"Paid\",\n" +
                "    \"Search.Filter.Sale\": \"On Sale\",\n" +
                "    \"Search.Filter.AllCategories\": \"All categories\",\n" +
                "    \"Search.Filter.BooksAndReference\": \"Books & reference\",\n" +
                "    \"Search.Filter.Business\": \"Business\",\n" +
                "    \"Search.Filter.DeveloperTools\": \"Developer tools\",\n" +
                "    \"Search.Filter.Education\": \"Education\",\n" +
                "    \"Search.Filter.Entertainment\": \"Entertainment\",\n" +
                "    \"Search.Filter.FoodAndDining\": \"Food & dining\",\n" +
                "    \"Search.Filter.GovernmentAndPolitics\": \"Government & politics\",\n" +
                "    \"Search.Filter.HealthAndFitness\": \"Health & fitness\",\n" +
                "    \"Search.Filter.KidsAndFamily\": \"Kids & family\",\n" +
                "    \"Search.Filter.Lifestyle\": \"Lifestyle\",\n" +
                "    \"Search.Filter.Medical\": \"Medical\",\n" +
                "    \"Search.Filter.MultimediaDesign\": \"Multimedia design\",\n" +
                "    \"Search.Filter.Music\": \"Music\",\n" +
                "    \"Search.Filter.NavigationAndMaps\": \"Navigation & maps\",\n" +
                "    \"Search.Filter.NewsAndWeather\": \"News & weather\",\n" +
                "    \"Search.Filter.PersonalFinance\": \"Personal finance\",\n" +
                "    \"Search.Filter.Personalization\": \"Personalization\",\n" +
                "    \"Search.Filter.PhotoAndVideo\": \"Photo & video\",\n" +
                "    \"Search.Filter.Productivity\": \"Productivity\",\n" +
                "    \"Search.Filter.Security\": \"Security\",\n" +
                "    \"Search.Filter.Shopping\": \"Shopping\",\n" +
                "    \"Search.Filter.Social\": \"Social\",\n" +
                "    \"Search.Filter.Sports\": \"Sports\",\n" +
                "    \"Search.Filter.Travel\": \"Travel\",\n" +
                "    \"Search.Filter.UtilitiesAndTools\": \"Utilities & tools\",\n" +
                "    \"Search.Filter.ActionAndAdventure\": \"Action & adventure\",\n" +
                "    \"Search.Filter.CardAndBoard\": \"Card & board\",\n" +
                "    \"Search.Filter.Casino\": \"Casino\",\n" +
                "    \"Search.Filter.Classics\": \"Classics\",\n" +
                "    \"Search.Filter.Companion\": \"Companion\",\n" +
                "    \"Search.Filter.Educational\": \"Educational\",\n" +
                "    \"Search.Filter.FamilyAndKids\": \"Family & kids\",\n" +
                "    \"Search.Filter.Fighting\": \"Fighting\",\n" +
                "    \"Search.Filter.MultiplayerOnlineBattleArena\": \"Multi-Player Online Battle Arena\",\n" +
                "    \"Search.Filter.Other\": \"Other\",\n" +
                "    \"Search.Filter.Platformer\": \"Platformer\",\n" +
                "    \"Search.Filter.PuzzleAndTrivia\": \"Puzzle & trivia\",\n" +
                "    \"Search.Filter.RacingAndFlying\": \"Racing & flying\",\n" +
                "    \"Search.Filter.Roleplaying\": \"Role playing\",\n" +
                "    \"Search.Filter.Shooter\": \"Shooter\",\n" +
                "    \"Search.Filter.Simulation\": \"Simulation\",\n" +
                "    \"Search.Filter.Strategy\": \"Strategy\",\n" +
                "    \"Search.Filter.Tools\": \"Tools\",\n" +
                "    \"Search.Filter.Video\": \"Video\",\n" +
                "    \"Search.Filter.Word\": \"Word\",\n" +
                "    \"Search.Filter.ActionAdventure\": \"Action/Adventure\",\n" +
                "    \"Search.Filter.Animation\": \"Animation\",\n" +
                "    \"Search.Filter.Anime\": \"Anime\",\n" +
                "    \"Search.Filter.Comedy\": \"Comedy\",\n" +
                "    \"Search.Filter.Documentary\": \"Documentary\",\n" +
                "    \"Search.Filter.Drama\": \"Drama\",\n" +
                "    \"Search.Filter.Family\": \"Family\",\n" +
                "    \"Search.Filter.ForeignIndependent\": \"Foreign/Independent\",\n" +
                "    \"Search.Filter.Horror\": \"Horror\",\n" +
                "    \"Search.Filter.Romance\": \"Romance\",\n" +
                "    \"Search.Filter.RomanticComedy\": \"Romantic Comedy\",\n" +
                "    \"Search.Filter.SciFiFantasy\": \"Sci-Fi/Fantasy\",\n" +
                "    \"Search.Filter.ThrillerMystery\": \"Thriller/Mystery\",\n" +
                "    \"Search.Filter.TVMovies\": \"TV Movies\",\n" +
                "    \"Search.Filter.DocumentaryBio\": \"Documentary/Bio\",\n" +
                "    \"Search.Filter.FamilyChildren\": \"Family/Children\",\n" +
                "    \"Search.Filter.News\": \"News\",\n" +
                "    \"Search.Filter.RealityTV\": \"Reality TV\",\n" +
                "    \"Search.Filter.Soap\": \"Soap\",\n" +
                "    \"Search.Filter.Accessories\": \"Accessories\",\n" +
                "    \"Search.Filter.MicrosoftSurface\": \"Microsoft Surface\",\n" +
                "    \"Search.Filter.PCsAndTablets\": \"PCs & tablets\",\n" +
                "    \"Search.Filter.VirtualReality\": \"Virtual Reality\",\n" +
                "    \"Search.Filter.WindowsPhone\": \"Windows Phone\",\n" +
                "    \"Search.Filter.Xbox\": \"Xbox\",\n" +
                "    \"Search.Filter.ForHomeAndStudents\": \"For home and students\",\n" +
                "    \"Search.Filter.ForMac\": \"For Mac\",\n" +
                "    \"Search.Filter.ForSmallBusiness\": \"For small business\",\n" +
                "    \"Search.Filter.Office365\": \"Office 365\",\n" +
                "    \"Search.Filter.Office365Renewal\": \"Office 365 Renewal\",\n" +
                "    \"Search.Filter.OfficeApps\": \"Office Apps\",\n" +
                "    \"Search.Filter.XboxLive\": \"Xbox Live\",\n" +
                "    \"Search.Filter.AllSubscriptions\": \"All subscriptions\",\n" +
                "    \"Search.Filter.GamePass\": \"Game Pass\",\n" +
                "    \"Search.Header\": \"Results for \\\"{0}\\\"\",\n" +
                "    \"SystemRequirements.Minimum\": \"(Minimum)\",\n" +
                "    \"SystemRequirements.Minimum.comment\": \"The minimum requirements text for the system requirements module on the product details page\",\n" +
                "    \"SystemRequirements.Recommended\": \"(Recommended)\",\n" +
                "    \"SystemRequirements.Recommended.comment\": \"The recommended requirements text for the system requirements module on the product details page\",\n" +
                "    \"HomePage.Description\": \"Get apps for your Windows device\",\n" +
                "    \"TrendingApps.ViewAllApps\": \"View all trending apps\",\n" +
                "    \"TrendingApps.ViewAllGames\": \"View all trending games\",\n" +
                "    \"TrendingApps.ViewAllMovies\": \"View all trending movies\",\n" +
                "    \"TrendingApps.ViewMore\": \"View more\",\n" +
                "    \"TrendingApps.TopApps\": \"Top apps\",\n" +
                "    \"TrendingApps.TopGames\": \"Top games\",\n" +
                "    \"TrendingApps.TopMovies\": \"Top movies\",\n" +
                "    \"TrendingApps.TrendingTitle\": \"Trending this week\",\n" +
                "    \"TrendingApps.Apps\": \"Apps\",\n" +
                "    \"TrendingApps.Games\": \"Games\",\n" +
                "    \"TrendingApps.Movies\": \"Movies & TV\",\n" +
                "    \"GamesPage.ClassicGames\": \"Classics that never get old \",\n" +
                "    \"GamesPage.PCGames\": \"The best games on PC\",\n" +
                "    \"GamesPage.GamesForKids\": \"Fun games for kids\",\n" +
                "    \"GamesPage.GamerApps\": \"Overclock your skills \",\n" +
                "    \"MoviesPage.Stream\": \"Stream\",\n" +
                "    \"MoviesPage.DisneyMembership\": \"Stream the magic of Disney\",\n" +
                "    \"MoviesPage.FamilyMovies\": \"Movie nights with the kids\",\n" +
                "    \"MoviesPage.ActionAdventureMovies\": \"Experience the adrenaline rush\",\n" +
                "    \"MoviesPage.HorrorMovies\": \"Don't watch alone\",\n" +
                "    \"MoviesPage.DramaMovies\": \"The classics and more\",\n" +
                "    \"MoviesPage.ComedyMovies\": \"Laugh out loud\",\n" +
                "    \"MoviesPage.TopMovies\": \"Top movies\",\n" +
                "    \"MoviesPage.TopTVShows\": \"Top TV shows\",\n" +
                "    \"WideDetailsCard.WhatPeopleAreSaying\": \"What people are saying about it:\",\n" +
                "    \"Collection.Filter.AllProducts\": \"All products\",\n" +
                "    \"Collection.Filter.AllCategories\": \"All categories\",\n" +
                "    \"Collection.Filter.AllGenres\": \"All genres\",\n" +
                "    \"Collection.Filter.AllSubscriptions\": \"All subscriptions\",\n" +
                "    \"Collection.Filter.AnyPlayers\": \"Any number of players\",\n" +
                "    \"Collection.Filter.AllStudios\": \"All studios\",\n" +
                "    \"Collection.Filter.AllNetworks\": \"All networks\",\n" +
                "    \"Collection.Filter.Specials\": \"Specials\",\n" +
                "    \"Collection.Filter.SortByAriaLabel\": \"Sort by {0}\",\n" +
                "    \"Collection.Filter.SortByAriaLabel.comment\": \"Sort by aria label accessible name. Parameter 0 is for the filter title.\",\n" +
                "    \"Collection.ErrorTitle\": \"We couldn't load this collection.\",\n" +
                "    \"Collection.ErrorDetails\": \"It may have been deleted or it may be unavailable in your market.\",\n" +
                "    \"Meta.Description\": \"Get apps, games, and more for your Windows device\",\n" +
                "    \"RelatedProducts.Title\": \"Alternative to {0} in Windows\",\n" +
                "    \"RelatedProducts.Description\": \"List of products related to {0}.\",\n" +
                "    \"RelatedProducts.Keywords\": \"similar,related,install,download\",\n" +
                "    \"ReportContent.Offensive\": \"Offensive Content\",\n" +
                "    \"ReportContent.Malware\": \"Malware or virus\",\n" +
                "    \"ReportContent.Privacy\": \"Privacy concerns\",\n" +
                "    \"ReportContent.Misleading\": \"Misleading app\",\n" +
                "    \"ReportContent.Poor\": \"Poor performance\",\n" +
                "    \"ReportContent.Reason\": \"Select a reason\",\n" +
                "    \"Error.NotFound.Title\": \"Page not found\",\n" +
                "    \"Error.NotFound.Description\": \"We couldn't find the page you're looking for. It may have been removed or may be unavailable in your market.\",\n" +
                "    \"Error.BadRequest.Title\": \"Invalid request\",\n" +
                "    \"Error.BadRequest.Description\": \"We couldn't process this request.\",\n" +
                "    \"Error.InvalidProductId.Title\": \"Invalid product Id {0}\",\n" +
                "    \"Error.InvalidProductId.Description\": \"We couldn't process this request. Please make sure that product id is valid and try again.\",\n" +
                "    \"Error.ProductNotFound.Title\": \"Product not found\",\n" +
                "    \"Error.ProductNotFound.Description\": \"We couldn't find {0}. It may be unavailable in your market.\",\n" +
                "    \"Error.ProductDelisted.Title\": \"Product removed\",\n" +
                "    \"Error.ProductDelisted.Description\": \"We couldn't find {0}. It may have been removed from the Store.\",\n" +
                "    \"Error.SearchNotFound.Title\": \"No results for {0}\",\n" +
                "    \"Error.SearchNotFound.Description\": \"Try searching with different keywords\",\n" +
                "    \"Error.CollectionNotFound.Title\": \"Collection not found.\",\n" +
                "    \"Error.CollectionNotFound.Description\": \"We couldn't find collection {0}. It may have been deleted or it may be unavailable in your market.\",\n" +
                "    \"Error.Code\": \"Error Code: {0}\",\n" +
                "    \"Error.Search\": \"Search for this item\",\n" +
                "    \"Error.UnavailableInMarket.Title\": \"Store is not available in your country or region\"\n" +
                "  },\n" +
                "  \"localeStringUrl\": \"/localized-strings/en-us/strings.json?v=faeb896f30\",\n" +
                "  \"availableForIndexing\": true,\n" +
                "  \"defaultAlternateRoute\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S\",\n" +
                "  \"alternateRoutes\": {\n" +
                "    \"af-ZA\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=af-ZA&gl=US\",\n" +
                "    \"am-ET\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=am-ET&gl=US\",\n" +
                "    \"ar-SA\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ar-SA&gl=US\",\n" +
                "    \"as-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=as-IN&gl=US\",\n" +
                "    \"az-Latn-AZ\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=az-Latn-AZ&gl=US\",\n" +
                "    \"bg-BG\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=bg-BG&gl=US\",\n" +
                "    \"bn-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=bn-IN&gl=US\",\n" +
                "    \"bs-Latn-BA\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=bs-Latn-BA&gl=US\",\n" +
                "    \"ca-ES\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ca-ES&gl=US\",\n" +
                "    \"ca-Es-VALENCIA\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ca-Es-VALENCIA&gl=US\",\n" +
                "    \"cs-CZ\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=cs-CZ&gl=US\",\n" +
                "    \"cy-GB\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=cy-GB&gl=US\",\n" +
                "    \"da-DK\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=da-DK&gl=US\",\n" +
                "    \"de-DE\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=de-DE&gl=US\",\n" +
                "    \"el-GR\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=el-GR&gl=US\",\n" +
                "    \"en-GB\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=en-GB&gl=US\",\n" +
                "    \"en-US\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=en-US&gl=US\",\n" +
                "    \"es-ES\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=es-ES&gl=US\",\n" +
                "    \"es-MX\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=es-MX&gl=US\",\n" +
                "    \"et-EE\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=et-EE&gl=US\",\n" +
                "    \"eu-ES\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=eu-ES&gl=US\",\n" +
                "    \"fa-IR\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=fa-IR&gl=US\",\n" +
                "    \"fi-FI\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=fi-FI&gl=US\",\n" +
                "    \"fil-PH\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=fil-PH&gl=US\",\n" +
                "    \"fr-CA\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=fr-CA&gl=US\",\n" +
                "    \"fr-FR\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=fr-FR&gl=US\",\n" +
                "    \"ga-IE\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ga-IE&gl=US\",\n" +
                "    \"gd-GB\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=gd-GB&gl=US\",\n" +
                "    \"gl-ES\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=gl-ES&gl=US\",\n" +
                "    \"gu-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=gu-IN&gl=US\",\n" +
                "    \"he-IL\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=he-IL&gl=US\",\n" +
                "    \"hi-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=hi-IN&gl=US\",\n" +
                "    \"hr-HR\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=hr-HR&gl=US\",\n" +
                "    \"hu-HU\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=hu-HU&gl=US\",\n" +
                "    \"id-ID\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=id-ID&gl=US\",\n" +
                "    \"is-IS\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=is-IS&gl=US\",\n" +
                "    \"it-IT\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=it-IT&gl=US\",\n" +
                "    \"ja-JP\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ja-JP&gl=US\",\n" +
                "    \"ka-GE\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ka-GE&gl=US\",\n" +
                "    \"kk-KZ\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=kk-KZ&gl=US\",\n" +
                "    \"km-KH\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=km-KH&gl=US\",\n" +
                "    \"kn-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=kn-IN&gl=US\",\n" +
                "    \"kok-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=kok-IN&gl=US\",\n" +
                "    \"ko-KR\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ko-KR&gl=US\",\n" +
                "    \"lb-LU\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=lb-LU&gl=US\",\n" +
                "    \"lo-LA\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=lo-LA&gl=US\",\n" +
                "    \"lt-LT\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=lt-LT&gl=US\",\n" +
                "    \"lv-LV\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=lv-LV&gl=US\",\n" +
                "    \"mi-NZ\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=mi-NZ&gl=US\",\n" +
                "    \"mk-MK\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=mk-MK&gl=US\",\n" +
                "    \"ml-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ml-IN&gl=US\",\n" +
                "    \"mr-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=mr-IN&gl=US\",\n" +
                "    \"ms-MY\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ms-MY&gl=US\",\n" +
                "    \"mt-MT\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=mt-MT&gl=US\",\n" +
                "    \"nb-NO\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=nb-NO&gl=US\",\n" +
                "    \"ne-NP\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ne-NP&gl=US\",\n" +
                "    \"nl-NL\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=nl-NL&gl=US\",\n" +
                "    \"nn-NO\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=nn-NO&gl=US\",\n" +
                "    \"or-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=or-IN&gl=US\",\n" +
                "    \"pa-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=pa-IN&gl=US\",\n" +
                "    \"pl-PL\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=pl-PL&gl=US\",\n" +
                "    \"pt-BR\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=pt-BR&gl=US\",\n" +
                "    \"pt-PT\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=pt-PT&gl=US\",\n" +
                "    \"quz-PE\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=quz-PE&gl=US\",\n" +
                "    \"ro-RO\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ro-RO&gl=US\",\n" +
                "    \"ru-RU\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ru-RU&gl=US\",\n" +
                "    \"sk-SK\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=sk-SK&gl=US\",\n" +
                "    \"sl-sI\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=sl-sI&gl=US\",\n" +
                "    \"sq-AL\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=sq-AL&gl=US\",\n" +
                "    \"sr-Cyrl-BA\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=sr-Cyrl-BA&gl=US\",\n" +
                "    \"sr-Cyrl-RS\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=sr-Cyrl-RS&gl=US\",\n" +
                "    \"sr-Latn-RS\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=sr-Latn-RS&gl=US\",\n" +
                "    \"sv-SE\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=sv-SE&gl=US\",\n" +
                "    \"ta-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ta-IN&gl=US\",\n" +
                "    \"te-IN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=te-IN&gl=US\",\n" +
                "    \"th-TH\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=th-TH&gl=US\",\n" +
                "    \"tr-TR\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=tr-TR&gl=US\",\n" +
                "    \"tt-RU\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=tt-RU&gl=US\",\n" +
                "    \"ug-CN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ug-CN&gl=US\",\n" +
                "    \"uk-UA\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=uk-UA&gl=US\",\n" +
                "    \"ur-PK\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=ur-PK&gl=US\",\n" +
                "    \"vi-VN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=vi-VN&gl=US\",\n" +
                "    \"zh-CN\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=zh-CN&gl=US\",\n" +
                "    \"zh-TW\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=zh-TW&gl=US\"\n" +
                "  },\n" +
                "  \"canonicalRoute\": \"https://apps.microsoft.com/detail/9PB2MZ1ZMB1S?hl=en-US&gl=US\",\n" +
                "  \"baseUrl\": \"https://apps.microsoft.com\",\n" +
                "  \"languageDirection\": \"ltr\",\n" +
                "  \"psiProductIds\": [\n" +
                "    \"9P1J8S7CCWWT\",\n" +
                "    \"9PKTQ5699M62\",\n" +
                "    \"9NTM2QC6QWS7\",\n" +
                "    \"9NZVDKPMR9RD\",\n" +
                "    \"9WZDNCRFJ27N\",\n" +
                "    \"9PF4KZ2VN4W9\",\n" +
                "    \"9WZDNCRFHWLH\",\n" +
                "    \"9PFDF2ZD4Z4N\",\n" +
                "    \"9NBLGGH4Z1JC\",\n" +
                "    \"9NBLGGH4TWWG\"\n" +
                "  ],\n" +
                "  \"psiDownloadUrl\": \"https://get.microsoft.com/installer/download/\",\n" +
                "  \"isEmbargoedMarket\": false\n" +
                "};\n" +
                "\n" +
                "Process finished with exit code 0\n";
    }
}