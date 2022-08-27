package com.github.ilittlewizard.vsu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public abstract class VsuConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path VSU_CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("vsu/");

    private final Gson SERIALIZER = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Gson DESERIALIZER = new Gson();

    private final transient String name;
    public VsuConfig(String name) {
        this.name = name;
    }

    protected File getConfigFile() {
        return VSU_CONFIG_PATH.resolve(name + ".config").toFile();
    }

    protected void save() {
        try (FileWriter writer = new FileWriter(getConfigFile(), false)) {
            SERIALIZER.toJson(this, writer);
        } catch (IOException e) {
            throw new VsuRuntimeException("Failed to save config of type " + getClass(), e);
        }
    }

    protected static <T> T load(String configName, Class<T> cls, T defaultConfig) {
        File config = VSU_CONFIG_PATH.resolve(configName + ".config").toFile();
        if(!config.exists()) {
            return defaultConfig;
        }

        try (FileReader reader = new FileReader(config)) {
            return DESERIALIZER.fromJson(reader, cls);
        } catch (IOException e) {
            throw new VsuRuntimeException(
                    "Failed to read config of type " + cls +
                            " from " + config.getPath(),
                    e);
        }
    }
}
