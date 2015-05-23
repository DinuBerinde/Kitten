package types;

import translation.Block;
import absyn.ClassMemberDeclaration;
import absyn.CodeDeclaration;
import absyn.TestDeclaration;

public class TestSignature extends ClassMemberSignature {
	
	// nome del test
	private final String name;
	
	// il codice intermedio Kitten del test
	private Block code;
	
	/**
	 * Costruisce la segnature del Test con il nome name.
	 * 
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
	
	 /**
     * Sets the Kitten code of this test 
     *
     * @param code the Kitten code
     */
    public void setCode(Block code) {
    	this.code = code;
    }
    

    /**
     * Yields the block where the Kitten bytecode of this test starts.
     *
     * @return the block where the Kitten bytecode of this test starts
     */
    public Block getCode() {
    	return code;
    }
    
    @Override
    public TestDeclaration getAbstractSyntax() {
    	return (TestDeclaration) super.getAbstractSyntax();
    }
    
    
}
