package net.heyzeer0.aladdin.profiles;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.utils.FileReader;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 09/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class LangProfile {

    public static final LangProfile baseLang = new LangProfile("base", true);

    String name;
    boolean isBase;
    File path;

    HashMap<String, String> messages = new HashMap<>();

    public LangProfile(String name, boolean isBase) {
        this.name = name;
        this.isBase = isBase;

        File bPath = new File(Main.getDataFolder(), "langs"); bPath.mkdirs();

        try{
            path = new File(bPath, name + ".lang"); path.createNewFile();

            loadLang();
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadLang() throws Exception {
        if(isBase) {
            messages = new FileReader(path).getValues();
            return;
        }

        HashMap<String, String> toCreate = new HashMap<>();
        FileReader reader = new FileReader(path);

        for(String k : baseLang.messages.keySet()) {
            if(reader.hasValue(k)) {
                messages.put(k, reader.getValue(k));
            }else{
                toCreate.put(k, baseLang.get(k));
            }
        }

        //autogen do file
        if(toCreate.size() > 0) {
            FileWriter r = new FileWriter(path);

            for(String k : reader.getValues().keySet()) {
                toCreate.put(k, reader.getValues().get(k));
            }

            for(String x : toCreate.keySet()) {
                r.write("[" + x + "] = " + toCreate.get(x) + "\n");
            }

            r.close();
        }
    }

    public String get(String key) {
        return messages.get(key).replace("\\n", "\n");
    }

    public HashMap<String, String> getMessages() {
        return messages;
    }

}