# CI-Upgrader

The CI-Upgrader will help you to upgrade your CodeIgniter 3 project to CodeIgniter 4.

To do this, these tasks are carried out automatically:

- Installation
- Copy Files
- Edit Files
  - For this, the CI-Upgrader takes usage of the 
  [ci3-to-4-upgrader-helper](https://github.com/kenjis/ci3-to-4-upgrade-helper) by Kenji Suzuka
    - test
    - test
    - test
  - test
  - test
- Create UpgradeLog

**But** the CI-Upgrader is **not** able to transfer all of your code to the new CodeIgniter 4 project, so 
some manually adjustment have to be done afterwards.
Thats because CodeIgniter projects can be very individual, specially with own created Libraries, Classes and Functions. 

For more informations about the CI-Upgrader and the adjustment that have to be done 
after the upgrade, check out the [Documentation](https://github.com/FlorianNelles/CI-Upgrader/blob/main/Documentation.md).

## Requirements
- Executable CodeIgniter 3 project
- Windows system (Linux and macOs not supported)
- Composer installed 
- Java ?

## Sample project 
Here you can see an example of a simple CodeIgniter 3 project, which was 
transferred to CodeIgniter 4 with the CI-Upgrader.

- CodeIgniter 3: [Testci3](link)
- CodeIgniter 4: [Testci4](link)

## Development
This repository contains the source code of CI-Upgrader, so fell free to take it.
You can work with it, develop your own tool or try to improve the CI-Upgrader.

Pull Requests are very welcome.