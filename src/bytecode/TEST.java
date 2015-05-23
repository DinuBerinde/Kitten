package bytecode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.generic.InstructionList;

import types.ClassType;
import types.CodeSignature;
import types.MethodSignature;
import types.TestS;
import types.Type;

public class TEST extends CALL {

	public TEST(ClassType receiverType, TestS staticTarget){		
		super(receiverType, staticTarget, dynamicTargets(receiverType.getInstances(), staticTarget));
		
	}

	private static Set<CodeSignature> dynamicTargets(List<ClassType> instances, TestS staticTarget) {
		Set<CodeSignature> dynamicTargets = new HashSet<CodeSignature>();

		for (ClassType rec: instances) {
			// we look up for the method from the dynamic receiver
			TestS candidate = rec.testLookup(staticTarget.getName());
			
			// we add the dynamic target
			if (candidate != null)
				dynamicTargets.add(candidate);
		}

		return dynamicTargets;
	}

	@Override
	public InstructionList generateJavaBytecode(JavaClassGenerator classGen) {
		// TODO Auto-generated method stub
		return null;
	}

}
