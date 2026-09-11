# Setup Instructions

This guide explains how to clone the MediTrack project from GitHub and run it locally on your machine.

---

## 1. Prerequisites

Before you begin, make sure you have the following installed:

- Git
- Java JDK 17 or later
- An IDE such as IntelliJ IDEA or VS Code (optional but recommended)
- Terminal or Command Prompt

### Check Java installation
Open a terminal and run:

```bash
java -version
```

If Java is not installed, install the JDK and then verify again.

### Check Git installation
Run:

```bash
git --version
```

If Git is not installed, install it from the official Git website.

---

## 2. Clone the Repository

Open a terminal and navigate to the folder where you want to store the project.

Example:

```bash
cd ~/Documents
```

Then clone the repository:

```bash
git clone https://github.com/abhi15798/MediTrack.git
```

This creates a folder named `MediTrack` in the current directory.

Navigate into the project:

```bash
cd MediTrack
```

---

## 3. Verify the Project Structure

Run:

```bash
ls
```

You should see files such as:

- `src/`
- `README.md`
- `.gitignore`
- `MediTrack.iml`

The Java source files are usually located inside:

```bash
src/com/airtribe/meditrack/
```

---

## 4. Open the Project in an IDE

### Option A: IntelliJ IDEA
1. Open IntelliJ IDEA
2. Click `Open`
3. Select the `MediTrack` folder
4. IntelliJ will detect the Java project automatically
5. Let the project import and index dependencies

### Option B: VS Code
1. Open VS Code
2. Open the project folder
3. Install the Java Extension Pack if prompted
4. Use the Java project explorer to open source files

---

## 5. Compile the Project

From the project root, run:

```bash
mkdir -p out
find src -name '*.java' -print | xargs javac -d out
```

This compiles all Java files and places the compiled `.class` files into the `out/` directory.

If the compile succeeds, you are ready to run the application.

---

## 6. Run the Application

The project contains a `Main` class under the package:

```bash
com.airtribe.meditrack.Main
```

Run it using:

```bash
java -cp out com.airtribe.meditrack.Main
```

This starts the MediTrack application and displays the console menu.

### Run `Main` in IntelliJ IDEA UI
1. Open the project in IntelliJ IDEA.
2. In the Project Explorer, locate `src/com/airtribe/meditrack/Main.java`.
3. Right-click the file.
4. Select `Run 'Main.main()'`.
5. IntelliJ will compile the project and start the application in the Run panel.

If no run configuration exists yet:
1. Click the `Run` menu in the top toolbar.
2. Select `Edit Configurations...`.
3. Click `+` and choose `Application`.
4. Set the `Main class` to `com.airtribe.meditrack.Main`.
5. Set the `Working directory` to the project root.
6. Click `Apply` and then `OK`.
7. Run it again from the green play button.

To run AI Assistant feature, you need to set up OpenAI/Gemini API URL and Keys. Follow below instructions to set up the configurations in your environment variables:
1. After configuring your main class using above steps, click on `Edit Configurations...`.
2. Under Environment Variables click on `Edit environment variables` located to the right side.
3. In the new pop-up opened, click on `Add (+)` button.
4. Under `Name` enter the value `API_KEY`
5. Under `Value` enter your API key.
6. Again click on `Add (+)` button.
7. Under `Name` enter the value `API_URL`
8. Under `Value` enter your API URL. 
9. Click `OK` to close the pop-up and then click `Apply` -> `OK`


---

## 7. Run a Specific Manual Test Runner

The project includes manual runner classes such as:

- `DoctorServiceManualRunner`
- `PatientServiceManualRunner`
- `AppointmentServiceManualRunner`
- `BillServiceManualRunner`
- `AIHelperManualRunner`

Example to run the Doctor service tests:

```bash
java -cp out com.airtribe.meditrack.service.DoctorServiceManualRunner
```

Example for Patient service:

```bash
java -cp out com.airtribe.meditrack.service.PatientServiceManualRunner
```

