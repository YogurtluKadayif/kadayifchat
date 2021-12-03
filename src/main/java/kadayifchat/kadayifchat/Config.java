package kadayifchat.kadayifchat;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final CommentedConfigurationNode node;
    public Config(@Nonnull final Path path) {
        if (!Files.exists(path)) {
            try { Files.createFile(path); }
            catch (final IOException exc) { Kadayifchat.instance.logger.error("Config dosyasi olusturulamadi: " + path.toString(), exc); }
        }
        this.loader = HoconConfigurationLoader.builder().setPath(path).build();
        this.node = this.load();
    }
    private CommentedConfigurationNode load() {
        try { return this.loader.load(); }
        catch (final IOException exc) { Kadayifchat.instance.logger.error("Config dosyasi yuklenemedi", exc); return this.loader.createEmptyNode(); }
    }
    public void save() {
        try { this.loader.save(this.node); }
        catch (final IOException exc) { Kadayifchat.instance.logger.error("Config dosyasi kaydedilemedi", exc); }
    }
    public CommentedConfigurationNode getNode() {
        return this.node;
    }
    public CommentedConfigurationNode getNode(@Nonnull final Object... keys) {
        return this.node.getNode(keys);
    }
}
