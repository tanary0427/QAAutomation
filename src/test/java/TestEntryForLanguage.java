import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.*;
import com.cybozu.labs.langdetect.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Method;

public class TestEntryForLanguage implements ITest {
    public WebDriver driver = null;
    public String mTestCaseName = "";

    @DataProvider(name = "Language")
    public static Iterator<Object[]> languageList(){
        List<Object[]> testData = new ArrayList<>();
        testData = CommonHelper.readCSVWithList("Language.csv");
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
        DetectorFactory.loadProfile("profiles");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://en.wikipedia.org/wiki/Main_Page");
        Thread.sleep(5000);

        CommonHelper.logPath = "out/TestResult_" + System.currentTimeMillis() + ".txt";
    }

    @Test(dataProvider = "Language")
    public void languageTest(int caseid, String para) throws Exception {
        String[] lan = para.split(",");
        String language = lan[0];
        String expecteddetectlan = lan[1]; // this is the language which expected after we using google library to detect
        CommonHelper.printInfo("Language selected in Wiki Page is " + language + ", Expected detected language is : " + expecteddetectlan);

        //click specified language link, if cant find the specified language link which means current page is the one
        if((driver.findElements(By.xpath("//a[@title='"+language+"']"))).size()>0){
            WebElement LanguageLink = driver.findElement(By.xpath("//a[@title='"+language+"']"));
            LanguageLink.click();
            Thread.sleep(5000);
        }

        //retrieve the web page text and then detect the language by using google library
        WebElement contentDiv = driver.findElement(By.xpath("//*[@id=\"content\"]"));
        String text = contentDiv.getText();
        CommonHelper.detectlanguage(text);
        Assert.assertEquals(CommonHelper.detectlanguage(text), expecteddetectlan,"Detected language in Web is same as expected");
        CommonHelper.printInfo("Validation Result : Pass\r\n");

        //back to first page which is easier to find the language name link in english
        driver.navigate().back();
        Thread.sleep(2000);
    }

    @AfterSuite
    public void release(){
        driver.close();
        CommonHelper.logPath="";
    }
}
