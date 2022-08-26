import net.neoremind.dynamicproxy.DefaultProxyCreator;
import net.neoremind.dynamicproxy.Interceptor;
import net.neoremind.dynamicproxy.Invocation;
import net.neoremind.dynamicproxy.ProxyCreator;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class Main {
   public static void main(String[] args) {
      /*  System.setProperty("webdriver.chrome.driver", "C:\\webDriver\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver(); */
    }

    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Target({METHOD, TYPE})
    public @interface Selector {

        String xpath();
    }

    public interface MainPage {

        @Selector(xpath = ".//*[@test-attr='input_search']")
        String textInputSearch();

        @Selector(xpath = ".//*[@test-attr='button_search']")
        String buttonSearch();
    }

    public class MethodInterception {

        @Test
        public void annotationValue() {
            MainPage mainPage = createPage(MainPage.class);
            assertNotNull(mainPage);
            assertEquals(mainPage.buttonSearch(), ".//*[@test-attr='button_search']");
            assertEquals(mainPage.textInputSearch(), ".//*[@test-attr='input_search']");
           // String text = assertEquals(mainPage.textInputSearch(), ".//*[@test-attr='input_search']");
        }
      /*  public interface Foo {
            Object bar(Object obj) throws BazException;

        }

        public class FooImpl implements Foo {
            @Override
            public Object bar(Object obj) throws BazException {
                return null;
            }
         /*   Object bar(Object obj) throws BazException {
                Foo foo = (Foo) DebugProxy.newInstance(new FooImpl());
                foo.bar(null);
            }*/

        public class DebugProxy implements java.lang.reflect.InvocationHandler {

            private Object obj;

            public Object newInstance(Object obj) {
                return java.lang.reflect.Proxy.newProxyInstance(
                        obj.getClass().getClassLoader(),
                        obj.getClass().getInterfaces(),
                        new DebugProxy(obj));
            }

            private DebugProxy(Object obj) {
                this.obj = obj;
            }

            public Object invoke(Object proxy, Method m, Object[] args)
                    throws Throwable
            {
                Object result;
                try {
                    System.out.println("before method " + m.getName());
                    result = m.invoke(obj, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                } catch (Exception e) {
                    throw new RuntimeException("unexpected invocation exception: " +
                            e.getMessage());
                } finally {
                    System.out.println("after method " + m.getName());
                }
                return result;
            }
        }
        private MainPage createPage(Class clazz) {
            Map mapProxyInstance = (Map) Proxy.newProxyInstance(
                    DynamicProxyTest.class.getClassLoader(), new Class[] { Map.class },
                    new TimingDynamicInvocationHandler(new HashMap<>()));

            mapProxyInstance.put("hello", "world");

            CharSequence csProxyInstance = (CharSequence) Proxy.newProxyInstance(
                    DynamicProxyTest.class.getClassLoader(),
                    new Class[] { CharSequence.class },
                    new TimingDynamicInvocationHandler("Hello World"));

                     csProxyInstance.length();
        }

    }
    public class TimingDynamicInvocationHandler implements InvocationHandler {

        private static Logger LOGGER = (Logger) LoggerFactory.getLogger(
                TimingDynamicInvocationHandler.class);

        private final Map<String, Method> methods = new HashMap<>();

        private Object target;

        public TimingDynamicInvocationHandler(Object target) {
            this.target = target;

            for(Method method: target.getClass().getDeclaredMethods()) {
                this.methods.put(method.getName(), method);
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            long start = System.nanoTime();
            Object result = methods.get(method.getName()).invoke(target, args);
            long elapsed = System.nanoTime() - start;

          // LOGGER.info("Executing {} finished in {} ns", method.getName(), elapsed);

            return result;
        }
    }
}