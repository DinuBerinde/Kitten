package javaBytecodeGenerator;

import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LSTORE;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import bytecode.NEW;
import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.FixtureSignature;
import types.TestSignature;

/**
 * A Java bytecode generator for the tests and fixtures. It transforms the Kitten intermediate language
 * into Java bytecode that can be dumped to Java class files and run.
 * It uses the BCEL library to represent Java classes and dump them on the file-system.
 *
 * @author Dinu
 */
@SuppressWarnings("serial")
public class TestClassGenerator extends JavaClassGenerator {
	private ClassType classType;

	public TestClassGenerator(ClassType clazz, Set<ClassMemberSignature> sigs){

		super(clazz.getName() + "Test", // name of the class
				"java.lang.Object", //superclass
				clazz.getName() + ".kit", // source file	
				Constants.ACC_PUBLIC, // Java attributes: public
				null, // no interfaces
				new ConstantPoolGen()); // empty constant pool, at the beginning

		this.classType = clazz;


		// we add the tests
		for (TestSignature test: clazz.getTests().values() )
			if (sigs.contains(test))
				test.createTest(this);


		// we add the fixtures
		for (FixtureSignature fixture: clazz.getFixtures() )
			if (sigs.contains(fixture))
				fixture.createFixture(this);

		// we build the main
		buildMain();

	}

	/**
	 * Builds the main method of this class
	 */
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

		// we add all the instructions to the method
		methodGen.setInstructionList(generateInstructions(methodGen));

		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		methodGen.setMaxStack();
		methodGen.setMaxLocals();

		this.addMethod(methodGen.getMethod());

	}


	/**
	 * Generates the instruction list of the main method of this class
	 * 
	 * @param methodGen the method generator of the main method
	 * @return il the instruction list
	 */
	private InstructionList generateInstructions(MethodGen methodGen){
		InstructionList il = new InstructionList();

		il.append(new GETSTATIC(this.getConstantPool().addFieldref("java.lang.System", "out", "Ljava/io/PrintStream;")));

		
		// we create the local variable total time
		LocalVariableGen totalTime = methodGen.addLocalVariable("totalTime", Type.LONG, null, null);
		int indexTotalTime = totalTime.getIndex();

		// and then we call System.currentTimeMillis 
		il.append(getFactory().createInvoke("java.lang.System", "currentTimeMillis", Type.LONG,
				Type.NO_ARGS, Constants.INVOKESTATIC));

		// we save the totalTime
		il.append(new LSTORE(indexTotalTime));



		// we build an object that has the type of the class were the test is defined
		NEW obj = new NEW(this.classType);

		// we translate obj to JB
		il.append(obj.generateJavaBytecode(this));

		il.append(InstructionFactory.DUP);


		// we invoke the constructor to create the obj
		il.append(getFactory().createInvoke(
				this.classType.toBCEL().toString(),  // the name of the class
				Constants.CONSTRUCTOR_NAME, // <init>
				org.apache.bcel.generic.Type.VOID, // return type
				org.apache.bcel.generic.Type.NO_ARGS, // parameters
				Constants.INVOKESPECIAL)); // the type of call


		// we create the local variable obj
		LocalVariableGen o = methodGen.addLocalVariable("obj", obj.getType().toBCEL(), null, null);
		int indexObj = o.getIndex();

		// we save the obj
		il.append(new ASTORE(indexObj));




		// local variable time
		LocalVariableGen time = methodGen.addLocalVariable("time", Type.LONG, null, null);
		int indexTime = time.getIndex();


		// and then we call System.currentTimeMillis 
		il.append(getFactory().createInvoke("java.lang.System", "currentTimeMillis", Type.LONG,
				Type.NO_ARGS, Constants.INVOKESTATIC));

		// we save the time
		il.append(new LSTORE(indexTime));



		// we call all tests with obj as argument
		for(TestSignature test: this.classType.getTests().values()){

			il.append(new ALOAD(indexObj));


			il.append(getFactory().createInvoke(this.classType.toBCEL().toString() + "Test",
					test.getName(),
					org.apache.bcel.generic.Type.VOID, 
					new org.apache.bcel.generic.Type[]
							{ obj.getType().toBCEL() },
							Constants.INVOKESTATIC));

		}

		// and all fixtures with obj as argument
		for(FixtureSignature fixture: this.classType.getFixtures()){
			il.append(new ALOAD(indexObj));

			il.append(getFactory().createInvoke(this.classType.toBCEL().toString() + "Test",
					fixture.getName(),
					org.apache.bcel.generic.Type.VOID, 
					new org.apache.bcel.generic.Type[]
							{ obj.getType().toBCEL() },
							Constants.INVOKESTATIC));
		}


		// and finally
		// we append the results of the tests
		il.append(generateTestResults());

		// and we finish with a return
		il.append(InstructionFactory.RETURN);

		return il;
	}

	/**
	 * Generates the instruction list of the results of the tests
	 * 
	 * @return il the instruction list
	 */
	private InstructionList generateTestResults(){
		InstructionList il = new InstructionList();

		String results = "\n";

		results += "Test execution for class " + this.classType.getName() + ":" + "\n ";

		for(TestSignature test: this.classType.getTests().values()){
			results += "\t - " + test.getName() + ":";
			results += "\n";
		}


		// we print the results
		il.append(getFactory().createPrintln(results));

		return il;
	}


}
