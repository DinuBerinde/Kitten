package absyn;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import bytecode.Bytecode;
import bytecode.BytecodeList;
import bytecode.CALL;
import bytecode.GETFIELD;
import bytecode.PUTFIELD;
import bytecode.RETURN;
import semantical.TypeChecker;
import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.CodeSignature;
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
	 *
	@Override
	protected void addTo(ClassType clazz) {
		this.sig = new TestSignature(name, clazz, this);
		clazz.addTest(name, sig);
	}*/

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

	/**
	 * Traduce questo test in codice intermedio Kitten.
	 *
	 * @param done il set delle code signatures che sono state già tradotte.
	 */
	public void translate(Set<ClassMemberSignature> done){
	
		if(done.add(this.sig)){
			// we translate the body of the test with a block containing RETURN as continuation. 
			this.sig.setCode(this.getBody().translate(new Block(new RETURN(VoidType.INSTANCE))));

			
			// we translate all methods and constructors that are referenced
    		// from the code we have generated
			this.translateReferenced(this.sig.getCode(),  done, new HashSet<Block>());
		}
	}

	/**
	 * Auxiliary method that translates into Kitten bytecode all class members that are
	 * referenced from the given block and the blocks reachable from it.
	 *
	 * @param block the block
	 * @param done the class member signatures already translated
	 * @param blocksDone the blocks that have been already processed
	 */

	private void translateReferenced(Block block, Set<ClassMemberSignature> done, Set<Block> blocksDone) {
		// if we already processed the block, we return immediately
		if (!blocksDone.add(block))
			return;

		for (BytecodeList cursor = block.getBytecode(); cursor != null; cursor = cursor.getTail()) {
			Bytecode h = cursor.getHead();

			if (h instanceof GETFIELD)
				done.add(((GETFIELD) h).getField());
			else if (h instanceof PUTFIELD)
				done.add(((PUTFIELD) h).getField());
			else if (h instanceof CALL)
				for (CodeSignature callee: ((CALL) h).getDynamicTargets())
					callee.getAbstractSyntax().translate(done);

		}

		// we continue with the following blocks
		for (Block follow: block.getFollows())
			translateReferenced(follow, done, blocksDone);
	}


	@Override
	protected void addTo(ClassType clazz) {
		// TODO Auto-generated method stub
		
	}

}




