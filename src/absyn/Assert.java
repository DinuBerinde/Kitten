package absyn;

import java.io.FileWriter;

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
	protected TypeChecker typeCheckAux(TypeChecker checker) {

		return null;
	}

	@Override
	public boolean checkForDeadcode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Block translate(CodeSignature where, Block continuation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void toDotAux(FileWriter where) throws java.io.IOException {
		linkToNode("condition", condition.toDot(where), where);
		
	}

}
