package org.example;

import java.util.Arrays;
import java.util.Random;

public class LoadAction {
    public boolean availability;


    public String[] regionsExclusion;
    public String host;
    public String secret;

    public LoadAction(boolean availability) {
        this.availability = availability;
        generateRegions();
        generateHost();
        generateSecret();
    }

    private void generateRegions(){
        int number = (int) (Math.random() * 3);
        String [] regions = new String[number];
        if (number > 0) {
            regions[0] = "North Korea";
        }
        if (number == 2) {
            regions[1] = "Algeria";
        }
        this.regionsExclusion = regions;
    }

    private void generateHost(){
        this.host = generateRandomString(30);
    }

    private void generateSecret(){
        this.secret = generateRandomString(13);
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }
        return randomString.toString();
    }

    @Override
    public String toString() {
        return "LoadAction{" +
                "availability=" + availability +
                ", regionsExclusion=" + Arrays.toString(regionsExclusion) +
                ", host='" + host + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }
}
