package absyn;

import java.io.FileWriter;
import java.io.IOException;

import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.FixtureSignature;

public class FixtureDeclaration extends ClassMemberDeclaration {
	private final Command body;
	 /**
     * The intermediate Kitten code for this constructor or method.
     * This is {@code null} if this constructor or method has not been
     * translated yet.
     */

    private Block code;
    private FixtureSignature sig;
	
	//new FixtureDeclaration(xleft, body, next);
	public FixtureDeclaration(int pos, Command body, ClassMemberDeclaration next){
		super(pos, next);
		
		this.body = body;
	}
	
	@Override
	public ClassMemberSignature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void toDotAux(FileWriter where) throws IOException {	
		linkToNode("body", getBody().toDot(where), where);
	}

	@Override
	protected void addTo(ClassType clazz) {
		this.sig = new FixtureSignature(clazz, this);
		clazz.addFixture(sig);
	}

	@Override
	protected void typeCheckAux(ClassType currentClass) {
		// TODO Auto-generated method stub

	}

	private Command getBody(){
		return this.body;
	}
	
	private Block getBlock(){
		return code;
	}
}
