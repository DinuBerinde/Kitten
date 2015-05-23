package absyn;

import bytecode.GETFIELD;
import bytecode.PUTFIELD;
import bytecode.TEST;
import semantical.TypeChecker;
import translation.Block;
import types.ClassType;
import types.TestS;
import types.Type;

public class TestAccess extends Lvalue {
	private final String name;
	private final Expression receiver;
	private TestS test;
	
	public TestAccess(int pos, String name, Expression receiver) {
		super(pos);
		this.name = name;
		this.receiver = receiver;

	}

	@Override
	public Block translateBeforeAssignment(Block continuation) {
		return receiver.translate(continuation);
	}

	@Override
	public Block translateAfterAssignment(Block continuation) {
		return new TEST(test).followedBy(continuation);
	}

	@Override
	protected Type typeCheckAux(TypeChecker checker) {
		Type receiverType = receiver.typeCheck(checker);

    	// the receiver must have class type!
    	if (!(receiverType instanceof ClassType))
    		return error("class type required");

    	ClassType receiverClass = (ClassType) receiverType;

    	// we read the signature of a field called name in the static class of the receiver
    	if ((test = receiverClass.testLookup(name)) == null)
    		// there is no such field!
    		return error("unknown test " + name);

    	// we return the static type of the field name in the class of the receiver
    	return test.getReturnType();
	}

	@Override
	public Block translate(Block continuation) {
		return receiver.translate(new TEST(test).followedBy(continuation));
	}

}
