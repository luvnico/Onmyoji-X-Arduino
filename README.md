# Onmyoji-X-Arduino
A hardware helper using **Arduino UNO R3** combined with CLI for the award winning game **[Onmyoji](https://en.onmyojigame.com/)**.

This Java program controls the micro servo on Arduino board via **jSerialComm** library, recognizes the game status as win/lose at the end of each turn and automatically taps screen to continue playing the next turn when you win the previous turn. If the program fails to find a match for win or lose after a certain period, it'll keep listening for another 30 secs and finally terminates if no match found.

You may configure all parameters (turn number, length of period, etc) in ```ThreadEx.java``` for the best result.

[GUI under construction ..]

## Library Dependencies
- OpenCV 3.4.1
- jSerialComm

## Software
Quicktime Player

## Hardware
 - Arduino UNO R3
 - A micro servo (SG 90)
 - A stylus pen
 
 ## OpenCV Installation Guide
 1. Install dependencies (Apache Ant, CMake required)
 2. Download opencv extended modules from official Git repo
 3. Open CMake GUI
     - Set full path to OpenCV source code, e.g. /home/user/opencv
     - Set full path to <cmake_build_dir>, e.g. /home/user/opencv/build
     - Click 'configure' 
     - Find name 'OPENCV_EXTRA_MODULES_PATH'
     - Set value to your extra modules directory (e.g. '/Users/../opencv-3.4.1/extra_modules/modules')
     - Click 'configure' 
     - Click 'generate' 
     - Done
 4. Access the OpenCV-3.4.1 directory in terminal, type commands:
 
     ```
        mkdir build
        cd build
        make -j8 # runs 8 jobs in parallel
        sudo make install
     ```
     
     The installation process could take about 10 ~ 30 mins.
     
