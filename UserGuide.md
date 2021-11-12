# User Guide CI-Upgrader

## Tabel of Contents
- [Before the Upgrade](#before-the-upgrade)
- [Run CI-Upgrader](#run-ci-upgrader)
- [After the Upgrade](#after-the-upgrade)
  - [Base URL](#base-url)
  - [Language, Localization](#)
  - [Database](#database)
  - [Pagination](#)
  - [Cookies](#)
  - [Own created Files, Classes etc.](#)
  - [Extend your Project](#extend-your-project)
  - [Reomve Upgrader-Helper by Kenjis Suzuka](#remove-upgrade-helper-by-kenji-suzuka)
  - [Solving Problems](#solving-problems)

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

---

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



### Base URL
Depending on your server settings, you have to adjust your BaseURL in **_App/Config/App.php_**

```php
    [Line 26]   public $baseURL = 'http://localhost/newprojectname';
```

**Note:** Your server should be configured to point at the **public** folder of your new CI4 project.
Public folder contains the main **_.htaccess_** file and **_index.php_**. In CI4 it is meant to be the web root
and prevents direct access to your source code.

Apart from that you have two other options:
1. Add `/public` to your BaseURL, but then you have to check if your `base_url()` calls in Controllers and
Views are still working
2. Move **_.htaccess_** and _**index.php**_ form public to root directory. Also edit _**index.php**_
```php
    [Line 20]   $pathsConfig = FCPATH . '../app/Config/Paths.php';
 to
    [Line 20]   $pathsConfig = FCPATH . '/app/Config/Paths.php';
```

---

### Language, Localization



The CI3 Language Loading in Controllers is no longer supported and was removed by CI-Upgrader:

```php
//  CI3 Example

//  Load File and set Language in Controller:  
    $this->lang->load('news', $lang);
    
//  Echo Language Line in Controllers or Views: 
    echo $this->lang->line('title');
```

Instead of this, you have to use CI4 Localization:

```php
//  CI4 Example

//  Load Language Service and set Locale: 
    $language = \Config\Services::language();
    $language->setLocale($lang);

//  Load File and echo Language Line in Controllers or Views:
    echo lang('news_lang.title');
```

Best way to load Language Service and to set Locale is in every Controllers Constructor or globally 
in BaseController (If all Controllers extend BaseController).

CI-Upgrader edited every Language Line in Controllers and Views automatically to `lang('FILENAME.message')`. 
All you have to do is replace `FILENAME` with the name of the Language File, which has to be loaded.

---
### Database 
**Note:** CI-Upgrader takes your database settings from your old CI3 project and transfers them into CI4 _**App/Config/Database.php**_,
so your new project is connected to the same database by default. You can easily change this settings in _**Database.php**_ if you want.

- #### Migrations
All Migrations files were transferred into CI4 syntax by CI-Upgrader. To run Migrations, all you have to do:

1. Open 'CMD'  &#8594;  Navigate to your project location
2. Enter `> php spark migrate`

More informations: CodeIgniter [User Guide](https://codeigniter4.github.io/userguide/dbmgmt/migration.html)


**Note:** If you used in CI3 your own created Controller _**Migrate.php**_ to run Migrations, it is no
longer needed in most cases. In case you still want to use this Controller, you have convert it by yourself.

- #### Seeding
CodeIgniter 3 has no Seeder Class, so you had to find your onw way to create Seeder files and to run them.
In contrast to this CodeIgniter 4 provides an own Class for Seeder and you should use it.

1. Create new Seeder files with CI4 Seeder Class and transfer your data ([User Guide](https://codeigniter4.github.io/userguide/dbmgmt/seeds.html))
2. Open 'CMD'  &#8594;  Navigate to your project location
3. Enter `> php spark db:seed SeederName`

- #### Query Builder
These three CI3 Query functions are **not** supported by Kenjis Suzuka Upgrade-Helper and will not run in CI4:

`group_by()`, `limit()` and  `update()`

If you use them, you have to change it to CI4 Query Builder ([User Guide](https://codeigniter4.github.io/CodeIgniter4/installation/upgrade_database.html)).

Example:

```php
   $query = $this->db->select('title')
                     ->from('News')
                     ->where('id', $id)
                     ->group_by('created_at')
                     ->limit(10,20)
                     ->get();                   
to
   $db = \Config\Database::connect();
   $builder = $db->table('News');
   $query = $builder->select('title')
                     ->where('id', $id)
                     ->groupBy('created_at')
                     ->limit(10,20)
                     ->get();
```

---

### Pagination
Open Kenji Suzukas [User Guide](https://github.com/kenjis/ci3-to-4-upgrade-helper/blob/1.x/docs/HowToUpgradeFromCI3ToCI4.md#pagination) 
and follow instructions to transfer Pagination to CodeIgniter 4.

If you don´t want to use Kenji Suzuka Upgrade-Helper and removed it, follow instructions of official
CodeIgniter [Upgrade Guide](https://codeigniter4.github.io/CodeIgniter4/installation/upgrade_pagination.html).

---

### Cookies
Cookie Anpassen, Funktionen, Aufrufe, Load?

---

### Own created Files, Classes etc.
Eigene Dateien (Libraries, Hooks, Custom config files(bsp Validations)) manuell kopieren

---

### Extend your Project

ci3 und ci4 code möglich

- #### Develper Mode

.env

---

### Remove Upgrade-Helper by Kenji Suzuka

use statement entfernen; manuell in ci4 überführen (upgrade guide)

kann in einzelnen files aber auch global gemacht werden(ordner kenjis löschen)

---

### Solving Problems

- #### Config Production
Config/Boot/production.php -> value =1 (Sonst Meldung whoops wenn Errors auftreten, was nach
Upgrade erstmal wahrscheinlich ist; Daher während Anpassungen auf 1 stellen)

---

- #### Assests Files
assets files falls nicht gefunden und kopiert

---

- #### View Parser
View Parser funktionieren vollständig mit Kenjishelper, allerdings funktioniert php 		
Code in Views nicht, wenn parse genutzt wird, bessere Alternative in CI4

---

- #### Redirect
redirect Aufrufe anpassen(code entfernen?)

---

- #### Error Files
error files, show_error