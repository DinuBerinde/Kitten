package absyn;

import java.io.FileWriter;
import java.io.IOException;

import semantical.TypeChecker;
import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.TestSignature;
import types.TypeList;
import types.VoidType;

public class TestDeclaration extends ClassMemberDeclaration {
	private final String name;
	private final Command body;
	private TestSignature sig;
    private Block code;
    
	
    /**
     * Costruisce la sintassi astratta di un Test.
     * @param pos La posizione dove eventualmente ci sarà un errore.
     * @param name Nome del test.
     * @param body La sintassi astratta del corpo del test.
     * @param next La sintassi astratta di class_members.
     */
	public TestDeclaration(int pos, String name, Command body, ClassMemberDeclaration next){
		super(pos, next);
		
		this.name = name;
		this.body = body;
	}


	/**
	 * Per dare dei nomi agli nodi per il file .dot
	 */
	@Override
	protected void toDotAux(FileWriter where) throws IOException {
		linkToNode("name", toDot(name, where), where);
		linkToNode("body", getBody().toDot(where), where);
	}

	/**
	 * Aggiunge la signature di questo Test alla classe clazz.
	 */
	@Override
	protected void addTo(ClassType clazz) {
		this.sig = new TestSignature(name, clazz, this);
		clazz.addTest(name, sig);
	}

	/**
	 * Si verifica i tipi della dichiarazione di un test.
	 */
	@Override
	protected void typeCheckAux(ClassType clazz) {
		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg(), true);
		checker = checker.putVar("this", clazz);
		
		getBody().typeCheck(checker, name);
		getBody().checkForDeadcode();
	}


	@Override
	public ClassMemberSignature getSignature() {
		return sig;
	}
	
	private Command getBody(){
		return body;
	}
	
	private Block getBlock(){
		return code;
	}
}
