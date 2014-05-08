FRC Driver Station
=================

Java implementation of the FRC driver station and communication protocol, initially in Java and later in other
languages. Java was chosen because it can be ported easily to other platforms, with Android being the next goal.
The entire project uses the Maven build system for compiling and packaging. To build the entire project from
scratch and create the JAR files, run "mvn clean compile package" from the java/ subdirectory, or from an
individual project directory to compile and package just that project. Everything can also be imported into
Eclipse and compiled from there (with the m2eclipse plugin).

Currently, FRCDS-Java-Comm is the Java library for the communication protocol. It implements classes for encapsulating
data that can be sent from the driver station to the robot (FRCCommonControl), and data that can be sent from the
robot to the driver station (FRCRobotControl) though that protocol hasn't been fully reversed yet. Each of these two
classes contain fields for simple data and other classes for more complicated types of data, some of which are shared
between the two protocols. Each of these classes including the two main classes all extend CommData and all implement
methods for serializing directly to a byte array and deserializing directly from a byte array, as well as automatically
generated equals and hashCode methods. Also included is a class that takes care of all the network communication that
sends and receives data on dedicated threads so the main flow of the program isn't interrupted.

FRC-DriverStation-Common contains a few classes for implementing a driver station on any platform. The two main classes
it contains are an abstract class to encapsulate the driver station and a class that is meant to be run in a
dedicated loop and performs such actions as incrementing the elapsed time counter and sending data packets at a
given frequency (default 50.0Hz).

FRC-DriverStation-PC is the driver station implementation for PC. It uses the classes in Common and builds a GUI
around it. The GUI is powered by Swing, using the Nimbus L&F. It uses the JGoodies FormLayout for laying out certain
GUI components. FormLayout is included in this repository and is automatically used by Maven when building.

FRC-Netconsole-PC contains the GUI-related classes for Netconsole. It generates the standalone netconsole JAR file, and
is used by FRC-DriverStation-PC to provide an embedded Netconsole panel.

FRC-DriverStation-Android is the driver station implementation for Android. It also uses the classes in Common and builds
an Android app around it. The app is targeted for 4.1.2 Jellybean (API 16) and has compliance down to 2.3.3 Gingerbread
(API 9). It uses ActionBarSherlock to achieve a consistent look down to Gingerbread and android-joystick to provide onscreen
joysticks.
