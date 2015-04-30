package absyn;

import java.io.FileWriter;
import java.io.IOException;

import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.TestSignature;

public class TestDeclaration extends ClassMemberDeclaration {
	private final String name;
	private final Command body;
	private TestSignature sig;
	 /**
     * The intermediate Kitten code for this constructor or method.
     * This is {@code null} if this constructor or method has not been
     * translated yet.
     */

    private Block code;
	
	public TestDeclaration(int pos, String name, Command body, ClassMemberDeclaration next){
		super(pos, next);
		
		this.name = name;
		this.body = body;
	}

	@Override
	public ClassMemberSignature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void toDotAux(FileWriter where) throws IOException {
		linkToNode("name", toDot(name, where), where);
		linkToNode("body", getBody().toDot(where), where);
	}

	@Override
	protected void addTo(ClassType clazz) {
		this.sig = new TestSignature(name, clazz, this);
		clazz.addTest(name, sig);
	}

	@Override
	protected void typeCheckAux(ClassType currentClass) {
		// TODO Auto-generated method stub

	}

	private Command getBody(){
		return body;
	}
	
	private Block getBlock(){
		return code;
	}
}
