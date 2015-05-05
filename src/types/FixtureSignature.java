package types;

import absyn.ClassMemberDeclaration;

public class FixtureSignature extends ClassMemberSignature {
   
    /**
     * Costruisce la segnature della Fixture.
     * @param clazz La classe dov'è definita la fixture.
     * @param abstractSyntax La sintassi astratta della fixture.
     */
	public FixtureSignature(ClassType clazz, ClassMemberDeclaration abstractSyntax){
		super(clazz, abstractSyntax);
	}
	
}
