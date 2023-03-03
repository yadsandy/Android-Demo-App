package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import logger.Log;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.asserts.SoftAssert;
import reporters.ExtentManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static long IMPLICIT_WAIT = 20;
    public static ArrayList<String> methodToBeExecuted = new ArrayList<String>();
    private static ExtentTest test;
    private static ExtentReports extent;

    /* This function will initialize the ExtentReport object */
    public static void initializeExtentReport() {
        extent = ExtentManager.getReporter();
    }

    /* This function will initialize the ExtentTest object */
    public static void initializeExtentTest(String methodName, String description, String category) {
        test = extent.createTest(methodName + " | " + GlobalVars.platform, description).assignCategory(category);
        test.log(Status.INFO, "Test execution started.");
    }

    /*
     * This function will be called after every test method
     */
    public static void closeExtentTest() {
        test.getExtent().flush();
    }

    /* This function will log function level logs based on the result */
    public static void logFunctionLevelLogs(boolean result, String functionName) {
        if (result)
            Log.info(functionName + ": successful!");
        else
            Log.error(functionName + ": failed!");
    }

    /* This function will log each step of a test case */
    public static void logStepInfo(String message) throws IOException, InterruptedException {
        test.log(Status.PASS, message);
        Log.info("Message: " + message);
        Reporter.log(message);
    }

    /* Function to assert and log the steps info in extent report */
    public static void assertAndlogStepInfo(boolean isResult, boolean isSoftAssert, String stepInfo)
            throws IOException, InterruptedException {
        logStepInfo(isResult, stepInfo);
        if (isSoftAssert) {
            SoftAssert sAssert = new SoftAssert();
            sAssert.assertTrue(isResult, stepInfo);
        } else {
            Assert.assertTrue(isResult, stepInfo);
        }
    }

    /* Function to log the steps info in extent report */
    public static void logStepInfo(boolean isResult, String stepInfo) {
        if (isResult) {
            test.log(Status.PASS, stepInfo + " | Status: Pass");
            // Thread.sleep(5000);
            // Utils.captureScreenshotNew();
        } else {
            test.log(Status.FAIL, stepInfo + " | Status: Fail");
            // Utils.captureScreenshotNew();

        }

    }

    public static void logStepAction(String message) {
        test.log(Status.PASS, message);
        // Utils.captureScreenshotNew(result);
    }

    /*
     * This function will return the formatted file name with date and time appended
     */
    public static String getFileName(String file) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        Calendar cal = Calendar.getInstance();
        String fileName = file + dateFormat.format(cal.getTime());
        return fileName;
    }

    /* capturing screenshot */
    public static String captureScreenshot(ITestResult result) throws IOException, InterruptedException {
        String screen = null;
        try {
            String screenshotName = Utils.getFileName(result.getName());

            try {
                File screenshot = ((TakesScreenshot) GlobalVars.driver).getScreenshotAs(OutputType.FILE);
                String path = ExtentManager.extentpath + "/" + screenshotName + ".png";
                File destination = new File(path);
                try {
                    FileUtils.copyFile(screenshot, destination);
                } catch (IOException e) {
                    System.out.println("Capture Failed " + e.getMessage());
                }
                test.fail(result.getThrowable().getMessage(),

                        MediaEntityBuilder.createScreenCaptureFromPath("./" + screenshotName + ".png").build());

            } catch (Exception e) {
                e.printStackTrace();
                test.fail(result.getThrowable().getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screen;
    }

    public static String captureScreenshotNew() {
        String screen = null;
        try {

            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            String methodName = stackTrace[3].getMethodName();

            System.out.println("Method Name : " + methodName);
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss.SSS").format(new Date());

            File screenshot = ((TakesScreenshot) GlobalVars.driver).getScreenshotAs(OutputType.FILE);
            String path = ExtentManager.extentpath + "/" + methodName + "_" + timeStamp + ".png";
            File destination = new File(path);

            try {
                FileUtils.copyFile(screenshot, destination);
            } catch (IOException e) {
                System.out.println("Capture Failed " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return screen;
    }

}