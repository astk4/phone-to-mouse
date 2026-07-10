# Phone To Mouse

A solution of 2 apps for Android mobile devices to function as a Bluetooth mouse with Windows machines. Pet project mostly developed back in Q3 2024 out of interest for some IoT, used on regular basis, and received bug fixes and usability improvements in Q3 2026.

[![Android Build](https://github.com/astk4/phone-to-mouse/actions/workflows/android-build.yml/badge.svg)](https://github.com/astk4/phone-to-mouse/actions/workflows/android-build.yml) [![WPF app and C++ DLL Build](https://github.com/astk4/phone-to-mouse/actions/workflows/wpf-cpp-build.yml/badge.svg)](https://github.com/astk4/phone-to-mouse/actions/workflows/wpf-cpp-build.yml) ![Release](https://img.shields.io/github/v/release/astk4/phone-to-mouse) ![License](https://img.shields.io/github/license/astk4/phone-to-mouse?label=license) ![Status](https://img.shields.io/badge/status-maitenance-white)

[![Target .NET](https://img.shields.io/badge/dynamic/xml?color=blueviolet&label=target&query=%2F%2FTargetFramework%5B1%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fastk4%2Fphone-to-mouse%2Fmain%2FWindowsSide%2FDesktopClient%2FDesktopClient.csproj&logo=.net)](https://github.com/astk4/phone-to-mouse/blob/main/WindowsSide/DesktopClient/DesktopClient.csproj) [![Target Android](https://img.shields.io/badge/target-14%20-3DDC84?logo=android&logoColor=3DDC84)](https://github.com/astk4/phone-to-mouse/blob/main/AndroidPart/app/build.gradle)
 [![Min Android](https://img.shields.io/badge/min-5.0%20-3DDC84?logo=android&logoColor=3DDC84)](https://github.com/astk4/phone-to-mouse/blob/main/AndroidPart/app/build.gradle)

### Video demo: [![Bluesky](https://img.shields.io/badge/Bluesky-0285FF?logo=bluesky&logoColor=fff)](https://bsky.app/profile/a43ti.bsky.social/post/3mpbjvyr6ik2z)

<details> 
    <summary>Table of contents</summary>
    <ol>
        <li>
            <a href="#prerequisites">Prerequisites</a>
        </li>
        <li>
            <a href="#getting-started">Getting started</a>
        </li>
        <li>
            <a href="#how-to-use">How to use</a>
            <ul>
                <li>
                    <a href="#how-to-do-mouse-gestures">How to do mouse gestures</a>
                </li>
            </ul>
        </li>
        <li>
            <a href="#tech-stack">Tech stack</a>
        </li>
        <li>
            <a href="#screenshots">Screenshots</a>
        </li>
    </ol>
</details> 

---

## Prerequisites
- Android 5.0+ (minimum API level 21, target API level 34)
- Windows laptop/computer
- Bluetooth supported on both devices
## Getting started
 - Download files on the Release tab
  - Install APK on the Android device
  - Unzip the archive with desktop client on the Windows machine
  - Ensure Bluetooth is enabled on both devices
  - Preferably, pair the devices by Bluetooth, but it can be done later as well
 - ***Important notice***: on the mobile device during app usage you may toggle Bluetooth on/off. Obviously turning it off will stop mouse simulation, but it is safe for the app. But on the desktop, please, **keep Bluetooth enabled the whole time** or else the client will crash. (this will be fixed in future versions)

## How to use
0. ***If you haven't paired your mobile device with the laptop/computer yet:*** In the mobile app, go to main menu, select Configure Bluetooth, click "Discover visible" and find the name/MAC of your laptop/computer in the list. Click on the arrow near the name/MAC, wait for a pairing request to appear and complete it. 
    - Make sure the machine is discoverable, for example on Windows 10/11 go to System -> Bluetooth & devices -> Devices -> Add devices -> Bluetooth
1. In the mobile app "Configure Bluetooth" page, select your laptop/computer from the paired devices list. You can click "Show paired" to update this list.
2. Go to desktop client, click the "Discover paired" button. List of the visible devices here is just for informational purposes.
3. Select a mobile device among paired devices list to connect to.
4. Click "Connect" button.
5. Go back to mobile app, then select Touchscreen from main menu.
6. Tap play button in the corner near the touchscreen. At successful connection the aspect ratio of the red outline will become the same as on the laptop/computer screen.
    - ***Keep in mind how it works:*** Gestures on the touchscreeen in the app are **mapped** to the area of the laptop/computer screen. So, for example, moving cursor across the mobile device will result in it moving just as much across the desktop screen, **not** to a distance the size of a mobile device dimension. During connection it is shown and written in the desktop app, where on the screen and on behalf on which button (or wheel) the last gesture was simulated.
8. ### How to do mouse gestures
    - Selecting simulated mouse button: toggle the switch between [L] and [R] next to the touchscreen, selected button will be highlighted, by default it is left
    - Moving the cursor: move finger on screen
    - Click: single tap
    - Double click: double tap
    - Mouse down: finger down on screen
    - Mouse up: lift finger up from screen
    - Scrolling wheel up: press volume up button
    - Scrolling wheel down: press volume down button

9. To disconnect, click either Disconnect in the desktop client or pause button in the mobile app. Disconnection happens also in such cases as:
    - closing either of the apps
    - exiting the mobile app
    - going to main menu in the mobile app
    - turning the phone off
    - turning Bluetooth off on either device
10. In Settings in the mobile app you can change:
    - cursor size
    - theme (light/dark)
    - language
    - presence of sound and vibration effects when using touchscreen
    - if should try to reconnect when bluetooth had went off and then returned 

## Tech stack

- Core library ![C++](https://img.shields.io/badge/C++-blue.svg?style=flat&logo=c%2B%2B)

- Desktop client ![.NET](https://img.shields.io/badge/.NET-512BD4?logo=.net&logoColor=white) ![WPF](https://custom-icon-badges.demolab.com/badge/WPF-0C54C2?logo=xaml&logoColor=fff)

- Data transmission ![Bluetooth](https://img.shields.io/badge/-Bluetooth|BLE-0082FC?style=flat&logo=bluetooth&logoColor=white)

- Mobile application ![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white) ![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white) ![Gradle](https://img.shields.io/badge/gradle-209bc4?logo=gradle)

- CI/CD ![GitHub Actions](https://img.shields.io/badge/-GitHub%20Actions-333333?style=flat&logo=github-actions)

- Project management ![Jira](https://img.shields.io/badge/Jira-0052CC?logo=jira&logoColor=fff?) *(tasks screenshot below)*
<img width="600" alt="jira work items of the last release development" src="https://github.com/user-attachments/assets/a9d3e202-630c-44a5-ab71-0eb43832ebff" />

## Screenshots
### Mobile part
<details>
    <summary>expand for screenshots</summary>

- Main menu
<p>
  <img width="250" alt="main_menu_idle" src="https://github.com/user-attachments/assets/be1433de-e304-47fe-b88f-560c19247788" />
  <img width="250" alt="main_menu_none_configured" src="https://github.com/user-attachments/assets/ecdea87a-ed16-4caa-8b59-34fc2e1eea6b" />
  <img width="250" alt="main_menu_bt_disabled" src="https://github.com/user-attachments/assets/0adfe9f8-0b50-49be-b41e-bd034cb3bd8a" />
</p>

- Bluetooth settings
<p>
<img width="250" alt="paired_devices" src="https://github.com/user-attachments/assets/d918f832-0a4b-4cc3-8424-9001300684c3" />
<img width="250" alt="finding visible devices" src="https://github.com/user-attachments/assets/b37b4f55-8324-4287-bf43-88bdd607a507" />
</p>

- Touchscreen
  - When not connected
<img height="250" alt="touchscreen idle" src="https://github.com/user-attachments/assets/fda015fd-1cb6-49cc-b3cd-aeefc306f6db" />

  - During most of mouse simulation
<img height="250" alt="touchscreen_right button" src="https://github.com/user-attachments/assets/10af327d-3074-4c6d-9ccc-17e38bf14f0b" />
<img height="250" alt="touchscreen_left_button" src="https://github.com/user-attachments/assets/6ab9ec0e-a2e1-4151-8118-4e54ecbcb116" />

  - When simulating long press/drag of a mouse button
<img height="250" alt="touchscreen_button_pressed" src="https://github.com/user-attachments/assets/d2c499fb-e3d7-4259-ad79-a7702be570d4" />

  - When Bluetooth is disabled
<img height="250" alt="touchscreen_bluetooth_disabled" src="https://github.com/user-attachments/assets/83d2167c-c9f0-4bd9-87f2-1ba46c90b4ac" />

- Settings
  - Android 8.0+/SDK 26+
<img width="250" alt="settings_api_26+" src="https://github.com/user-attachments/assets/8c5978ce-6d7e-4a19-a680-974fb411c871" />

  - Android below 8.0
<img width="250" alt="settings_api_below_26" src="https://github.com/user-attachments/assets/2fef9228-ff59-447b-bce8-b408840cd78b" />
  
- Info (for all 3 see above)
<img width="250" alt="info page" src="https://github.com/user-attachments/assets/15eda47f-a72b-4942-9a1a-f164d051c7df" />

</details>

### Desktop part
<details>
    <summary>expand for screenshots</summary>

- Not connected
<img width="700" alt="not connected" src="https://github.com/user-attachments/assets/d1324e6a-a4e5-4f8e-bf40-ea28dca635a0" />

- Not connected + searching for visible devices
<img width="700" alt="search visible" src="https://github.com/user-attachments/assets/121f65dd-b026-403c-a1fa-d1b8ba03226d" />

- Waiting for connection with a selected mobile device
<img width="700" alt="waiting for connection" src="https://github.com/user-attachments/assets/acdc0b85-c2fb-45c0-b519-29ea7701e89b" />

- "Mouse" clicks
<img width="700" alt="right click" src="https://github.com/user-attachments/assets/9c071d0a-fba3-4f5a-b969-41e801bd8c84" />
<img width="700" alt="left click" src="https://github.com/user-attachments/assets/81b484ed-6c57-4959-853d-016746511a47" />

- Dragging pressed "mouse button"
<img width="700" alt="mouse down" src="https://github.com/user-attachments/assets/96ddfbab-dc5a-436b-bd91-fbbd1567a509" />
<img width="700" alt="mouse down right" src="https://github.com/user-attachments/assets/1ea45036-5913-45de-be6b-92193d6377d8" />

- Scrolling with "wheel" *(basically it doesn't reflect on this white screen)*
<img width="700" alt="wheel up" src="https://github.com/user-attachments/assets/53239b8e-6c10-476a-9473-5d5ec0484695" />
<img width="700" alt="wheel down" src="https://github.com/user-attachments/assets/e2d7f6fc-555d-4a23-a591-984183096c07" />

- Closing confirmation window
<img width="700" alt="closing confirmation" src="https://github.com/user-attachments/assets/f2eac7e6-9116-422d-870c-af956c137cf5" />

</details>
