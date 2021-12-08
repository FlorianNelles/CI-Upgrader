# CI-Upgrader

CI-Upgrader will help you to upgrade your CodeIgniter 3 project to CodeIgniter 4.

To do this, CI-Upgrader transfers a lot of CI3 files and code into CI4 Syntax, but takes also usage 
of the [ci3-to-4-upgrader-helper](https://github.com/kenjis/ci3-to-4-upgrade-helper) 
by Kenji Suzuki. Kenjis upgrader-helper provides interfaces for common use cases in CodeIgniter, to make them 
compatible and executable in CI4.

This tool is most suitable for developers who want to perform a quick upgrade of there CI3 project 
with as little work as possible. If you want to extend your project after the upgrade, you have the option
to use CI3 and CI4 syntax in the most parts of your code. 

On the other hand, you have also the opportunity to remove Kenji Suzukis upgrade-helper from selected files or
even from the whole project. In this case, you have to transfer the affected code manually into CI4 syntax.
For this it is recommended to take a look at the [Upgrade Guide](https://codeigniter4.github.io/CodeIgniter4/installation/upgrade_4xx.html) 
of the official documentation, which will be of great help in this regard.

---

These following tasks are carried out automatically by CI-Upgrader:

- **Installation**
  - Installs a new CI4 project in the same directory as the CI3 project
  - The installation includes also phpunit and kenjis upgrade-helper
- **Copy Files**
  - Copies all relevant directories and files from the old CI3 to the new CI4 project
- **Edit Files**
  - Convert CI3 files/settings into CI4 (Config, Languages, Migrations, Routes)
  - Use Kenji Suzukis upgrade-helper to make CI3 code compatible (Controllers, Models, Libraries)
- **Create UpgradeLog**
  - This created file contains all informations about the upgrade process and all tasks the CI-Upgrader has executed


**However** CI-Upgrader is **not** able to transfer all of your code into CodeIgniter 4 without errors.
So some manually adjustment has to be done afterwards.
Thats because there are a lot of new things in CI4, like different application structure, syntax and namespaces. 
Also CodeIgniter projects can be very individual, specially with own created Libraries, Classes and Functions. 

For more informations about CI-Upgrader and the adjustment that have to be done 
after the upgrade, check out [User Guide](https://github.com/FlorianNelles/CI-Upgrader/blob/main/UserGuide.md).

## Requirements
- Executable CodeIgniter 3 project
- Windows system (Linux and macOs are **not** supported)
- Composer installed 
- JRE 1.6.6 (or later)

## Sample Project 
Here you can see an example of a simple CodeIgniter 3 project, which was 
transferred to CodeIgniter 4 with CI-Upgrader.

- CodeIgniter 3: [test_ci3](https://github.com/FlorianNelles/test_ci3)
- CodeIgniter 4: [test_ci3_upgrade_to_ci4](https://github.com/FlorianNelles/test_ci3_upgrade_to_ci4)

## Development
This repository contains the source code of CI-Upgrader, so fell free to take it.
You can work with it, develop your own tool or try to improve CI-Upgrader.

Pull Requests are very welcome.

<a href="https://trackgit.com">
<img src="https://us-central1-trackgit-analytics.cloudfunctions.net/token/ping/kwxjbcaqzqirily9e8ik" alt="trackgit-views" />
</a>
