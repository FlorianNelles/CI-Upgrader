package ci_upgrader;

import java.io.*;
import java.nio.file.Path;

public class CopyDirFiles {

    //|-------->  |RUN METHOD|  <-------|
    public void run(Path path_ci3, Path path_ci4) throws IOException {
        System.out.println("|-------->  |COPY DIRECTORIES AND FILES FROM CI3 TO CI4 PROJECT|  <-------|" + "\n");

//----- ###  |Copy Views|  ###

        System.out.println("###  |Views|  ###");
        File src_views = new File(path_ci3 + "\\application\\views");
        File tar_views = new File (path_ci4 + "\\app\\Views");
        copy(src_views, tar_views);

//----- ###  |Copy Models|  ###

        System.out.println("###  |Models|  ###");
        File src_models = new File(path_ci3 + "\\application\\models");
        File tar_models = new File (path_ci4 + "\\app\\Models");
        copy(src_models, tar_models);

//----- ###  |Copy Controllers|  ###

        System.out.println("###  |Controllers|  ###");
        File src_controllers = new File(path_ci3 + "\\application\\controllers");
        File tar_controllers = new File (path_ci4 + "\\app\\Controllers");
        copy(src_controllers, tar_controllers);

//----- ###  |Copy MY_Controller|  ###

        System.out.println("###  |MY_Controller|  ###");
        File src_mycontrollers = new File(path_ci3 + "\\application\\core");
        File tar_mycontrollers = new File (path_ci4 + "\\app\\Controllers");
        copy(src_mycontrollers, tar_mycontrollers);

//----- ###  |Copy Migrations|  ###

        System.out.println("###  |Migrations|  ###");
        File src_migrations = new File(path_ci3 + "\\application\\migrations");
        File tar_migrations = new File (path_ci4 + "\\app\\Database\\Migrations");
        copy(src_migrations, tar_migrations);

//----- ###  |Copy Languages|  ###

        System.out.println("###  |Languages|  ###");
        File src_languages = new File(path_ci3 + "\\application\\language");
        File tar_languages = new File (path_ci4 + "\\app\\Language");
        copy(src_languages, tar_languages);

//----- ###  |Copy Libraries|  ###

        System.out.println("###  |Libraries|  ###");
        File src_libraries = new File(path_ci3 + "\\application\\libraries");
        File tar_libraries = new File (path_ci4 + "\\app\\Libraries");
        copy(src_libraries, tar_libraries);

//----- ###  |Copy Helpers|  ###

        System.out.println("###  |Helpers|  ###");
        File src_helpers = new File(path_ci3 + "\\application\\helpers");
        File tar_helpers = new File (path_ci4 + "\\app\\Helpers");
        copy(src_helpers, tar_helpers);

//----- ###  |Assets Files|  ###

        System.out.println("###  |Assets Files|  ###");
        File src_assets = getAssetsFiles(path_ci3);

//----- Check if assets folder exits (most used name for folder which contains css, js, img)
//      If it has another name or is placed in an unusual folder, it can not be copied and has to be done by hand

        if (src_assets != null){
            src_assets = new File (src_assets + "\\assets");
            copyAssets(path_ci3, path_ci4, src_assets);
        }else {System.out.println("No Assets Folder found \n");
                copy_upgrade_log = copy_upgrade_log + "###  |ASSETS|  ### \n" +
                                                        "No Assets directory found in your CI3 root and application path\n\n";
        }

        System.out.println("|-------->  |COPY DIRECTORIES AND FILES: COMPLETED|  <-------|" + "\n");
    }

