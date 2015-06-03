package javaBytecodeGenerator;

import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ConstantPoolGen;
import types.ClassMemberSignature;
import types.ClassType;
import types.ConstructorSignature;
import types.FieldSignature;
import types.MethodSignature;

@SuppressWarnings("serial")
public class NormalJavaClassGenerator extends JavaClassGenerator {


	public NormalJavaClassGenerator(ClassType clazz, Set<ClassMemberSignature> sigs) {
		
		super(clazz.getName(), // name of the class
			 // the superclass of the Kitten Object class is set to be the Java java.lang.Object class
			 clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : "java.lang.Object",
			 clazz.getName() + ".kit", // source file
			 Constants.ACC_PUBLIC, // Java attributes: public!
			 null, // no interfaces
			 new ConstantPoolGen()); // empty constant pool, at the beginning

		

		// we add the fields
		for (FieldSignature field: clazz.getFields().values())
			if (sigs.contains(field))
				field.createField(this);

		// we add the constructors
		for (ConstructorSignature constructor: clazz.getConstructors())
			if (sigs.contains(constructor))
				constructor.createConstructor(this);

		// we add the methods
		for (Set<MethodSignature> s: clazz.getMethods().values())
			for (MethodSignature method: s)
				if (sigs.contains(method))
					method.createMethod(this);
	}

}
