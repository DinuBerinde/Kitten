package absyn;

import java.io.FileWriter;
import bytecode.NEWSTRING;
import bytecode.VIRTUALCALL;
import semantical.TypeChecker;
import translation.Block;
import types.ClassType;
import types.MethodSignature;
import types.TypeList;

/**
 * A node of abstract syntax representing an assert command.
 *
 * @author Dinu
 */

public class Assert extends Command {
	/**
	 * The guard or condition of the loop.
	 */
	private final Expression condition;
	private String failedAssert;
	private String className;
	private String where;
	



	/**
	 * Constructs the abstract syntax of an assert command.
	 *
	 * @param pos the position in the source file where it starts
	 *            the concrete syntax represented by this abstract syntax
	 * @param condition the guard or condition of the loop
	 */
	public Assert(int pos, Expression condition){
		super(pos);

		this.condition = condition;
	}

	/**
	 * Performs the type-checking of the assert command
	 * by using a given type-checker. It type-checks the condition and body
	 * of the assert command. It checks that the condition is
	 * a Boolean expression. Returns the original type-checker
	 * passed as a parameter.
	 *
	 * @param checker the type-checker to be used for type-checking
	 * @return {@code checker} itself
	 */
	@Override
	protected TypeChecker typeCheckAux(TypeChecker checker, String name) {
		this.condition.mustBeBoolean(checker);		
		
		// nome della classe
		//className = this.condition.getStaticType().getObjectType().getName();
		className = this.getTypeChecker().getVar("this").toString();
		
		// errore riga.col
		where = checker.getErrRowCol(this.getPos());
		
		failedAssert = "test fallito @" + className + ".kit" + where;
		
		
		String err = "";
		err += "Illegal assert inside " + name;

		// se l'assert non è permesso
		if(!checker.isAssertAllowed())
			error(checker, err);

		return checker;
	}

		
	@Override
	public boolean checkForDeadcode() {
		
		return false;
	}


	@Override
	protected void toDotAux(FileWriter where) throws java.io.IOException {
		linkToNode("condition", condition.toDot(where), where);

	}
	
	/**
	 * Translates this command into intermediate Kitten bytecode. Namely,
	 * it returns a code which evaluates the {@link #condition} 
	 * 
	 * @param continuation the continuation to be executed after this command
	 * @return the code executing this command and then the {@code continuation}
	 */

	@Override
	public Block translate(Block continuation) {		
		ClassType classType = ClassType.mk("String");
		
		MethodSignature method = classType.methodLookup("output", TypeList.EMPTY);
	
		Block temp = new VIRTUALCALL(classType, method).followedBy(continuation);
		
		Block result = this.condition.translateAsTest(continuation, new NEWSTRING(this.failedAssert).followedBy(temp));
				
		return result;
	}

}
