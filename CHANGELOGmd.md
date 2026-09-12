# **v1.0.0**



**Init**



**This has a lot of things from the over a year of dev so like yeah ill briefly mention**



* **Properties panel to viewe and edit the properties of objects**
* **Grid2DPanel to draft 2d construction in a 3d plane**
* **Commands (More so tools) which feature:**

  * **transform command with scale, rotate, translate and quick-translate**
  * **camera command with info subcommands and orbit**
  * **measure command**
  * **extrude**
  * **join**
  * **create**
  * **prism**
  * **help**
  * **explode**
  * **engine**
  * **etc...**
* **Documentation in-app parsed from readable markdown that can either be opened in the app or within any other standard markdown reader (with some quirks)**
* **Tutorial/Guide**
* **Projects frame to make a new project, start the tutorial, pin recent projects or the debug project**
* **context menu in the engine frame itself with some quick commands**
* **refactor toolbox to include some stats about your CPU**
* **make all (for the most part) colouring be consistent with J3Engine themes**
* **custom J3Engine themes that persist**
* **persistant user settings**
* **command hints and suggestions along with right click autocomplete**
* **menu bar with some quick actions**
* **make logging a lot more verbose**
* **Refactor old implementation of GObject drawing itself to instead compose of other smaller geometry that's completely detached from the GObject machinery**
* **fix a lot of command palette flashing bugs and UI tearing (I blame swing)**
* **Remove Signup/Login and all database stuff so we are released from the shackles that is the PAT**



# **v1.1.0**



* **.msi build with all windows configuration as an app and project file stuff so you can access easily without having to run the shaded jar (shaded jar is still included)**
* **Fix bug where copy/paste revealed GPoint's equals was not really an equals**
* **Add curve support to properties filter as it was supposed to**
* **Add main startup with a project file for msi support. basically skips the entire projects frame opening and immediately goes to splash text**
* **fix some theme changer stuff not working**
* **begin back integrated Jaiva!** because why not

