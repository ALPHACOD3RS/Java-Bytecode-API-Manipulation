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
                mv.visitVarInsn(Opcodes.ALOAD, 2); // Load the HttpURLConnection object (assuming it's stored in local variable 1)
                mv.visitLdcInsn("Authorization");
                mv.visitLdcInsn("Bearer token");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/HttpURLConnection", "setRequestProperty", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            }
        }
    }
}

//
//import org.objectweb.asm.ClassReader;
//import org.objectweb.asm.ClassVisitor;
//import org.objectweb.asm.ClassWriter;
//import org.objectweb.asm.MethodVisitor;
//import org.objectweb.asm.Opcodes;
//import java.util.logging.Level;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.util.logging.Logger;
//
//
//public class BytecodeManipulator {
//    private static final Logger logger = Logger.getLogger(BytecodeManipulator.class.getName());
//
//    public static void main(String[] args) throws Exception {
//        String className = "/home/alpha/Documents/projects/project-x/apimanipulation/app/build/classes/java/main/apimanipulation/App.class";
//
//        FileInputStream fis = new FileInputStream(className);
//        ClassReader classReader = new ClassReader(fis);
//
//        // Create a ClassWriter with COMPUTE_FRAMES and COMPUTE_MAXS flags
//        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
//
//        // Create a ClassVisitor to visit the class structure
//        classReader.accept(new ClassVisitor(Opcodes.ASM9, classWriter) {
//            @Override
//            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
//                logger.log(Level.INFO, "Visiting method: " + name + descriptor);
//
//                // Find the main method to manipulate
//                if ("main".equals(name) && "([Ljava/lang/String;)V".equals(descriptor)) {
//                    logger.log(Level.INFO, "Found main method. Beginning bytecode manipulation...");
//
//                    return new MethodVisitor(Opcodes.ASM9, mv) {
//                        @Override
//                        public void visitCode() {
//                            super.visitCode();
//                            logger.log(Level.INFO, "Inserting bytecode instructions at the beginning of main method...");
//                        }
//
//                        @Override
//                        public void visitInsn(int opcode) {
//                            if (opcode == Opcodes.RETURN) {
//                                // Insert instructions to set the Authorization header
//                                mv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'con' (HttpURLConnection)
//                                mv.visitFieldInsn(Opcodes.GETFIELD, "con/setRequestProperty", "con", "Ljava/util/List;");
//                                // Before we call add method of list we have to put target value on top of the stack
////                                mv.visitTypeInsn(Opcodes.NEW, "com/shehabic/sherlock/interceptors/SherlockOkHttpInterceptor");
//                               mv. visitInsn(Opcodes.DUP);
////                                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new SherlockOkHttpInterceptor()).build()
////                                con.setRequestProperty("Authorization", "Bearer " + bearerToken);
//
//                                // We have to call classes constructor
//                                // Internal name of constructor - <init>
//                                // ()V - signature of method. () - method doesn't have parameters. V - method returns void
////                                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/shehabic/sherlock/interceptors/SherlockOkHttpInterceptor", "<init>", "()V", false);
//                                // So on top of the stack we have initialized instance of com/shehabic/sherlock/interceptors/SherlockOkHttpInterceptor
//                                // Now we can put it into list
////                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
//                                mv.visitLdcInsn("Authorization"); // Load "Authorization"
//                                mv.visitLdcInsn("Bearer naol"); // Load "Bearer naol"
//                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/HttpURLConnection", "setRequestProperty", "(Ljava/lang/String;Ljava/lang/String;)V", false);
//                            }
//                            super.visitInsn(opcode);
//                        }
//                    };
//                }
//                return mv;
//            }
//        }, 0);
//
//        // Get the modified bytecode
//        byte[] bytecode = classWriter.toByteArray();
//
//        // Write the modified bytecode back to the class file
//        FileOutputStream fos = new FileOutputStream(className);
//        fos.write(bytecode);
//
//        // Close streams
//        fos.close();
//        fis.close();
//
//        logger.log(Level.INFO, "Bytecode manipulation completed successfully. Modified class file saved to: " + className);
//    }
//}


//public class BytecodeManipulator {
//    private static final Logger logger = Logger.getLogger(BytecodeManipulator.class.getName());
//
//    public static void main(String[] args) throws Exception {
//        String className = "/home/alpha/Documents/projects/project-x/apimanipulation/app/build/classes/java/main/apimanipulation/App.class";
//
//        logger.log(Level.INFO, "Starting bytecode manipulation for class: " + className);
//
//        // Read the original class file
//        FileInputStream fis = new FileInputStream(className);
//        ClassReader classReader = new ClassReader(fis);
//
//        // Create a ClassWriter with COMPUTE_FRAMES and COMPUTE_MAXS flags
//        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
//
//        // Create a ClassVisitor to visit the class structure
//        classReader.accept(new ClassVisitor(Opcodes.ASM9, classWriter) {
//            // Override visitMethod to manipulate methods
//            @Override
//            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
//                logger.log(Level.INFO, "Visiting method: " + name + descriptor);
//
//                // Find the main method to manipulate
//                if ("main".equals(name) && "([Ljava/lang/String;)V".equals(descriptor)) {
//                    logger.log(Level.INFO, "Found main method. Beginning bytecode manipulation...");
//
//                    return new MethodVisitor(Opcodes.ASM9, mv) {
//                        // Override visitCode to insert bytecode at the beginning of the method
//                        @Override
//                        public void visitCode() {
//                            super.visitCode();
//                            logger.log(Level.INFO, "Inserting bytecode instructions at the beginning of main method...");
//
//                            // Insert instructions at the beginning of the main method
//                            mv.visitVarInsn(Opcodes.ALOAD, 0); // Load args array
//                            mv.visitLdcInsn("Authorization"); // Load "Authorization"
//                            mv.visitLdcInsn("siuuu"); // Load "siuuu"
//                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/HttpURLConnection", "setRequestProperty", "(Ljava/lang/String;Ljava/lang/String;)V", false);
//                        }
//                    };
//                }
//                return mv;
//            }
//        }, 0);
//
//        // Get the modified bytecode
//        byte[] bytecode = classWriter.toByteArray();
//
//        // Write the modified bytecode back to the class file
//        FileOutputStream fos = new FileOutputStream(className);
//        fos.write(bytecode);
//
//        // Close streams
//        fos.close();
//        fis.close();
//
//        logger.log(Level.INFO, "Bytecode manipulation completed successfully. Modified class file saved to: " + className);
//    }
//}

//String className = "/home/alpha/Documents/projects/project-x/apimanipulation/app/build/classes/java/main/apimanipulation/App.class";