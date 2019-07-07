/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/** Common desktop log, standard JRE */
public class L {

	/** Global log */
	private static Logger logger;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd 'at' HH:mm:ss.SSS");

    /* Initialize the log and log file */
    public static void init(String logFilePath) {

    	logger = Logger.getLogger("com.ehaviewer.log");
    	logger.setLevel(Level.INFO);

        try {
        	Formatter formatter = new Formatter() {
                @Override
                public String format(LogRecord r) {

                    String levelName = r.getLevel().getName();
                    String logRecordText = "[" + "        ".substring(levelName.length()) + levelName + "] com.ehaviewer; on ";
                    String timeFormatted = dateFormat.format(r.getMillis());
//                    String timeFormatted = DateUtils.formatDateTime(EpbApp.getAppContext(), r.getMillis(),
//                            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR |
//                                    DateUtils.FORMAT_SHOW_TIME);
                    logRecordText = logRecordText + timeFormatted;
                    logRecordText = logRecordText + "\n" + r.getMessage() + "\n";

                    return logRecordText;
                }
            };
        	
            FileHandler fileHandler = new FileHandler(logFilePath, 50000, 5, true);
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
            
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            logger.addHandler(consoleHandler);
        }
        catch (IOException ioException) {
			L.e("init => logFilePath: " + logFilePath, ioException);
			throw new E(ioException);
        }
    }
    
    /** Log text lines */
    private static String logTextLines(String logText, Throwable throwable) {

        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        String methodName = stackTraceElement.getMethodName();
        
        String fullClassName = stackTraceElement.getClassName();
        int dIndex = fullClassName.indexOf("$");
        if (dIndex > -1) {
        	fullClassName = fullClassName.substring(0, dIndex);
        }

        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String fullMethodName = fullClassName + "." + methodName;
        int codeLineNumber = stackTraceElement.getLineNumber();

        String text = "from " + fullMethodName + "(" + className + ".java:" + codeLineNumber + "):" + "\n";
        text = text + logText;

        if (throwable != null) {
            text = text + "\n";
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArrayOutputStream, true);
            throwable.printStackTrace(printStream);
            text = text + byteArrayOutputStream.toString();
        }

        String[] lines = text.replace("\r", "").split("\n");
        int size = lines.length;

        String concatText = "";
        for (int index = 0; index < size; index++) {

            String lineNumber = "(" + (index + 1) + "/" + size + ")";
            lineNumber = "            ".substring(0, 3 + 2 * (("" + size).length())).substring(lineNumber.length()) + lineNumber;
            concatText = concatText + lineNumber + ": " + lines[index];
            if (index < size - 1){
                concatText = concatText +"\n";
            }
        }

        return concatText;
    }

    /** Used only in development, will be false in the apk */
    public static boolean isP() {

        return true;
//        return false;
    }

    /** Used only in development, will be commented in the apk */
    public static void p(String devMessage) {

        for (String line : logTextLines(devMessage + "\n ", null).split("\n")) {
            System.out.println("static-catalog [P] " + line);
        }
    }

    /** Log.i replacement */
    public static void i(String message) {

        logger.log(Level.INFO, logTextLines(message, null));
    }

    /** Log.w replacement */
    public static void w(String message) {

        logger.log(Level.WARNING, logTextLines(message, null));

        Display currentDisplay = Display.getCurrent(); 
        if (currentDisplay != null) {
        	Shell currentShell = currentDisplay.getActiveShell();
        	if (currentShell != null) {
                MessageBox warningMessageBox = new MessageBox(currentShell, SWT.OK | SWT.ICON_WARNING);
                warningMessageBox.setText("static-catalog Warning");
                warningMessageBox.setMessage(message);
                warningMessageBox.open();
        	}
        }
    }

    /** Log.e replacement */
    public static void e(String message, Throwable throwable) {

        logger.log(Level.SEVERE, logTextLines(message, throwable));
        
        Display currentDisplay = Display.getCurrent(); 
        if (currentDisplay != null) {
        	Shell currentShell = currentDisplay.getActiveShell();
        	if (currentShell != null) {
                MessageBox errorMessageBox = new MessageBox(currentShell, SWT.OK | SWT.ICON_ERROR);
                errorMessageBox.setText("static-catalog Error, the program will exit");
                errorMessageBox.setMessage(throwable.getClass().getSimpleName() + ":\n" + throwable.getMessage());
                errorMessageBox.open();
        	}
        }
    }
}
