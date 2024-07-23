package apimanipulation;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BytecodeManipulator {

    public static void main(String[] args) throws IOException {
                String className = "/home/alpha/Documents/projects/project-x/apimanipulation/app/build/classes/java/main/apimanipulation/App.class";

        FileInputStream fis = new FileInputStream(className);
        ClassReader cr = new ClassReader(fis);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new MyClassVisitor(Opcodes.ASM9, cw);
        cr.accept(cv, 0);
        fis.close();

        FileOutputStream fos = new FileOutputStream(className);
        fos.write(cw.toByteArray());
        fos.close();

        System.out.println("Bytecode manipulation complete!");
    }

    static class MyClassVisitor extends ClassVisitor {
        public MyClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new MyMethodVisitor(api, mv);
        }
    }

    static class MyMethodVisitor extends MethodVisitor {
        public MyMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

            if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/net/HttpURLConnection") && name.equals("setRequestMethod")) {
                // Insert code after setRequestMethod call to add Authorization header
                mv.visitVarInsn(Opcodes.ALOAD, 2); // this is the problem we'r facing rn load the HttpURLConnection object (assuming it's stored in local variable 1)
                mv.visitLdcInsn("Authorization");
                mv.visitLdcInsn("Bearer token");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/HttpURLConnection", "setRequestProperty", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            }
        }
    }
}

//

//String className = "/home/alpha/Documents/projects/project-x/apimanipulation/app/build/classes/java/main/apimanipulation/App.class";