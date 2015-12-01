import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.pool.TypePool;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author Dmitry Spikhalskiy <dmitry@spikhalskiy.com>
 */
public class InterceptorsInitializer {
    //This code should be executed before any usage/loading of MyObject class
    public static void registerModificationInterception() {
        TypePool typePool = TypePool.Default.ofClassPath();
        new ByteBuddy()
                .rebase(typePool.describe("MyObject").resolve(), ClassFileLocator.ForClassLoader.ofClassPath())
                .modifiers(TypeManifestation.PLAIN) //our class can be final and we have no access to it - so remove final
                .defineField("group", String.class, Visibility.PUBLIC)
                .method(named("modify")).intercept(MethodDelegation.to(typePool.describe("Interceptors").resolve()))
                .method(named("copy")).intercept(MethodDelegation.to(typePool.describe("Interceptors").resolve()))
                .make()
                .load(InterceptorsInitializer.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION);
    }
}
