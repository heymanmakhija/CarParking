package com.practise.parking.lot.misc;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CrawlerExcel {

    private final Set<String> visitedUrls;
    private final Set<String> internalUrls;
    private final HashMap<String, String> linksSource;

    public CrawlerExcel() {
        visitedUrls = new HashSet<>();
        internalUrls = new HashSet<>();
        linksSource = new HashMap<>();
    }

    public static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
        return now.format(formatter);
    }

    public void crawlWebsite(String url, Workbook workbook) throws IOException {
        visitedUrls.add(url);
        String pageText = "";
        Elements links;
        Document document;

        try {
            document = Jsoup.connect(url).get();
            // Get all text from the current page
            pageText = document.body().text();

            if (!pageText.isEmpty()) {

                System.out.println("URL: " + url);
                System.out.println("Text: " + pageText);
                System.out.println(" ");

                // Add URL and text to the Excel sheet
                Sheet sheet = workbook.getSheetAt(0);
                int rowCount = sheet.getLastRowNum();
                Row row = sheet.createRow(rowCount + 1);
                row.createCell(0).setCellValue(url);
                row.createCell(1).setCellValue(linksSource.get(url));
                row.createCell(2).setCellValue(pageText);

                // Find all links on the current page
                links = document.select("a[href]");
                for (Element link : links) {
                    String href = link.absUrl("href");
                    System.out.println("href: " + href);

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
                        crawlWebsite(href, workbook);
                    }
                }

                System.out.println("internalUrls: " + internalUrls);
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("exception occurred while writing data to excel : " + ex.getMessage());
        } catch (HttpStatusException ex) {
            System.out.println("exception occurred while fetch data: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("exception occurred " + ex.getMessage());
        }
    }


    public static void main(String[] args) {
        CrawlerExcel crawler = new CrawlerExcel();

        // Create a new workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Website Data");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("URL");
        headerRow.createCell(1).setCellValue("Source");
        headerRow.createCell(2).setCellValue("Text");

        // Create bold font style for header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);
        headerRow.getCell(2).setCellStyle(headerStyle);

//        String startUrl = "https://www.jiomart.com/";
        String startUrl = "https://programmingworld.in/";
//        String startUrl = "https://www.valuewala.com/";
        long startTime = System.currentTimeMillis() / 1000;

        try {
            crawler.crawlWebsite(startUrl, workbook);

            // Adjust column widths
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            // Construct file name with website name & timestamp
            String websiteName = startUrl.split("//")[1].replace("/", "");
            System.out.println(" websiteName: " + websiteName);
            String timestamp = getCurrentDateTime();
            String fileName = websiteName + "_" + timestamp + ".xlsx";
            FileOutputStream fileOut = new FileOutputStream(fileName);

            // Write workbook to an Excel file
            workbook.write(fileOut);
            fileOut.close();
            long endTime = System.currentTimeMillis() / 1000;

            System.out.println("Data exported successfully!. Time Taken: "
                    + (endTime - startTime) + " sec. ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

