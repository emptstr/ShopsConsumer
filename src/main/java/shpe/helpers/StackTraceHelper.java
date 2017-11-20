package shpe.helpers;

import java.util.Arrays;

/**
 * Created by jordan on 11/19/17.
 */
public class StackTraceHelper {

    public static String getAsFormattedString(StackTraceElement[] stackTraceElements) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(stackTraceElements).forEach(stackTraceElement -> {
            String traceString = String.format("FileName: %s\n\tClassNumber: %s\n\t\tMethodName: %s\n\t\t\tLineNumber: %d", stackTraceElement.getFileName(),
                    stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            builder.append(traceString);
            builder.append(System.lineSeparator());
        });
        return builder.toString();
    }
}
