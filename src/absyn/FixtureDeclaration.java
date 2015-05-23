package absyn;

import java.io.FileWriter;
import java.io.IOException;

import semantical.TypeChecker;
import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.FixtureSignature;
import types.TypeList;
import types.VoidType;

public class FixtureDeclaration extends ClassMemberDeclaration {
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

	
	/**
	 * Si verifica i tipi della dichiarazione di una fixture.
	 */
	@Override
	protected void typeCheckAux(ClassType clazz) {
		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg(), false);
		checker = checker.putVar("this", clazz);

		getBody().typeCheck(checker, "fixture");
		getBody().checkForDeadcode();
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
