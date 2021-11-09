package ci_upgrader;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class EditFilesViews {

    //|-------->  |RUN METHOD|  <-------|
    public void run(Path path_ci3, Path path_ci4) throws IOException {
        System.out.println("\n" + "###  |Views|  ###");

//----- ###  |Edit Config/View.php|  ###
        File tar = new File (path_ci4 + "\\app\\Config\\View.php");
        editConfigView(tar);

        outputFiles(tar);
        config_upgrade_log = buildUpgradeLogString(tar.getName());

//----- ###  |Edit View Files|  ###
        File views = new File (path_ci4 + "\\app\\Views");

        ArrayList<File> viewslist = new ArrayList<>();
        getViewsList(views, viewslist);

        for (int i = 0; i < viewslist.size(); i++){
            editFiles(viewslist.get(i));
            outputFiles(viewslist.get(i));
            if (filesedit_count != 0){files_upgrade_log = files_upgrade_log + "-------------------------------------------------------------------------------\n\n";}
            files_upgrade_log = files_upgrade_log + buildUpgradeLogString(viewslist.get(i).getName());
            filesedit_count++;
        }

    }

    //|-------->  |METHOD TO EDIT CONFIG/VIEW.PHP|  <-------|
    private void editConfigView(File tar) throws IOException {
        String line = null;
        String content = "";
        String newcontent = "";
        int countline = 1;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null) {               //Read target file line by line

            //Add namespace and use statement
            if (line.contains("use CodeIgniter\\Config\\View as BaseView;")){
                newcontent = "use CodeIgniter\\Config\\View as BaseView;" + "\n" + "use Kenjis\\CI3Compatible\\Traits\\View\\ThisConfigInView;\n";
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + "use Kenjis\\CI3Compatible\\Traits\\View\\ThisConfigInView;   |added| \n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline + 1) + "]: use Kenjis\\CI3Compatible\\Traits\\View\\ThisConfigInView;   |added|\n";
                edit_numb++;
            }

            if (line.contains("class View extends BaseView")){
                newcontent = "class View extends BaseView \n{ \n" + "use ThisConfigInView;\n";
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + "use ThisConfigInView;   |added| \n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline + 2) + "]: use ThisConfigInView;   |added|\n";
                edit_numb++;
            }

            if (line.equals("{")){line = "";}

            content = content + line + "\n";
            countline++;
        }
        frtar.close();
        brtar.close();

        FileWriter fwtar = new FileWriter(tar);
        fwtar.write(content);
        fwtar.close();
    }

    //|-------->  |METHOD TO GET ALL VIEW FILES|  <-------|
    public void getViewsList (File tar, ArrayList<File> viewslist){

        for (File file : tar.listFiles()){

            if (file.isDirectory()){
                getViewsList(file, viewslist);}

            if (file.isFile()){
                if (file.getName().matches(".*\\.php")) {            //Check if file is a php file
                        viewslist.add(file);

                        files_numb++;
                }
            }
        }
    }

    //|-------->  |METHOD TO EDIT CONTENT OF VIEW FILES|  <-------|
    private void editFiles(File views) throws IOException {

        String line = null;
        String content = "";
        String newcontent = "";
        int countline = 1;

        FileReader frtar = new FileReader(views);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null) {               //Read target file line by line

//----------Edit Url Helper
            if (line.contains("url_title(")){
                newcontent = line.replace("'underscore'", "_");
                newcontent = newcontent.replace("'dash'" , "-");

                //Check if line was edited
                if (!line.equals(newcontent)){
                    line = newcontent;

                    //Outputeditfiles = editfiles + "\t" + line.stripLeading() + "\n";
                    countfiles++;
                    //UpgradeLog
                    edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                    edit_numb++;
                }
            }

            if (line.contains("base_url()")){
                newcontent = line.replace("base_url()" , "base_url(),'/'");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }

            if (line.contains("redirect(")){
                int first = line.indexOf("redirect(");
                String begin = line.substring(0, first);

                String uri = line.substring(first + 8);
                uri = uri.stripLeading();

                int last = uri.indexOf(";");
                String end = uri.substring(last);

                uri = uri.substring(0, last);
                String edit = "return redirect()->to(site_url" + uri + ")";

                line = begin + edit + end;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }

//----------Remove line "definded basepath"
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

//----------Edit language line
            if(line.contains("$this->lang->line(")){
                newcontent = line.replace("$this->lang->line('", "lang('FILENAME.");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
//----------Edit session->userdata line
            if(line.contains("$this->session->userdata(")){

                String[] sessions = line.split("(?=\\$this->session->userdata)");
                newcontent = "";

                for (int i = 0 ; i < sessions.length; i++){
                    if (sessions[i].contains("$this->session->userdata(")){
                        sessions[i] = sessions[i].replace("$this->session->userdata(", "$_SESSION[").replaceFirst("\\)", "]");
                    }
                    newcontent = newcontent + sessions[i];
                }
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
//----------Edit session->has_userdata line
            if(line.contains("$this->session->has_userdata(")){

                String[] sessions = line.split("(?=\\$this->session->has_userdata)");
                newcontent = "";

                for (int i = 0 ; i < sessions.length; i++){
                    if (sessions[i].contains("$this->session->has_userdata(")){
                        sessions[i] = sessions[i].replace("$this->session->has_userdata(", "isset($_SESSION[").replaceFirst("\\)", "])");
                    }
                    newcontent = newcontent + sessions[i];
                }
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
//----------Edit load views
            if (line.contains("$this->load->view(")){
                newcontent = line.replace("$this->load->", "echo ");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + countline + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }

            content = content + line + "\n";
            countline++;
        }
        frtar.close();
        brtar.close();

        FileWriter fwtar = new FileWriter(views);
        fwtar.write(content);
        fwtar.close();
    }

    //|-------->  |VARIABLES AND METHODS FOR OUTPUT|  <-------|
    public int countfiles = 0;
    public String editfiles = "";

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
    protected static String files_upgrade_log = "";
    protected static Integer edit_numb = 0;
    protected static Integer filesedit_count = 0;
    protected static Integer files_numb = 0;


    public String getUpgradeLog(Path path_ci3, Path path_ci4) throws IOException{
        upgradelog = "################################  |" + "VIEWS" + "| ################################\n";

        upgradelog = upgradelog + "\n|--------------------| Views Config |--------------------|\n";

        upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Config\\View.php'\n\n";

        upgradelog = upgradelog + config_upgrade_log;

        upgradelog = upgradelog + "-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + "|--------------------| Views Files |--------------------|\n";

        upgradelog = upgradelog + "[CI3] '" + path_ci3 + "\\application\\views'\n";
        upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Views'\n\n";

        if (files_numb == 0){
            upgradelog = upgradelog + "No Views files found\n\n";
        }else{
        upgradelog = upgradelog + files_upgrade_log;
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
