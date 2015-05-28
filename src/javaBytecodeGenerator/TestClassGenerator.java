package javaBytecodeGenerator;

import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;

import types.ClassMemberSignature;
import types.ClassType;
import types.FixtureSignature;
import types.TestSignature;

@SuppressWarnings("serial")
public class TestClassGenerator extends JavaClassGenerator {


	public TestClassGenerator(ClassType clazz, Set<ClassMemberSignature> sigs){
		
		
		super(clazz.getName(), // name of the class
				// the superclass of the Kitten Object class is set to be the Java java.lang.Object class
				clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : "java.lang.Object",
						clazz.getName() + ".kit", // source file	
						Constants.ACC_PUBLIC, // Java attributes: public!
						null, // no interfaces
						new ConstantPoolGen()); // empty constant pool, at the beginning

		
		// we add the tests
		for (TestSignature test: clazz.getTests().values() )
			if (sigs.contains(test))
				test.createTest(this);

		// we add the fixtures
		for (FixtureSignature fixture: clazz.getFixtures() )
			if (sigs.contains(fixture))
				fixture.createFixture(this);

	}



}