### Run Manual Test Runners in IntelliJ IDEA UI
1. Open the project in IntelliJ IDEA.
2. Navigate to one of the runner files, for example:
   - `src/com/airtribe/meditrack/service/DoctorServiceManualRunner.java`
   - `src/com/airtribe/meditrack/service/PatientServiceManualRunner.java`
   - `src/com/airtribe/meditrack/service/AppointmentServiceManualRunner.java`
   - `src/com/airtribe/meditrack/service/BillServiceManualRunner.java`
   - `src/com/airtribe/meditrack/util/AIHelperManualRunner.java`
3. Right-click the file.
4. Select `Run 'ClassName.main()'`.
5. IntelliJ builds the project and executes the manual tests.

If you want to configure a run command manually:
1. Go to `Run > Edit Configurations...`
2. Click `+` and choose `Application`
3. In the `Main class` field, enter the runner class such as:
   - `com.airtribe.meditrack.service.DoctorServiceManualRunner`
4. Set `Working directory` to the project root
5. Click `Apply` and then `OK`
6. Press the green run button

---

## 8. If You Need to Refresh the Build

After changing Java files, recompile the project:

```bash
find src -name '*.java' -print | xargs javac -d out
```

Then rerun the required class.

---

## 9. Common Setup Issues and Fixes

### Issue: `javac: command not found`
This means Java is not installed or not configured in your PATH.

Fix:
- Install JDK
- Add Java to the system PATH
- Restart terminal and verify using:

```bash
java -version
```

### Issue: `ClassNotFoundException` when running
This usually means the class was not compiled or the classpath is incorrect.

Fix:

```bash
mkdir -p out
find src -name '*.java' -print | xargs javac -d out
```

Then run using the correct package name.

### Issue: `Main` does not run
Check whether the project contains a valid `main` method in the `Main` class.

Example:

```java
public static void main(String[] args) {
    // application start
}
```

---

## 10. Useful Git Commands

To check status:

```bash
git status
```

To pull latest changes:

```bash
git pull origin main
```

To create a new branch:

```bash
git checkout -b feature-name
```

To commit changes:

```bash
git add .
git commit -m "Your message"
```

To push to GitHub:

```bash
git push origin feature-name
```

---

## 11. Quick Start Summary

If you want the shortest setup path:

```bash
git clone https://github.com/abhi15798/MediTrack.git
cd MediTrack
mkdir -p out
find src -name '*.java' -print | xargs javac -d out
java -cp out com.airtribe.meditrack.Main
```

---

## 12. Final Notes

This project is a Java-based clinic management application. Once the project is built and running, you can interact with the application through its console menu and test the service behavior via the manual runner classes.

If you are working in a team or learning Java, it is also a good idea to use IntelliJ IDEA or VS Code with Java support for easier debugging and navigation.

---

## 8. If You Need to Refresh the Build

After changing Java files, recompile the project:

```bash
find src -name '*.java' -print | xargs javac -d out
```

Then rerun the required class.

---

## 9. Common Setup Issues and Fixes

### Issue: `javac: command not found`
This means Java is not installed or not configured in your PATH.

Fix:
- Install JDK
- Add Java to the system PATH
- Restart terminal and verify using:

```bash
java -version
```

### Issue: `ClassNotFoundException` when running
This usually means the class was not compiled or the classpath is incorrect.

Fix:

```bash
mkdir -p out
find src -name '*.java' -print | xargs javac -d out
```

Then run using the correct package name.

### Issue: `Main` does not run
Check whether the project contains a valid `main` method in the `Main` class.

Example:

```java
public static void main(String[] args) {
    // application start
}
```

---

## 10. Useful Git Commands

To check status:

```bash
git status
```

To pull latest changes:

```bash
git pull origin main
```

To create a new branch:

```bash
git checkout -b feature-name
```

To commit changes:

```bash
git add .
git commit -m "Your message"
```

To push to GitHub:

```bash
git push origin feature-name
```

---

## 11. Quick Start Summary

If you want the shortest setup path:

```bash
git clone https://github.com/abhi15798/MediTrack.git
cd MediTrack
mkdir -p out
find src -name '*.java' -print | xargs javac -d out
java -cp out com.airtribe.meditrack.Main
```

---

## 12. Final Notes

This project is a Java-based clinic management application. Once the project is built and running, you can interact with the application through its console menu and test the service behavior via the manual runner classes.

If you are working in a team or learning Java, it is also a good idea to use IntelliJ IDEA or VS Code with Java support for easier debugging and navigation.