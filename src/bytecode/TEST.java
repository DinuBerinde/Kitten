package bytecode;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.generic.InstructionList;

import types.TestS;

public class TEST extends SequentialBytecode {
	private final TestS sig;
	
	public TEST(TestS sig){
		this.sig = sig;
	}
	
	@Override
	public String toString() {
		return "test " + sig;
	}
	
	
	
	@Override
	public InstructionList generateJavaBytecode(JavaClassGenerator classGen) {
		
		return new InstructionList(sig.createINVOKEVIRTUAL(classGen));
	}


	public TestS getSig() {
		return sig;
	}

}
