# CI-Upgrader

The CI-Upgrader will help you to upgrade your CodeIgniter 3 project to CodeIgniter 4.
To do this, the CI-Upgrader tranfers a lot of CI3 files and code into CI4 Syntax, but takes also usage 
of the [ci3-to-4-upgrader-helper](https://github.com/kenjis/ci3-to-4-upgrade-helper) 
by Kenji Suzuka. This upgrade helper provides interfaces for common use cases in CodeIgniter, to make them 
compatible and executable in CI4. 

!!!todo!!!

So this CI-Upgrader is

Also you have the opportunity to remove kenjis helper 


---

These following tasks are carried out automatically by CI-Upgrader:

- **Installation**
  - Installs a new CI4 project in the same directory as the CI3 project
  - The installation includes also phpunit and kenjis upgrade-helper
- **Copy Files**
  - Copies all relevant directories and files from the old CI3 to the new CI4 project
- **Edit Files**
  - Convert CI3 file into CI4 syntax (Config, Languages, Migrations, Routes)
  - Use kenjis upgrade-helper to make CI3 code compatible (Controllers, Models, Libraries)
- **Create UpgradeLog**
  - This created file contains all informations about the upgrade process and all tasks the CI-Upgrader has done


**However** the CI-Upgrader is **not** able to transfer all of your code into CodeIgniter 4 without errors.
So some manually adjustment has to be done afterwards.
Thats because there are a lot of changes between CI3 and CI4 and CodeIgniter projects can be very individual, specially with own created Libraries, Classes and Functions. 

For more informations about the CI-Upgrader and the adjustment that have to be done 
after the upgrade, check out the [Documentation](https://github.com/FlorianNelles/CI-Upgrader/blob/main/Documentation.md).

## Requirements
- Executable CodeIgniter 3 project
- Windows system (Linux and macOs are **not** supported)
- Composer installed 
- Java ?

## Sample Project 
Here you can see an example of a simple CodeIgniter 3 project, which was 
transferred to CodeIgniter 4 with the CI-Upgrader.

- CodeIgniter 3: [Testci3](link)
- CodeIgniter 4: [Testci4](link)

## Development
This repository contains the source code of CI-Upgrader, so fell free to take it.
You can work with it, develop your own tool or try to improve the CI-Upgrader.

Pull Requests are very welcome.