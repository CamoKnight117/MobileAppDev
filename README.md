Lifestyle App - CS 4530 Spring 2023
==========================

The Lifestyle App is a fully functional Android app. The app is an all-in-one place where a user can easily determine how many calories they are using, what the weather is like for the current day, and where nearby hikes can be found. 

This was a semester long project where we implemented features as detailed by the client, and which details are described below in the "App Details" and "Design Features" sections.

Additionally, the second phase of the project involved a major refactoring, which is detailed in the section titled "Project Phase 2: Refactoring."

Development Info
------------------

The app is written in Kotlin, and Android Studio was the primary developer environment utilized in the apps creation. The app was co-developed by a team of four, namely:

- Cameron Knight - Design Lead (Phase 1) and "That Other Guy" (Phase 2)
- Preston Hales - Team Lead (Phase 1) and Test Lead (Phase 2)
- Silas Barber - Test Lead (Phase 1) and Team Lead (Phase 2)
- Jacob Bullard - "Java Wizard" (Phase 1) and Design Lead (Phase 2)

We utilized GitHub's Kanban for planning out and assigning tasks for the app throughout development, so all tasks and plans (and bugs) are documented there.

We did some designing before development of the project in each phase. See the references folder in this project for module designs, class diagrams, and test diagrams for each phase of the project.

App Details
-----------------------
There are four primary modules (i.e. pages) in this app, namely the:

- Profile Create/Edit - Create and edit a user with a name, age, location, height, weight, sex, and "activity level" (see BMR page for details). Additionally, the user can take their own profile picture as part of their profile.
- BMR Calculator - Calculator that determines the user's BMR (Base Metabolic Rate) based on their height, weight, and sex. Additionally, the calculator determines how many calories the user burns per day/week based on how much exercise they do. The daily calorie intake is also calculated which takes into account the user's BMR in combination with their exercise to determine the daily calorie needs for the given invidual.
- Weather Page - A basic page which displays the weather in the user's area (based on their location) for the current day.
- Hikes Map - Find hikes near the user's area at the press of a button. Utilizes Google maps to search for nearby hikes.

Additionally, there are a couple other main features:
- Main page - A main page that you start at with buttons to help navigate to each part of the app. 
- Bottom navbar - A bottom navbar with buttons to each part of the app that is shown at all times to allow for quick navigation while using different modules.
- Top navbar - Displays at all times a basic summary of the user's information, including their daily calories needs and their profile picture.

The app was tested using a emulators for the Pixel 6 (for phones) and Nexus 10 (for tablets) with API 33, so the app should look good and function well on both phones and tablets, especially for these devices specifically.

Design Features
-----------------------
There are a set of 11 design features detailed by the client (and 2 optional features as well). Below is a description of each and how they were implemented (or not implemented):
1. The app should look good and function well on both a phone and a tablet.
  - Implemented: Yes
  - Details: Alternative layouts are used for bigger screens. The threshold we determined was anything wider than 600dp to be a tablet. As mentioned above, all testing was done using a Pixel 6 (for phones) and Nexus 10 (for tablets), so everything should look good and function well on both phones and tablets, especially on those devices specifically.
2. The app should display module names on-screen at all times, and tapping on one should allow the user access to the particular module. On a tablet, the list of modules should always be visible on the left, even while using a module. On a phone, there is not enough room, so it should only display the module the user is on.
  - Implemented: Yes
  - Details: On both phones and tablets, we have a bottom navbar which displays all modules. Clicking on one switches to that particular module. This navbar is visible at all times.
