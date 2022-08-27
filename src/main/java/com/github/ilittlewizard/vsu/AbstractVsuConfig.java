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
import java.util.Objects;

public abstract class AbstractVsuConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path VSU_CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("vsu/");

    private transient final Gson SERIALIZER = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Gson DESERIALIZER = new Gson();

    private final transient String name;
    public AbstractVsuConfig(String name) {
        this.name = name;
    }

    protected File getConfigFile() {
        File configFile = VSU_CONFIG_PATH.resolve(name + ".config").toFile();
        if(!configFile.exists()) {
            configFile.getParentFile().mkdirs();
        }
        return configFile;
    }

    protected void save() {
        try (FileWriter writer = new FileWriter(getConfigFile(), false)) {
            SERIALIZER.toJson(this, writer);
        } catch (Exception e) {
            LOGGER.error("Failed to save config", e);
            throw new VsuRuntimeException("Failed to save config of type " + getClass(), e);
        }
    }

    protected static <T> T load(String configName, Class<T> cls, T defaultConfig) {
        File config = VSU_CONFIG_PATH.resolve(configName + ".config").toFile();
        try {
            if(!config.exists()) {
                config.getParentFile().mkdirs();
                config.createNewFile();
                return defaultConfig;
            }
        } catch (IOException e) {
            // Try it next time
        }

        try (FileReader reader = new FileReader(config)) {
            return Objects.requireNonNullElse(DESERIALIZER.fromJson(reader, cls),
                    defaultConfig);
        } catch (Exception e) {
            LOGGER.error("Failed to read config", e);
            throw new VsuRuntimeException(
                    "Failed to read config of type " + cls +
                            " from " + config.getPath(),
                    e);
        }
    }
}
