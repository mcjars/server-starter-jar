package dev.mcvapi.forgeserverjar;

import dev.mcvapi.forgeserverjar.server.ServerBootstrap;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ForgeServerJAR {
	public static void main(final String[] args) {
		String[] searchPaths = { "libraries/net/minecraftforge/forge", "libraries/net/neoforged/neoforge",
				"libraries/net/neoforged/forge", "libraries/org/magmafoundation/magma" };
		File directory = null;
		String loaderVersion = null;

		for (String path : searchPaths) {
			directory = new File(path);
			if (directory.isDirectory()) {
				for (File sub : directory.listFiles(File::isDirectory)) {
					loaderVersion = sub.getName();
					break;
				}

				if (loaderVersion != null)
					break;
			}
		}

		String thisJar = "server.jar";
		try {
			thisJar = ForgeServerJAR.class
					.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.toURI()
					.getPath();
		} catch (URISyntaxException e) {
		}

		if (directory == null || loaderVersion == null) {
			System.err.println(
					"Neither Forge 1.17+, NeoForge or Magma were found. Try to remove `" + thisJar + "` or reinstall loader.");
			System.exit(1);
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
			argsFilePath = directory.getPath() + "/" + loaderVersion + "/" + argsFileName;
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
