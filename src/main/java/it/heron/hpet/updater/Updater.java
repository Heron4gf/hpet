package it.heron.hpet.updater;

import it.heron.hpet.Pet;
import it.heron.hpet.Utils;

public class Updater {

    private static int versionId(String version) {
        String[] splitted = version.split("[.]");
        int a = Integer.parseInt(splitted[0]);
        int b = Integer.parseInt(splitted[1]);
        int c = Integer.parseInt(splitted[2]);

        return a+b+c;
    }

    //if(p.hasPermission("pet.admin.notifications")) {
    //            Utils.runAsync(() -> {
    //                String s = Utils.getResponse("http://heron4gf.space/versions.txt").split("L:")[1];
    //                if(!s.equals(Pet.getInstance().getDescription().getVersion())) {
    //                    new BukkitRunnable() {
    //                        @Override
    //                        public void run() {
    //                            p.sendMessage("§a§lHPET: §eGuess what? There's an HPET update!");
    //                            p.sendMessage("§e Download HPET §a"+s+" §enow! §ahttps://www.spigotmc.org/resources/hpet-1-8-1-18-packet-based-pet-system.93891/");
    //                        }
    //                    }.runTaskLater(Pet.getInstance(), 30);
    //                }
    //            });
    //        }


    private static boolean isUpdate = false;
    private static long lastCheck = 0l;

    public static boolean isThereUpdate() {
        if(System.currentTimeMillis()-lastCheck < 100000) return isUpdate;
        int current = versionId(Pet.getInstance().getDescription().getVersion());
        int latest = versionId(Utils.getResponse("http://heron4gf.space/versions.txt").split("L:")[1]);
        isUpdate = latest>current;
        lastCheck = System.currentTimeMillis();
        return isThereUpdate();
    }

}
