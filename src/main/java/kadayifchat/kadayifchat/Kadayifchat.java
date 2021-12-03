package kadayifchat.kadayifchat;

import com.google.inject.Inject;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Plugin(
        id = "kadayifchat",
        name = "Kadayifchat",
        description = "YogurtluKadayif Chat Plugini",
        authors = {
                "YogurtluKadayif"
        }
)
public class Kadayifchat {

    public static Kadayifchat instance;
    @Inject
    public Logger logger;
    @Inject
    public GuiceObjectMapperFactory factory;
    public final PluginContainer container;
    public final Path configDir;
    public static Config config;
    public String del = "";
    public String[] delA;
    public String[] web = {"www", ".net", ".com", "play.", ".gg", "http", ".xyz"};

    @Inject
    public Kadayifchat(@ConfigDir(sharedRoot = false) final Path path, final PluginContainer container) {
        this.container = container;
        instance = this;
        this.configDir = path;

        if (Files.notExists(path)) {
            container.getAsset("config.txt").ifPresent(asset -> {
                try {
                    asset.copyToDirectory(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (final IOException exc) {
                this.logger.error("Config klasoru olusturulamadi: {}", exc.getMessage());
            }
        }

        this.config = new Config(this.configDir.resolve("config.txt"));
    }

    public void readConf() {
        if(this.config.getNode("del:").getValue() != null) {
            del = this.config.getNode("del:").getValue().toString();
            delA = del.split(",");
            int vallen = delA.length;
            System.out.println(Arrays.toString(delA) + " " + vallen);
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Sponge.getCommandManager().register(this, myCommandSpec, "kchat");
        if(this.config.getNode("del:").getValue() == null)
        {
            this.config.getNode("del:").setValue("test,");
            this.config.save();
        }
        readConf();
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }

    @Listener
    public void onMessage(MessageChannelEvent.Chat event, @First Player p) {
        if(del != "") {
            String message = event.getMessage().toPlain().toLowerCase();
            if(stringContainsItemFromList(message, delA) == true) {
                event.setCancelled(true);
                p.sendMessage(Text.of(TextColors.BLUE, "[", TextColors.AQUA, "kChat", TextColors.BLUE, "] ", TextColors.RED, "Yasaklanmis bir kelime kullandiniz."));
            } else {
                if(stringContainsItemFromList(message, web) == true) {
                    event.setCancelled(true);
                    p.sendMessage(Text.of(TextColors.BLUE, "[", TextColors.AQUA, "kChat", TextColors.BLUE, "] ", TextColors.RED, "Yasaklanmis bir kelime kullandiniz."));
                } else {
                    if(stringContainsItemFromList(message.replaceAll(" ", ""), delA) == true || stringContainsItemFromList(message.replaceAll(" ", ""), web) == true) {
                        event.setCancelled(true);
                        p.sendMessage(Text.of(TextColors.BLUE, "[", TextColors.AQUA, "kChat", TextColors.BLUE, "] ", TextColors.RED, "Yasaklanmis bir kelime kullandiniz."));
                    }
                }
            }
        }
    }

    CommandSpec reload = CommandSpec.builder()
            .description(Text.of("Reload config"))
            .permission("admin.kchatreload")
            .executor((CommandSource src, CommandContext args) -> {
                readConf();
                src.sendMessage(Text.of(TextColors.BLUE, "[", TextColors.AQUA, "kChat", TextColors.BLUE, "] ", TextColors.RED, "Reload basarili."));
                return CommandResult.success();
            })
            .build();

    CommandSpec myCommandSpec = CommandSpec.builder()
            .description(Text.of("kadayifChat"))
            .permission("kchat.kchat")
            .child(reload, "reload")
            .executor((CommandSource src, CommandContext args) -> {
                src.sendMessage(Text.of(" "));
                src.sendMessage(Text.of(TextColors.DARK_BLUE, "-----", TextColors.BLUE, "kadayifChat v1.0", TextColors.DARK_BLUE, "-----"));
                src.sendMessage(Text.builder("/kchat reload                ").color(TextColors.DARK_AQUA).append(
                        Text.builder("| Plugini reloadlar.").color(TextColors.DARK_PURPLE).build()).build()
                );
                return CommandResult.success();
            })
            .build();
}
