package types;

import javaBytecodeGenerator.TestClassGenerator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.MethodGen;

import absyn.CodeDeclaration;
import translation.Block;

/**
 * The signature of a fixture of a Kitten class.
 *
 * @author Dinu
 */
public class FixtureSignature extends CodeSignature {
	private static int counter = 0;
	private final int identifier;

	/**
	 * Constructs the signature of a fixture with the given name, return type
	 * and parameters types.
	 *
	 * @param clazz the class where this fixture is defined
	 * @param abstractSyntax the abstract syntax of the declaration of this fixture
	 */
	public FixtureSignature(ClassType clazz, CodeDeclaration abstractSyntax) {

		super(clazz, VoidType.INSTANCE, TypeList.EMPTY, "fixture", abstractSyntax);

		this.identifier = counter++;
	}



	@Override
	public String toString() {
		return getDefiningClass() + "." + "Fixture:" + identifier ;			
	}

	@Override
	protected Block addPrefixToCode(Block code) {

		return code;
	}



	public void createFixture(TestClassGenerator classGen) {
		TypeList type = new TypeList( this.getDefiningClass().getObjectType(), null);
		
		MethodGen methodGen = new MethodGen
				(Constants.ACC_PRIVATE | Constants.ACC_STATIC, // private and static
						org.apache.bcel.generic.Type.VOID, // return type
						type.toBCEL(), // parameters types 
						null, // parameters names: we do not care
						null, // name of the fixture
						classGen.getClassName(), // name of the class
						classGen.generateJavaBytecode(getCode()), // bytecode of the test
						classGen.getConstantPool()); // constant pool

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		methodGen.setMaxStack();
		methodGen.setMaxLocals();
		
		classGen.addMethod(methodGen.getMethod());
	}

}