3. There should be nice looking buttons for each module (they shouldn't be grey or just text).
  - Implemented: Yes
  - Details: The profile module button is blue, the BMR module is green, the weather module is yellow, and the hikes module is red. Each one has it's own custom made icon to help differentiate them (and make them look that much better).
4. The user should be able to go in and change their profile data at any time.
  - Implemented: Yes
  - Details: The user profile module is accessible at any time. Going to it will allow the user to edit most of their information. The rest is editable at any time as well in the other modules. The BMR page contains some activity level information that can be edited, and the hikes module allows the user to update their location at any time. Any changes made take immediate effect (for example, updating the user's age, weight, or height will update their BMR automatically).
5. The app should be programmed efficiently right from the outset to allow to add other modules in the future.
  - Implemented: Yes
  - Details: We programmed the app to utilized fragments as opposed to activities since it is more efficient to use fragments due to their lightweight nature. The fragments are managed by one fragment manager, and are all display in the same FrameLayout in the main activity. Each fragment gets displayed whenever the user navigates to a particular module. This allows for future fragments to be developed independently from activities, and easily added to the app in the future as is  needed.
6. The app should be designed/programmed in such a way that it can allow for the ability to add multiple users to the app in the future.
  - Implemented: Yes
  - Details: We have a User class that contains all the user's information. We provide the current user to each fragment so each fragment can use and update its data across all fragments (or modules) in the app. In the future if we have multiple users, whoever is currently using the app is provided to the fragment, and as such could be easily swapped to another user in the event that a user is logged out and another one logs back in.
7. The whole app should look appealing in terms of colors and text. It should follow the Material Design guidelines for button sizes, text sizes, and text colors.
  - Implemented: Yes
  - Details: Text sizes are appropriately large enough to be read, and in some cases (mainly the top navbar) are dynamically changed in the app when information changes or a larger screen is used. Additionally, a common color scheme of gray backgrounds along with colored text and buttons are utilized. Red text is used for card headers, black for plain text, green for values that change but can't be edited, and blue for values that change and can be edited. Additionally, as detailed above, each module button is color coded and have appealing icons associated with them. 
8. The app should work even after rotating the device, hit the phone's back button while in the app, or switch out of the app and back into it.
  - Implemented: Yes
  - Details: Data is saved in the onSaveInstanceState method in the main activity, and loaded back in the onCreate method. This allows for data to remain consistent in the app no matter whether the device is rotated, exited, etc. Similarly, the screens are scrollable, which is very helpful when using the app after rotating. Overall, the app will still work as expected even after rotating the screen or leaving the app and re-entering.
9. The app should be well-tested. It should not crash or be too annoying.
  - Implemented: Yes
  - Details: We did a lot of testing to make sure things weren't crashing or being too annoying. This included some unit testing as well as just using the app and fixing bugs that we came across. There are a couple minor bugs we found in the app that we didn't have time to fix, but they are few and have small impact. Some of the unit tests are also no longer passing due to some changes made after the tests were written which we also didn't have time to fix, but the items they were testing were tested manually and they appear to be working properly.
10. When entering the user's data, the user shouldn't be forced to type anything other than their name.
  - Implemented: Yes
  - Details: The user page and bmr page use only spinners for selecting items to make it more convenient for the user. The location page has an option to autofill based on the user's current location, so there is no need for them to type their information (only if they want to specify a location different than where they currently are). The name is the only one that the user is forced to type.
11. The user should be able to view a summary of their entered data at any time.
  - Implemented: Yes
  - Details: The top navbar for the app contains a summary of the user's information all the time. This includes their name, age, sex, activity level, daily caloric intake, and their profile picture. Other more detailed/specific data like location, weather, weight, calories burned per workout, etc. can be easily viewed in their corresponding modules at any time.
12. (optional) A custom icon should be design for each module button along with text below it.
  - Implemented: Yes
  - Details: As detailed above, each module has a button in both the main page and in the bottom navbar, each with their own icons and text (and color) to differentiate them.
13. (optional) The app should be optimized for both portrait and landscape modes on both phones and tablets.
  - Implemented: Yes
  - Details: As mentioned above, there are specific layouts for just the tablet, and also as mentioned above, the app is usable in both portrait and landscape modes. Additionally, since we tested the app on the Pixel 6 and Nexus 10 (each using API 33), the app is especially optimized on these devices specifically.

Project Phase 2: Refactoring
-----------------------
As part of phase 2 of the project, a major refactoring was done to utilize Kotlin view models, repositories, and room databases. All of these were implemented into the project, and the app works just as it did before but utilizing these features.

The app was thoroughly tested after implementation, and many bugs were fixed as a result, even to the point that we got all of the bugs we found fixed. However, due to issues with gradle builds not allowing use of JUnit and time contraints, we weren't able to finish all the unit tests for this phase that we had planned for the project (in addition to fixing the ones from phase 1).

Additionally, a project presentation was made explaining and showcasing the app, which can be found at https://www.youtube.com/watch?v=FXlMVY1PkMU
