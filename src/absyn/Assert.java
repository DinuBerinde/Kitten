package absyn;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import bytecode.NEWSTRING;
import bytecode.VIRTUALCALL;
import semantical.TypeChecker;
import translation.Block;
import types.ClassType;
import types.CodeSignature;
import types.MethodSignature;

public class Assert extends Command {
	private final Expression condition;
	private String failedAssert;
	private String className;
	private String where;
	

	// new AssertDeclaration(aleft, condition)
	public Assert(int pos, Expression condition){
		super(pos);

		this.condition = condition;
	}

	
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

	@Override
	public Block translate(Block continuation) {
		
		return this.condition.translateAsTest(continuation, new NEWSTRING(this.failedAssert).followedBy(continuation));
	}

}
