package ci_upgrader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditFilesMigrations {

    //|-------->  |RUN METHOD|  <-------|
    public void run(Path path_ci3, Path path_ci4) throws IOException {
        System.out.println("\n" + "###  |Migrations|  ###");

//----- ###  |Edit Migrations Config|  ###

        File src_migrations = new File (path_ci3 + "\\application\\config\\migration.php");
        File tar_migrations = new File (path_ci4 + "\\app\\Config\\Migrations.php");
        src_migrations = removeComments(src_migrations);

        searchReplaceConfig(src_migrations, tar_migrations, "$config['migration_enabled']", "public $enabled");
        searchReplaceConfig(src_migrations, tar_migrations, "$config['migration_table']", "public $table");
        replace(tar_migrations, "public $timestampFormat", "= 'YmdHis_';");

        outputConfig(tar_migrations);
        config_upgrade_log = buildUpgradeLogString(tar_migrations.getName());

        //Delete the helper-file -nocomments.php, which was created by method removeCommentes()
        if (src_migrations != null) {
            src_migrations.delete();
        }

//----- ###  |Edit Migrations Files|  ###

        File tar = new File (path_ci4 + "\\App\\Database\\Migrations");

        //###  |Name of Migrations Files|  ###

        ArrayList<File> migrations_list = getMigrationsList(tar);
        migrations_list = correctMigrationsFileNames(migrations_list);

        outputFilenames();

        //###  |Edit Content of Migrations Files|  ###

        for (int i = 0; i < migrations_list.size(); i++){
            editFile(migrations_list.get(i));
        }
    }

    //|-------->  |METHODS TO SEARCH AND REPLACE CONFIGURATIONS|  <-------|
    public void searchReplaceConfig (File src, File tar, String srctag, String tartag) throws IOException {
        if (src != null) {
            String config_value = search(src, srctag);

            if (config_value != null) {
                replace(tar, tartag, config_value);
            }
        }
    }

    public String search (File src, String srctag) throws IOException {
        String value = null;
        String srcline = null;

        FileReader frsrc = new FileReader(src);
        BufferedReader brsrc = new BufferedReader(frsrc);

        while ((srcline = brsrc.readLine()) != null){               //Reade source file line by line
            if(srcline.contains(srctag)){
                value = srcline.replace(srctag, "");     //Remove source-tag
                value = value.stripLeading();                       //Remove leading whitespace
                break;                                              //dont have to run through the whole file
            }
        }
        frsrc.close();
        brsrc.close();

        return value;
    }

    public void replace(File tar, String tartag, String value) throws IOException {
        String tarline = null;
        String content = "";
        String newcontent = "";
        int countline = 1;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((tarline = brtar.readLine()) != null){               //Read target file line by line
            if(tarline.contains(tartag)){

                newcontent = "\t" + tartag + " " + value;              //Edit line with value from CI3

                //Check if default value form CI4 is equals to CI3 value (if its same don´t edit line)
                if(!tarline.toLowerCase().strip().replaceAll(" ", "").equals(newcontent.toLowerCase().strip().replaceAll(" ", ""))){

                    tarline = newcontent;
                    //Output
                    editconfig = editconfig + tarline + "\n";
                    countconfig++;
                    //UpgradeLog
                    edit_upgrade_log = edit_upgrade_log + "\n   - [Line " + countline + "]: " + tarline.stripLeading();
                    edit_numb++;
                }
            }
            content = content + tarline + "\n";
            countline++;
        }
        frtar.close();
        brtar.close();

        FileWriter fwtar = new FileWriter(tar);
        fwtar.write(content);
        fwtar.close();
    }

    //|-------->  |METHOD TO REMOVE COMMENTS FROM CI3 FILE|  <-------|
    public File removeComments(File src_config) throws IOException {
        File src_no_comments = new File(src_config.getParent() + "\\nocomments.php");       //Create new help-file, to cache new content without comments
        String content = "";
        String content_no_comments = "";
        String line = "";
        String checkcommet = "";

        try {
        FileReader frsrc = new FileReader(src_config);
        BufferedReader brsrc = new BufferedReader(frsrc);

        //Get whole content of file into string
        while ((line = brsrc.readLine()) != null) {

            if (line.length() >= 2){
                checkcommet = line.stripLeading().substring(0,2);
            }

            if (!checkcommet.equals("//")){                 //Check if line begins with //, then its a comment line and has to be removed
                content = content + line + "\n";
            }
        }
        frsrc.close();
        brsrc.close();

        //Remove comments and write new file
        content_no_comments = content.replaceAll("(?s)(?>\\/\\*(?>(?:(?>[^*]+)|\\*(?!\\/))*)\\*\\/)", "");
        FileWriter fwtar = new FileWriter(src_no_comments);
        fwtar.write(content_no_comments);
        fwtar.close();

        return src_no_comments;
        }catch (FileNotFoundException e){
            System.out.println(src_config.getName() + " not found.\n");
            return null;
        }
    }

    //|-------->  |METHODS TO GET CORRECT MIGRATIONS FILENAMES (IN YmdHis_ FORMAT)|  <-------|
    public ArrayList<File> getMigrationsList (File tar){            //returns List with all Files in Migrations Folder
        ArrayList<File> list = new ArrayList<>();
        File[] migrations = tar.listFiles();

        for (File file : migrations){
            if(file.getName().matches(".*\\.php")) {            //Check if file is a php file
                list.add(file);
                migrations_numb++;
            }
        }
        return list;
    }

    public ArrayList<File> correctMigrationsFileNames (ArrayList<File> list) throws IOException {
        Boolean filename = null;
        ArrayList<File> correctlist = new ArrayList<>();

        for (File f: list){
            filename = checkMigrationsFileNames(f);         //Check if migration filenames are in correct format

            if (filename != true){                          //Edit filenames, if they are in wrong format
                String oldname = f.getName();

                f = editMigrationsFileNames(f);
                correctlist.add(f);

                //Output
                editfilenames = editfilenames + "\t" + f.getName() + "\n";
                //UpgradeLog
                filenames_upgrade_log = filenames_upgrade_log + "   [CI3] " + oldname + " ---> [CI4] " + f.getName() + "\n";
                filenames_count++;
            }else {
                correctlist.add(f);
            }
        }
        return correctlist;
    }

    public Boolean checkMigrationsFileNames (File tar){
        Boolean checkname = false;
        String filename = tar.getName();
        String filetype = "";
        int fileend = 0;

        //Cut string in filename and filetype
        fileend = filename.indexOf(".");
        filetype = filename.substring(fileend);
        filename = filename.substring(0, fileend);

        //Check if filetype is correct  (= ".php")
        if (!filetype.equals(".php")){checkname = false; /*System.out.println("Wrong filetype: " + filename + filetype);*/ return checkname;}

        //Check if filename is long enough for YmdHis_ Format
        if (filename.length() < 14) {checkname = false; /*System.out.println("Filename to short: " + filename + filetype);*/ return checkname;}

        //Check if first 14 character are numeric
        String filebegin = filename.substring(0,14);
        boolean numeric = filebegin.chars().allMatch(Character::isDigit);
        if (!numeric) {checkname = false; /*System.out.println("Filebegin is not numeric " + filename + filetype);*/ return checkname;}

        //Filename is in correct format
        checkname = true;

        return checkname;
    }

    public File editMigrationsFileNames (File file) throws IOException {
        Path oldfile = Paths.get(file.getPath());
        String oldfilename = file.getName();
        String newfilename = getNewFileName(oldfilename);

        //Rename file with new filename
        Path newpath = Files.move(oldfile, oldfile.resolveSibling(newfilename));
        File newfile = new File(String.valueOf(newpath));

        return newfile;
    }

    public String getNewFileName(String oldfilename){
        //Get filename without filetype
        int fileend = oldfilename.indexOf(".");
        oldfilename = oldfilename.substring(0,fileend);

        //Remove all numbers from oldfilename
        oldfilename = oldfilename.replaceAll("\\d", "");

        //Check if first character of remaining string is "_", otherwise add "_"
        if(!oldfilename.substring(0,1).equals("_")){oldfilename = "_" + oldfilename;}

        //Get leading ymdhis timestamp
        String leading = getTimestamp();

        //Build new filename
        String newfilename = leading + oldfilename + ".php";

        return newfilename;
    }

    public String getTimestamp (){
        if (countnames == 0){
            SimpleDateFormat formatteryear = new SimpleDateFormat("yyyy");
            SimpleDateFormat formattermonth = new SimpleDateFormat("MM");
            SimpleDateFormat formatterday = new SimpleDateFormat("dd");
            SimpleDateFormat formatterhour = new SimpleDateFormat("HH");
            SimpleDateFormat formatterminute = new SimpleDateFormat("mm");
            SimpleDateFormat formattersecond = new SimpleDateFormat("ss");

            Date dateyear = new Date(System.currentTimeMillis());
            Date datemonth = new Date(System.currentTimeMillis());
            Date dateday = new Date(System.currentTimeMillis());
            Date datehour = new Date(System.currentTimeMillis());
            Date dateminute = new Date(System.currentTimeMillis());
            Date datesecond = new Date(System.currentTimeMillis());

            year = formatteryear.format(dateyear);
            month = formattermonth.format(datemonth);
            day = formatterday.format(dateday);
            hour = formatterhour.format(datehour);
            minute = formatterminute.format(dateminute);
            second = formattersecond.format(datesecond);
        }

        //Get sure, that timestamp improves correctly
        if(second.equals("60")){
            second = "00";
            int mm = Integer.parseInt(minute);
            mm++;
            minute = String.valueOf(mm);
            if (minute.length() < 2){minute = "0" + minute;}}
        if (minute.equals("60")){
            minute = "00";
            int hh = Integer.parseInt(hour);
            hh++;
            hour = String.valueOf(hh);
            if (hour.length() < 2){hour = "0" + hour;}}
        if (hour.equals("24")){
            hour = "00";
            int dd = Integer.parseInt(day);
            dd++;
            day = String.valueOf(dd);
            if (day.length() < 2){day = "0" + day;}}
        if (day.equals("32")){
            day = "01";
            int MM = Integer.parseInt(month);
            MM++;
            month = String.valueOf(MM);
            if (month.length() < 2){month = "0" + month;}}
        if (month.equals("13")){
            month = "01";
            int yy = Integer.parseInt(year);
            yy++;
            year = String.valueOf(yy);}

        //Build leading timestamp together
        String leading = year + month + day + hour + minute + second;

        //Improve second to get unique timestamps
        int ss = Integer.parseInt(second);
        ss++; countnames++;
        second = String.valueOf(ss);
        if (second.length() < 2){second = "0" + second;}

        return leading;
    }

    //Variables to get timestamp
    public String year = "";
    public String month = "";
    public String day = "";
    public String hour = "";
    public String minute = "";
    public String second = "";

    //|-------->  |METHODS TO EDIT CONTENT OF MIGRATIONS FILES|  <-------|
    public void editFile(File tar) throws IOException {

        if (filesedit_count != 0){files_upgrade_log = files_upgrade_log + "-------------------------------------------------------------------------------\n\n";}

        editFilesHeader(tar);
        editFilesMethods(tar);

        outputFiles(tar);
        files_upgrade_log = files_upgrade_log + buildUpgradeLogString(tar.getName());
        filesedit_count++;
    }

    public void editFilesHeader(File tar) throws IOException {
        String line = null;
        String content = "";
        String newcontent = "";
        int phptag = 0;
        int countline = 1;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null){               //Read target file line by line

            //Remove line "definded basepath"
            if(line.contains("defined('BASEPATH') OR exit('No direct script access allowed');")){
                newcontent = line.replace("defined('BASEPATH') OR exit('No direct script access allowed');", "");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + "defined('BASEPATH') OR exit('No direct script access allowed');" + "   |removed|" + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "\n   - [Line " + countline + "]: " + "defined('BASEPATH') OR exit('No direct script access allowed');   |deleted|";
                edit_numb++;
            }
            //Add namespace and use statement
            if (line.contains("<?php") && phptag == 0){
                newcontent = "<?php \n" + "namespace App\\Database\\Migrations; \n" + "use CodeIgniter\\Database\\Migration; ";
                line = newcontent;
                phptag++;

                //Output
                editfiles = editfiles + "\t" + "namespace App\\Database\\Migrations;   |added| \n" + "\t" + "use CodeIgniter\\Database\\Migration;   |added| \n";
                countfiles++;countfiles++;
                //UpgradeLog

                edit_upgrade_log = edit_upgrade_log + "\n   - [Line " + (countline+1) + "]: " + "namespace App\\Database\\Migrations; use CodeIgniter\\Database\\Migration;   |added|";
                edit_numb++;}
            //Edit class name (CamelCase and remove "Migration_")
            if (line.contains("class") && line.contains("extends")){

                int index = line.indexOf("extends");
                String classname = line.substring(0,index).replaceAll("class", "").replaceAll(" ", "");
                String newclassname = classname.replaceAll("Migration_", "").replaceAll("_", "");

                if (!classname.equals(newclassname)){
                    newcontent = line.replace(classname, newclassname);
                    line = newcontent;

                    //Output
                    editfiles = editfiles + "\t" + newclassname + "\n";
                    countfiles++;
                    //UpgradeLog
                    edit_upgrade_log = edit_upgrade_log + "\n   - [Line " + countline + "]: " + line;
                    edit_numb++;
                }
            }
            //Replace "CI_Migration" with "Migration" (new CI4 syntax)
            if (line.contains("extends CI_Migration")){
                newcontent = line.replace("extends CI_Migration", "extends Migration");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + "extends Migration" + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "\n   - [Line " + countline + "]: " + line;
                edit_numb++;}

            content = content + line + "\n";
            countline++;
        }
        frtar.close();
        brtar.close();

        FileWriter fwtar = new FileWriter(tar);
        fwtar.write(content);
        fwtar.close();
    }

    public void editFilesMethods(File tar) throws IOException {
        String line = null;
        String content = "";
        String newcontent = "";
        String oldcontent = "";
        int countline = 1;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null){               //Read target file line by line
            oldcontent = line;

            //Replace all methods with CI4 syntax (camelCase); Example: add_Field -> addField
            if(line.contains("add_field")){newcontent = line.replace("add_field", "addField"); line = newcontent;}
            if(line.contains("add_key")){newcontent = line.replace("add_key", "addKey"); line = newcontent;}
            if(line.contains("create_table")){newcontent = line.replace("create_table", "createTable"); line = newcontent;}
            if(line.contains("drop_table")){newcontent = line.replace("drop_table", "dropTable"); line = newcontent;}
            if(line.contains("add_column")){newcontent = line.replace("add_column", "addColumn"); line = newcontent;}
            if(line.contains("create_database")){newcontent = line.replace("create_database", "createDatabase"); line = newcontent;}
            if(line.contains("drop_column")){newcontent = line.replace("drop_column", "dropColumn"); line = newcontent;}
            if(line.contains("drop_database")){newcontent = line.replace("drop_database", "dropDatabase"); line = newcontent;}
            if(line.contains("modify_column")){newcontent = line.replace("modify_column", "modifyColumn"); line = newcontent;}
            if(line.contains("rename_table")){newcontent = line.replace("rename_table", "renameTable"); line = newcontent;}

            //Replace all CI3-syntax "dbforge" methods to CI4-syntax "forge"
            if(line.contains("dbforge")){newcontent = line.replace("dbforge", "forge"); line = newcontent;}

            //Output & UpgradeLog
            if (!oldcontent.equals(line)){
                countfiles++;
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                edit_upgrade_log = edit_upgrade_log + "\n   - [Line " + countline + "]: " + line.stripLeading();
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

    //|-------->  |VARIABLES AND METHODS FOR OUTPUT|  <-------|
    public int countconfig = 0;
    public String editconfig = "";
    public int countnames = 0;
    public String editfilenames = "";
    public int countfiles = 0;
    public String editfiles = "";

    public void outputConfig (File tar){
        if (countconfig == 0){System.out.println("No Lines edited in app/Config/" + tar.getName());}
        else if (countconfig == 1){System.out.println(countconfig + " Line edited in app/Config/" + tar.getName() + ": ");}
        else {System.out.println(countconfig + " Lines edited in app/Config/" + tar.getName() + ": ");}

        System.out.println(editconfig);
    }

    public void outputFilenames (){
        if (countnames == 0){System.out.println("No Migration-Filenames found/edited");}
        else if (countnames == 1){System.out.println(countnames + " Migration-Filename edited: ");}
        else {System.out.println(countnames + " Migration-Filenames edited: ");}

        System.out.println(editfilenames);
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
    protected static String config_upgrade_log = "";
    protected static String filenames_upgrade_log = "";
    protected static String files_upgrade_log = "";
    protected static Integer filesedit_count = 0;
    protected static Integer filenames_count = 0;
    protected static Integer edit_numb = 0;
    protected static Integer migrations_numb = 0;

    public String getUpgradeLog(Path path_ci3, Path path_ci4) throws IOException{
        upgradelog = "################################  |" + "MIGRATIONS" + "| ################################\n";

        upgradelog = upgradelog + "\n|--------------------| Migrations Config |--------------------|\n";

        upgradelog = upgradelog + "[CI3] '" + path_ci3 + "\\application\\config\\migration.php'\n";
        upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Config\\Migrations.php'\n\n";

        upgradelog = upgradelog + config_upgrade_log;

        upgradelog = upgradelog + "-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + "|--------------------| Migrations Files |--------------------|\n";

        upgradelog = upgradelog + "[CI3] '" + path_ci3 + "\\application\\migrations'\n";
        upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Database\\Migrations'\n\n";

        if (migrations_numb == 0){
            upgradelog = upgradelog + "No Migrations files found\n\n";
        }else{
            upgradelog = upgradelog + "#####  | 1. Filenames |  #####\n(In CI4, Migrations files must be in timestamp format 'YmdHis_')\n\n";

            if (filenames_count == 0){
                upgradelog = upgradelog + "Migrations files renamed: 0\n   - All migrations files are already in timestamp format\n";
            }else {upgradelog = upgradelog + filenames_count + " Migrations files renamed: \n" + filenames_upgrade_log;}

            upgradelog = upgradelog + "\n-------------------------------------------------------------------------------\n";

            upgradelog = upgradelog + "\n#####  | 2. Files content |  #####\n\n";

            upgradelog = upgradelog + files_upgrade_log;
        }
        return upgradelog;
    }

    public String buildUpgradeLogString (String title){
        String log = "| "  + title + " (CI4) |\n";

        log = log + "Lines edited: " + edit_numb;

        log = log + edit_upgrade_log + "\n\n";

        edit_numb = 0;
        edit_upgrade_log = "";

        return log;
    }

}
