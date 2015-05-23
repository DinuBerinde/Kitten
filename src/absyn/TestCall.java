package absyn;

import java.io.FileWriter;
import java.util.Set;

import bytecode.POP;
import bytecode.TEST;
import bytecode.VIRTUALCALL;
import semantical.TypeChecker;
import translation.Block;
import types.ClassType;
import types.MethodSignature;
import types.TestS;
import types.Type;
import types.TypeList;
import types.VoidType;

public class TestCall extends Command {
	private final Expression receiver;
	private final String name;
	private TestS test;
	
	public TestCall(int pos, Expression receiver, String name) {
		super(pos);
		
		this.name = name;
		this.receiver = receiver;
		
	}

	@Override
	protected void toDotAux(FileWriter where) throws java.io.IOException {
		linkToNode("receiver", receiver.toDot(where), where);
		linkToNode("name", toDot(name, where), where);
	}
	
	@Override
	protected TypeChecker typeCheckAux(TypeChecker checker, String err) {
		Type receiverType = receiver.typeCheck(checker);
		
		// the receiver must have class type. Hence we cannot call method of an array.
		// This is fine since arrays are subclasses of Object which has no methods
		if (!(receiverType instanceof ClassType))
			error("class type required");
		else {
			// we collect the set of methods that are compatible with the
			// static types of the parameters and have no other compatible method
			// that is more specific than them
			//Set<MethodSignature> methods = ((ClassType) receiverType).methodsLookup(name,actualsTypes);
			TestS test = ((ClassType) receiverType).testLookup(name);
			
			if(test == null)
				error("no matching test for call to \"" + name + "\"");
			else
				this.test = test;
		}

		// the type-checker has not been modified
		return checker;
		
	}

	

	@Override
	public Block translate(Block continuation) {
		
		continuation = new TEST((ClassType) receiver.getStaticType(), test).followedBy(continuation);

		// we translate the receiver of the call
		return receiver.translate(continuation);
	}
	
	
	

	@Override
	public boolean checkForDeadcode() {
		return false;
	}
	
	public Expression getReceiver() {
		return receiver;
	}

	public String getName() {
		return name;
	}

	public TestS getTest() {
		return test;
	}

	public void setTest(TestS test) {
		this.test = test;
	}

}
