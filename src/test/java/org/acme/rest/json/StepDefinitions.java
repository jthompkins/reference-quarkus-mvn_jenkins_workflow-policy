package org.acme.rest.json;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import javax.json.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class StepDefinitions {
    private final String hubUrl = System.getProperty("selenium.hub.url");
    private final String targetBaseURL = System.getProperty("target.base.url");

    private JsonArray list;
    private String response;
    private String createdName;
    private String createdDescription;

    private boolean isDriverReady = false;
    private WebDriver driver = null;

    @Given("^I use Chrome browser$")
    public void I_use_Chrome_browser() throws Throwable {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setBrowserName("chrome");
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        initDriver(capabilities);
    }

    @When("^I get (.*)$")
    public void i_get_collection(String path) throws Throwable {
        driver.navigate().to(new URL(String.format("%s/%s", targetBaseURL, path)));
        list = parseJsonArrayResult();
    }

    @When("^I add to (.*) with name (.*) and description (.*)$")
    public void i_add_to_collection(String path, String name, String description) throws Throwable {
        createdName = name;
        createdDescription = description;
        URL url = new URL(String.format("%s/%s", targetBaseURL, path));
        JsonObject object = Json.createObjectBuilder()
                .add("name", name)
                .add("description", description)
                .build();
        response = post(url, object.toString());
    }

    @Then("^I should have at least (.*) total$")
    public void i_should_have_total(int count) {
        assertTrue(count <= list.size());
    }

    @Then("^the names are:$")
    public void the_names_are(List<String> names) {
        List<String> resultNames = new ArrayList<>();
        list.forEach(result -> resultNames.add(result.asJsonObject().getString("name")));
        names.forEach(name -> assertTrue(resultNames.contains(name)));
    }

    @Then("it should be created")
    public void it_should_be_created() {
        JsonArray array = parseArray(response);
        boolean found = array.stream()
                .map(JsonValue::asJsonObject)
                .anyMatch(item -> createdName.equals(item.getString("name")) &&
                        createdDescription.equals(item.getString("description")));
        assertTrue(found);
    }

    private String post(URL url, String data) throws IOException {
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    private JsonArray parseJsonArrayResult() {
        String body = driver.findElement(By.cssSelector("pre")).getText();
        return parseArray(body);
    }

    private JsonArray parseArray(String body) {
        JsonReader reader = null;
        JsonArray result = Json.createArrayBuilder().build();
        try {
            reader = Json.createReader(new StringReader(body));
            result = reader.readArray();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

    private void initDriver(DesiredCapabilities capabilities) {
        if(this.isDriverReady) {
            return;
        }
        try {
            this.driver = new RemoteWebDriver(new URL(this.hubUrl + "/wd/hub"), capabilities);
            this.driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        } catch(UnreachableBrowserException e) {
            Assert.fail("UnreachableBrowserException: " + e.getMessage());
        } catch(MalformedURLException e) {
            Assert.fail("MalformedURLException: " + this.hubUrl + "/wd/hub");
        } catch(WebDriverException e) {
            Assert.fail("WebDriverException: " + e.getMessage());
        }
        this.isDriverReady = true;
    }
}
