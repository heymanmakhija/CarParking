package com.practise.parking.lot.misc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Test {

    public static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
        return now.format(formatter);
    }

    public static void main(String[] args) {
        String startUrl = "https://www.valuewala.com/";
        String websiteName = startUrl.split("//")[1].replace("/", "");
        System.out.println(" websiteName: " + websiteName);

        startUrl = "https://programmingworld.in/";
        websiteName = startUrl.split("//")[1].replace("/", "");
        System.out.println(" websiteName: " + websiteName);

        System.out.println(" getCurrentDateTime: " + getCurrentDateTime());
    }

}
