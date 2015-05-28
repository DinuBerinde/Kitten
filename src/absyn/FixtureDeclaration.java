package absyn;

import java.io.FileWriter;
import java.io.IOException;

import semantical.TypeChecker;
import types.ClassType;
import types.FixtureSignature;
import types.VoidType;

/**
 * A node of abstract syntax representing the declaration of a fixture of a Kitten class.
 *
 * @author Dinu
 */
public class FixtureDeclaration extends CodeDeclaration {
	private FixtureSignature sig;
	
	/**
	 * Constructs the abstract syntax of a fixture declaration.
	 * 
	 * @param pos the starting position in the source file of
	 *            the concrete syntax represented by this abstract syntax
	 * @param body the abstract syntax of the body of the method
	 * @param next the abstract syntax of the declaration of the
	 *             subsequent class member, if any
	 */
	public FixtureDeclaration(int pos, Command body, ClassMemberDeclaration next) {
		super(pos, null, body, next);
	}

	/**
	 * Adds arcs between the dot node for this piece of abstract syntax
	 *
	 * @param where the file where the dot representation must be written
	 */
	@Override
	protected void toDotAux(FileWriter where) throws IOException {
		linkToNode("body", getBody().toDot(where), where);
	}


	/**
	 * Adds the signature of this fixture declaration to the given class.
	 *
	 * @param clazz the class where the signature of this fixture declaration must be added
	 */
	@Override
	protected void addTo(ClassType clazz) {
		this.sig = new FixtureSignature(clazz, this);
		clazz.addFixture(sig);

		// we record the signature of this method inside this abstract syntax
		setSignature(sig);	
	}

	/**
	 * Type-checks this fixture declaration.
	 */
	@Override
	protected void typeCheckAux(ClassType clazz) {
		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg(), false);
		checker = checker.putVar("this", clazz);

		getBody().typeCheck(checker, "fixture");
		getBody().checkForDeadcode();
	}

	/**
	 * Yields the signature of this fixture declaration.
	 *
	 * @return the signature of this fixture declaration. Yields {@code null}
	 *         if type-checking has not been performed yet
	 */

	@Override
	public FixtureSignature getSignature() {
		return (FixtureSignature) super.getSignature();
	}

}
