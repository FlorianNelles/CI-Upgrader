package ci_upgrader;

import java.io.*;
import java.nio.file.Path;
import java.util.Locale;

public class Installation {

    //|-------->  |RUN METHOD|  <-------|
    public void run() throws IOException {
        System.out.println("|-------->  |START INSTALLATION|  <-------|" + "\n");

//----- Get all important things with |GETTER METHODS| for the installations like:
//      Path to ci3 project/parent directory, drive, name and path for the new ci4 project

        String dir_ci3 = getDirCi3();
        path_ci3 = getPathCi3(dir_ci3);
        Path path_ci3_parent = getPathCi3Parent(path_ci3);
        String dir_ci3_parent = getDirCi3Parent(path_ci3_parent);
        drive = getDrive(dir_ci3);
        String ci4_name = getCi4Name(dir_ci3_parent);
        String dir_ci4  = getDirCi4(dir_ci3_parent, ci4_name);
        path_ci4 = getPathCi4(dir_ci4);

//----- Call method |runCi4Installation| run CI4 composer installation with with kenjis ci3-to-4-upgrade-helper via cmd
        runCi4Installation(drive, dir_ci3_parent, ci4_name);

//----- Check if installation was successfully, otherwise stop program
        File ci4_project = new File(String.valueOf(path_ci4));
        if (ci4_project.list() == null){
            System.out.println("\n\n!!! Installation failed !!! \n" +
                                "To run installation, its required that Composer is installed on your system. \n" +
                                "Check if Composer is installed: \n" +
                                "\t 1. Open CMD\n" +
                                "\t 2. Enter 'composer' \n" +
                                "\t -> If the command 'composer' was not found, Composer is not installed on your system.\n" +
                                "\t    Open https://getcomposer.org/ and follow the instructions to install Composer.\n" +
                                "\t    When your done with Composer installation, start the CI-Upgrader again.");
            System.exit(0);
        }

        System.out.println("\n" + "|-------->  |INSTALLATION: COMPLETED|  <-------|" + "\n");
    }

    //|-------->  |GETTER METHODS|  <-------|

    public String getDirCi3() throws IOException {

        System.out.println("|Path to your CodeIgniter 3 project|");
        System.out.println("The Path has to be in this format: Drive\\Path\\To\\Your\\CI3_Project (Example: C:\\xampp\\htdocs\\projectname)");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Boolean input = false;
        String dir_ci3 = "";

        while (!input){

            System.out.print("\n" + "-->  Enter your path: ");
            dir_ci3 = br.readLine();

//--------- Check if input is correct (no illegal symbol, leads to a directory which contains a CI3 project)
//          To check if directory contains a CI3 project, check if application, system, config, controllers, models and views folder exists
            String subfolder = getSubfolders (dir_ci3);

            if (dir_ci3.contains("|") || dir_ci3.contains("<") || dir_ci3.contains(">") || dir_ci3.contains("?")){System.out.println("Input contains illegal symbol"); input= false;}
            else if (!dir_ci3.contains("\\")){System.out.println("Input is no path"); input= false;}
            else if (!subfolder.contains("application")){System.out.println("Input is no path to a CI3 project: No |application| folder"); input = false;}
            else if (!subfolder.contains("system")){System.out.println("Input is no path to a CI3 project: No |system| folder"); input = false;}
            else if (!subfolder.contains("config")){System.out.println("Input is no path to a CI3 project: No |config| folder"); input = false;}
            else if (!subfolder.contains("controllers")){System.out.println("Input is no path to a CI3 project: No |controllers| folder"); input = false;}
            else if (!subfolder.contains("models")){System.out.println("Input is no path to a CI3 project: No |models| folder"); input = false;}
            else if (!subfolder.contains("views")){System.out.println("Input is no path to a CI3 project: No |views| folder"); input = false;}
            else {input= true;}
        }
        return dir_ci3;
    }

    public String getSubfolders (String dir){
        String subfolders = "";

        //Get CI3 root folders
        File root = new File(dir);
        String[] rootfolders = root.list(new FilenameFilter() {
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();}
        });

        if (rootfolders == null)return subfolders;

        for (int i = 0; i <rootfolders.length; i++){
            subfolders = subfolders + rootfolders[i] + "\n";}

