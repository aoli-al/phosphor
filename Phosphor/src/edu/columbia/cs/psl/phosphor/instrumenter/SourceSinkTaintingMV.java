package edu.columbia.cs.psl.phosphor.instrumenter;


import edu.columbia.cs.psl.phosphor.BasicSourceSinkManager;
import edu.columbia.cs.psl.phosphor.Instrumenter;
import edu.columbia.cs.psl.phosphor.SourceSinkManager;
import edu.columbia.cs.psl.phosphor.org.objectweb.asm.MethodVisitor;
import edu.columbia.cs.psl.phosphor.org.objectweb.asm.Opcodes;
import edu.columbia.cs.psl.phosphor.org.objectweb.asm.Type;
import edu.columbia.cs.psl.phosphor.org.objectweb.asm.commons.InstructionAdapter;
import edu.columbia.cs.psl.phosphor.runtime.TaintChecker;
import edu.columbia.cs.psl.phosphor.struct.TaintedPrimitive;
import edu.columbia.cs.psl.phosphor.struct.TaintedPrimitiveArray;
import edu.columbia.cs.psl.phosphor.struct.multid.MultiDTaintedArray;

public class SourceSinkTaintingMV extends InstructionAdapter implements Opcodes{
	static SourceSinkManager sourceSinkManager = BasicSourceSinkManager.getInstance(Instrumenter.callgraph);

	String owner;
	String name;
	String desc;
	boolean thisIsASource;
	boolean thisIsASink;
	String origDesc;
	int access;
	boolean isStatic;
	public SourceSinkTaintingMV(MethodVisitor mv,int access, String owner, String name, String desc, String origDesc) {
		super(ASM5,mv);
		this.owner = owner;
		this.name = name;
		this.desc = desc;
		this.access =access;
		this.origDesc = origDesc;
		this.thisIsASource = sourceSinkManager.isSource(owner,name,desc);
		this.thisIsASink = sourceSinkManager.isSink(owner, name, desc);
		this.isStatic = (access & Opcodes.ACC_STATIC) != 0;
		if(this.thisIsASource)
			System.out.println("Source: " + owner+"."+name+desc);
		if(this.thisIsASink)
			System.out.println("Sink: " + owner+"."+name+desc);
	}

	@Override
	public void visitCode() {
		super.visitCode();
		if(this.thisIsASource)
		{
			Type[] args = Type.getArgumentTypes(desc);
			int idx = 0;
			if(!isStatic)
				idx++;
			boolean skipNextArray  =false;
			for(int i = 0; i < args.length; i++)
			{
				if(args[i].getSort() == Type.OBJECT)
				{
					super.visitVarInsn(ALOAD, idx);
					super.visitInsn(ICONST_1);
					super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(TaintChecker.class), "setTaints", "(Ljava/lang/Object;I)V", false);
				}
				else if(!skipNextArray && args[i].getSort() == Type.ARRAY && args[i].getElementType().getSort() != Type.OBJECT && args[i].getDimensions() == 1)
				{
					skipNextArray = true;
					super.visitVarInsn(ALOAD, idx);
					super.visitInsn(ICONST_1);
					super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(TaintChecker.class), "setTaints", "([II)V",false);
				}
				else if(skipNextArray)
					skipNextArray = false;
				idx += args[i].getSize();
			}
		}
		if(sourceSinkManager.isSink(owner,name,desc))
		{
			//TODO - check every arg to see if is taint tag
			Type[] args = Type.getArgumentTypes(desc);
			int idx = 0;
			if(!isStatic)
				idx++;
			boolean skipNextPrimitive =false;
			for(int i = 0; i < args.length; i++)
			{
				if(args[i].getSort() == Type.OBJECT || args[i].getSort() == Type.ARRAY)
				{
					if(args[i].getSort() == Type.ARRAY && args[i].getElementType().getSort() != Type.OBJECT && args[i].getDimensions() == 1)
					{
						if(!skipNextPrimitive)
						{
							super.visitVarInsn(ALOAD, idx);
							super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(TaintChecker.class), "checkTaint", "(Ljava/lang/Object;)V", false);
						}
						skipNextPrimitive = !skipNextPrimitive;
					}
					else{
					super.visitVarInsn(ALOAD, idx);
					super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(TaintChecker.class), "checkTaint", "(Ljava/lang/Object;)V", false);
					}
				}
				else if(!skipNextPrimitive)
				{
					super.visitVarInsn(ILOAD, idx);
					super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(TaintChecker.class), "checkTaint", "(I)V", false);
					skipNextPrimitive = true;
				}
				else if(skipNextPrimitive)
					skipNextPrimitive = false;
				idx += args[i].getSize();
			}
		}
	}
	@Override
	public void visitInsn(int opcode) {
		if(opcode == ARETURN && this.thisIsASource)
		{
			Type returnType = Type.getReturnType(this.origDesc);
			if(returnType.getSort() == Type.OBJECT || returnType.getSort() == Type.ARRAY)
			{
				super.visitInsn(DUP);
				super.visitInsn(ICONST_1);
				super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(TaintChecker.class), "setTaints", "(Ljava/lang/Object;I)V", false);
			}
			else if(returnType.getSort() == Type.VOID)
			{
				
			}
			else
			{
				//primitive
				super.visitInsn(DUP);
				super.visitInsn(ICONST_1);
				super.visitFieldInsn(PUTFIELD, Type.getInternalName(TaintedPrimitive.class), "taint", "I");
			}
		}
		super.visitInsn(opcode);
	}
}