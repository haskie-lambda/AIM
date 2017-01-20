# AIM OS
###Analysis of Interplanetary Magnetic field data; Open Sense- Android App, Java

##About AIM OS
AIM OS is a project for developing an open source, programmable, sensoric input for humans.
For describing best how it works let’s take a look at the example that was the foundation of the project:

GEOS Magnetometer is a satellite orbiting earth and measuring the fluctuations of the interplanetary magnetic field between earth and sun. The measured data are published on a publicly available website in form of a text document which is updated every minute.

The app downloads this text file and extracts the latest record (aka. the latest data coming from the satellite). The AIM app has a, what I call “strength array”. This array is configured by the user in the following way:

e.g. (with temperature data): from 24°C – 26°C = category 1
	from 27°C – 30°C = category 2

The categories along with the scale on the left side make up the strength array. It is used for categorizing the incoming data.
The various categories relate to specific sounds. According to the current value of the analysed data the app sends the corresponding sound to the Bluetooth cuff.

This Bluetooth cuff converts the incoming sounds to magnetic fields. (These magnetic fields are chainging their polarity, depending on the Hertz count of the sound and are therefore feelable with the magnetic implant.)

As this was a bit technical here is another example:
The app can be configured to query your mobile phones’ temperature sensor every second.
You can then configure the strength array so that certain temperatures correspond to certain sounds and therefore to certain strengths of vibration you feel in your finger.

There are also some advanced configuration features for the app:
Option for disabling the analysis temporary when there is no internet connection
Option for disabling the analysis temporary when you get a phone call or play music
Option for converting incoming messages to morse code and parsing it into the bluetooth cuff

*See the project overview for screenshots, etc.*

##History
Originally it was designed for analyising the interplanetary magneticfield fluctuations between sun and earth, obtained from the GEOS-Satellite.
The main purpose of the  app was to categorize the fluctuations in real-time and to generate tone signals that map to the signal-strengths. 
In this context, the signal strengths (integer data from 0 to 4) are converted to the corresponding tones and then sent to the audio Output.

Have fun experimenting.
If you have any questions, want to collaborate etc. don't hesitate to contact me at faebl.taylor@protonmail.com or 
w.fabian.schneider@gmail.com.

Although the Project is licenced under the MIT-Licence, some credit would be nice if you develop it on your own.
If you work on the project please send a pull request so everybody can make use of the improvements.
Soon I will also add the schedules for the wristband and some links for magnetic implants.

Fantastic times ahead of us...


Have fun discovering fascination,
Fabian Schneider

- Enhance your mind: Become a pround transhumanist... -
