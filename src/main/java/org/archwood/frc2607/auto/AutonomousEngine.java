package org.archwood.frc2607.auto;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonomousEngine implements Runnable {

    private int step;
    private int mode;

    private List<AutonomousMode> modes = new ArrayList<AutonomousMode>();

    private class DefaultDoNothing implements AutonomousMode {

        @Override
        public String getName() {
            return "DefaultDoNothing !!";
        }

        @Override
        public void run() {
            return;
        }
    }

    public AutonomousEngine() {
        step = 0;
        mode = 1;
        modes.add(new DefaultDoNothing());
    }

    public void addMode(AutonomousMode m) {
        modes.add(m);
    }

    public void displayActiveMode() {
        SmartDashboard.putNumber("autoMode", mode);
        try {
            SmartDashboard.putString("autonMode", modes.get(mode).getName());
        } catch (Exception e) {
            SmartDashboard.putString("autonMode","UNKNOWN");
        }
    }

    public int getActiveModeNumber() {
        return mode;
    }

    public void saveActiveMode() {
        try {
            PrintWriter p = new PrintWriter(new File("/home/lvuser/autoMode.txt"));
            p.printf("%d", mode);
            p.flush();
            p.close();
        } catch (Exception e) {}
    }

    public void selectActiveMode() {
        if (++mode > (modes.size() - 1)) mode = 1;
        saveActiveMode();
        displayActiveMode();
    }

    public void setActiveMode(int i) {
        mode = i;
        saveActiveMode();
        displayActiveMode();
    }

    public void loadSavedMode() {
        try {
            FileInputStream fin = new FileInputStream("/home/lvuser/autoMode.txt");
            Scanner s = new Scanner(fin);
            if (s.hasNextInt()) mode = s.nextInt();
            else mode = 0;
            fin.close();
        } catch (Exception e) {
            mode = 0;
        }

        displayActiveMode();
    }

    @Override
    public void run() {
        // called by Thread.start();
        System.out.println("Auto Thread Start");
        AutonomousMode m;
        try {
            m = modes.get(mode);
        } catch (Exception e) {
            System.out.println("Invalid auto mode number: " + mode + ", exiting");
            return;
        }

        System.out.println("Running Mode: " + m.getName());
        m.run();
        System.out.println("Exiting Auto Thread");
    }
}