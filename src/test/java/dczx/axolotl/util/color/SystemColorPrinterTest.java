package dczx.axolotl.util.color;

import org.junit.jupiter.api.Test;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/10/2 10:59
 */
class SystemColorPrinterTest {

    SystemColorPrinter logger = new SystemColorPrinter();

    @Test
    void print() {
        // 使用默认颜色（蓝色）
        logger.setDEFAULT_COLOR(ColoredPrinter.Color.GREEN).println("这是一条默认颜色的消息");
        logger.printlnf("用户 %s 登录，ID: %d", "Alice", 1001);

        // 指定不同颜色
        logger.println(ColoredPrinter.Color.RED, "错误：文件未找到");
        logger.printlnf(ColoredPrinter.Color.YELLOW, "警告：内存使用率 %.1f%%", 87.5);
        logger.printlnf(ColoredPrinter.Color.CYAN, "调试信息：%s - %s", "线程1", "正在初始化");

        // 使用其他颜色
        logger.println(ColoredPrinter.Color.BRIGHT_GREEN, "成功！操作完成");
    }
}