package ci_upgrader;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class EditFilesModels {

    //|-------->  |RUN METHOD|  <-------|
    public void run(Path path_ci3, Path path_ci4) throws IOException {
        System.out.println("\n" + "###  |Models|  ###");

        File tar = new File (path_ci4 + "\\app\\Models");

//----- ###  |Edit Models Subfoldernames|  ###

        editSubfolderNames(tar);
        outputFoldernames(tar);

//----- ###  |Edit Models Files|  ###

        ArrayList<File> modelslist = new ArrayList<>();
        getModelsList(tar, modelslist);

        for (int i = 0; i < modelslist.size(); i++){
            editFiles(modelslist.get(i));
        }

    }

    //|-------->  |METHOD TO RENAME MODELS SUBFOLDERS (JUST FIRST LETTER UPPERCASE)|  <-------|
    public void editSubfolderNames(File tar) throws IOException {

        for (File file : tar.listFiles()){
            if (file.isDirectory()){

                //Get new filename (first letter of folder uppercase and remove underscore from name)
                String oldfilename = file.getName();
                String newfilename = oldfilename.substring(0,1).toUpperCase() + oldfilename.substring(1).toLowerCase();

                if (!oldfilename.equals(newfilename)) {

                    //Rename file with new filename
                    File newfile = new File(file.getParent() + "\\" + newfilename);
                    file.renameTo(newfile);

                    //Output
                    editfoldernames = editfoldernames + "\t" + newfile.getName() + "\n";
                    countfoldernames++;
                    //UpgradeLog
                    edit_subfolder_log = edit_subfolder_log + "   [CI3] " + oldfilename + " ---> [CI4] " + newfile.getName() + "\n";
                    subfolder_numb++;
                }
                editSubfolderNames(file);
            }
            files_numb++;
        }
    }

    //|-------->  |METHOD TO GET ALL MODELS FILES|  <-------|
    public void getModelsList (File tar, ArrayList<File> modelslist){

        for (File file : tar.listFiles()){

            if (file.isDirectory()){
                getModelsList(file, modelslist);}

            if (file.isFile()){
                if (file.getName().matches(".*\\.php")){            //Check if file is a php file
                    modelslist.add(file);}
            }
        }
    }

    //|-------->  |METHODS TO EDIT CONTENT OF MODELS FILES|  <-------|
    public void editFiles (File tar)throws IOException{

        if (filesedit_count != 0){models_log = models_log + "-------------------------------------------------------------------------------\n\n";}

        editFilesHeader(tar);

        outputFiles(tar);
        models_log = models_log + buildUpgradeLogString(tar.getName());
        filesedit_count++;
    }

    private void editFilesHeader(File tar) throws IOException {
        String line = null;
        String content = "";
        String newcontent = "";
        int phptag = 0;
        int countline = 1;
        String namespace = getNamespace(tar);       //Get correct namesapce, important when you use subfolders

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null) {               //Read target file line by line

            if (line.contains("<?php") && phptag == 0){
                newcontent = "<?php \n" + namespace + " \n" + "use Kenjis\\CI3Compatible\\Core\\CI_Model; \n";
                line = newcontent;
                phptag++;

                //Output
                editfiles = editfiles + "\t" + namespace + "   |added| \n" + "\t" + "use Kenjis\\CI3Compatible\\Core\\CI_Model;   |added| \n";
                countfiles++;countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline + 1) + "]: " + namespace + " use Kenjis\\CI3Compatible\\Core\\CI_Model;   |added|\n";
                edit_numb++;
            }

            //Remove line "definded basepath"
            if(line.contains("defined('BASEPATH') OR exit('No direct script access allowed');")){

                newcontent = line.replace("defined('BASEPATH') OR exit('No direct script access allowed');", "");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + "defined('BASEPATH') OR exit('No direct script access allowed');" + "   |removed|" + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + countline + "]: " + "defined('BASEPATH') OR exit('No direct script access allowed');   |deleted|\n";
                edit_numb++;
            }

            content = content + line + "\n";
            countline++;
        }
        frtar.close();
        brtar.close();

        FileWriter fwtar = new FileWriter(tar);
        fwtar.write(content);
        fwtar.close();
    }

    public String getNamespace (File tar){
        String namespace = "";
        String path = tar.getParent();
        int index = 0;

        index = path.indexOf("app\\Models");
        path = path.substring(index);
        path = path.substring(0,1).toUpperCase() + path.substring(1);

        namespace = "namespace " + path + ";";
        return namespace;
    }

    //|-------->  |VARIABLES AND METHODS FOR OUTPUT|  <-------|
    public int countfoldernames = 0;
    public String editfoldernames = "";
    public int countfiles = 0;
    public String editfiles = "";

    public void outputFoldernames (File tar){
        if (countfoldernames == 0){System.out.println("No subfolders found/edited in app/Models/");}
        else if (countfoldernames == 1){System.out.println(countfoldernames + " subfoldername edited in app/Models/ : ");}
        else {System.out.println(countfoldernames + " subfoldernames edited in app/Models/ : ");}

        System.out.println(editfoldernames);
    }

    public void outputFiles(File tar){
        if (countfiles == 0){System.out.println("No Lines edited in " + tar.getName());}
        else if (countfiles == 1){System.out.println(countfiles + " Line edited in " + tar.getName() + ": ");}
        else {System.out.println(countfiles + " Lines edited in " + tar.getName() + ": ");}

        System.out.println(editfiles);
        countfiles = 0;
        editfiles = "";
    }

    //|-------->  |METHOD AND VARIABLES TO CREATE STRING FOR UPGRADE LOG|  <-------|
    protected static String upgradelog = "";
    protected static String edit_upgrade_log = "";
    protected static String models_log = "";
    protected static Integer files_numb = 0;
    protected static Integer edit_numb = 0;
    protected static String edit_subfolder_log = "";
    protected static Integer subfolder_numb = 0;
    protected static Integer filesedit_count = 0;

    public String getUpgradeLog(Path path_ci3, Path path_ci4) throws IOException{
        upgradelog = "################################  |" + "MODELS" + "| ################################\n";

        upgradelog = upgradelog + "[CI3] '" + path_ci3 + "\\application\\models'\n";
        upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Models'\n\n";

        if (files_numb == 0){
            upgradelog = upgradelog + "No Models files found\n\n";
        }else{
            upgradelog = upgradelog + "#####  | 1. Subfolders |  #####\n(In CI4, all names of Models subfolders has to be first letter uppercase)\n\n";

            if (subfolder_numb == 0){
                upgradelog = upgradelog + "Subfolder names edited: 0\n   - All subfoler names are already correct or no subfolders found\n";
            }else {upgradelog = upgradelog + "Subfolder names edited: " + subfolder_numb + "\n" + edit_subfolder_log;}

            upgradelog = upgradelog + "\n-------------------------------------------------------------------------------\n";

            upgradelog = upgradelog + "\n#####  | 2. Files content |  #####\n\n";

            upgradelog = upgradelog + models_log;
        }

        return upgradelog;
    }

    public String buildUpgradeLogString (String title){
        String log = "| "  + title + " (CI4) |\n";

        log = log + "Lines edited: " + edit_numb + "\n";

        log = log + edit_upgrade_log + "\n";

        edit_numb = 0;
        edit_upgrade_log = "";

        return log;
    }

}
