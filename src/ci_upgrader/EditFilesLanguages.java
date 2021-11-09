package ci_upgrader;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class EditFilesLanguages {

    //|-------->  |RUN METHOD|  <-------|
    public void run(Path path_ci3, Path path_ci4) throws IOException {
        System.out.println("\n" + "###  |Languages|  ###");

//----- ###  |Edit Language Files|  ###

        File tar = new File (path_ci4 + "\\app\\Language");

        ArrayList<File> language_list = new ArrayList<>();
        getLanguageFiles(tar, language_list);

        for (int i = 0; i < language_list.size(); i++){
            editLanguageFile(language_list.get(i));
            outputFiles(language_list.get(i));

//----- ###  |Get Strings for UpgradeLog|  ###
            if (files_numb >= 1) {
                lang_log = lang_log + "-------------------------------------------------------------------------------\n\n";
            }
            lang_log = lang_log + buildUpgradeLogString(language_list.get(i).getParentFile().getName() + "\\" + language_list.get(i).getName());
            files_numb++;
        }
    }

    //|-------->  |METHOD TO GET ALL LANGUAGE FILES|  <-------|
    public static void getLanguageFiles (File tar, ArrayList<File> language_list){            //Returns list with all files in migrations folder

        for (File file : tar.listFiles()){

            if (file.isDirectory()){
                getLanguageFiles(file, language_list);}

            if (file.isFile()){
                if (file.getName().matches(".*\\.php")){            //Check if file is a php file
                language_list.add(file);}
            }
        }
    }

    //|-------->  |METHODS TO EDIT CONTENT OF LANGUAGES FILES|  <-------|
    public void editLanguageFile (File tar) throws IOException {

        tar = removeComments(tar);      //Remove comments to make sure, only used language lines are edited

        ArrayList<String> langlines = getLanguageLines(tar);
        ArrayList<String> langlinesedit = editLanguageLines(langlines);

        //Build new CI4 language file content
        String content = "";
        String filebegin = "return [" + "\n";       //Add filebegin and fileend of CI4 language file
        String fileend = "];";
        String languagelines = "";
        countfiles = 2;
        edit_numb = 2;

        for (int i = 0; i < langlinesedit.size(); i++){
            languagelines = languagelines + langlinesedit.get(i) + "\n";
            countfiles++;
            edit_numb++;
        }
        //Output
        editfiles = "\t" + filebegin + languagelines + "\t" + fileend + "\n";
        //UpgradeLog
        edit_upgrade_log = edit_upgrade_log + "\n   " + filebegin + languagelines + "   " +fileend + "\n";

        //Build all parts of language file together
        content = "<?php \n" + filebegin + languagelines + fileend;

        //Write new language file
        FileWriter fwtar = new FileWriter(tar);
        fwtar.write(content);
        fwtar.close();
    }

    private ArrayList<String> getLanguageLines(File tar) throws IOException {
        ArrayList<String> langlines = new ArrayList<String>();
        String line = null;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null){               //Read target file line by line

            if (line.contains("$lang") && !line.contains("//$lang")){
                langlines.add(line);
            }
        }
        return langlines;
    }

    private ArrayList<String> editLanguageLines(ArrayList<String> langlines) {

        ArrayList<String> langlinesedit = new ArrayList<String>();

        for (int i = 0; i < langlines.size(); i++){
            String line = langlines.get(i);

            //Edit line into CI4 language syntax
            String newline = line.replace("$lang[", "");
            newline = newline.replace("]", "");
            newline = newline.replace("=", "=>");
            newline = newline.replace(";", ",");
            newline = "\t" + newline;

            langlinesedit.add(newline);
        }
        return langlinesedit;
    }

    public File removeComments(File tar) throws IOException {
        String content = "";
        String content_no_comments = "";
        String line = "";

        FileReader frsrc = new FileReader(tar);
        BufferedReader brsrc = new BufferedReader(frsrc);

        //Get whole content of file into string
        while ((line = brsrc.readLine()) != null) {
            content = content + line + "\n";
        }
        frsrc.close();
        brsrc.close();

        //Remove comments and write the file new
        content_no_comments = content.replaceAll("(?s)(?>\\/\\*(?>(?:(?>[^*]+)|\\*(?!\\/))*)\\*\\/)", "");
        FileWriter fwtar = new FileWriter(tar);
        fwtar.write(content_no_comments);
        fwtar.close();

        return tar;
    }

    //|-------->  |VARIABLES AND METHOD FOR OUTPUT|  <-------|
    public int countfiles = 0;
    public String editfiles = "";

    public void outputFiles(File tar){
        if (countfiles == 0){System.out.println("No Lines edited in " + tar.getParentFile().getName() + "\\" + tar.getName());}
        else if (countfiles == 1){System.out.println(countfiles + " Line edited in " + tar.getParentFile().getName() + "\\" + tar.getName() + ": ");}
        else {System.out.println(countfiles + " Lines edited in " + tar.getParentFile().getName() + "\\" + tar.getName() + ": ");}

        System.out.println(editfiles);
        countfiles = 0;
        editfiles = "";
    }

    //|-------->  |METHOD AND VARIABLES TO CREATE STRING FOR UPGRADE LOG|  <-------|
    protected static String upgradelog = "";
    protected static String edit_upgrade_log = "";
    protected static String lang_log = "";
    protected static Integer files_numb = 0;
    protected static Integer edit_numb = 0;

    public String getUpgradeLog(Path path_ci3, Path path_ci4) throws IOException{
        upgradelog = "################################  |" + "LANGUAGES" + "| ################################\n";

        upgradelog = upgradelog + "[CI3] '" + path_ci3 + "\\application\\libraries'\n";
        upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Libraries'\n\n";

        if (files_numb == 0){
            upgradelog = upgradelog + "No Languages files found\n\n";
        }else{
            upgradelog = upgradelog + lang_log;
        }

        return upgradelog;
    }

    public String buildUpgradeLogString (String title){
        String log = "| "  + title + " (CI4) |\n";

        log = log + "Lines edited: " + edit_numb;

        log = log + edit_upgrade_log + "\n";

        edit_numb = 0;
        edit_upgrade_log = "";

        return log;
    }

}
