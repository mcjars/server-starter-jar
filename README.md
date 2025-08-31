# Forge/NeoForge/Magma Server JAR

## based on [sparkedhost/forge-server-jar](https://github.com/sparkedhost/forge-server-jar)

This jar simply runs another JVM with arguments in the `unix_args.txt` or `win_args.txt` file that's located in a subdirectory of `libraries`. Arguments file in current working directory will take precedence on those in `libraries`. Essentially the same as the run scripts typically included in server modpacks.
