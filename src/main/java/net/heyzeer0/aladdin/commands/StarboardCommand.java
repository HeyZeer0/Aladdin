package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.heyzeer0.aladdin.database.entities.StarboardProfile;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;
import net.heyzeer0.aladdin.profiles.utilities.Reactioner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by HeyZeer0 on 12/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class StarboardCommand implements CommandExecutor {

    @Command(command = "starboard", description = "Crie ou delete starboards", aliasses = {"sboard"}, parameters = {"criar/remover/list"}, type = CommandType.MISCELLANEOUS, isAllowedToDefault = false,
            usage = "a!starboard criar 3 #starboard\na!starboard remover 0\na!starboard list")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("criar")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "criar", "quantidade necessária de emotes", "#canal");
            }

            if(e.getMessage().getMentionedChannels().size() < 1) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não mencionou nenhum canal!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            try{

                Integer amount = Integer.valueOf(args.get(1));
                String ch = e.getMessage().getMentionedChannels().get(0).getId();

                new Reactioner(EmojiList.THINKING + " Adicione como reação nesta mensagem o emote que quer utilizar", e.getAuthor().getIdLong(), e.getChannel(), (v) -> {
                    String emote = v.getReactionEmote().getName() + "|" + (v.getReactionEmote().getId() == null ? "null" : v.getReactionEmote().getId());
                    if(e.getGuildProfile().getStarboards().containsKey(emote)) {
                        e.sendMessage(EmojiList.WORRIED + " Oops, o emote mencionado já pertence a outra starboard.");
                        return;
                    }
                    e.getGuildProfile().createStarboard(emote, amount, ch);

                    e.sendMessage(EmojiList.CORRECT + " Você criou com sucesso a starboard.");
                });

            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Oops, a quantidade inserida é invalida");
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("remover")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "deletar", "id");
            }

            try{
                Integer id = Integer.valueOf(args.get(1));

                if(id < 0) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, o número precisa ser maior ou igual a zero");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(e.getGuildProfile().getStarboards().size() < id) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, a starboard com a id inserida não existe");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                e.getGuildProfile().deleteStarboard(id);

                e.sendMessage(EmojiList.CORRECT + " Você deletou com sucesso a starboard de id ``" + id + "``");

            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Oops, a id inserida é invalida");
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("list")) {

            HashMap<String, StarboardProfile> starboards = e.getGuildProfile().getStarboards();
            String[] keyset = starboards.keySet().toArray(new String[] {});

            if(starboards.size() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que você não posssui uma starboard você pode criar uma utilizando o comando ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "starboard criar``");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            Paginator ph = new Paginator(e, ":newspaper: Listando todas as starboads!");

            int pages = (starboards.size() + (10 + 1)) / 10;


            Integer actual = 0;
            Integer pactual = 1;


            for (int i = 1; i <= pages; i++) {
                String pg = "";
                for (int p = actual; p < pactual * 10; p++) {
                    if (starboards.size() <= p) {
                        break;
                    }
                    actual++;

                    String emote = starboards.get(keyset[p]).getEmote().split("\\|")[0];

                    pg = pg + "ID " + p + " | Emote: " +  emote + "(" + starboards.get(keyset[p]).getMessages().size() + " mensagens)\n";
                }
                pactual++;
                ph.addPage(pg);

            }

            ph.start();
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
