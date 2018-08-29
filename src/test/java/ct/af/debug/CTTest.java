package ct.af.debug;

import ec02.core.utils.ECStringUtils;
import ec02.server.EC02Handler;
import ec02.server.EC02Server;
import ec02.server.Version;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CTTest {

    private CTTest() {
    }

    private static Logger LOGGER = Logger.getLogger("CTTest");

    public static CTTest getInstance() {
        return CTTest.SingletonHelper.instance;
    }

    public void run(String var1, String var2, String var3, String var4, String var5) {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter(){
            private static final String format = "%1$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format, lr.getMessage()
                );
            }
        });
        LOGGER.addHandler(handler);

        String var6 = this.setupConfiguration(var1, var2, var4, var5);
        ArrayList var7 = new ArrayList();
        FileReader var8 = this.getFileReader(var3);

        try (BufferedReader var10 = new BufferedReader(var8)) {

            StringBuilder var9 = new StringBuilder();
            String var11;
            while((var11 = var10.readLine()) != null) {
                var9.append(var11);
                var9.append(System.getProperty("line.separator"));
            }

            var10.close();
            Pattern var12 = Pattern.compile("(?is)<dummy\\-message id\\=\"\\d+\">.*?</dummy\\-message>");
            Matcher var13 = var12.matcher(var9.toString());

            while(var13.find()) {
                String var14 = var13.group().replaceAll("(?is)(<dummy\\-message id\\=\"\\d+\">)|(</dummy\\-message>)", "").replace("\r","");
                var7.add(var14);
            }

            if(!var7.isEmpty()) {
                LOGGER.info("[" + var1 + "] Running ");
                EC02Handler var16 = new EC02Handler();
                if(var16.verifyAFConfig(var6) == null) {
                    LOGGER.severe("Verify AF Config Error");
                    System.exit(0);
                }

                this.sendMessage(var7, var16);
                LOGGER.info("[" + var1 + "] run successful\n\n");
            } else {
                LOGGER.info("Not found messages to send!!! Please check your request file");
            }

            this.closeFileReader(var8);
        } catch (Exception var15) {
            LOGGER.severe("AFTest is fail!!!");
            LOGGER.log(Level.SEVERE, var15.getMessage(), var15);
            this.closeFileReader(var8);
            System.exit(-1);
        }

    }

    private String setupConfiguration(String var1, String var2, String var3, String var4) {
        Path var5 = Paths.get(var3, new String[0]);
        Path var6 = Paths.get(var4, new String[0]);
        LOGGER.info("[" + var1 + "]Loading configuration...");
        String var7 = Paths.get(var2, new String[0]).getFileName().toString();
        if(var7.split("\\.").length != 4) {
            LOGGER.info("Please check your config file's name.");
            System.exit(0);
        }

        String var8 = var7.split("\\.")[0];
        String var9 = var7.split("\\.")[2];
        String var10 = var7.split("\\.")[3];
        FileReader var11 = this.getFileReader(var2);
        String var12 = "";

        try {
            StringBuilder var13 = new StringBuilder("");
            BufferedReader var14 = new BufferedReader(var11);

            String var15;
            while((var15 = var14.readLine()) != null) {
                var13.append(var15);
            }

            var14.close();
            var12 = var13.toString();
            Pattern var16 = Pattern.compile("<HomeDirectory\\s{1,}value=\"(.*?)\"");
            Matcher var17 = var16.matcher(var12);
            if(var17.find()) {
                var12 = var12.replaceAll("<HomeDirectory\\s{1,}value=\"(.*?)\"", "<HomeDirectory value=\"" + var5.toAbsolutePath().toString().replaceAll("\\\\", "/") + "\"");
            }

            var16 = Pattern.compile("<Library\\s{1,}directory=\"(.*?)\"");
            var17 = var16.matcher(var12);
            if(var17.find()) {
                var12 = var12.replaceAll("<Library\\s{1,}directory=\"(.*?)\"", "<Library directory=\"" + var6.toAbsolutePath().toString().replaceAll("\\\\", "/") + "\"");
            }

            String[] var18 = new String[]{var8, var9, var10, var12, Version.getLibraryVersion()};
            EC02Server.main(var18);
        } catch (Exception var22) {
            LOGGER.severe("Load configuration error");
            this.closeFileReader(var11);
            LOGGER.log(Level.SEVERE, var22.getMessage(), var22);
            System.exit(-1);
        } finally {
            this.closeFileReader(var11);
        }

        return var12;
    }

    private FileReader getFileReader(String var1) {
        FileReader var2 = null;

        try {
            var2 = new FileReader(var1);
        } catch (FileNotFoundException var4) {
            LOGGER.info("Not found : " + var1);
            System.exit(-1);
        }

        return var2;
    }

    private void closeFileReader(FileReader var1) {
        try {
            var1.close();
        } catch (IOException var3) {
            LOGGER.log(Level.SEVERE, var3.getMessage(), var3);
        }

    }

    private void sendMessage(List<String> var1, EC02Handler var2) {
        String var3 = null;
        int var4 = 1;

        for(Iterator var5 = var1.iterator(); var5.hasNext(); ++var4) {
            String var6 = (String)var5.next();
            String var7 = var6;
            if(var3 != null && !var3.trim().isEmpty()) {
                var7 = var6.replaceAll("(?is)<EquinoxInstance>.*?</EquinoxInstance>", var3);
            }

            String var8 = var2.handle(var7.getBytes(), 8404992);
            LOGGER.info("[" + var4 + "] IN  >>>>> " + ECStringUtils.trimString(var7).replaceAll("^\\s+", ""));
            LOGGER.info("[" + var4 + "] OUT <<<<< " + ECStringUtils.trimString(var8).replaceAll("^\\s+", ""));
            Pattern var9 = Pattern.compile("(?is)<EquinoxInstance>.*?</EquinoxInstance>");
            Matcher var10 = var9.matcher(var8);
            if(var10.find()) {
                var3 = var10.group();
            }
        }

    }

    private static class SingletonHelper {
        public static final CTTest instance = new CTTest();

        private SingletonHelper() {
        }
    }
}
