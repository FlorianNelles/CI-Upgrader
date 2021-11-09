package ci_upgrader;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class EditFilesRoutes {

    //|-------->  |RUN METHOD|  <-------|
    public void run (Path path_ci3, Path path_ci4) throws IOException {
        System.out.println("\n" + "###  |Routes|  ###");

        File src = new File (path_ci3 + "\\application\\config\\routes.php");
        File tar = new File (path_ci4 + "\\app\\Config\\Routes.php");
        src = removeComments(src);

//----- ###  |Search and Replace Routes Settings in Routes.php|  ###

        defaultRouteSettings(src, tar, "$route['default_controller']");
        routeSettings(src, tar, "$route['404_override']", "$routes->set404Override");
        routeSettings(src, tar, "$route['translate_uri_dashes']", "$routes->setTranslateURIDashes");

        edit_output(tar);

//----- ###  |Get and edit Routes from CI3 and add them in CI4 Routes.php|  ###

        ArrayList<String> routes_ci3 = getRoutes(src, "$route");
        ArrayList<String> routes_ci4 = editRoutes(routes_ci3);
        addRoutes(tar, routes_ci4);

        add_output(tar);

        //Delete the helper-file -nocomments.php, which was created by method removeCommentes()
        if (src != null) {
            src.delete();
        }

//----- ###  |Get Strings for UpgradeLog|  ###

        routes_log = buildUpgradeLogString();

    }

    //|-------->  |METHODS TO SEARCH AND REPLACE ROUTES SETTINGS|  <-------|
    private void defaultRouteSettings(File src, File tar, String srctag) throws IOException {
        if (src != null) {
            String setting_value = searchSettings(src, srctag);
            if (setting_value != null) {
                setting_value = removeSymbWhite(setting_value);
            } else return;


            String defcontroller = "";
            String defmethod = "";

            //Check if default_controller and default_method exists (example: $route['default_controller'] = 'home/index'; )
            if (setting_value.contains("/")) {
                String[] parts = setting_value.split("/");

                defcontroller = parts[0];
                defmethod = parts[1];
            } else {
                defcontroller = setting_value;
            }

            //Build new CI4 routes with setting from CI3 and transfer them to CI4 Routes.php
            replaceSettings(tar, "$routes->setDefaultController", "$routes->setDefaultController('" + defcontroller + "');");
            replaceSettings(tar, "$routes->setDefaultMethod", "$routes->setDefaultMethod('" + defmethod + "');");
            replaceSettings(tar, "$routes->get", "$routes->get('/', '" + defcontroller + "::" + defmethod + "');");
        }
    }

    public void routeSettings (File src, File tar, String srctag, String tartag) throws IOException {
        if (src != null) {
            String config_value = searchSettings(src, srctag);
            if (config_value != null){
                config_value = removeSymbWhite(config_value);
            }else return;

            //Build new CI4 routes with setting from CI3 and transfer them to CI4 Routes.php
            String value = tartag + "(" + config_value + ");";
            replaceSettings(tar, tartag, value);
        }
    }

    public String searchSettings (File src, String srctag) throws IOException {
        String value = null;
        String srcline = null;

        FileReader frsrc = new FileReader(src);
        BufferedReader brsrc = new BufferedReader(frsrc);

        while ((srcline = brsrc.readLine()) != null){               //Read source file line by line
            if(srcline.contains(srctag)){
                value = srcline.replace(srctag, "");    //Remove source-tag
                value = value.stripLeading();                       //Remove whitespace
                break;                                              //Don't have to run through the whole file
            }
        }
        frsrc.close();
        brsrc.close();

        return value;
    }

    public void replaceSettings(File tar, String tartag, String value) throws IOException {

        String line = null;
        String content = "";
        String newcontent = "";
        int countline = 1;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((line = brtar.readLine()) != null){               //Read target file line by line
            if(line.contains(tartag)){

                newcontent = value;

                //Check if default value form CI4 is equals to CI3 value (if its same don´t edit line)
                if(!line.toLowerCase().strip().replaceAll(" ", "").equals(newcontent.toLowerCase().strip().replaceAll(" ", ""))){
                    line = newcontent;

                    //Output
                    output = output + "\t" + line + "\n";
                    count ++;
                    //UpgradeLog
                    edit_upgrade_log = edit_upgrade_log + "\n   - [Line " + countline + "]: " + line.stripLeading();
                    edit_numb++;
                }
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

    //|-------->  |METHODS TO GET CI3 ROUTES, EDIT AND ADD THEM TO CI4 ROUTES|  <-------|
    public ArrayList<String> getRoutes(File src, String srctag) throws IOException {
        ArrayList<String> routes = new ArrayList<String>();
        String srcline = null;

        if (src != null) {
            FileReader frsrc = new FileReader(src);
            BufferedReader brsrc = new BufferedReader(frsrc);

            while ((srcline = brsrc.readLine()) != null) {               //Read source file line by line
                if (srcline.contains(srctag)) {

                    //Check if this line contains NO route settings and no route which is commented out
                    if (!srcline.contains("$route['default_controller']") && !srcline.contains("$route['404_override']") && !srcline.contains("$route['translate_uri_dashes']") && !srcline.contains("//route"))

                        routes.add(srcline);
                }
            }
            frsrc.close();
            brsrc.close();
        }
        return routes;
    }

    public ArrayList<String> editRoutes (ArrayList<String> routes_ci3){

        ArrayList<String> routes_ci4 = new ArrayList<String>();

        for (int i = 0; i < routes_ci3.size(); i++){
            String line = routes_ci3.get(i);

            //Split line in two parts
            String [] routessplited = line.split("=");

            //Edit first part of route
            String tag = routessplited [0];
            tag = tag.substring(tag.indexOf("[") + 1, tag.indexOf("]"));

            //Edit second part of route
            String value = routessplited [1];
            value = value.stripLeading();
            value = value.replace("\\s", "");
            value = value.substring(0, value.indexOf(";"));
            value = value.replaceFirst("/", "::");

            //Put both parts together and create CI4 route
            String route = "$routes->add(" + tag + ", " + value + ");";
            routes_ci4.add(route);
        }
        return routes_ci4;
    }

    public void addRoutes(File tar, ArrayList<String> routes_ci4) throws IOException {
        String line = null;
        String content = "";

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        //Get already existing content of routes.php
        while ((line = brtar.readLine()) != null){
            content = content + line + "\n";
        }
        frtar.close();
        brtar.close();

        //Add CI4 routes after already existing content
        content = content + "\n";
        for (int i = 0; i < routes_ci4.size(); i++){
            content = content + routes_ci4.get(i) + "\n";

            //Output
            output = output + "\t" + routes_ci4.get(i) + "\n";
            count ++;
            //UpgradeLog
            add_upgrade_log = add_upgrade_log + "\n   - " + routes_ci4.get(i);
            add_numb++;
        }

        //Write new Routes.php file
        FileWriter fwtar = new FileWriter(tar);
        fwtar.write(content);
        fwtar.close();
    }

    //|-------->  |METHOD TO REMOVE COMMENTS FROM CI3 FILE|  <-------|
    public File removeComments(File src_config) throws IOException {
//----- Before search method runs over file, it's important to remove all comments
//      Otherwise it can happen, that routes and settings which are commented out are detected and transferred
//----- To avoid this, create a helper-file with no comments, search the routes and settings, transfer them to CI4 project and delete the helper file after that

        File src_no_comments = new File(src_config.getParent() + "\\nocomments.php");
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

    //|-------->  |METHOD TO REMOVE SYMBOLS AND WHITESPACES|  <-------|
    public String removeSymbWhite (String value){
        String clean = "";

        clean = value.replace("=", "");
        clean = clean.replace("'", "");
        clean = clean.replace(";", "");
        clean = clean.replaceAll("\\s", "");

        return clean;
    }

    //|-------->  |VARIABLES AND METHODS FOR OUTPUT|  <-------|
    protected static String output = "";
    protected static Integer count = 0;

    public void edit_output(File tar_filename) {
        if (count == 0) {System.out.println("No Lines edited in app/Config/" + tar_filename.getName());
        }else if (count == 1) {System.out.println("1 Line edited in app/Config/" + tar_filename.getName() + ":");
        } else System.out.println(count + " Lines edited in app/Config/" + tar_filename.getName() + ":");

        System.out.println(output);

        count = 0;
        output = "";
    }

    public void add_output(File tar_filename) {
        if (count == 0) {System.out.println("No Routes found in CI3 application/config/routes.php");
        }else if (count == 1) {System.out.println("1 Route added to app/Config/" + tar_filename.getName() + ":");
        } else System.out.println(count + " Routes added to app/Config/" + tar_filename.getName() + ":");

        System.out.println(output);

        count = 0;
        output = "";
    }

    //|-------->  |METHOD AND VARIABLES TO CREATE STRING FOR UPGRADE LOG|  <-------|
    protected static String upgradelog = "";
    protected static String edit_upgrade_log = "";
    protected static String add_upgrade_log = "";
    protected static String routes_log = "";
    protected static Integer edit_numb = 0;
    protected static Integer add_numb = 0;

    public String getUpgradeLog(Path path_ci3, Path path_ci4) throws IOException{
        upgradelog = "################################  |" + "ROUTES" + "|  ################################\n";

        upgradelog = upgradelog + "[CI3] '" + path_ci3 + "\\application\\config\\routes.php'\n";
        upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Config\\Routes.php'\n\n";

        upgradelog = upgradelog + routes_log;

        return upgradelog;
    }

    public String buildUpgradeLogString (){
        String log = "Lines edited: " + edit_numb;

        log = log + edit_upgrade_log + "\n\n";

        log = log + "Lines added: " + add_numb;

        log = log + add_upgrade_log;

        edit_numb = 0;
        add_numb = 0;
        add_upgrade_log = "";
        edit_upgrade_log = "";

        return log;
    }

}