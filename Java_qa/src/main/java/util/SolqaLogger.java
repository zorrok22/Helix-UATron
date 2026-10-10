package util;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class SolqaLogger {
    private Logger log;
    public SolqaLogger(String className){
        log = Logger.getLogger(className);
        DOMConfigurator.configure("log4j_Config.xml");
    }

    List<String[]> rowList = new ArrayList<>();


    public void logTime(String timeTaken, String Step){
        rowList.add(new String[] {timeTaken,Step});
    }

    public void submitTime(){
        if (Listener.extentTest.get() != null){
            String[][] data = new String[rowList.size()+1][2];
            int i =1;
            data[0][0] = "Time Taken in Seconds";
            data[0][1] = "Step Performed";
            for (String[] row:rowList){
                data[i][0] = row[0];
                data[i][1] = row[1];
                i++;
            }
            Markup m = MarkupHelper.createTable(data);
            Listener.extentTest.get().info(m);
            rowList.clear();
        }

    }

    public void info(String message) {
        log.info(message);
        if (Listener.extentTest.get() != null){
            Listener.extentTest.get().info(message);
        }
    }

    public void pass(String message) {
        log.info(message);
        if (Listener.extentTest.get() != null){
            Listener.extentTest.get().pass(message);
        }
    }

    public void error(String message){
        log.error(message);
    }

    public void error(String message, Exception e){
        log.error(message, e);
    }

    public void warn(String message){
        log.warn(message);
    }

    public void debug(String message){
        log.debug(message);
    }

}
