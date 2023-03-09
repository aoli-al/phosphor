package edu.columbia.cs.psl.jigsaw.phosphor.instrumenter;

import edu.columbia.cs.psl.phosphor.Configuration;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class JLinkInvoker {

    public static final String MODULES_PROPERTY = "jvmModules";

    public static final List<String> INTRINSICS = List.of(
            "UseLibmIntrinsic",
            "UseMD5Intrinsics",
            "UseMathExactIntrinsics",
            "UseMontgomeryMultiplyIntrinsic",
            "UseMontgomerySquareIntrinsic",
            "UseMulAddIntrinsic",
            "UseMultiplyToLenIntrinsic",
            "UseSHA1Intrinsics",
            "UseSHA256Intrinsics",
            "UseSHA3Intrinsics",
            "UseSHA512Intrinsics",
            "UseSSE42Intrinsics",
            "UseSignumIntrinsic",
            "UseSquareToLenIntrinsic",
            "UseVectorizedMismatchIntrinsic"
    );

    public static void invokeJLink(File jvmDir, File instJVMDir, Properties properties) {

        String jlinkBin = jvmDir + File.separator + "bin" + File.separator + "jlink";
        File jlinkFile = getPhosphorJLinkJarFile();
        String modulesToAdd = properties.getProperty(MODULES_PROPERTY,
                "java.base,jdk.jdwp.agent,java.instrument,jdk.unsupported");

        Set<String> classPaths = new HashSet<>();
        if (Configuration.PRIOR_CLASS_VISITOR != null) {
            classPaths.add(Configuration.PRIOR_CLASS_VISITOR.getProtectionDomain().getCodeSource().getLocation().getPath());
        }
        if (Configuration.POST_CLASS_VISITOR != null) {
            classPaths.add(Configuration.POST_CLASS_VISITOR.getProtectionDomain().getCodeSource().getLocation().getPath());
        }
        if (Configuration.taintTagFactoryPackage != null) {
            classPaths.add(Configuration.taintTagFactory.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        }

        List<String> commands = new ArrayList<>(List.of(
                jlinkBin,
                "-J-javaagent:" + jlinkFile,
                "-J--module-path=" + jlinkFile,
                "-J--add-modules=edu.columbia.cs.psl.jigsaw.phosphor.instrumenter",
                "-J--class-path=" + String.join(":", classPaths),
                "--output=" + instJVMDir,
                "--phosphor-transformer=transform" + createPhosphorJLinkPluginArgument(properties),
                "--add-modules=" + modulesToAdd
        ));

        if (!Configuration.taintTagFactory.getClass().getName().contains("FieldOnly")) {
            commands.add("--add-options=-XX:+UnlockDiagnosticVMOptions " +
                    INTRINSICS.stream().map(it -> "-XX:-" + it).collect(Collectors.joining(" ")));
        }

        ProcessBuilder pb = new ProcessBuilder(commands.toArray(new String[]{}));
        try {
            for(String s : pb.command()){
                System.out.print(s + " ");
            }
            System.out.println();
            Process p = pb.inheritIO().start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return a File object pointing to the JAR file for Phosphor-jlink bridge
     */
    public static File getPhosphorJLinkJarFile() {
        try {
            return new File(JLinkInvoker.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new AssertionError();
        }
    }

    /**
     * @param properties canonicalized properties that specify the Phosphor configuration options that should set in the
     *                   created argument
     * @return a String formatted for {@link PhosphorJLinkPlugin}'s arguments
     * String argument
     */
    public static String createPhosphorJLinkPluginArgument(Properties properties) {
        if (properties.isEmpty()) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            Set<String> propNames = properties.stringPropertyNames();
            for (String propName : propNames) {
                if(propName.equals(MODULES_PROPERTY)) {
                    continue;
                }
                builder.append(':');
                builder.append(propName);
                builder.append('=').append(properties.getProperty(propName));
            }
            return builder.toString();
        }
    }
}