    //|-------->  |METHOD TO COPY FILES FROM CI3 TO CI4 WITH OUTPUT|  <-------|
    public void copy (File src, File tar) throws IOException {
        //Variables for output
        copy_output_dir = "";
        copy_output_file = "";
        copy_output_numb_dir = 0;
        copy_output_numb_file = 0;
        //Varibales for UpgradeLog
        upgrade_log_file = "";
        upgrade_log_dir = "";
        upgrade_log_numb_file = 0;
        upgrade_log_numb_dir = 0;

//----- Call Method to copyDirectory
        copyDirectory(src, tar);

        //Output
        if(copy_output_numb_dir > 1){System.out.println(copy_output_numb_dir + " Directories copied: \n" + copy_output_dir);
        }else if(copy_output_numb_dir == 1){System.out.println(copy_output_numb_dir + " Directory copied: \n" + copy_output_dir);
        }else System.out.println("No Directories found \n");

        if(copy_output_numb_file > 1){System.out.println(copy_output_numb_file + " Files copied: \n" + copy_output_file);
        }else if(copy_output_numb_file == 1){System.out.println(copy_output_numb_file + " File copied: \n" + copy_output_file);
        }else System.out.println("No Files found \n");

        //Upgrade Log
        copy_upgrade_log += "################################  |" + src.getName().toUpperCase() + "|  ################################\n";
        copy_upgrade_log += "[CI3] '" + src.getPath() + "'\n";
        copy_upgrade_log += "[CI4] '" + tar.getPath() + "'\n\n";

        if (upgrade_log_numb_dir == 0){copy_upgrade_log += "|Directories: 0 |\n" + "   - No Directories found in CI3-Path\n";}
        else {copy_upgrade_log += "|Directories: " + upgrade_log_numb_dir + " |\n";}
        copy_upgrade_log += upgrade_log_dir + "\n";

        if (upgrade_log_numb_file == 0){copy_upgrade_log += "|Files: 0 |\n" + "   - No Files found in CI3-Path\n";}
        else {copy_upgrade_log += "|Files: " + upgrade_log_numb_file + " |\n";}
        copy_upgrade_log += upgrade_log_file + "\n";

        if(!src.getName().equals("assets")){copy_upgrade_log = copy_upgrade_log + "-------------------------------------------------------------------------------\n\n";}
    }

    private static void copyDirectory(File src_dir, File tar_dir) throws IOException {
        //Check if directory already exists in CI4 where the CI3 files need to be copied, else create new directory
        if (!tar_dir.exists()) {
            tar_dir.mkdir();
        }

        if (src_dir.list() != null) {

            for (String f : src_dir.list()) {
                copyDirectoryCompatibityMode(new File(src_dir, f), new File(tar_dir, f));

            }
        }
    }

