package com.moguang.visiblejs;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class FileHandler {
    private Path validateAndNormalizePath(String path) {
        Path minecraftDir = FMLPaths.GAMEDIR.get().normalize().toAbsolutePath();
        path = path.replace('\\', '/');
        return minecraftDir.resolve(path).normalize().toAbsolutePath();
    }
}
