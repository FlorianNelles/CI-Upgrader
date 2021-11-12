# Documentation CI-Upgrader

## Tabel of Contents
- [Before the Upgrade](#before-the-upgrade)
- [Run CI-Upgrader](#run-ci-upgrader)
- [After the Upgrade](#after-the-upgrade)
  - [What needs to be done afterwards](#what-needs-to-be-done-afterwards)
  - [Problem Solving](#problem-solving)
  

## Before the Upgrade

Check if all these requirements are met:

- Windows system (Linux and macOS **not** supported)
- Composer has to be installed on your system
  - To check if Composer is installed, open 'CMD' and enter `composer`
  - If the command 'composer' was not found, it´s not installed
  - In this case, open [GetComposer](https://getcomposer.org/) and follow the instructions to install Composer
- Java ?
- Executable CodeIgniter 3 project, that you want to upgrade to CodeIgniter 4
  - Must be saved locally on the system
  - The project structure must be unchanged from the original

```
---root|---application|---config
       |              |---controllers
       |              |---models
       |              |---views
       |              |...
       |---system
       |...
```

## Run CI-Upgrader

Download CI-Upgrader and unzip folder.

---

### Step 1: Start CI-Upgrader
There are to ways to start CI-Upgrader:

- **.exe:** Just doubleclick on ci-upgrader.exe  

- **.jar:** Open 'CMD'  &#8594; Navigate to the location you saved 'ci-upgrader.jar' &#8594; Enter `java -jar ci-upgrader.jar`

( If its not working, try 'Run as administrator' )

---

### Step 2: Enter Path to your CodeIgniter 3 Project

The Path has to be in this format: `Drive\Path\To\Your\CI3_Project` (Example: `C:\xampp\htdocs\projectname`)

Best way to get the path: Open Windows Explorer &#8594;  Navigate to your CI3 project that you want 
to upgrade &#8594; Copy path from address bar 

CI-Upgrader will check if your input is correct:
- Check if input is a path
- Check if input has invalid symbols
- Check if path leads to a CodeIgniter 3 project

---

### Step 3: Enter Name for your new CodeIgniter 4 Project

You can name your new project whatever you want. Just make sure there is no already existing project
with the same name in your CI3 project location. Also don´t use these symbols: `< > : ? * | \\ / "`

---

### Step 4: Installation and Upgrade is running automatically
This tool takes the settings from Step 2 and 3 to install your new CI4 project in the same directory 
as your old CI3 project. The installation includes phpunit and [ci3-to-4-upgrader-helper](https://github.com/kenjis/ci3-to-4-upgrade-helper)
by Kenji Suzuka.

CI-Upgrader checks, if installation was successful. If not, you probably don´t have Composer installed on 
your system. Go to [GetComposer](https://getcomposer.org/) and follow the instructions to install Composer.

After the installation, CI-Upgrader starts to copy all the relevant files from the old to new project,
transfer settings, edit files and create UpgradeLog.

## After the Upgrade

When CI-Upgrader is finished with the upgrade process, the directory of your new CI4 project and the UpgradeLog 
are going to open on your screen.

UpgradeLog will show you all relevant informations about the upgrade process and all tasks CI-Upgrader
has executed:
- Location of your CI3 and new CI4 project
- Which files and directories were copied (with path)
- Which lines were edited, added or removed in which file

It is highly recommended to take a look on the UpgradeLog, because it gives you a better understanding
on what this tool has done during the upgrade and what you have to do afterwards. You should also check
out the official [Upgrade Guide](https://codeigniter4.github.io/CodeIgniter4/installation/upgrade_4xx.html) 
from CodeIgniter and the documentation of Kenjis Suzukas 
[upgrade-helper](https://github.com/kenjis/ci3-to-4-upgrade-helper/blob/1.x/docs/HowToUpgradeFromCI3ToCI4.md).

### What needs to be done afterwards

---

#### Base URL
baseurl, in app.php, public aus url entfernen, aufrufe von baseurl im project

---

#### Language, Localization
language, Filename in lang lines (Views and Controller), $language load + setlocal einsetzten da 
$this->lang->load('file','value') entfernet werden musste, kann auch in BaseController gemacht werden, wenn 
alle Controller diese erweitern

#### Database Migrations
Databse Migration durchführen: evt neue Database anlegen und in Database.php anpassen
cmd -> zum CI4-Projektordner navigieren -> php spark migrate

#### Databse Seeder
Databse Seeder: SeederFiles erstellen und ausführen (php spark db:seed SeederName)nach CI4 Dokumentation

#### Working with Databse, Query Builder
Working with database; funktionen welche nicht funktinieren auflisten, $db = config... einfügen je nach funktion

#### Pagination
Pagination manuell überführen (mit kenjis Anleitung oder UPgrade Guide, falls auf kenjis verzichtet)

#### Cookies
Cookie Anpassen, Funktionen, Aufrufe, Load?

#### Own created Files, Classes etc.
Eigene Dateien (Libraries, Hooks, Custom config files(bsp Validations)) manuell kopieren

### Extend your project

ci3 und ci4 code möglich

### Remove Upgrade-Helper by Kenji Suzuka

use statement entfernen; manuell in ci4 überführen (upgrade guide)

kann in einzelnen files aber auch global gemacht werden(ordner kenjis löschen)

### Problem Solving

Config/Boot/production.php -> value =1 (Sonst Meldung whoops wenn Errors auftreten, was nach
Upgrade erstmal wahrscheinlich ist; Daher während Anpassungen auf 1 stellen)

assets files falls nicht gefunden und kopiert

View Parser funktionieren vollständig mit Kenjishelper, allerdings funktioniert php 		
Code in Views nicht, wenn parse genutzt wird, bessere Alternative in CI4

redirect Aufrufe anpassen(code entfernen?)

error files, show_error