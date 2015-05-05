package types;

import absyn.ClassMemberDeclaration;

public class TestSignature extends ClassMemberSignature {
	
	/**
	 * Nome del test.
	 */
	private final String name;
	
	
	/**
	 * Costruisce la segnature del Test con il nome name.
	 * @param name Nome del test.
	 * @param clazz La classe dov'è definito il test.
	 * @param abstractSyntax La sintassi astratta del test.
	 */
	public TestSignature(String name, ClassType clazz, ClassMemberDeclaration abstractSyntax){
		super(clazz, abstractSyntax);
		
		this.name = name;
	}

	public String toString(){
		return this.getDefiningClass() + ":" + name;
	}
	
	
}
