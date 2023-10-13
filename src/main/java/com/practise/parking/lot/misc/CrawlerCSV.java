package com.practise.parking.lot.misc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CrawlerCSV {

    private final Set<String> visitedUrls;
    private final Set<String> internalUrls;
    private final HashMap<String, String> linksSource;

    public CrawlerCSV() {
        visitedUrls = new HashSet<>();
        internalUrls = new HashSet<>();
        linksSource = new HashMap<>();
    }

    public static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
        return now.format(formatter);
    }

    public void crawlWebsite(String url, FileWriter writer) throws IOException {
        visitedUrls.add(url);
        String pageText = "";
        Elements links;
        Document document = null;

        try {
            document = Jsoup.connect(url).get();
            // Get all text from the current page
            pageText = document.body().text();

        } catch (Exception ex) {
            System.out.println("exception occurred : " + ex.getMessage());
        }

        if (!pageText.isEmpty()) {

            // Write URL and text to the CSV file
            writer.append(url);
            writer.append(",");
            writer.append(linksSource.get(url));
            writer.append(",");
            writer.append(pageText);
            writer.append("\n");

            // Find all links on the current page
            links = document.select("a[href]");
            for (Element link : links) {
                String href = link.absUrl("href");

                // Skip external links
                if (!href.startsWith(url)) {
                    continue;
                }

                // Add internal links to the set
                internalUrls.add(href);

                // Store source for the internal link
                linksSource.put(href, url);

                // Recursively crawl internal pages and skip same page urls with #
                if (!visitedUrls.contains(href) && !href.contains("#")) {
                    crawlWebsite(href, writer);
                }
            }

            System.out.println("internalUrls: " + internalUrls);
        }
    }

    public static void main(String[] args) {
        String startUrl = "https://www.jiomart.com/";
//        String startUrl = "https://programmingworld.in/";
//        String startUrl = "https://www.valuewala.com/";
        long startTime = System.currentTimeMillis() / 1000;
        CrawlerCSV crawler = new CrawlerCSV();

        try {

            // Construct file name with website name & timestamp
            String websiteName = startUrl.split("//")[1].replace("/", "");
            System.out.println(" websiteName: " + websiteName);
            String timestamp = getCurrentDateTime();
            String fileName = websiteName + "_"
                    + timestamp + ".csv";

            // Create FileWriter for the CSV file
            FileWriter writer = new FileWriter(fileName);

            crawler.crawlWebsite(startUrl, writer);

            // Close the FileWriter
            writer.close();

            long endTime = System.currentTimeMillis() / 1000;

            System.out.println("Data exported successfully!. Time Taken: "
                    + (endTime - startTime) + " sec. ");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

