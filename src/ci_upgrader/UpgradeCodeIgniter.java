
/*
 * Copyright (c) 2021 FlorianNelles https://github.com/FlorianNelles
 * Copyright (c) 2014-2019 British Columbia Institute of Technology
 * Copyright (c) 2019-2021 CodeIgniter Foundation
 * Copyright (c) 2021 Kenji Suzuki https://github.com/kenjis
 *
 * For the full copyright and license information, please view
 * the LICENSE.md file that was distributed with this source code.
 *
 * @see https://github.com/FlorianNelles/CI-Upgrader
 */

package ci_upgrader;

import java.io.*;

public class UpgradeCodeIgniter {

//  |-------->  |MAIN METHOD|  <-------|
    public static void main (String[]args) throws IOException {
        System.out.println("|-------->  |START UPGRADE|  <-------|" + "\n");

//----- ###  |RUN INSTALLATION|  ###
//      Install a new CI4 project in the same directory as the old CI3 project.
//----- The path to the old CI3 project and the name for the new project is read from the console.

        Installation install = new Installation();
        install.run();

//----- ###  |COPY DIRECTORIES AND FILES|  ###
//      Copy all relevant directories and files from the old to the new project.
//----- Views, Models, Controllers, Migrations, Languages, Libraries, Helpers, Assets Files

        CopyDirFiles copy = new CopyDirFiles();
        copy.run(install.path_ci3, install.path_ci4);

//----- ###  |EDIT FILES|  ###
//      Edit the new installed CI4 files to transfer settings the settings of the old CI3 project (e.g. Config Files, Routes, BaseController)
//----- Also adjust the copied files to the new syntax (e.g. Views, Controllers, Models, Libraries, Languages, Migrations)

        EditFiles edit = new EditFiles();
        edit.run(install.path_ci3, install.path_ci4);

//----- ###  |CREATE UPGRADELOG|  ###
//      In every class is a method, which will create a part of the UpgradeLog and returns a String. At the end the UpgradeLog will be created, by putting
//      all Strings together. This UpgradeLog will contain all relevant informations to the upgrade process.
//----- The UpgradeLog and the directory of the new project will open automatically.

        UpgradeLog log = new UpgradeLog();
        String installupgradelog = install.getUpgradeLog();
        String copyupgradelog = copy.getUpgradeLog();
        String editupgradelog = edit.getUpgradeLog();
        log.run(install.path_ci4, installupgradelog, copyupgradelog, editupgradelog);

        System.out.println("\n" + "|-------->  |UPGRADE: COMPLETED|  <-------|");
        System.out.println("\n!!! For more informations check out the file Upgrade_Log.txt !!!");

        openProject(install);
    }

//  |-------->  |METHOD TO OPEN THE NEW CI4 PROJECT AND UPGRADE_LOG WITH CMD|  <-------|
    public static void openProject(Installation install) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd/ && " + install.drive + "&& cd " + install.path_ci4.toString() + "&& start ." +
                " && Upgrade_Log.txt");
        builder.redirectErrorStream(true);
        Process p = builder.start();
    }

}