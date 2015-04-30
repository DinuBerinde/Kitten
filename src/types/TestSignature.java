package types;

import absyn.ClassMemberDeclaration;

public class TestSignature extends ClassMemberSignature {
	
	/**
	 * Nome del test.
	 */
	private final String name;
	
	public TestSignature(String name, ClassType clazz, ClassMemberDeclaration abstractSyntax){
		super(clazz, abstractSyntax);
		
		this.name = name;
	}

	public String toString(){
		return this.getDefiningClass() + ":" + name;
	}
	
	
}
