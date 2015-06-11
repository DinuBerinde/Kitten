package javaBytecodeGenerator;

import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DDIV;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DSTORE;
import org.apache.bcel.generic.DSUB;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.ISUB;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUSH;
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

		// we build the fields
		buildFields();

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
	 * Builds the main method of this class.
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
	 * We build the fields of this class; the number of tests present in this class, 
	 * the number of failed tests, and a flag that we use to understand which test failed.
	 */
	private void buildFields(){
		FieldGen fieldGen1 = new FieldGen(Constants.ACC_PRIVATE | Constants.ACC_STATIC,
				Type.STRING, "flag", this.getConstantPool());

		FieldGen fieldGen2 = new FieldGen(Constants.ACC_PRIVATE  | Constants.ACC_STATIC,
				Type.INT, "failedTest", this.getConstantPool());

		FieldGen fieldGen3 = new FieldGen(Constants.ACC_PRIVATE  | Constants.ACC_STATIC,
				Type.INT, "totalTests", this.getConstantPool());


		this.addField(fieldGen1.getField());
		this.addField(fieldGen2.getField());
		this.addField(fieldGen3.getField());
	}




	/**
	 * Generates the instruction list of the main method of this class
	 * 
	 * @param methodGen the method generator main
	 * @return il the instruction list
	 */
	private InstructionList generateInstructions(MethodGen methodGen){
		InstructionList il = new InstructionList();

		il.append(new PUSH(this.getConstantPool(), 0));
		il.append(getFactory().createPutStatic(this.getClassName(), "failedTest", Type.INT));

		il.append(new PUSH(this.getConstantPool(), 0));
		il.append(getFactory().createPutStatic(this.getClassName(), "totalTests", Type.INT));


		// we create the local variable totalTime
		LocalVariableGen totalTime = methodGen.addLocalVariable("totalTime", Type.DOUBLE, null, null);
		int indexTotalTime = totalTime.getIndex();

		// and then we call System.currentTimeMillis 
		il.append(getFactory().createInvoke("java.lang.System", "currentTimeMillis", Type.LONG,
				Type.NO_ARGS, Constants.INVOKESTATIC));

		il.append(getFactory().createCast(Type.LONG, Type.DOUBLE));
		il.append(getFactory().createConstant(1.00));
		il.append(new DDIV());

		// and we save the the value into totalTime
		il.append(new DSTORE(indexTotalTime));


		// we build an object that has the type of the class were the test is defined
		NEW obj = new NEW(this.classType);

		// we translate the obj to JB
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

		// and we save the created obj into the local variable obj
		il.append(new ASTORE(indexObj));

		// we create a local variable time
		LocalVariableGen time = methodGen.addLocalVariable("time", Type.DOUBLE, null, null);
		int indexTime = time.getIndex();

		// and then we call System.currentTimeMillis 
		il.append(getFactory().createInvoke("java.lang.System", "currentTimeMillis", Type.LONG,
				Type.NO_ARGS, Constants.INVOKESTATIC));

		il.append(getFactory().createCast(Type.LONG, Type.DOUBLE));
		il.append(getFactory().createConstant(1.00));
		il.append(new DDIV());

		// and then we save the result into time
		il.append(new DSTORE(indexTime));

		// we call all tests with obj as argument
		for(TestSignature test: this.classType.getTests().values()){
			il.append(new ALOAD(indexObj));

			il.append(new PUSH(this.getConstantPool(), "\t -" + test.getName() + ": passed ["));
			il.append(getFactory().createPutStatic(getClassName(), "flag", Type.STRING));

			il.append(getFactory().createInvoke(this.classType.toBCEL().toString() + "Test",
					test.getName(),
					org.apache.bcel.generic.Type.VOID, 
					new org.apache.bcel.generic.Type[]
							{ obj.getType().toBCEL() },
							Constants.INVOKESTATIC));


			// we save the time that passed for this test
			FieldGen fieldTime = new FieldGen(Constants.ACC_PUBLIC | Constants.ACC_STATIC, Type.DOUBLE, test.getName() + "Time", this.getConstantPool());
			this.addField(fieldTime.getField());

			il.append(getFactory().createInvoke("java.lang.System", "currentTimeMillis", Type.LONG,	Type.NO_ARGS, Constants.INVOKESTATIC));
			il.append(getFactory().createCast(Type.LONG, Type.DOUBLE));
			il.append(getFactory().createConstant(1.00));
			il.append(new DDIV());
			il.append(new DLOAD(indexTime));
			il.append(new DSUB());

			il.append(getFactory().createPutStatic(getClassName(), test.getName() + "Time", Type.DOUBLE));


			// we increment the number of totalTests
			il.append(getFactory().createGetStatic(getClassName(), "totalTests", Type.INT));
			il.append(new PUSH(this.getConstantPool(), 1));
			il.append(new IADD());
			il.append(getFactory().createPutStatic(getClassName(), "totalTests", Type.INT));


			// we create a field with the name of the test 
			FieldGen fieldGen1 = new FieldGen(Constants.ACC_PUBLIC | Constants.ACC_STATIC, Type.STRING, test.getName(), this.getConstantPool());

			this.addField(fieldGen1.getField());
			il.append(new PUSH(this.getConstantPool(), "\t -" + test.getName() + ": passed ["));
			il.append(getFactory().createPutStatic(getClassName(), test.getName(), Type.STRING));

			// we save the name of the failed test
			il.append(getFactory().createGetStatic(getClassName(), "flag", Type.STRING));	
			il.append(getFactory().createPutStatic(getClassName(), test.getName(), Type.STRING));

			il.append(new PUSH(this.getConstantPool(), "\t -" + test.getName() + ": passed ["));
			il.append(getFactory().createPutStatic(getClassName(), "flag", Type.STRING));

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
		il.append(generateTestResults(indexTotalTime));

		// and we finish with a return
		il.append(InstructionFactory.RETURN);

		return il;
	}



	/**
	 * Generates the instruction list of the results of the tests
	 * 
	 * @param indexTotalTime the index to were it is stored the local variable totalTime
	 * 
	 * @return il the instruction list
	 */
	private InstructionList generateTestResults(int indexTotalTime){
		InstructionList il = new InstructionList();

		String head = " \nTest execution for class " + this.classType.getName() + ":";
		il.append(getFactory().createPrintln(head));

		// we get all the fields 
		Field[] field = new Field[this.getFields().length];
		field = this.getFields();

		// we prepare the println...
		il.append(new GETSTATIC(this.getConstantPool().addFieldref("java.lang.System", "out", "Ljava/io/PrintStream;")));
		il.append(getFactory().createNew(Type.STRINGBUFFER));
		il.append(InstructionConstants.DUP);
		il.append(new PUSH(this.getConstantPool(), ""));
		il.append(getFactory().createInvoke("java.lang.StringBuffer", "<init>", Type.VOID, new Type[] { Type.STRING },	Constants.INVOKESPECIAL));

		// we search for the fields of the failed/passed test
		// and we append the names
		for(int i = 0; i < field.length; i++){

			for(TestSignature test: this.classType.getTests().values()){	
				String testName = test.getName();

				if(testName.equals(field[i].getName())){

					il.append(getFactory().createGetStatic(getClassName(), testName, Type.STRING));	
					il.append(getFactory().createInvoke("java.lang.StringBuffer", "append",	Type.STRINGBUFFER, new Type[] { Type.STRING}, Constants.INVOKEVIRTUAL));
					il.append(getFactory().createGetStatic(getClassName(), testName + "Time", Type.DOUBLE));
					il.append(getFactory().createInvoke("java.lang.StringBuffer", "append",	Type.STRINGBUFFER, new Type[] { Type.DOUBLE }, Constants.INVOKEVIRTUAL));
					il.append(new PUSH(this.getConstantPool(), "ms]"));
					il.append(getFactory().createInvoke("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));
					il.append(new PUSH(this.getConstantPool(), "\n"));
					il.append(getFactory().createInvoke("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));

					break;
				}
			}
		}

		il.append(new PUSH(this.getConstantPool(), " \n "));
		il.append(getFactory().createInvoke("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));

		// we push on the stack the number of tests passed: totalTests - failedTest and append it to the string
		il.append(getFactory().createGetStatic(getClassName(), "totalTests", Type.INT));
		il.append(getFactory().createGetStatic(getClassName(), "failedTest", Type.INT));
		il.append(new ISUB());
		il.append(getFactory().createInvoke("java.lang.StringBuffer", "append",	Type.STRINGBUFFER, new Type[] { Type.INT }, Constants.INVOKEVIRTUAL));
		il.append(new PUSH(this.getConstantPool(), " tests passed, "));
		il.append(getFactory().createInvoke("java.lang.StringBuffer", "append",	Type.STRINGBUFFER, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));


		// we push on the stack the number of failed tests
		il.append(getFactory().createGetStatic(getClassName(), "failedTest", Type.INT));
		il.append(getFactory().createInvoke("java.lang.StringBuffer", "append",	Type.STRINGBUFFER, new Type[] { Type.INT }, Constants.INVOKEVIRTUAL));
		il.append(new PUSH(this.getConstantPool(), " failed ["));
		il.append(getFactory().createInvoke("java.lang.StringBuffer", "append",	Type.STRINGBUFFER, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));

		// we calculate the total time
		il.append(getFactory().createInvoke("java.lang.System", "currentTimeMillis", Type.LONG,	Type.NO_ARGS, Constants.INVOKESTATIC));

		il.append(getFactory().createCast(Type.LONG, Type.DOUBLE));
		il.append(getFactory().createConstant(1.00));
		il.append(new DDIV());
		il.append(new DLOAD(indexTotalTime));
		il.append(new DSUB());

		il.append(getFactory().createInvoke("java.lang.StringBuffer", "append",	Type.STRINGBUFFER, new Type[] { Type.DOUBLE}, Constants.INVOKEVIRTUAL));
		il.append(new PUSH(this.getConstantPool(), "ms]"));
		il.append(getFactory().createInvoke("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));

		// we print the results
		il.append(getFactory().createInvoke("java.lang.StringBuffer", "toString", Type.STRING, Type.NO_ARGS, 	Constants.INVOKEVIRTUAL));
		il.append(getFactory().createInvoke("java.io.PrintStream", "println", Type.VOID, new Type[] {Type.STRING}, Constants.INVOKEVIRTUAL));

		return il;
	}


}
