/*
package kidnox.eventbus;

import kidnox.eventbus.test.*;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class TempTest {

    @Test public void testTime() {
        long time = System.currentTimeMillis();
//        for(int i = 0; i < 1000; i++) {
//            for(Annotation annotation : SimpleSubscriber.class.getAnnotations()) {
//                annotation.annotationType();
//            }
//            for(Annotation annotation : SimpleProducer.class.getAnnotations()) {
//                annotation.annotationType();
//            }
//            for(Annotation annotation : SimpleNone.class.getAnnotations()) {
//                annotation.annotationType();
//            }
//        }
//        System.out.println("1 class extraction time = "+(System.currentTimeMillis() - time));
//        time = System.currentTimeMillis();
//        for(int i = 0; i < 1000; i++) {
//            SimpleSubscriber.class.isAnnotationPresent(Subscriber.class);
//            SimpleProducer.class.isAnnotationPresent(Producer.class);
//            SimpleNone.class.isAnnotationPresent(EventServiceFactory.class);
//        }
//        System.out.println("2 class extraction time = "+(System.currentTimeMillis() - time));

        for(int i = 0; i < 100; i++) {
            for(Method method : LargeSubscriber.class.getMethods()) {
                for(Annotation annotation : method.getAnnotations()) {
                    annotation.annotationType();
                }
            }
        }
        System.out.println("method extraction time = "+(System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        for(int i = 0; i < 100; i++) {
            for(Method method : LargeProducer.class.getMethods()) {
                method.isAnnotationPresent(Produce.class);
            }
        }
        System.out.println("method extraction time = "+(System.currentTimeMillis() - time));


    }




}
*/
