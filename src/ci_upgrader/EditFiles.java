package ci_upgrader;

import java.io.IOException;
import java.nio.file.Path;

public class EditFiles {

    //|-------->  |RUN METHOD|  <-------|
    public void run (Path path_ci3, Path path_ci4) throws IOException {
        System.out.println("|-------->  |EDIT FILES IN NEW CI4 PROJECT|  <-------|");

//----- ###  |Edit Configuration Files|  ###
//      Get settings of the CI3 config files (with search method) and transfer them to the new CI4 project (with replace method).
//      CI3 config.php -> CI4 App.php, Logger.php, Cache.php, Encryption.php
//----- CI3 database.php -> CI4 Database.php

        EditFilesConfig editConfig = new EditFilesConfig();
        editConfig.run(path_ci3, path_ci4);

//----- ###  |Edit Routes|  ###
//      Search and get all routes from CI3 routes.php, transfer to CI4 syntax and add them into CI4 Routes.php.

        EditFilesRoutes editRoutes = new EditFilesRoutes();
        editRoutes.run(path_ci3, path_ci4);

//----- ###  |Edit Migrations|  ###
//      Get settings of the CI3 config/migrations.php (with search method) and transfer them to the new CI4 project (with replace method)
//      Also edit migration files:
//            - Migration filenames (Check if CI3 migration files are in timestamp format, which is required in CI4.
//                                  If not, edit filename with generated timestamp.)
//            - Content of migration files (Transfer CI3 files to syntax of CI4 (Methods, Header, Namespace...))

        EditFilesMigrations editMigration = new EditFilesMigrations();
        editMigration.run(path_ci3, path_ci4);

//----- ###  |Edit Languages|  ###
//      Transfer all CI3 Language files in CI4 syntax

        EditFilesLanguages editLanguages = new EditFilesLanguages();
        editLanguages.run(path_ci3, path_ci4);

//----- ###  |Edit Libraries|  ###
//      Check if names of Libraries subfolders are correct (just first letter uppercase), otherwise edit subfolder names.
//      Also small changes at the content of Libraries files (header). The most work to transfer libraries files from CI3 to CI4
//----- has to be done by hand, because these files can be very individual/different in syntax, structure and function.

        EditFilesLibraries editLibraries = new EditFilesLibraries();
        editLibraries.run(path_ci3, path_ci4);

//----- ###  |Edit Models|  ###
//      Check if names of Models subfolders are correct (just first letter uppercase), otherwise edit subfolder names.
//      Also edit header (add namespace, add use kenjis, remove defined basepath line). Rest of the file does not to be
//      edited, because kenjis ci3-to-4-upgrade-helper makes the Models files in CI4 compatible.

        EditFilesModels editModels = new EditFilesModels();
        editModels.run(path_ci3, path_ci4);

//----- ###  |Edit Controllers|  ###
//      Check if names of Controllers subfolders are correct (just first letter uppercase), otherwise edit subfolder names.
//      Transfer settings from CI3 autolaod.php to CI4 BaseController.php (New place to load helper, models, libraries... globally).
//      Also edit content of other Controllers files:
//              - Header (add namespace, add use kenjis, remove defined basepath line)
//              - Transfer to CI4 syntax (edit load views call, adjust URL-helper methods, remove load language lines)

        EditFilesControllers editControllers = new EditFilesControllers();
        editControllers.run(path_ci3, path_ci4);

//----- ###  |Edit Views|  ###
//      Adjust Config/View.php (add kenjis use statement) to keep using $this->config call in Views files
//      Also transfer Views to CI4 syntax (ajust URL-helper-methods, remove definded basepath line, edit load language line)

        EditFilesViews editViews = new EditFilesViews();
        editViews.run(path_ci3, path_ci4);

//----- ###  |Get Strings for UpgradeLog|  ###

        editconfiglog = editConfig.getUpgradeLog(path_ci3, path_ci4);
        editrouteslog = editRoutes.getUpgradeLog(path_ci3, path_ci4);
        editmigrationslog = editMigration.getUpgradeLog(path_ci3, path_ci4);
        editlanguageslog = editLanguages.getUpgradeLog(path_ci3, path_ci4);
        editlibrarieslog = editLibraries.getUpgradeLog(path_ci3, path_ci4);
        editmodelslog = editModels.getUpgradeLog(path_ci3, path_ci4);
        editcontrollerslog = editControllers.getUpgradeLog(path_ci3, path_ci4);
        editviewslog = editViews.getUpgradeLog(path_ci3, path_ci4);

        System.out.println("\n" + "|-------->  |EDIT FILES: COMPLETED|  <-------|");
    }


    //|-------->  |METHOD AND VARIABLES TO CREATE STRING FOR UPGRADE LOG|  <-------|
    protected static String editconfiglog = "";
    protected static String editrouteslog = "";
    protected static String editmigrationslog = "";
    protected static String editlanguageslog = "";
    protected static String editlibrarieslog = "";
    protected static String editmodelslog = "";
    protected static String editcontrollerslog = "";
    protected static String editviewslog = "";

    public String getUpgradeLog() throws IOException{
        String upgradelog = "|-----| 3. EDITED FILES |-----| ";

        upgradelog = upgradelog + "\n################################################################################\n\n";

        upgradelog = upgradelog + "Here you can check out, which lines were edited, added or removed from every file. \n" +
                                    "You should consider, that the CI-Upgrader can not transfer all files and functions of your CI3 project without " +
                                    "errors into CI4. For more informations about the adjustments you have to make or what can cause errors after the Upgrade, take a look at section '4. WHAT TO DO AFTER CI-UPGRADER.'\n\n" +
                                    "The CI-Upgrader transferred all your settings from the most important Config files (autoload.php, config.php, database.php, migration.php, routes.php) and " +
                                    "a lot of code from your Models, Views, Controllers, Libraries, Languages, and Migrations files with the help of the ci3-to-4-upgrade-helper from kenjis in CI4.";

        upgradelog = upgradelog + "\n\n-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + editconfiglog;

        upgradelog = upgradelog + "\n\n-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + editrouteslog;

        upgradelog = upgradelog + "\n\n-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + editmigrationslog;

        upgradelog = upgradelog + "-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + editlanguageslog;

        upgradelog = upgradelog + "-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + editlibrarieslog;

        upgradelog = upgradelog + "-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + editmodelslog;

        upgradelog = upgradelog + "-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + editcontrollerslog;

        upgradelog = upgradelog + "-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + editviewslog;

        upgradelog = upgradelog + "-------------------------------------------------------------------------------\n\n";

        upgradelog = upgradelog + "################################################################################\n";

        return upgradelog;
    }

}
