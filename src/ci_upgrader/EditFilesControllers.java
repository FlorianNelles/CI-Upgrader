package ci_upgrader;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class EditFilesControllers {

    //|-------->  |RUN METHOD|  <-------|
    public void run(Path path_ci3, Path path_ci4) throws IOException {
        System.out.println("\n" + "###  |Controllers|  ###");

        File tar = new File (path_ci4 + "\\app\\Controllers");

//----- ###  |Edit Controllers Subfoldernames|  ###

        editSubfolderNames(tar);
        outputFoldernames(tar);

//----- ###  |Edit BaseController File|  ###
        File autolaod = new File(path_ci3 + "\\application\\config\\autoload.php");
        File basecontroller = new File(path_ci4 + "\\app\\Controllers\\BaseController.php");
        autolaod = removeComments(autolaod);

        editBaseController(autolaod, basecontroller);

        //Delete the helper-file -nocomments.php, which was created by method removeCommentes()
        if (autolaod != null){
            autolaod.delete();
        }

//----- ###  |Edit Controllers Files|  ###
        ArrayList<File> controllerslist = new ArrayList<>();
        getControllersList(tar, controllerslist);

        for (int i = 0; i < controllerslist.size(); i++){
            editFiles(controllerslist.get(i));
        }
    }

    //|-------->  |METHOD TO RENAME CONTROLLERS SUBFOLDERS (JUST FIRST LETTER UPPERCASE)|  <-------|
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

    //|-------->  |METHOD TO GET ALL CONTROLLERS FILES|  <-------|
    public void getControllersList (File tar, ArrayList<File> controllerslist){

        for (File file : tar.listFiles()){

            if (file.isDirectory()){
                getControllersList(file, controllerslist);}

            if (file.isFile()){
                if (file.getName().matches(".*\\.php")) {            //Check if file is a php file
                    if (!file.getName().equals("BaseController.php")) {         //Check if file is not ci4 basecontroller (basecontoller is edited separately)
                            controllerslist.add(file);
                    }
                }
            }
        }
    }

    //|-------->  |METHODS TO EDIT CONTENT OF BASECONTROLLER  <-------|
    public void editBaseController (File src, File tar) throws IOException {

        if (src != null){

        //Autolaod helper
        String helper = searchAutolaod(src, "$autoload['helper']");
        helper = getCleanValue(helper);
        if (!helper.equals("")){
            addHelper(tar, helper);
        }

        //Autolaod libraries
        String libraries = searchAutolaod(src, "$autoload['libraries']");
        libraries = getCleanValue(libraries);
        addLibraries(tar, libraries);

        //Autolaod model
        String model = searchAutolaod(src, "$autoload['model']");
        model = getCleanValue(model);
        if (!model.equals("")){
            addModels (tar, model);
        }

        outputFiles(tar);
        edit_basecontroller_log = edit_basecontroller_log + buildUpgradeLogString(tar.getName());
        }
    }

    public String searchAutolaod (File src, String srctag) throws IOException {
        String value = null;
        String srcline = null;

        FileReader frsrc = new FileReader(src);
        BufferedReader brsrc = new BufferedReader(frsrc);

        while ((srcline = brsrc.readLine()) != null) {               //Reade source file line by line
            if (srcline.contains(srctag)) {
                value = srcline.replace(srctag, "");     //Remove source-tag
                value = value.stripLeading();                       //Remove leading whitespace

                break;                              //Dont have to run through the whole file
            }
        }
        frsrc.close();
        brsrc.close();

        return value;
    }

    public String getCleanValue (String value){
        String cleanvalue = "";

        cleanvalue = value.replaceAll("=", "");
        cleanvalue = cleanvalue.replaceAll("array", "");
        cleanvalue = cleanvalue.replace("(", "");
        cleanvalue = cleanvalue.replace(")", "");
        cleanvalue = cleanvalue.replace(";", "");
        cleanvalue = cleanvalue.replace(" ", "");

        return cleanvalue;
    }

    public void addHelper (File tar, String value) throws IOException {
        String line = null;
        String content = "";
        String newcontent = "";
        int countline = 1;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null) {               //Read target file line by line

            //Add helpers in protected $helpers array
            if (line.contains("protected $helpers")){
                newcontent = "\t" + "protected $helpers = [" + value + "];";
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + "protected $helpers = [" + value + "];" + "\n";
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

        FileWriter fwtar = new FileWriter(tar);
        fwtar.write(content);
        fwtar.close();
    }

    public void addLibraries (File tar, String value) throws IOException {
        String line = null;
        String content = "";
        String newcontent = "";
        int countline = 1;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null) {               //Read target file line by line

            //Add protected $libraries array with all libraries
            if (line.contains("protected $request")){
                newcontent = "\t" + "protected $request; \n\n" + "\t" + "protected $libraries = [" + value + "];\n";
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + "protected $libraries = [" + value + "]; \n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline+2) + "]: " + "protected $libraries = [" + value + "];    |added| " + "\n";
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

    public void addModels (File tar, String value) throws IOException {
        String [] models = value.split(",");
        String addmodels = "";
        String addnamespaces = "";

        //Get lines to add in initController
        for (int i = 0; i < models.length; i++){
            String modelname = models[i].replaceAll("'", "");

            if (modelname.length() > 2){
            String newmodel = modelname.substring(0,1).toUpperCase() + modelname.substring(1).toLowerCase();
            addmodels = addmodels + "\t\t" + "$this->" + modelname + " = new " + newmodel + "(); \n";
            }
        }

        //Get line to add as namespace in BaseController
        for (int i = 0; i < models.length; i++){
            String modelname = models[i].replaceAll("'", "");

            if (modelname.length() > 2){
            modelname = modelname.substring(0,1).toUpperCase() + modelname.substring(1).toLowerCase();
            String namespace = "use App\\Models\\" + modelname + ";";
            addnamespaces = addnamespaces + namespace + "\n";
            }
        }

        int countaddmodels = (int) addmodels.lines().count();
        int countaddnamespaces = (int) addnamespaces.lines().count();

        //Add models and namespaces to BaseController with FileReader/Writer
        String line = null;
        String content = "";
        String newcontent = "";
        int countline = 1;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null) {               //Read target file line by line

            //Add use statement
            if (line.contains("namespace App\\Controllers;")){
                newcontent = "namespace App\\Controllers;" + "\n\n" + addnamespaces;
                line = newcontent;

                //Output
                countfiles = countfiles + countaddnamespaces;
                editfiles = editfiles + "\t" + addnamespaces;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + countline + "]: \n" + addnamespaces;
                edit_numb++;
            }
            //Add models lines in initController
            if (line.contains("// E.g.: $this->session = \\Config\\Services::session();")){
                newcontent = "\t// E.g.: $this->session = \\Config\\Services::session();" + "\n\n" + addmodels;
                line = newcontent;

                //Output
                countfiles = countfiles + countaddmodels;
                editfiles = editfiles + addmodels;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + countline + "]: \n" + addmodels;
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

    //|-------->  |METHODS TO EDIT CONTENT OF CONTROLLERS FILES|  <-------|
    public void editFiles (File tar)throws IOException{

        if (filesedit_count != 0){controllers_log = controllers_log + "-------------------------------------------------------------------------------\n\n";}

        editFilesHeader(tar);
        editFilesContent(tar);

        outputFiles(tar);
        controllers_log = controllers_log + buildUpgradeLogString(tar.getName());
        filesedit_count++;
    }

    private void editFilesHeader(File tar) throws IOException {
        String line = null;
        String content = "";
        String newcontent = "";
        int phptag = 0;
        int countline = 1;
        String namespace = getNamespace(tar);       //Get correct namespace, important when you use subfolders

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null) {               //Read target file line by line

            //Add namespace and use statement
            if (line.contains("<?php") && phptag == 0){
                newcontent = "<?php \n" + namespace + " \n" + "use Kenjis\\CI3Compatible\\Core\\CI_Controller; \n";
                line = newcontent;
                phptag++;

                //Output
                editfiles = editfiles + "\t" + namespace + "   |added| \n" + "\t" + "use Kenjis\\CI3Compatible\\Core\\CI_Controller;   |added| \n";
                countfiles++;countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline + 1) + "]: " + namespace + " use Kenjis\\CI3Compatible\\Core\\CI_Controller;   |added|\n";
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

        index = path.indexOf("app\\Controllers");
        path = path.substring(index);
        path = path.substring(0,1).toUpperCase() + path.substring(1);

        namespace = "namespace " + path + ";";
        return namespace;
    }

    private void editFilesContent(File tar) throws IOException {
        String line = null;
        String content = "";
        String newcontent = "";
        int countline = 1;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null) {               //Read target file line by line

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

//----------Edit Url Helper
            if (line.contains("url_title(")){
                newcontent = line.replace("'underscore'", "_");
                newcontent = newcontent.replace("'dash'" , "-");

                //Check if line was edited
                if (!line.equals(newcontent)){
                    line = newcontent;

                    //Output
                    editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                    countfiles++;
                    //UpgradeLog
                    edit_upgrade_log = edit_upgrade_log + "   - [Line " + countline + "]: " + line.stripLeading() + "\n";
                    edit_numb++;}
            }

            if (line.contains("base_url()")){
                newcontent = line.replace("base_url()" , "base_url(),'/'");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + countline + "]: " + line.stripLeading() + "\n";
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
                String edit = "return redirect()->to" + uri;

                line = begin + edit + end;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + countline + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }

//----------Remove load lang line
            if (line.contains("$this->lang->load(")){
                String oldline = line.stripLeading();
                line = "";

                //Output
                editfiles = editfiles + "\t" + oldline + "   |removed|\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + countline + "]: " + oldline.stripLeading() + "   |deleted| \n";
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
//----------Edit HTML-Table lines
            if(line.contains("$this->load->library('table')")){
                newcontent = line.replace("$this->load->library('table')", "$table = new \\CodeIgniter\\View\\Table()");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
            if(line.contains("$this->table->")){
                newcontent = line.replace("$this->table->", "$table->");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
            if(line.contains("table->set_heading(")){
                newcontent = line.replace("table->set_heading(", "table->setHeading(");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
            if(line.contains("table->add_row(")){
                newcontent = line.replace("table->add_row(", "table->addRow(");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
            if(line.contains("table->set_template(")){
                newcontent = line.replace("table->set_template(", "table->setTemplate(");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
            if(line.contains("table->set_caption(")){
                newcontent = line.replace("table->set_caption(", "table->setCaption(");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
            if(line.contains("table->make_columns(")){
                newcontent = line.replace("table->make_columns(", "table->makeColumns(");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
            if(line.contains("table->set_empty(")){
                newcontent = line.replace("table->set_empty(", "table->setEmpty(");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
//----------Edit Encryption lines
            if(line.contains("$this->load->library('encryption')")){
                newcontent = line.replace("$this->load->library('encryption')", "$encrypter = \\Config\\Services::encrypter()");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
            if(line.contains("$this->encryption->")){
                newcontent = line.replace("$this->encryption->", "$encrypter->");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
                edit_numb++;
            }
            if(line.contains("$encrypter->create_key(")){
                newcontent = line.replace("$encrypter->create_key(", "$encrypter->createKey(");
                line = newcontent;

                //Output
                editfiles = editfiles + "\t" + line.stripLeading() + "\n";
                countfiles++;
                //UpgradeLog
                edit_upgrade_log = edit_upgrade_log + "   - [Line " + (countline) + "]: " + line.stripLeading() + "\n";
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
    public int countfoldernames = 0;
    public String editfoldernames = "";
    public int countfiles = 0;
    public String editfiles = "";

    public void outputFoldernames (File tar){
        if (countfoldernames == 0){System.out.println("No subfolders found/edited in app/Controllers/");}
        else if (countfoldernames == 1){System.out.println(countfoldernames + " subfoldername edited in app/Controllers/ : ");}
        else {System.out.println(countfoldernames + " subfoldernames edited in app/Controllers/ : ");}

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
    protected static Integer edit_numb = 0;
    protected static String edit_subfolder_log = "";
    protected static String edit_basecontroller_log = "";
    protected static String controllers_log = "";
    protected static Integer subfolder_numb = 0;
    protected static Integer files_numb = 0;
    protected static Integer filesedit_count = 0;


    public String getUpgradeLog(Path path_ci3, Path path_ci4) throws IOException{
        upgradelog = "################################  |" + "CONTROLLERS" + "| ################################\n";

        upgradelog = upgradelog + "[CI3] '" + path_ci3 + "\\application\\controllers'\n";
        upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Controllers'\n\n";

        if (files_numb == 0){
            upgradelog = upgradelog + "No Controllers files found\n\n";
        }else {
            upgradelog = upgradelog + "#####  | 1. Subfolders |  #####\n(In CI4, all names of Models subfolders has to be first letter uppercase)\n\n";

            if (subfolder_numb == 0){
                upgradelog = upgradelog + "Subfolder names edited: 0\n   - All subfoler names are already correct or no subfolders found\n";
            }else {upgradelog = upgradelog + "Subfolder names edited: " + subfolder_numb + "\n" + edit_subfolder_log;}

            upgradelog = upgradelog + "\n-------------------------------------------------------------------------------\n";

            upgradelog = upgradelog + "\n#####  | 2. Files Content |  #####\n\n";

            upgradelog = upgradelog + "|--------------------| BaseController |--------------------|\n";

            upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Controllers\\BaseController.php'\n\n";

            upgradelog = upgradelog + edit_basecontroller_log;

            upgradelog = upgradelog + "|--------------------| Other Controllers Files |--------------------|\n";

            upgradelog = upgradelog + "[CI3] '" + path_ci3 + "\\application\\controllers'\n";
            upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Controllers'\n\n";

            upgradelog = upgradelog + controllers_log;

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
