package apimanipulation;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddAuthorizationHeaderOkhttp {

    public static void main(String[] args) throws IOException {
        //String className = "/home/alpha/Documents/projects/project-x/apimanipulation/app/build/classes/java/main/apimanipulation/App.class";
        FileInputStream fis = new FileInputStream("/home/alpha/Documents/projects/project-x/apimanipulation/app/build/classes/java/main/apimanipulation/ApiClient.class");
        ClassReader cr = new ClassReader(fis);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new MyClassVisitor(Opcodes.ASM9, cw);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        fis.close();

        FileOutputStream fos = new FileOutputStream("/home/alpha/Documents/projects/project-x/apimanipulation/app/build/classes/java/main/apimanipulation/ApiClient.class");
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
        private boolean isRequestBuilder = false;

        protected MyMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            super.visitTypeInsn(opcode, type);
            if (opcode == Opcodes.NEW && type.equals("okhttp3/Request$Builder")) {
                isRequestBuilder = true;
            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

            if (isRequestBuilder && opcode == Opcodes.INVOKEVIRTUAL && owner.equals("okhttp3/Request$Builder") && name.equals("build")) {
                isRequestBuilder = false;

                // Inject code before build() call to add additional authorization header
                mv.visitInsn(Opcodes.DUP);
                mv.visitLdcInsn("Authorization");
                mv.visitLdcInsn("Bearer graciac");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "okhttp3/Request$Builder", "header", "(Ljava/lang/String;Ljava/lang/String;)Lokhttp3/Request$Builder;", false);
            }
        }
    }
}
