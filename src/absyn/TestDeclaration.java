package absyn;

import java.io.FileWriter;
import java.io.IOException;

import semantical.TypeChecker;
import translation.Block;
import types.ClassType;
import types.TestSignature;
import types.VoidType;

/**
 * A node of abstract syntax representing the declaration of a test of a Kitten class.
 *
 * @author Dinu
 */
public class TestDeclaration extends CodeDeclaration {
	private final String name;
	private TestSignature sig;
	private Block code;
	
	/**
	 * Constructs the abstract syntax of a test declaration.
	 * 
	 * @param pos the starting position in the source file of
	 *            the concrete syntax represented by this abstract syntax
	 * @param name the name of the test
	 * @param body the abstract syntax of the body of the method
	 * @param next the abstract syntax of the declaration of the
	 *             subsequent class member, if any
	 */
	public TestDeclaration(int pos, Command body, ClassMemberDeclaration next, String name){
		
		super(pos, null, body, next);
		
		this.name = name;
		
	}
	
	
	/**
	 * Adds arcs between the dot node for this piece of abstract syntax
	 *
	 * @param where the file where the dot representation must be written
	 */
	@Override
	protected void toDotAux(FileWriter where) throws IOException {
		linkToNode("name", toDot(name, where), where);
		linkToNode("body", getBody().toDot(where), where);

	}

	
	/**
	 * Adds the signature of this test declaration to the given class.
	 *
	 * @param clazz the class where the signature of this test declaration must be added
	 */
	@Override
	protected void addTo(ClassType clazz) {
		this.sig = new TestSignature(clazz, name, this);
		clazz.addTest(name, sig);
		
		this.setSignature(sig);
	}

	
	/**
	 * Type-checks this test declaration.
	 */
	@Override
	protected void typeCheckAux(ClassType clazz) {
		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg(), true);
		checker = checker.putVar("this", clazz);

		getBody().typeCheck(checker, name);
		getBody().checkForDeadcode();

	}
	
	/**
	 * Yields the signature of this test declaration.
	 *
	 * @return the signature of this test declaration.
	 *         Yields {@code null} if type-checking has not been performed yet
	 */
	@Override
	public TestSignature getSignature() {
		return (TestSignature) super.getSignature();
	}

	
	public Block getCode() {
		return code;
	}

	public void setCode(Block code) {
		this.code = code;
	}

}
