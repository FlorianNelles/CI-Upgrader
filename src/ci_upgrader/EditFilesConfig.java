package ci_upgrader;

import java.io.*;
import java.nio.file.Path;

public class EditFilesConfig {

    //|-------->  |RUN METHOD|  <-------|
    public void run(Path path_ci3, Path path_ci4) throws IOException {
        System.out.println("\n" + "###  |Config|  ###");

//----- ###  |Search and Replace config.php/App.php|  ###

        File src_config = new File(path_ci3 + "\\application\\config\\config.php");
        File tar_config = new File(path_ci4 + "\\app\\Config\\App.php");
        src_config = removeComments(src_config);

        searchReplaceConfig(src_config, tar_config, "$config['base_url']", "public $baseURL");
        searchReplaceConfig(src_config, tar_config, "$config['index_page']", "public $indexPage");
        searchReplaceConfig(src_config, tar_config, "$config['uri_protocol']", "public $uriProtocol");
        searchReplaceConfig(src_config, tar_config, "$config['language']", "public $defaultLocale");
        searchReplaceConfig(src_config, tar_config, "$config['charset']", "public $charset");
        //#  |session variables|  #
        searchReplaceConfig(src_config, tar_config, "$config['sess_cookie_name']", "public $sessionCookieName");
        searchReplaceConfig(src_config, tar_config, "$config['sess_expiration']", "public $sessionExpiration");
        searchReplaceConfig(src_config, tar_config, "$config['sess_match_ip']", "public $sessionMatchIP");
        searchReplaceConfig(src_config, tar_config, "$config['sess_time_to_update']", "public $sessionTimeToUpdate");
        searchReplaceConfig(src_config, tar_config, "$config['sess_regenerate_destroy']", "public $sessionRegenerateDestroy");
        //#  |cookie related variables|  #
        searchReplaceConfig(src_config, tar_config, "$config['cookie_prefix']", "public $cookiePrefix");
        searchReplaceConfig(src_config, tar_config, "$config['cookie_domain']", "public $cookieDomain");
        searchReplaceConfig(src_config, tar_config, "$config['cookie_path']", "public $cookiePath");
        searchReplaceConfig(src_config, tar_config, "$config['cookie_secure']", "public $cookieSecure");
        searchReplaceConfig(src_config, tar_config, "$config['cookie_httponly']", "public $cookieHTTPOnly");
        //#  |csrf variables|  #
        searchReplaceConfig(src_config, tar_config, "$config['csrf_token_name']", "public $CSRFTokenName");
        searchReplaceConfig(src_config, tar_config, "$config['csrf_cookie_name']", "public $CSRFCookieName");
        searchReplaceConfig(src_config, tar_config, "$config['csrf_expire']", "public $CSRFExpire");
        searchReplaceConfig(src_config, tar_config, "$config['csrf_regenerate']", "public $CSRFRegenerate");
        //#  |proxy ips|  #
        searchReplaceConfig(src_config, tar_config, "$config['proxy_ips']", "public $proxyIPs");

        output(tar_config);
        config_log = buildUpgradeLogString("App.php (CI4)");

//----- ###  |Search and Replace config.php/Logger.php|  ###

        File src_logger = new File(path_ci3 + "\\application\\config\\config.php");
        File tar_logger = new File(path_ci4 + "\\app\\Config\\Logger.php");
        src_logger = removeComments(src_logger);

        searchReplaceConfig(src_logger, tar_logger, "$config['log_threshold']", "public $threshold");
        searchReplaceConfig(src_logger, tar_logger, "$config['log_date_format']", "public $dateFormat");

        output(tar_logger);
        logger_log = buildUpgradeLogString("Logger.php (CI4");

//----- ###  |Search and Replace config.php/Cache.php|  ###

        File src_cache = new File(path_ci3 + "\\application\\config\\config.php");
        File tar_cache = new File(path_ci4 + "\\app\\Config\\Cache.php");
        src_cache = removeComments(src_cache);

        searchReplaceConfig(src_cache, tar_cache, "$config['cache_query_string']", "public $cacheQueryString");

        output(tar_cache);
        cache_log = buildUpgradeLogString("Cache.php (CI4)");

//----- ###  |Search and Replace config.php/Encryption.php|  ###

        File src_encrypt = new File(path_ci3 + "\\application\\config\\config.php");
        File tar_encrypt = new File(path_ci4 + "\\app\\Config\\Encryption.php");
        src_encrypt = removeComments(src_encrypt);

        searchReplaceConfig(src_encrypt, tar_encrypt, "$config['encryption_key']", "public $key");

        output(tar_encrypt);
        encryption_log = buildUpgradeLogString("Encryption.php CI4)");

//----- ###  |Search and Replace database.php/Database.php|  ###

        File src_database = new File(path_ci3 + "\\application\\config\\database.php");
        File tar_database = new File(path_ci4 + "\\app\\Config\\Database.php");
        src_database = removeComments(src_database);

        searchReplaceConfig(src_database, tar_database, "$active_group", "public $defaultGroup");
        searchReplaceConfig(src_database, tar_database, "'dsn'", "'DSN'");
        searchReplaceConfig(src_database, tar_database, "'hostname'", "'hostname'");
        searchReplaceConfig(src_database, tar_database, "'username'", "'username'");
        searchReplaceConfig(src_database, tar_database, "'password'", "'password'");
        searchReplaceConfig(src_database, tar_database, "'database'", "'database'");
        searchReplaceConfig(src_database, tar_database, "'dbdriver'", "'DBDriver'");
        searchReplaceConfig(src_database, tar_database, "'dbprefix'", "'DBPrefix'");
        searchReplaceConfig(src_database, tar_database, "'pconnect'", "'pConnect'");
        searchReplaceConfig(src_database, tar_database, "'db_debug'", "'DBDebug'");
        searchReplaceConfig(src_database, tar_database, "'char_set'", "'charset'");
        searchReplaceConfig(src_database, tar_database, "'dbcollat'", "'DBCollat'");
        searchReplaceConfig(src_database, tar_database, "'swap_pre'", "'swapPre'");
        searchReplaceConfig(src_database, tar_database, "'encrypt'", "'encrypt'");
        searchReplaceConfig(src_database, tar_database, "'compress'", "'compress'");
        searchReplaceConfig(src_database, tar_database, "'stricton'", "'strictOn'");

        output(tar_database);
        database_log = buildUpgradeLogString("Database.php (CI4)");

        //Delete the helper-file -nocomments.php, which was created by method removeCommentes()
        if (src_database != null){
            src_database.delete();
        }
    }

