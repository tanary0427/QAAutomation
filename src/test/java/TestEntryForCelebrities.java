import com.cybozu.labs.langdetect.DetectorFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestEntryForCelebrities implements ITest {
    public WebDriver driver = null;
    public String mTestCaseName = "";

    @DataProvider(name = "Celebrities")
    public static Iterator<Object[]> celebritiesList(){
        List<Object[]> testData = new ArrayList<>();
        testData = CommonHelper.readCSVWithList("Celebrities.csv");
        return testData.iterator();
    }

    @BeforeMethod(alwaysRun = true)
    public void testData(Method method, Object[] testData){
        if(testData != null && testData.length > 0){
            this.mTestCaseName = testData[0].toString() + "_" + testData[1].toString();
        }
    }

    @Override
    public String getTestName(){
        return this.mTestCaseName;
    }

    @BeforeSuite
    public void initial() throws Exception{
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://en.wikipedia.org/wiki/Main_Page");
        Thread.sleep(5000);
        CommonHelper.logPath = "out/TestResult_" + System.currentTimeMillis() + ".txt";
    }

    @Test(dataProvider = "Celebrities")
    void retrieveCelebritiesInfo(int caseid, String name) throws Exception {
        WebElement searchBox = driver.findElement(By.xpath("//*[@id=\"searchInput\"]"));
        searchBox.sendKeys(name);
        WebElement searchButton = driver.findElement(By.xpath("//*[@id=\"searchButton\"]"));
        searchButton.click();
        Thread.sleep(5000);

        CommonHelper.printInfo("***********************************  " + name + "  ***********************************");

        //check whether celebrities table has been retrieved out and then get birth and spouse info out
        if((driver.findElements(By.className("biography"))).size()>0){
            WebElement infoTable = driver.findElement(By.className("biography"));
            //check whether the table info is for the correct people or not
            if(infoTable.getText().contains(name)){
                List<WebElement> rows = infoTable.findElements(By.tagName("tr"));

                for(WebElement row : rows){
                    if(row.getText().contains("Born")){
                        WebElement column = row.findElement(By.tagName("td"));
                        CommonHelper.printInfo("------Birth info as below : ------\r\n" + column.getText() + "\r\n");
                    }else if(row.getText().contains("Spouse(s)")){
                        WebElement column = row.findElement(By.tagName("td"));
                        CommonHelper.printInfo("------Spouse info as below : ------\r\n" + column.getText() + "\r\n\r\n");
                    }
                }
            }else{
                CommonHelper.printInfo("Incorrect data found. Please check details");
            }

        }else{
            Assert.assertFalse(false,"No data found");
        }
    }



    @AfterSuite
    public void release(){
        driver.close();
        CommonHelper.logPath="";
    }
}
