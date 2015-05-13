package absyn;

import java.io.FileWriter;

import bytecode.NEWSTRING;
import semantical.TypeChecker;
import translation.Block;
import types.CodeSignature;

public class Assert extends Command {
	private final Expression condition;

	// new AssertDeclaration(aleft, condition)
	public Assert(int pos, Expression condition){
		super(pos);

		this.condition = condition;
	}

	@Override
	protected TypeChecker typeCheckAux(TypeChecker checker, String name) {
		this.condition.mustBeBoolean(checker);

		String err = "";
		err += "Illegal assert inside " + name;

		// se l'assert non è permesso
		if(!checker.isAssertAllowed())
			error(checker, err);

		return checker;
	}

	@Override
	public boolean checkForDeadcode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void toDotAux(FileWriter where) throws java.io.IOException {
		linkToNode("condition", condition.toDot(where), where);

	}

	@Override
	public Block translate(Block continuation) {

		//Block res = this.condition.translateAsTest(continuation, new VIRTUALCALL(new NEWSTRING("ASSERT FAILED"), ).followedBy(continuation));

		return null;
	}

}
