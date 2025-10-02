package dczx.axolotl.util.color;

import java.io.PrintStream;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/10/2 10:59
 */
import java.io.PrintStream;

public abstract class ColoredPrinter {
    public Color DEFAULT_COLOR = Color.GREEN;

    public ColoredPrinter setDEFAULT_COLOR(Color DEFAULT_COLOR) {
        this.DEFAULT_COLOR = DEFAULT_COLOR;
        return this;
    }

    public enum Color {
        RESET("\u001B[0m"),
        GREEN("\u001B[32m"),
        RED("\u001B[31m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m"),
        BRIGHT_GREEN("\u001B[92m"),
        BRIGHT_RED("\u001B[91m"),
        BRIGHT_YELLOW("\u001B[93m"),
        BRIGHT_BLUE("\u001B[94m"),
        BRIGHT_PURPLE("\u001B[95m"),
        BRIGHT_CYAN("\u001B[96m");

        private final String code;

        Color(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    protected final PrintStream out;

    protected ColoredPrinter(PrintStream out) {
        this.out = out;
    }

    public void print(Color color, String msg) {
        if (msg != null) {
            out.print(color.getCode());
            out.print(msg);
            out.print(Color.RESET.getCode());
        }
    }

    public void println(Color color, String msg) {
        print(color, msg);
        out.println();
    }

    public void printf(Color color, String format, Object... args) {
        if (format != null) {
            String formatted = String.format(format, args);
            out.print(color.getCode());
            out.print(formatted);
            out.print(Color.RESET.getCode());
        }
    }

    public void printlnf(Color color, String format, Object... args) {
        printf(color, format, args);
        out.println();
    }

    public void print(String msg) {
        print(DEFAULT_COLOR, msg);
    }

    public void printf(String format, Object... args) {
        printf(DEFAULT_COLOR, format, args);
    }

    public void println(String msg) {
        println(DEFAULT_COLOR, msg);
    }

    public void printlnf(String format, Object... args) {
        printlnf(DEFAULT_COLOR, format, args);
    }
}
