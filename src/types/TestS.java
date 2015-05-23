package types;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.MethodGen;

import absyn.CodeDeclaration;
import absyn.Test;
import translation.Block;

// TestSignature
public class TestS extends CodeSignature {
	private final String name;
	
	public TestS(ClassType clazz, Type returnType, TypeList parameters,
			String name, Test abstractSyntax) {
		
		
		super(clazz, VoidType.INSTANCE, TypeList.EMPTY, name, abstractSyntax);
		this.name = name;
	}

	
	@Override
	public String toString() {
		return getDefiningClass() + name + "dinutest";
	}
	
	
	@Override
	protected Block addPrefixToCode(Block code) {
		
		return code;
	}
	
	/**
	 * Generates an {@code invokevirtual} Java bytecode that calls this
	 * method. The Java {@code invokevirtual} bytecode calls a method by using
	 * the run-time class of the receiver to look up for the method's implementation.
	 *
	 * @param classGen the class generator to be used to generate
	 *                 the {@code invokevirtual} Java bytecode
	 * @return an {@code invokevirtual} Java bytecode that calls this method
	 */

	public INVOKEVIRTUAL createINVOKEVIRTUAL(JavaClassGenerator classGen) {
		return (INVOKEVIRTUAL) createInvokeInstruction(classGen, Constants.INVOKEVIRTUAL);
	}
	
	public void createMethod(JavaClassGenerator classGen) {
		MethodGen methodGen;
		if (getName().equals("main"))
			methodGen = new MethodGen
				(Constants.ACC_PUBLIC | Constants.ACC_STATIC, // public and static
				org.apache.bcel.generic.Type.VOID, // return type
				new org.apache.bcel.generic.Type[] // parameters
					{ new org.apache.bcel.generic.ArrayType("java.lang.String", 1) },
				null, // parameters names: we do not care
				"main", // method's name
				classGen.getClassName(), // defining class
				classGen.generateJavaBytecode(getCode()), // bytecode of the method
				classGen.getConstantPool()); // constant pool
		else
			methodGen = new MethodGen
				(Constants.ACC_PUBLIC, // public
				getReturnType().toBCEL(), // return type
				getParameters().toBCEL(), // parameters types, if any
				null, // parameters names: we do not care
				getName(), // method's name
				classGen.getClassName(), // defining class
				classGen.generateJavaBytecode(getCode()), // bytecode of the method
				classGen.getConstantPool()); // constant pool

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		methodGen.setMaxStack();
		methodGen.setMaxLocals();

		// we add a method to the class that we are generating
		classGen.addMethod(methodGen.getMethod());
	}

}