    public static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        //Check if file is directory (copyDirectory()) or if File is file (copyFile())
        if (source.isDirectory()) {
            //Output
            copy_output_dir += "\t" + source.getName() + "\n";
            copy_output_numb_dir++;
            //Upgrade Log
            upgrade_log_dir += "   - " + source.getName() + "\n";
            upgrade_log_numb_dir++;

            copyDirectory(source, destination);
        } else {
            //Prevent overwriting welcome_message.php
            if(source.getName().equals("welcome_message.php"))return;

            //Just copy MY_Controller.php from CI3 application/core
            if(source.getParentFile().getName().equals("core") && !source.getName().equals("MY_Controller.php")){return;}

            copyFile(source, destination);
        }
    }

    private static void copyFile(File sourceFile, File destinationFile) throws IOException {
        try{
        File src = sourceFile;

        //Prevent overwriting of error view files (Copy CI3 error file with new name to CI4)
        if (sourceFile.getName().equals("error_404.php") || sourceFile.getName().equals("error_exception.php")){
            src = copyErrorFile(sourceFile);
            File des = new File(destinationFile.getParent() + "\\" + destinationFile.getName().replace(".php", "_ci3.php"));
            copyFile(src, des);
        }else {
            try (InputStream in = new FileInputStream(src);
                 OutputStream out = new FileOutputStream(destinationFile)) {
                byte[] buf = new byte[1024];
                int length;
                while ((length = in.read(buf)) > 0) {
                    out.write(buf, 0, length);
                }
                //Output
                copy_output_file += "\t" + src.getName() + "\n";
                copy_output_numb_file++;
                //Upgrade Log
                upgrade_log_file += "   - " + src.getName() + "\n";
                upgrade_log_numb_file++;
            }
        }
        }catch (FileNotFoundException e){
            System.out.println("File not found.");
        }
    }

    public static File copyErrorFile(File src) throws IOException{

        String filename = src.getName();
        int type = filename.indexOf(".");
        filename = filename.substring(0, type);
        filename = filename + "_ci3.php";

        File newfile = new File(src.getParent() + "\\" + filename);

        String content = "";
        String line = "";

        FileReader frsrc = new FileReader(src);
        BufferedReader brsrc = new BufferedReader(frsrc);

        //Get whole content of file into string
        while ((line = brsrc.readLine()) != null) {
            content = content + line + "\n";
        }
        frsrc.close();
        brsrc.close();

        FileWriter fwtar = new FileWriter(newfile);
        fwtar.write(content);
        fwtar.close();

        return newfile;
    }

    //|-------->  |METHOD TO SEARCH AND COPY ASSETS FILES FROM CI3 TO CI4 WITH OUTPUT|  <-------|
    public File getAssetsFiles (Path path_ci3){
        File assets_root = new File(String.valueOf(path_ci3));
        File assets_application = new File (path_ci3 + "\\application");

        if (checkAssets(assets_root)){return assets_root;}
        else if (checkAssets(assets_application)){return assets_application;}
        else {return null;}
    }

    public Boolean checkAssets (File assets){
        Boolean exists = false;
        String[] filelist = assets.list();

        if (filelist != null) {
            for (int i = 0; i < filelist.length; i++) {
                if (filelist[i].equals("assets")){
                    exists = true;
                    return exists;
                }
            }
        }
        return exists;
    }

    public void copyAssets(Path path_ci3, Path path_ci4, File src_assets) throws IOException {

            int pathlength = path_ci3.toString().length();
            String path = src_assets.getPath();
            path = path.substring(pathlength);
            path = path.replaceAll("application" , "app");
            String newpath = path_ci4 + path;
            File tar_assets = new File(newpath);

            copy(src_assets, tar_assets);
    }

    //|-------->  |VARIABLES FOR OUTPUT|  <-------|
    protected static String copy_output_dir;
    protected static String copy_output_file;
    protected static Integer copy_output_numb_dir;
    protected static Integer copy_output_numb_file;

    //|-------->  |METHOD AND VARIABLES TO CREATE STRING FOR UPGRADE LOG|  <-------|
    protected static String copy_upgrade_log = "";
    protected static String upgrade_log_file;
    protected static String upgrade_log_dir;
    protected static Integer upgrade_log_numb_file;
    protected static Integer upgrade_log_numb_dir;

    public String getUpgradeLog() throws IOException{
        String upgradelog = "                 |-----| 2. COPIED DIRECTORIES AND FILES |-----|";

        upgradelog = upgradelog + "\n################################################################################\n\n";

        upgradelog = upgradelog + "Here you can check out, which directories and files were copied from your old CI3 project to your new CI4 project. \n" +
                                "If your are missing something, you have to copy it by your own hand. \n\n" +
                                "The CI-Upgrader ran through your Views, Models, Controllers, Core (if exists, the Upgrader will just copy your MY_Controller.php from Core)," +
                                " Migrations, Language, Libraries, Helper and Assets. \n" +
                                "(Assets if the most used name for the directory, which contains your img, css, js, etc... files. " +
                                "If you use a different name in your CI3 project or if your assets folder is not in your root or application directory, the Upgrader will " +
                                "not find and copy these files. In this case, you have to copy it manually.)";

        upgradelog = upgradelog + "\n\n-------------------------------------------------------------------------------\n\n";

        if (copy_upgrade_log.equals("")){upgradelog = upgradelog + "No directories and files found to copy\n\n";}

        upgradelog = upgradelog + copy_upgrade_log;

        upgradelog = upgradelog + "################################################################################\n";

        return upgradelog;
    }

}
