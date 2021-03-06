package ex0130;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Scanner;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

public class CustomClassLoader extends ClassLoader {
	static String WORK_DIR = System.getProperty("user.dir");
	static String INPUT_DIR = WORK_DIR + File.separator + "classfiles";
	Scanner input = new Scanner(System.in);
	private ClassPool pool;
	static String[] classAndField = new String[2];
	
	public void CustcomClassLoader() throws NotFoundException {
		pool = new ClassPool();
		pool.insertClassPath(INPUT_DIR);
	}

	public static void main(String[] args) throws Throwable {
		CustomClassLoader loader = new CustomClassLoader();

		// Get class and field for modification.
		System.out.println("Enter ComponentApp and f1 or ServiceApp and f2.");
		loader.getInput(classAndField);

		// Find class
		Class<?> foundClass = loader.loadClass(classAndField[0]);
		foundClass.getDeclaredMethod("main", new Class[] { String[].class }). //
				invoke(null, new Object[] { classAndField });

	}

	/*
	 * Constructor
	 */
	public CustomClassLoader() throws NotFoundException {
		pool = new ClassPool();
		pool.insertClassPath(INPUT_DIR);
	}

	/*
	 * Find a specified class and modify the bytecode.
	 */
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			CtClass cc = pool.get(name);
			if (name.equals("ComponentApp") | name.equals("ServiceApp")) {
				CtField field = new CtField(CtClass.doubleType, classAndField[1], cc);
				field.setModifiers(Modifier.PUBLIC);
				cc.addField(field);
			}
			byte[] bArr = cc.toBytecode();
			return defineClass(name, bArr, 0, bArr.length);
		} catch (NotFoundException e) {
			throw new ClassNotFoundException();
		} catch (IOException e) {
			throw new ClassNotFoundException();
		} catch (CannotCompileException e) {
			throw new ClassNotFoundException();
		}
	}

	void getInput(String[] classAndField) {
		System.out.println("Enter a class name: ");
		classAndField[0] = input.nextLine().trim();

		System.out.println("Enter a field name: ");
		classAndField[1] = input.nextLine().trim();

		for (String arg : classAndField) {
			if (arg.isEmpty()) {
				getInput(classAndField);
			}
		}
	}
}