    //|-------->  |METHOD TO REMOVE COMMENTS FROM CI3 FILE|  <-------|
    public File removeComments(File src_config) throws IOException {
//----- Before search method runs over file, it's important to remove all comments
//      Otherwise it can happen, that settings which are commented out are detected and transferred
//----- To avoid this, create a helper-file with no comments, search the settings, transfer them to CI4 project and delete the helper file after that

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

    //|-------->  |METHODS TO SEARCH AND REPLACE CONFIGURATIONS|  <-------|
    public void searchReplaceConfig(File src, File tar, String srctag, String tartag) throws IOException {
        if (src != null) {
            String config_value = search(src, srctag);

            if (config_value != null) {
                replace(tar, tartag, config_value);
            }
        }
    }

    public String search(File src, String srctag) throws IOException {
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

    public void replace(File tar, String tartag, String value) throws IOException {
        String tarline = null;
        String content = "";
        String newcontent = "";
        int count = 1;

        FileReader frtar = new FileReader(tar);
        BufferedReader brtar = new BufferedReader(frtar);

        while ((tarline = brtar.readLine()) != null) {               //Read target file line by line
            if (tarline.contains(tartag)) {

                newcontent = "\t" + tartag + " " + value;              //Edit line with value from CI3

                //Check if default value form CI4 is equals to CI3 value (if its same don´t edit line)
                if (!tarline.toLowerCase().strip().replaceAll(" ", "").equals(newcontent.toLowerCase().strip().replaceAll(" ", ""))) {

                    tarline = newcontent;

                    //Output
                    edit_output = edit_output + "\t" + tarline + "\n";        //Important for output
                    edit_count++;
                    //UpgradeLog
                    edit_upgrade_log = edit_upgrade_log + "\n   - [Line " + count + "]: " + tarline.stripLeading();
                    edit_numb++;
                }
            }
            content = content + tarline + "\n";
            count++;
        }
        frtar.close();
        brtar.close();

        FileWriter fwtar = new FileWriter(tar);
        fwtar.write(content);
        fwtar.close();
    }

    //|-------->  |VARIABLES AND METHOD FOR OUTPUT|  <-------|
    protected static String edit_output = "";
    protected static Integer edit_count = 0;

    public void output(File tar_filename) {
        if (edit_count == 0) {System.out.println("No Lines edited in app/Config/" + tar_filename.getName());
        }else if (edit_count == 1) {System.out.println("1 Line edited in app/Config/" + tar_filename.getName() + ":");
        } else System.out.println(edit_count + " Lines edited in app/Config/" + tar_filename.getName() + ":");

        System.out.println(edit_output);

        edit_count = 0;
        edit_output = "";
    }

    //|-------->  |METHOD AND VARIABLES TO CREATE STRING FOR UPGRADE LOG|  <-------|
    protected static String upgradelog = "";
    protected static String edit_upgrade_log = "";
    protected static String config_log = "";
    protected static String logger_log = "";
    protected static String cache_log = "";
    protected static String encryption_log = "";
    protected static String database_log = "";
    protected static Integer edit_numb = 0;

    public String getUpgradeLog(Path path_ci3, Path path_ci4) throws IOException{
        upgradelog = "################################  |" + "CONFIG FILES" + "|  ################################\n";

        upgradelog = upgradelog + "[CI3] '" + path_ci3 + "\\application\\config'\n";
        upgradelog = upgradelog + "[CI4] '" + path_ci4 + "\\app\\Config'\n\n";

        upgradelog = upgradelog + config_log;

        upgradelog = upgradelog + "\n\n-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + logger_log;

        upgradelog = upgradelog + "\n\n-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + cache_log;

        upgradelog = upgradelog + "\n\n-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + encryption_log;

        upgradelog = upgradelog + "\n\n-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + database_log;

        return upgradelog;
    }

    public String buildUpgradeLogString (String title){
        String log = "| " + title + " |\n";


        log = log + "Lines edited: " + edit_numb;

        log = log + edit_upgrade_log;

        edit_numb = 0;
        edit_upgrade_log = "";

        return log;
    }

}
