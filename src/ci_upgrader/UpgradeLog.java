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

            String upgradelog = loginstall + logcopy + logedit + getLogAfterUpgrade() + getLogProblemSolving();

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

        afterupgrade = afterupgrade + "Read docu of codeigniter upgrade and kenjis upgrade helper docu\n\n" +
                                    "Check base_url in config/app.php; .env and .htaccess file \n\n";

        afterupgrade = afterupgrade + "todo\n\n";

        afterupgrade = afterupgrade + "################################################################################\n";

        return afterupgrade;
    }

    public String getLogProblemSolving(){
        String problemsolving = "|-----| 5. SOLVING PROBLEMS |-----|";

        problemsolving = problemsolving + "\n################################################################################\n\n";

        problemsolving = problemsolving + "todo";

        return problemsolving;
    }

}
