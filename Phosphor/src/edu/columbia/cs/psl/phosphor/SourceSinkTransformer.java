package edu.columbia.cs.psl.phosphor;

import edu.columbia.cs.psl.phosphor.instrumenter.SourceSinkTaintingClassVisitor;
import edu.columbia.cs.psl.phosphor.struct.LinkedList;
import org.objectweb.asm.*;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

/* Transforms classes modifying the code for sink, source, and taintThrough methods. */
public class SourceSinkTransformer extends PhosphorBaseTransformer {

    /* Stores classes for which retransform was called before VM was initialized. */
    private static LinkedList<Class<?>> retransformQueue = new LinkedList<>();
    public static boolean isBasicSourceSinkManagerInit = false;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cr.accept(new SourceSinkTaintingClassVisitor(cw), 0);
        return cw.toByteArray();
    }

    /* Retransforms the specified class modifying the code for sink, source, and taintThrough methods. Called by <clinit>. Stores
     * classes until the VM is initialized at which point all stored classes are retransformed. */
    public static void retransform(Class<?> clazz) {
        try{
            // Check if VM is initialized, that PreMain's instrumentation has been set by a call to premain and that
            // Configuration.init() has been called to initialize the configuration
        	if(INITED && PreMain.getInstrumentation() != null && isBasicSourceSinkManagerInit == true) {
        	    retransformQueue.add(clazz);
                // Retransform clazz and any classes that were initialized before retransformation could occur.
        	    while(!retransformQueue.isEmpty()) {
        	        Class<?> poppedClazz = retransformQueue.pop();
                    // If poppedClazz represents a class or interface that is or is a subtype of a class or interface with
                    // at least one method labeled as being a sink or source or taintThrough method
        	        if(BasicSourceSinkManager.getInstance().isSourceOrSinkOrTaintThrough(poppedClazz)) {
                        PreMain.getInstrumentation().retransformClasses(poppedClazz);
                    }
                }
            } else {
        	    retransformQueue.add(clazz);
            }
        } catch (UnmodifiableClassException e) {
            //
        }
    }
}
