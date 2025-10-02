package dczx.axolotl.util.color;

import lombok.Setter;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/10/2 11:05
 */
public class StaticSystemPrinter {
    @Setter
    private static SystemColorPrinter logger = new SystemColorPrinter();

    public static void print(ColoredPrinter.Color color, String msg) {
        logger.print(color, msg);
    }

    public static void println(ColoredPrinter.Color color, String msg) {
        logger.println(color, msg);
    }

    public static void printf(ColoredPrinter.Color color, String format, Object... args) {
        logger.printf(color, format, args);
    }

    public static void printlnf(ColoredPrinter.Color color, String format, Object... args) {
        logger.printlnf(color, format, args);
    }

    public static void print(String msg) {
        logger.print(msg);
    }

    public static void printf(String format, Object... args) {
        logger.printf(format, args);
    }

    public static void println(String msg) {
        logger.println(msg);
    }

    public static void printlnf(String format, Object... args) {
        logger.printlnf(format, args);
    }
}
