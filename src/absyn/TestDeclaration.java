package absyn;

import java.io.FileWriter;
import java.io.IOException;

import types.ClassMemberSignature;
import types.ClassType;

public class TestDeclaration extends ClassMemberDeclaration {
	private final String name;
	private final Command body;
	
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void typeCheckAux(ClassType currentClass) {
		// TODO Auto-generated method stub

	}

	private Command getBody(){
		return body;
	}
}
