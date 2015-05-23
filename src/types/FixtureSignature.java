package types;

import translation.Block;
import absyn.ClassMemberDeclaration;

public class FixtureSignature extends ClassMemberSignature {
	// codice intermedio Kitten per questa fixture
	private Block code;

	private static int counter = 0;
	
	// identifier per le fixture della stessa classe
	private final int identifier;


	/**
	 * Costruisce la segnature della Fixture.
	 * @param clazz La classe dov'è definita la fixture.
	 * @param abstractSyntax La sintassi astratta della fixture.
	 */
	public FixtureSignature(ClassType clazz, ClassMemberDeclaration abstractSyntax){
		super(clazz, abstractSyntax);

		this.identifier = counter++;
	}

	@Override
	public String toString() {
		return getDefiningClass() + "." + "fixture" + identifier ;			
	}

	
	public Block getCode() {
		return code;
	}

	public void setCode(Block code) {
		this.code = code;
	}



}
