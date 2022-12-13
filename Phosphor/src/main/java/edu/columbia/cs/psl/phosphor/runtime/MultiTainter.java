package edu.columbia.cs.psl.phosphor.runtime;

import edu.columbia.cs.psl.phosphor.Configuration;
import edu.columbia.cs.psl.phosphor.control.ControlFlowStack;
import edu.columbia.cs.psl.phosphor.runtime.proxied.InstrumentedJREFieldHelper;
import edu.columbia.cs.psl.phosphor.struct.*;

public final class MultiTainter {

    private MultiTainter() {
        // Prevents this class from being instantiated
    }

    public static ControlFlowStack getControlFlow() {
        return PhosphorStackFrame.forMethod(null).controlFlowTags;
    }

    public static boolean taintedBoolean(boolean in, Object lbl) {
        PhosphorStackFrame.forMethod(null).setReturnTaint(Taint.withLabel(lbl));
        return in;
    }

    public static byte taintedByte(byte in, Object lbl) {
        PhosphorStackFrame.forMethod(null).setReturnTaint(Taint.withLabel(lbl));
        return in;
    }

    public static char taintedChar(char in, Object lbl) {
        PhosphorStackFrame.forMethod(null).setReturnTaint(Taint.withLabel(lbl));
        return in;
    }

    public static double taintedDouble(double in, Object lbl) {
        PhosphorStackFrame.forMethod(null).setReturnTaint(Taint.withLabel(lbl));
        return in;
    }

    public static float taintedFloat(float in, Object lbl) {
        PhosphorStackFrame.forMethod(null).setReturnTaint(Taint.withLabel(lbl));
        return in;
    }

    public static long taintedLong(long in, Object lbl) {
        PhosphorStackFrame.forMethod(null).setReturnTaint(Taint.withLabel(lbl));
        return in;
    }

    public static int taintedInt(int in, Object lbl) {
        PhosphorStackFrame.forMethod(null).setReturnTaint(Taint.withLabel(lbl));
        return in;
    }

    public static short taintedShort(short in, Object lbl) {
        PhosphorStackFrame.forMethod(null).setReturnTaint(Taint.withLabel(lbl));
        return in;
    }

