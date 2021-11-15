package ci_upgrader;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;


public class UpgradeLog {

    public void run(Path path_ci4, String loginstall, String logcopy, String logedit){

        try {
            File log = new File(path_ci4 + "\\Upgrade_Log.txt");
            log.createNewFile();
            System.out.println("File created: " + log.getName());

            String upgradelog = loginstall + logcopy + logedit + getLogAfterUpgrade();

            FileWriter myWriter = new FileWriter(log);
            myWriter.write(upgradelog);
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public String getLogAfterUpgrade(){
        String afterupgrade = "|-----| 4. WHAT TO DO AFTER CI-UPGRADER |-----|";

        afterupgrade = afterupgrade + "\n################################################################################\n\n";

        afterupgrade = afterupgrade + "Check out the User-Guide of CI-Upgrader. Here you will find all informations about the tasks you have to do after the upgrade. " +
                "\n(https://github.com/FlorianNelles/CI-Upgrader/blob/main/Documentation.md#after-the-upgrade)\n";

        return afterupgrade;
    }


}