        //Get CI3 application folders
        File app = new File(dir + "\\application");
        String[] appfolders = app.list(new FilenameFilter() {
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();}
        });

        if (appfolders == null)return subfolders;

        for (int i = 0; i <appfolders.length; i++){
            subfolders = subfolders + appfolders[i] + "\n";}


        return subfolders;
    }

    public Path getPathCi3(String dir_ci3){
        Path path_ci3 = Path.of(dir_ci3);
        File oldfile = new File(String.valueOf(path_ci3));
        oldname = oldfile.getName();
        return path_ci3;
    }

    public Path getPathCi3Parent(Path path_ci3){
        Path path_ci3_parent = path_ci3.getParent();
        return path_ci3_parent;
    }

    public String getDirCi3Parent(Path path_ci3_parent){
        String dir_ci3_parent = path_ci3_parent.toString();
        return dir_ci3_parent;
    }

    public String getDrive(String dir_ci3){
        int index = dir_ci3.indexOf(":");
        String drive = dir_ci3.substring(0,index+1);
        return drive;
    }

    public String getCi4Name(String dir) throws IOException {

        System.out.println("\n" + "|Name your new CodeIgniter 4 project|");
        System.out.println("The name must not contain symbols like: < > : ? * | \\ / ");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Boolean input = false;
        String ci4_name = "";

        //Required to check if project name already exists
        File dirfiles = new File(dir);
        String [] filelist = dirfiles.list();

//----- Check if input is correct (no illegal symbol, no existing project with same name)

        while (!input){
            System.out.print("\n" + "-->  Enter the new name: ");

            ci4_name = br.readLine();

            //Check if there is already a project that exists with input name and if input name contains no illegal symbols; otherwise repeat input
            Boolean exists = false;
            for (int i = 0; i < filelist.length; i++){
                if (filelist[i].equals(ci4_name)){ exists = true; }
            }

            if (exists){System.out.println("There is already a project in this directory (" + dir + ") with the name '" + ci4_name + "'"); input = false;}
            else if (ci4_name.contains("<") || ci4_name.contains(">") || ci4_name.contains(":") || ci4_name.contains("?") || ci4_name.contains("*") || ci4_name.contains("|") || ci4_name.contains("/") || ci4_name.contains("\\")) {
                System.out.println("Input contains illegal symbol"); input = false;}
            else if (ci4_name.equals("") || ci4_name.replace(" ", "").equals("")){System.out.println("Input is empty"); input = false;}
            else {input = true;}
        }
        newname = ci4_name;
        return ci4_name;
    }

    public String getDirCi4(String dir_ci3_parent, String ci4_name){
        String dir_ci4 = dir_ci3_parent + "\\" + ci4_name;
        return dir_ci4;
    }

    public Path getPathCi4(String dir_ci4){
        Path path_ci4 = Path.of(dir_ci4);
        return path_ci4;
    }

    //|-------->  |PUBLIC VARIABLES TO COPY AND EDIT FILES|  <-------|
//  Other classes (CopyDirFiles, EditFiles, UpgradeLog) also need path and name of both projects, so they must be accessible globally
    public Path path_ci3;
    public Path path_ci4;
    public String drive;
    public String newname;
    public String oldname;

    //|-------->  |METHOD FOR CI4 INSTALLATION|  <-------|
    public void runCi4Installation(String drive, String dir_ci3_parent, String ci4_name) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd/ && " + drive + "&& cd " + dir_ci3_parent +                //Start cmd, move to correct drive and open dir above your CI3 project
                "&& composer create-project codeigniter4/appstarter "+ ci4_name + " --no-dev " +           //CI4 composer installation, with own name and no dev setting
                "&& cd " + ci4_name +" &&composer require kenjis/ci3-to-4-upgrade-helper:1.x-dev");        //Move to dir from new CI4 project and install kenjis upgrade helper
        builder.redirectErrorStream(true);
        Process p = builder.start();

        //Output
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            System.out.println(line);
        }
    }

    //|-------->  |METHOD TO CREATE STRING FOR UPGRADE LOG|  <-------|
    public String getUpgradeLog() throws IOException{
        String upgradelog = "################################################################################\n" +
                "                     |-----| UPGRADE: COMPLETED |-----|\n" +
                "################################################################################\n" +
                "This Upgrade Log sums up all adjustment that were accomplished by the CI-Upgrader. \n\n";

        upgradelog = upgradelog + "SECTIONS: \n1. INSTALLATION\n2. COPIED DIRECTORIES AND FILES\n3. EDITED FILES\n4. WHAT TO DO AFTER CI-UPGRADER";

        upgradelog = upgradelog + "\n\n################################################################################\n";

        upgradelog = upgradelog + "                       |-----| 1. INSTALLATION |-----|                       ";

        upgradelog = upgradelog + "\n################################################################################\n\n";

        upgradelog = upgradelog + "Your CodeIgniter 3 source project is '" + oldname + "' (Path: '" + path_ci3 + "')\n\n" +
                "New CodeIgniter 4 project '" + newname + "' was successfully installed (Path: '" + path_ci4 + "')\n\n" +
                "This installation includes the ci3-to-4-upgrade-helper from kenjis (For more informations: https://github.com/kenjis/ci3-to-4-upgrade-helper).";

        upgradelog = upgradelog + "\n\n################################################################################\n";

        return upgradelog;
    }

}
