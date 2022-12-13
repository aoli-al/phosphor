package edu.columbia.cs.psl.phosphor.instrumenter;

import edu.columbia.cs.psl.phosphor.Configuration;
import edu.columbia.cs.psl.phosphor.runtime.PhosphorStackFrame;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * A MethodVisitor that patches frames to add local variables that store taint
 * tags for each
 * slot on the operand stack.
 *
 * Adjusts each stack frame to have space to hold MAX_STACK shadows, adjusts
 * local variable accesses
 * so that they refer to the correct spot.
 *
 * The new shadow variables are added after all arguments.
 */
public class LocalVariableAdder extends MethodVisitor {
    /**
     * Local variable inidices - considers long/double as taking 2 spots
     */
    private int indexOfFirstNonArgLV;
    private int indexOfFirstStackTaintTag;
    private int indexOfLastStackTaintTag;
    private int numLocalVariablesAddedAfterArgs;
    private int indexOfPhosphorStackData;
    private int indexOfStackDataNeedsPoppingLV;

    /**
     * Stackmap frame indices - considers long/double as taking just one spot
     */
    private int nArgs;
    private int indexOfPhosphorStackDataForStackFrame;
    private int indexOfPhosphorStackDataNeedsPoppingForStackFrame;
    private int indexOfFirstStackTaintTagForStackFrames;
    private int indexOfLastStackTaintTagForStackFrames;
    private Label firstLabel = new Label();
    private String descriptor;

    public LocalVariableAdder(MethodVisitor mv, boolean isStatic, String desc) {
        super(Configuration.ASM_VERSION, mv);
        indexOfFirstNonArgLV = isStatic ? 0 : 1;
        Type[] args = Type.getArgumentTypes(desc);
        for (Type t : args) {
            indexOfFirstNonArgLV += t.getSize();
        }
        nArgs = args.length + (isStatic ? 0 : 1);
        this.descriptor = desc;
        this.indexOfPhosphorStackData = indexOfFirstNonArgLV; //double/long counts as two
        this.indexOfStackDataNeedsPoppingLV = indexOfFirstNonArgLV + 1;
        this.indexOfFirstStackTaintTag = this.indexOfStackDataNeedsPoppingLV + 1;

        this.indexOfPhosphorStackDataForStackFrame = nArgs; //double/long counts as one
        this.indexOfPhosphorStackDataNeedsPoppingForStackFrame = nArgs + 1;
        this.indexOfFirstStackTaintTagForStackFrames = this.indexOfPhosphorStackDataNeedsPoppingForStackFrame + 1;

        this.numLocalVariablesAddedAfterArgs = 2;
    }

    public void setMaxStack(int maxStack) {
        this.indexOfLastStackTaintTag = this.indexOfFirstStackTaintTag + maxStack;
        this.indexOfLastStackTaintTagForStackFrames = this.indexOfFirstStackTaintTagForStackFrames + maxStack;
        this.numLocalVariablesAddedAfterArgs += maxStack;
    }

    public int getIndexOfFirstStackTaintTag() {
        return indexOfFirstStackTaintTag;
    }

    public int getIndexOfLastStackTaintTag() {
        return indexOfLastStackTaintTag;
    }

    public int getIndexOfStackDataNeedsPoppingLV() {
        return indexOfStackDataNeedsPoppingLV;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        super.visitLabel(firstLabel);
    }

    private int remap(int var) {
        if (var < indexOfFirstNonArgLV) {
            return var;
        }
        return var + this.numLocalVariablesAddedAfterArgs;
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        // Expand the locals to add all of the newly created
        Object[] newLocals = new Object[Math.max(numLocal + numLocalVariablesAddedAfterArgs, this.indexOfLastStackTaintTagForStackFrames)];
        // Arguments
        System.arraycopy(local, 0, newLocals, 0, Math.min(nArgs, local.length));

        newLocals[this.indexOfPhosphorStackDataForStackFrame] = PhosphorStackFrame.INTERNAL_NAME;
        newLocals[this.indexOfPhosphorStackDataNeedsPoppingForStackFrame] = Opcodes.INTEGER;
        // Added slots for tags
        for (int i = this.indexOfFirstStackTaintTagForStackFrames; i < this.indexOfLastStackTaintTagForStackFrames; i++) {
            newLocals[i] = Configuration.TAINT_TAG_STACK_TYPE;
        }

        // Non-arguments, shifted right
        if (nArgs < local.length) {
            System.arraycopy(local, nArgs, newLocals,
                    this.indexOfLastStackTaintTagForStackFrames, numLocal - nArgs);
        }

        super.visitFrame(type, numLocal + numLocalVariablesAddedAfterArgs, newLocals, numStack, stack);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, remap(var));
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        super.visitIincInsn(remap(var), increment);
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end,
            int index) {
        super.visitLocalVariable(name, descriptor, signature, start, end, remap(index));
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        Label endLabel = new Label();
        super.visitLabel(endLabel);
        super.visitLocalVariable("phosphorStackFrame",
                PhosphorStackFrame.DESCRIPTOR, null, firstLabel, endLabel,
                this.indexOfPhosphorStackData);
        super.visitLocalVariable("phosphorStackFrameShouldBeClearedAtEnd",
                "Z", null, firstLabel, endLabel,
                this.indexOfStackDataNeedsPoppingLV);
        for (int i = this.indexOfFirstStackTaintTag; i < this.indexOfLastStackTaintTag; i++) {
            super.visitLocalVariable("phosphorStackTaint" + (i - this.indexOfFirstStackTaintTag),
                    Configuration.TAINT_TAG_DESC, null, firstLabel, endLabel, i);
        }
        super.visitMaxs(maxStack, maxLocals);
    }

    public int getNumLocalVariablesAddedAfterArgs() {
        return this.numLocalVariablesAddedAfterArgs;
    }

    public int getIndexOfPhosphorStackData() {
        return indexOfPhosphorStackData;
    }
}
