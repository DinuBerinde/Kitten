package absyn;

import java.io.FileWriter;
import java.io.IOException;

import semantical.TypeChecker;
import translation.Block;
import types.ClassType;
import types.ConstructorSignature;
import types.NilType;
import types.TestS;
import types.TestSignature;
import types.TypeList;
import types.VoidType;

public class Test extends CodeDeclaration {
	private final String name;
	private TestS sig;
	private Block code;
	
	public Test(int pos, FormalParameters formals, Command body,
			ClassMemberDeclaration next, String name){
		
		super(pos, null, body, next);
		
		this.name = name;
		
	}
	
	/**
	 * Yields the signature of this constructor declaration.
	 *
	 * @return the signature of this constructor declaration.
	 *         Yields {@code null} if type-checking has not been performed yet
	 */

	@Override
	public TestS getSignature() {
		return (TestS) super.getSignature();
	}

	@Override
	protected void toDotAux(FileWriter where) throws IOException {
		linkToNode("name", toDot(name, where), where);
		linkToNode("body", getBody().toDot(where), where);

	}

	@Override
	protected void addTo(ClassType clazz) {
		this.sig = new TestS(clazz, VoidType.INSTANCE,TypeList.EMPTY, name, this);
		clazz.addTest(name, sig);
		
		this.setSignature(sig);

	}

	@Override
	protected void typeCheckAux(ClassType clazz) {
		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg(), true);
		checker = checker.putVar("this", clazz);

		getBody().typeCheck(checker, name);
		getBody().checkForDeadcode();

	}

}