    public static boolean[] taintedBooleanArray(boolean[] in, Object lbl) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        TaggedArray wrapper = phosphorStackFrame.getArgWrapper(0, in);
        taintedArray(wrapper, lbl);
        phosphorStackFrame.setWrappedReturn(wrapper);
        return in;
    }

    public static byte[] taintedByteArray(byte[] in, Object lbl) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        TaggedArray wrapper = phosphorStackFrame.getArgWrapper(0, in);
        taintedArray(wrapper, lbl);
        phosphorStackFrame.setWrappedReturn(wrapper);
        return in;
    }

    public static char[] taintedCharArray(char[] in, Object lbl) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        TaggedArray wrapper = phosphorStackFrame.getArgWrapper(0, in);
        taintedArray(wrapper, lbl);
        phosphorStackFrame.setWrappedReturn(wrapper);
        return in;
    }

    public static double[] taintedDoubleArray(double[] in, Object lbl) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        TaggedArray wrapper = phosphorStackFrame.getArgWrapper(0, in);
        taintedArray(wrapper, lbl);
        phosphorStackFrame.setWrappedReturn(wrapper);
        return in;
    }

    public static float[] taintedFloatArray(float[] in, Object lbl) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        TaggedArray wrapper = phosphorStackFrame.getArgWrapper(0, in);
        taintedArray(wrapper, lbl);
        phosphorStackFrame.setWrappedReturn(wrapper);
        return in;
    }

    public static long[] taintedLongArray(long[] in, Object lbl) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        TaggedArray wrapper = phosphorStackFrame.getArgWrapper(0, in);
        taintedArray(wrapper, lbl);
        phosphorStackFrame.setWrappedReturn(wrapper);
        return in;
    }

    public static int[] taintedIntArray(int[] in, Object lbl) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        TaggedArray wrapper = phosphorStackFrame.getArgWrapper(0, in);
        taintedArray(wrapper, lbl);
        phosphorStackFrame.setWrappedReturn(wrapper);
        return in;
    }

    public static short[] taintedShortArray(short[] in, Object lbl) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        TaggedArray wrapper = phosphorStackFrame.getArgWrapper(0, in);
        taintedArray(wrapper, lbl);
        phosphorStackFrame.setWrappedReturn(wrapper);
        return in;
    }

    public static Taint getTaint(boolean in) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        phosphorStackFrame.setReturnTaint(Taint.emptyTaint());
        return phosphorStackFrame.getArgTaint(0);
    }

    public static Taint getTaint(byte in) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        phosphorStackFrame.setReturnTaint(Taint.emptyTaint());
        return phosphorStackFrame.getArgTaint(0);
    }

    public static Taint getTaint(char in) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        phosphorStackFrame.setReturnTaint(Taint.emptyTaint());
        return phosphorStackFrame.getArgTaint(0);
    }

    public static Taint getTaint(double in) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        phosphorStackFrame.setReturnTaint(Taint.emptyTaint());
        return phosphorStackFrame.getArgTaint(0);
    }

    public static Taint getTaint(float in) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        phosphorStackFrame.setReturnTaint(Taint.emptyTaint());
        return phosphorStackFrame.getArgTaint(0);
    }

    public static Taint getTaint(int in) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        phosphorStackFrame.setReturnTaint(Taint.emptyTaint());
        return phosphorStackFrame.getArgTaint(0);
    }

    public static Taint getTaint(long in) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        phosphorStackFrame.setReturnTaint(Taint.emptyTaint());
        return phosphorStackFrame.getArgTaint(0);
    }

    public static Taint getTaint(short in) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        phosphorStackFrame.setReturnTaint(Taint.emptyTaint());
        return phosphorStackFrame.getArgTaint(0);
    }

    public static Taint getTaint(Object in) {
        PhosphorStackFrame phosphorStackFrame = PhosphorStackFrame.forMethod(null);
        phosphorStackFrame.setReturnTaint(Taint.emptyTaint());
        return phosphorStackFrame.getArgTaint(0);
    }

    private static void taintedArray(TaggedArray in, Object lbl) {
        if(in.taints == null) {
            in.taints = new Taint[in.getLength()];
        }
        for(int i = 0; i < in.getLength(); i++) {
            in.taints[i] = Taint.withLabel(lbl);
        }
    }


    public static <T> T taintedReference(T toTaint, Object lbl) {
        PhosphorStackFrame.forMethod(null).setReturnTaint(Taint.withLabel(lbl));
        return toTaint;
    }

    public static Taint[] getStringCharTaints(String str) {
        if(str == null) {
            return null;
        }
        if(Configuration.IS_JAVA_8){
            return InstrumentedJREFieldHelper.JAVA_8getvaluePHOSPHOR_WRAPPER(str).taints;
        } else {
            return InstrumentedJREFieldHelper.getvaluePHOSPHOR_WRAPPER(str).taints;
        }
    }

    public static void setStringCharTaints(String str, Taint[] tags) {
        if(str == null) {
            return;
        }
        if(Configuration.IS_JAVA_8){
            TaggedCharArray onStr = InstrumentedJREFieldHelper.JAVA_8getvaluePHOSPHOR_WRAPPER(str);
            if(onStr == null){
                InstrumentedJREFieldHelper.JAVA_8setvaluePHOSPHOR_WRAPPER(str, TaggedCharArray.factory(InstrumentedJREFieldHelper.JAVA_8getvalue(str)));
            }
            onStr.taints = tags;
        } else {
            TaggedByteArray onStr = InstrumentedJREFieldHelper.getvaluePHOSPHOR_WRAPPER(str);
            if(onStr == null){
                InstrumentedJREFieldHelper.setvaluePHOSPHOR_WRAPPER(str, TaggedByteArray.factory(InstrumentedJREFieldHelper.getvalue(str)));
            }
            onStr.taints = tags;
        }
    }

    @Deprecated
    public static void taintedObject(Object obj, Taint tag) {
        if(obj instanceof TaintedWithObjTag) {
            ((TaintedWithObjTag) obj).setPHOSPHOR_TAG(tag);
        } else if(obj instanceof TaggedArray) {
            // TODO
        } else if(obj != null && ArrayHelper.engaged == 1) {
            ArrayHelper.setTag(obj, tag);
        }
    }

    /* If the specified object is a one-dimensional primitive array sets the taint of each element in the array to be
     * the specified tag. */
    public static void setPrimitiveArrayTaints(Object obj, Taint tag) {
        if(obj instanceof TaggedArray) {
            ((TaggedArray) obj).setTaints(tag);
        }
    }

    /* If multi-tainted and the specified object is a multi-dimensional array returns a Taint tag containing the taints of
     * all of its elements, otherwise returns the object's taint. */
    public static Taint getMergedTaint(Object obj) {
        //TODO reference taints?
        if(obj instanceof TaggedReferenceArray) {
            Object[] ar = ((TaggedReferenceArray) obj).val;
            Taint[] taints = new Taint[ar.length];
            int i = 0;
            for(Object el : ar) {
                taints[i] = getMergedTaint(el);
            }
            return Taint.combineTaintArray(taints);
        } else if(obj instanceof TaggedArray) {
            return Taint.combineTaintArray(((TaggedArray) obj).taints);
        } else if(obj instanceof Object[]) {
            throw new IllegalStateException("Object[] should not exist!");
        } else {
            // return getTaint(obj);
            return Taint.emptyTaint();
        }
    }

}
