package absyn;

import java.io.FileWriter;
import java.io.IOException;

import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.FixtureSignature;

public class FixtureDeclaration extends ClassMemberDeclaration {
	 /**
     * The intermediate Kitten code for this constructor or method.
     * This is {@code null} if this constructor or method has not been
     * translated yet.
     */

    private Block code;
    private FixtureSignature sig;
	private final Command body;
	
	/**
	 * Costruisce la sintassi astratta della Fixture.
	 * @param pos La posizione dove eventualmente ci sarà un errore.
	 * @param body La sintassi astratta del corpo del test.
	 * @param next La sintassi astratta di class_members.
	 */
	public FixtureDeclaration(int pos, Command body, ClassMemberDeclaration next){
		super(pos, next);
		
		this.body = body;
	}
	

	/**
	 * Per dare dei nomi agli nodi per il file .dot
	 */
	@Override
	protected void toDotAux(FileWriter where) throws IOException {	
		linkToNode("body", getBody().toDot(where), where);
	}

	/**
	 * Aggiunge la signature di questa Fixture alla classe clazz.
	 */
	@Override
	protected void addTo(ClassType clazz) {
		this.sig = new FixtureSignature(clazz, this);
		clazz.addFixture(sig);
	}

	
	
	@Override
	protected void typeCheckAux(ClassType currentClass) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public ClassMemberSignature getSignature() {
		return sig;
	}

	private Command getBody(){
		return this.body;
	}
	
	private Block getBlock(){
		return code;
	}
}
