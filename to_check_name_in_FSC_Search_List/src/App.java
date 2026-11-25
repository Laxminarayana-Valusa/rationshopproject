package com.example;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class App {
        public static void main(String[] args) throws InterruptedException, IOException {
                WebDriver driver = new ChromeDriver();
                driver.manage().window().maximize();

                driver.get("https://epds.telangana.gov.in/FoodSecurityAct");

                // new WebDriverWait(driver, Duration.ofSeconds(10))
                // .until(ExpectedConditions.elementToBeClickable(By.linkText("\r\n" + //
                // "Click here to login again")))
                // .click();

                java.io.PrintWriter csvWriter = new java.io.PrintWriter(
                                new java.io.FileWriter("ration_card_matches.csv", true));
                csvWriter.println(
                                "District,Office,ShopNumber,SNo,ReferenceNo,RationCardNo,CardTypeId,HeadOfFamily,HouseNo,Units,GasCylinders,ApprovedDate,CardPoolType");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                try {
                        // Locate and click the 'X' close button at the top-right of the popup modal
                        WebElement closePopupBtn = wait.until(
                                        ExpectedConditions.elementToBeClickable(
                                                        By.xpath(
                                                                        "//button[normalize-space()='X' or normalize-space()='×'] | //a[normalize-space()='X' or normalize-space()='×'] | //span[normalize-space()='X' or normalize-space()='×'] | //*[contains(@class,'close')]")));
                        closePopupBtn.click();
                } catch (Exception e) {
                        // Fallback: press Escape key to dismiss the modal if the 'X' uses a different
                        // tag
                        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                }

                WebElement reportsMenu = wait.until(
                                ExpectedConditions.elementToBeClickable(
                                                By.xpath("//a[.//font[normalize-space()='Reports']] | //font[normalize-space()='Reports']")));
                reportsMenu.click();

                // 1. Locate the "Ration Cards Reports" dropdown button and hover over it
                WebElement rationCardsDropdown = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(
                                                By.xpath("//button[contains(@class,'dropbtn') and normalize-space()='Ration Cards Reports']")));
                new Actions(driver).moveToElement(rationCardsDropdown).perform();

                // 2. Wait for "FSC Card Status Report" to appear and click it
                WebElement fscStatusReportLink = wait.until(
                                ExpectedConditions.elementToBeClickable(
                                                By.xpath(
                                                                "//div[contains(@class,'dropdown-content')]//a[normalize-space()='FSC Card Status Report']")));
                fscStatusReportLink.click();

                java.util.Scanner scanner = new java.util.Scanner(System.in);
                System.out.print("Enter partial Head of Family name to search: ");
                String searchName = scanner.nextLine().trim().toLowerCase();

                // 1. Dynamically collect and display all District Names with numbers
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("tr.rowOdd td.leftOne a")));
                List<WebElement> districtElements = driver.findElements(
                                By.cssSelector("tr.rowOdd td.leftOne a"));

                java.util.List<String> allDistricts = new java.util.ArrayList<>();
                System.out.println("\n=== AVAILABLE DISTRICTS ===");
                for (int i = 0; i < districtElements.size(); i++) {
                        String distName = districtElements.get(i).getText().trim().replaceAll("\\s+", " ");
                        allDistricts.add(distName);
                        System.out.println((i + 1) + ". " + distName);
                }

                // Prompt user to choose a District number
                System.out.print("\nEnter District number (1-" + allDistricts.size() + "): ");
                int distChoice = Integer.parseInt(scanner.nextLine().trim());
                String selectedDistrict = allDistricts.get(distChoice - 1);
                System.out.println("Selected District: " + selectedDistrict);

                WebElement districtLink = wait.until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//tr[@class='rowOdd']//a[normalize-space()='" + selectedDistrict + "']")));
                districtLink.click();

                // 2. Dynamically collect and display all Office Names inside the chosen
                // District
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("tr.rowOdd td.leftOne a")));
                List<WebElement> officeElements = driver.findElements(
                                By.cssSelector("tr.rowOdd td.leftOne a"));

                java.util.List<String> allOffices = new java.util.ArrayList<>();
                System.out.println("\n=== AVAILABLE OFFICES IN " + selectedDistrict.toUpperCase() + " ===");
                for (int i = 0; i < officeElements.size(); i++) {
                        String offName = officeElements.get(i).getText().trim().replaceAll("\\s+", " ");
                        allOffices.add(offName);
                        System.out.println((i + 1) + ". " + offName);
                }

                // Prompt user to choose specific offices selectively (e.g., "11, 12" or one by
                // one)
                java.util.List<String> selectedOffices = new java.util.ArrayList<>();
                System.out.println("\nEnter Office numbers (e.g., 11, 12), type 'all', or press Enter to start:");
                while (true) {
                        System.out.print("Select Office #(s) (or Enter to start): ");
                        String input = scanner.nextLine().trim();

                        if (input.isEmpty() || input.equalsIgnoreCase("done")) {
                                if (selectedOffices.isEmpty()) {
                                        System.out.println("  Please select at least one office first.");
                                        continue;
                                }
                                break;
                        } else if (input.equalsIgnoreCase("all")) {
                                selectedOffices.clear();
                                selectedOffices.addAll(allOffices);
                                break;
                        } else {
                                for (String part : input.split(",")) {
                                        try {
                                                int offIdx = Integer.parseInt(part.trim());
                                                if (offIdx >= 1 && offIdx <= allOffices.size()) {
                                                        String chosen = allOffices.get(offIdx - 1);
                                                        if (!selectedOffices.contains(chosen)) {
                                                                selectedOffices.add(chosen);
                                                                System.out.println("  Added: " + chosen + " | Queue: "
                                                                                + selectedOffices);
                                                        } else {
                                                                System.out.println("  Already added: " + chosen);
                                                        }
                                                } else {
                                                        System.out.println("  Invalid number: " + part.trim());
                                                }
                                        } catch (NumberFormatException e) {
                                                System.out.println("  Invalid input: " + part.trim());
                                        }
                                }
                        }
                }

                System.out.println("\nStarting scan for offices: " + selectedOffices);

                // 3. Iterate through your selected Office Names
                for (String officeName : selectedOffices) {
                        System.out.println("=========================================");
                        System.out.println("Entering Office: " + officeName + " (" + selectedDistrict + ")");

                        WebElement officeLink = wait.until(ExpectedConditions.elementToBeClickable(
                                        By.xpath("//tr[@class='rowOdd']//a[normalize-space()='" + officeName + "']")));
                        officeLink.click();

                        // Collect all Shop Numbers inside this Office
                        wait.until(ExpectedConditions.visibilityOfElementLocated(
                                        By.cssSelector("tr.rowOdd td.leftOne a span")));
                        List<WebElement> shopElements = driver.findElements(
                                        By.cssSelector("tr.rowOdd td.leftOne a span"));

                        java.util.List<String> allShops = new java.util.ArrayList<>();
                        for (WebElement shopEl : shopElements) {
                                allShops.add(shopEl.getText().trim());
                        }
                        System.out.println("Found " + allShops.size() + " shops under " + officeName);

                        // 4. Iterate through each Shop No
                        for (String shopNumber : allShops) {
                                System.out.println("Checking shop: " + shopNumber);

                                WebElement shopLink = wait.until(ExpectedConditions.elementToBeClickable(
                                                By.xpath("//tr[@class='rowOdd']//a[span[normalize-space()='"
                                                                + shopNumber + "']]")));
                                shopLink.click();

                                // Wait for the shop's beneficiary table to load
                                wait.until(ExpectedConditions.visibilityOfElementLocated(
                                                By.cssSelector("div.tabletwoPr table tbody tr")));

                                // Search specifically in column 5 (Head Of The family)
                                List<WebElement> matchingRows = driver.findElements(By.xpath(
                                                "//div[@class='tabletwoPr']//tr[td[5][contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"
                                                                + searchName + "')]]"));

                                for (WebElement row : matchingRows) {
                                        List<WebElement> cols = row.findElements(By.tagName("td"));
                                        if (!cols.isEmpty()) {
                                                java.util.List<String> rowValues = new java.util.ArrayList<>();
                                                java.util.List<String> csvValues = new java.util.ArrayList<>();

                                                for (WebElement col : cols) {
                                                        String cellText = col.getText().trim().replaceAll("\\s+", " ");
                                                        rowValues.add(cellText);
                                                        csvValues.add("\"" + cellText.replace("\"", "\"\"") + "\"");
                                                }

                                                // Print complete row in Terminal
                                                System.out.println(selectedDistrict + " | " + officeName + " | "
                                                                + shopNumber + " -> " + String.join(" | ", rowValues));

                                                // Save complete row to CSV file
                                                csvWriter.println("\"" + selectedDistrict + "\",\"" + officeName
                                                                + "\",\"" + shopNumber + "\","
                                                                + String.join(",", csvValues));
                                                csvWriter.flush();
                                        }
                                }

                                // Navigate back: Shop beneficiary table -> Shops list
                                WebElement shopBackBtn = wait.until(
                                                ExpectedConditions.presenceOfElementLocated(
                                                                By.cssSelector("#principal input[type='button'][value='Back']")));
                                String shopBackOnClick = shopBackBtn.getDomAttribute("onclick");
                                int sStart = shopBackOnClick.indexOf("window.location.href='") + 22;
                                int sEnd = shopBackOnClick.indexOf("'", sStart);
                                String shopBackRelUrl = shopBackOnClick.substring(sStart, sEnd);

                                String currUrl1 = driver.getCurrentUrl();
                                String baseUrl1 = currUrl1.contains("?") ? currUrl1.substring(0, currUrl1.indexOf("?"))
                                                : currUrl1;
                                driver.get(baseUrl1 + shopBackRelUrl);
                        }

                        // Navigate back: Shops list -> Office Name table for the next selected office
                        WebElement officeBackBtn = wait.until(
                                        ExpectedConditions.presenceOfElementLocated(
                                                        By.cssSelector("input[type='button'][value='Back']")));
                        String officeBackOnClick = officeBackBtn.getDomAttribute("onclick");
                        int oStart = officeBackOnClick.indexOf("window.location.href='") + 22;
                        int oEnd = officeBackOnClick.indexOf("'", oStart);
                        String officeBackRelUrl = officeBackOnClick.substring(oStart, oEnd);

                        String currUrl2 = driver.getCurrentUrl();
                        String baseUrl2 = currUrl2.contains("?") ? currUrl2.substring(0, currUrl2.indexOf("?"))
                                        : currUrl2;
                        driver.get(baseUrl2 + officeBackRelUrl);

                        Thread.sleep(1500);
                }
                csvWriter.close();
                scanner.close();
                driver.quit();
        }
}
