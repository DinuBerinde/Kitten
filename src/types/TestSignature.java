package types;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;

import javaBytecodeGenerator.TestClassGenerator;
import absyn.TestDeclaration;
import translation.Block;

/**
 * The signature of a test of a Kitten class.
 *
 * @author Dinu
 */
public class TestSignature extends CodeSignature {
	private final String name;


	/**
	 * Constructs the signature of a test with the given name, return type
	 * and parameters types.
	 *
	 * @param clazz the class where this test is defined
	 * @param name the name of the test
	 * @param abstractSyntax the abstract syntax of the declaration of this test
	 */
	public TestSignature(ClassType clazz, String name, TestDeclaration abstractSyntax) {

		super(clazz, VoidType.INSTANCE, TypeList.EMPTY, name, abstractSyntax);
		this.name = name;
	}


	@Override
	public String toString() {
		return getDefiningClass() + "Test:" + name;
	}



	@Override
	protected Block addPrefixToCode(Block code) {

		return code;
	}

	/**
	 * Adds to the given class generator a Java bytecode method for this test.
	 *
	 * @param classGen the generator of the class where the test lives
	 */
	public void createTest(TestClassGenerator classGen) {
		// we build the type of the test
		TypeList type = new TypeList(this.getDefiningClass(), TypeList.EMPTY);

		InstructionList il = classGen.generateJavaBytecode(this.getCode());
		ConstantPoolGen cp = classGen.getConstantPool();

		InstructionHandle[] ih = new InstructionHandle[il.getInstructionHandles().length];
		ih = il.getInstructionHandles();

		// we prepare the new bytecode of the test
		// we get the number and name of the failed tests
		for(int i = 0; i < ih.length; i++){

			if(ih[i].getInstruction().getOpcode() == Constants.LDC){	
				LDC l = (LDC) ih[i].getInstruction();
				String failed = (String) l.getValue(cp);

				if( failed.length() > 12 ){
					String s =  failed.substring(0, 12);
					int ind = failed.indexOf(':');
					String rowCol = failed.substring(ind + 1, failed.length() - 1);
					
					if(s.equals("test fallito")){	
						
						// we save the name of the failed test
						il.append(ih[i].getInstruction(), classGen.getFactory().createPutStatic(classGen.getClassName(), "flag", Type.STRING));
						il.append(ih[i].getInstruction(), new PUSH(cp, "\t -" + this.getName() + " failed" + " at" + rowCol + " ["));

						// we increment the counter of failedTests
						il.append(ih[i].getInstruction(), classGen.getFactory().createPutStatic(classGen.getClassName(), "failedTest", Type.INT));
						il.append(ih[i].getInstruction(), new IADD());
						il.append(ih[i].getInstruction(), new PUSH(cp, 1));
						il.append(ih[i].getInstruction(),classGen.getFactory().createGetStatic(classGen.getClassName(), "failedTest", Type.INT));

					}
				}
			}
		}

		MethodGen methodGen = new MethodGen
				(Constants.ACC_PRIVATE | Constants.ACC_STATIC, // private and static
						org.apache.bcel.generic.Type.VOID, // return type
						type.toBCEL(), // parameters types 
						null, // parameters names: we do not care
						name, // name of the test
						classGen.getClassName(), // name of the class
						il, // bytecode of the test
						classGen.getConstantPool()); // constant pool

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		methodGen.setMaxStack();
		methodGen.setMaxLocals();

		classGen.addMethod(methodGen.getMethod());
	}



	
}

