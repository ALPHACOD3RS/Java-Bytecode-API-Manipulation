package apimanipulation;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BytecodeManipulator {

    public static void main(String[] args) throws IOException {
        String className = "/home/alpha/Documents/projects/project-x/apimanipulation/app/build/classes/java/main/apimanipulation/App.class";
        FileInputStream fis = new FileInputStream(className);
        ClassReader cr = new ClassReader(fis);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new MyClassVisitor(Opcodes.ASM9, cw);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
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
            return new MyMethodVisitor(api, mv, access, name, descriptor);
        }
    }

    static class MyMethodVisitor extends AdviceAdapter {
        private int httpURLConnectionVarIndex = -1;

        protected MyMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            super.visitTypeInsn(opcode, type);
            if (opcode == Opcodes.NEW && type.equals("java/net/HttpURLConnection")) {
                // Identifying the allocation of HttpURLConnection object
                httpURLConnectionVarIndex = newLocal(Type.getObjectType("java/net/HttpURLConnection"));
            }
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            super.visitVarInsn(opcode, var);
            if (opcode == Opcodes.ASTORE && httpURLConnectionVarIndex == -1) {
                // Storing the index of the HttpURLConnection object
                httpURLConnectionVarIndex = var;
            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

            if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/net/HttpURLConnection") && name.equals("setRequestMethod")) {
                if (httpURLConnectionVarIndex != -1) {
                    // Inject code after setRequestMethod call to add Authorization header
                    mv.visitVarInsn(Opcodes.ALOAD, httpURLConnectionVarIndex); // Load the HttpURLConnection object
                    mv.visitLdcInsn("Authorization");
                    mv.visitLdcInsn("Bearer token");
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/HttpURLConnection", "setRequestProperty", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                }
            }
        }
    }
}

//

//String className = "/home/alpha/Documents/projects/project-x/apimanipulation/app/build/classes/java/main/apimanipulation/App.class";