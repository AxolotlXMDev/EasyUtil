package dczx.axolotl.reflect;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/10/1 21:18
 */
class MethodUtilTest {


    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream testOut;

    @BeforeEach
    void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should invoke PrintStream.printf with varargs correctly")
    void testPrintfWithVarargs() throws Exception {
        PrintStream out = System.out;
        Method method = MethodUtil.findMethodByObjects(out.getClass(), "printf", "Hello %s %d", "World", 42);
        MethodUtil.invokeWithVarargs(out, method, "Hello %s %d", "World", 42);

        String output = testOut.toString().trim();
        assertEquals("Hello World 42", output);
    }

    @Test
    @DisplayName("Should invoke normal non-varargs method")
    void testNormalMethod() throws Exception {
        String str = "  hello  ";
        Method method = MethodUtil.findMethodByObjects(str.getClass(), "trim");
        Object result = MethodUtil.invokeWithVarargs(str, method);
        assertEquals("hello", result);
    }

    @Test
    @DisplayName("Should handle varargs with primitive types (via helper class)")
    void testVarargsWithPrimitives() throws Exception {
        VarArgsHelper helper = new VarArgsHelper();
        Method method = MethodUtil.findMethodByObjects(helper.getClass(), "sum", 1, 2, 3);
        Object result = MethodUtil.invokeWithVarargs(helper, method, 1, 2, 3);
        assertEquals(6, result);
    }

    @Test
    @DisplayName("Should invoke no-argument method")
    void testNoArgsMethod() throws Exception {
        Object obj = new Object();
        Method method = MethodUtil.findMethodByObjects(obj.getClass(), "toString");
        assertNotNull(MethodUtil.invokeWithVarargs(obj, method));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when method not found")
    void testMethodNotFound() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> MethodUtil.findMethodByObjects(String.class, "nonExistentMethod", "arg")
        );
        assertTrue(exception.getMessage().contains("No such method"));
    }

    @Test
    @DisplayName("Should handle zero varargs (empty array)")
    void testZeroVarargs() throws Exception {
        PrintStream out = System.out;
        Method method = MethodUtil.findMethodByObjects(out.getClass(), "printf", "No args");
        MethodUtil.invokeWithVarargs(out, method, "No args");

        String output = testOut.toString().trim();
        assertEquals("No args", output);
    }

    // Helper class for testing int... varargs
    static class VarArgsHelper {
        public int sum(int... numbers) {
            int total = 0;
            for (int n : numbers) total += n;
            return total;
        }
    }
}