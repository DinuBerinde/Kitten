package javaBytecodeGenerator;

import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;

import bytecode.NEW;
import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.FixtureSignature;
import types.TestSignature;

@SuppressWarnings("serial")
public class TestClassGenerator extends JavaClassGenerator {
	private ClassType classType;

	public TestClassGenerator(ClassType clazz, Set<ClassMemberSignature> sigs){

		super(clazz.getName() + "Test", // name of the class
				"java.lang.Object", //superclass
				clazz.getName() + ".kit", // source file	
				Constants.ACC_PUBLIC, // Java attributes: public!
				null, // no interfaces
				new ConstantPoolGen()); // empty constant pool, at the beginning

		this.classType = clazz;

		// we build the main
		buildMain();

		// we add the tests
		for (TestSignature test: clazz.getTests().values() )
			if (sigs.contains(test))
				test.createTest(this);

		// we add the fixtures
		for (FixtureSignature fixture: clazz.getFixtures() )
			if (sigs.contains(fixture))
				fixture.createFixture(this);


	}

	private void buildMain(){
		MethodGen methodGen = new MethodGen
				(Constants.ACC_PUBLIC | Constants.ACC_STATIC, // public and static
						org.apache.bcel.generic.Type.VOID, // return type
						new org.apache.bcel.generic.Type[] // parameters
								{ new org.apache.bcel.generic.ArrayType("java.lang.String", 1) }, // parameters types 
								null, // parameters names: we do not care
								"main", // method's name
								this.getClassName(), // name of the class
								this.generateJavaBytecode(new Block()), // bytecode of the main
								this.getConstantPool()); // constant pool

		// we add all the instruction of main
		methodGen.setInstructionList(generateInstructions());

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		methodGen.setMaxStack();
		methodGen.setMaxLocals();

		this.addMethod(methodGen.getMethod());

	}


	private InstructionList generateInstructions(){
		InstructionList il = new InstructionList();

		// we build an object that has the type of the class were the test is defined
		NEW obj = new NEW(this.classType);

		// ???????
		il.append(obj.generateJavaBytecode(this));

		for(FixtureSignature fixture: this.classType.getFixtures())
			il.append(getFactory().createInvoke(this.classType.getName(),
					fixture.getName(),
					org.apache.bcel.generic.Type.VOID, 
					new org.apache.bcel.generic.Type[]
							{ obj.getType().toBCEL() },
							Constants.INVOKESTATIC));


		for(TestSignature test: this.classType.getTests().values())
			il.append(getFactory().createInvoke(this.classType.getName(),
					test.getName(),
					org.apache.bcel.generic.Type.VOID, 
					new org.apache.bcel.generic.Type[]
							{ obj.getType().toBCEL() },
							Constants.INVOKESTATIC));

		return il;
	}

	private Block getCode(){
		Block res = new Block();

		return res;
	}

}
