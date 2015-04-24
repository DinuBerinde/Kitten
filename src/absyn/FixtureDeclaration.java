package absyn;

import java.io.FileWriter;
import java.io.IOException;

import types.ClassMemberSignature;
import types.ClassType;

public class FixtureDeclaration extends ClassMemberDeclaration {
	private final Command body;
	
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void typeCheckAux(ClassType currentClass) {
		// TODO Auto-generated method stub

	}

	private Command getBody(){
		return this.body;
	}
}
