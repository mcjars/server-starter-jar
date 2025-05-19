package dev.mcvapi.forgeserverjar;

import dev.mcvapi.forgeserverjar.server.ServerBootstrap;
import dev.mcvapi.forgeserverjar.utils.ErrorReporter;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ForgeServerJAR {
	public static void main(final String[] args) {
		String directoryPath = "libraries/net/minecraftforge/forge";
		String forgeVersion = null;
		File directory = new File(directoryPath);
		File[] filesAndDirs = directory.listFiles();

		if (filesAndDirs == null) {
			ErrorReporter.error("08", true);
		}

		assert filesAndDirs != null;
		for (File fileOrDir : filesAndDirs) {
			if (fileOrDir.isDirectory()) {
				forgeVersion = fileOrDir.getName();
			}
		}

		if (forgeVersion == null) {
			ErrorReporter.error("09", true);
		}

		String[] vmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
		String[] cmd = new String[vmArgs.length + args.length + 2];

		String javaHome = System.getenv("JAVA_HOME");
		if (javaHome == null) {
			cmd[0] = "java";
		} else {
			cmd[0] = javaHome + "/bin/java";
		}

		System.arraycopy(vmArgs, 0, cmd, 1, vmArgs.length);

		boolean windows = System.getProperty("os.name").startsWith("Windows");
		String argsFileName = (windows ? "win" : "unix") + "_args.txt";

		Path localArgsPath = Paths.get(argsFileName);
		String argsFilePath;

		if (Files.exists(localArgsPath)) {
			argsFilePath = localArgsPath.toString();
		} else {
			argsFilePath = directoryPath + "/" + forgeVersion + "/" + argsFileName;
		}

		cmd[1 + vmArgs.length] = "@" + argsFilePath;

		System.arraycopy(args, 0, cmd, 2 + vmArgs.length, args.length);

		try {
			new ServerBootstrap().startServer(cmd);
		} catch (ServerBootstrap.ServerStartupException exception) {
			exception.printStackTrace();
			System.exit(1);
		}
	}
}
