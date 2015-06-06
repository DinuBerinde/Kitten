package types;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.MethodGen;

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
	private String assertName;
	
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

public void assertName(String name){
	this.assertName = name;
}

public String getAssertName(){
	return assertName;
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

		MethodGen methodGen = new MethodGen
				(Constants.ACC_PRIVATE | Constants.ACC_STATIC, // private and static
						org.apache.bcel.generic.Type.VOID, // return type
						type.toBCEL(), // parameters types 
						null, // parameters names: we do not care
						name, // name of the test
						classGen.getClassName(), // name of the class
						classGen.generateJavaBytecode(this.getCode()), // bytecode of the test
						classGen.getConstantPool()); // constant pool

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		methodGen.setMaxStack();
		methodGen.setMaxLocals();

		classGen.addMethod(methodGen.getMethod());
	}

}
