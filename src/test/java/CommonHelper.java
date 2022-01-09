import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;
import com.cybozu.labs.langdetect.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class CommonHelper {
    public static String logPath = "";

    //detect language by using google language detect library and this will return the language which has highest rate
    public static String detectlanguage(String text){
        System.out.println("Content from Web is : " + text);
        String detectedLan = "";
        try {
            Detector detector = DetectorFactory.create();
            detector.append(text);
            String detectedLang = detector.detect();
            System.out.println("Detected language in Web page is : " + detectedLang);
            detectedLan = detectedLang;
        }
        catch (LangDetectException e) {
            System.out.println(e.getMessage());
        }
        return detectedLan;
    }

    public static List<Object[]> readCSVWithList(String fileName){
        List<Object[]> testData = new ArrayList<>();
        try{
            String path = CommonHelper.class.getClassLoader().getResource(fileName).getPath();
            BufferedReader reader = new BufferedReader(new FileReader(path));
            reader.readLine();
            String line = null;
            int i=0;
            while(((line=reader.readLine())!=null) && (!(line.equals("")))){
                testData.add(new Object[] {i+1, line.toString()});
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return testData;
    }

    public static void printInfo(String content){
        System.out.println(content);
        saveLogFile(content);
    }

    /*create log file if file is not exist
    if file exist, then append log info into file
     */
    public static void saveLogFile(String content){
        FileWriter fw = null;
        try{
            File f = new File(logPath);
            fw = new FileWriter(f,true);
        }catch(IOException e){
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(fw);
        pw.println(content);
        pw.flush();
        try{
            fw.flush();
            pw.close();
            fw.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
