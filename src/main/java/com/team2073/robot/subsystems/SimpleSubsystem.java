package com.team2073.robot.subsystems;

 import com.revrobotics.CANSparkMax;
 import com.revrobotics.CANSparkMaxLowLevel;
 import com.team2073.common.periodic.AsyncPeriodicRunnable;
 import com.team2073.robot.ApplicationContext;
 import com.revrobotics.CANEncoder;
 import com.team2073.common.util.*;

public class SimpleSubsystem implements AsyncPeriodicRunnable {
    private final ApplicationContext appCtx = ApplicationContext.getInstance();
    private final CANSparkMax motor = appCtx.getMotor();
    private double originalPosition = 0;
    private SimpleSubsystemState currentState = SimpleSubsystemState.TEMP;
    private double output = 0;
    public double originalOutput = 0;
    public Timer timer = new Timer();
    public SimpleSubsystem() {
        autoRegisterWithPeriodicRunner();
    }


    @Override
    public void onPeriodicAsync() {
        switch (currentState) {
            case STOP:
                output = 0;
                break;
            case HALF_POWER:
                output = 0.5;
                System.out.println(output);

                /*
                if (appCtx.getController().getRawAxis(2) != 0 || appCtx.getController().getRawAxis(3) != 0) {
                    double totalChange = appCtx.getController().getRawAxis(3) - appCtx.getController().getRawAxis(2);
                    output = (output * totalChange) + output;
                }
                */

                break;
            case STICK:
                output = -appCtx.getController().getY();


                if (appCtx.getController().getRawAxis(2) != 0 || appCtx.getController().getRawAxis(3) != 0) {
                    double totalChange = appCtx.getController().getRawAxis(3) - appCtx.getController().getRawAxis(2);
                    output = (output * totalChange) + output;
                }


                break;
            case PULSE:
                if (timer.getElapsedTime() == 0) {
                    timer.start();
                }
                if (((int) timer.getElapsedTime() / 1000) % 2 == 0) {
                    output = 0.25;
                } else {
                    output = 0;
                }
                break;
            case RESET:
                System.out.println(motor.getEncoder().getPosition());
                if (!((int) motor.getEncoder().getPosition() < 50 && (int) motor.getEncoder().getPosition() > -50)) {
                    System.out.println(motor.getEncoder().getPosition());
                    if ((int) motor.getEncoder().getPosition() > 50) {
                        output = -0.2;
                        if (appCtx.getController().getY() < -0.2 || appCtx.getController().getY() > 0.2) {
                            output = -appCtx.getController().getY();
                        }
                    } else if ((int) motor.getEncoder().getPosition() < -50) {
                        output = 0.2;
                        if (appCtx.getController().getY() < -0.2 || appCtx.getController().getY() > 0.2) {
                            output = -appCtx.getController().getY();
                        }
                    }
                } else {
                    output = 0;
                    System.out.println("Done");
                }
                break;
            case CRUISE:
                if (originalOutput == 0) {
                    originalOutput = output;
                }
                System.out.println("original output: " + originalOutput);
                System.out.println("output " + output);
                System.out.println("appCtx.getController().getY() " + appCtx.getController().getY());
                if (originalOutput > 0) {
                    if (appCtx.getController().getY() > originalOutput) {
                        output = appCtx.getController().getY();
                    } else {
                        output = originalOutput;
                    }
                } else {
                    if (appCtx.getController().getY() < originalOutput) {
                        output = appCtx.getController().getY();
                    } else {
                        output = originalOutput;
                    }
                }
                break;
            case REVOLUTION:
                if (originalPosition == 0) {
                    originalPosition = motor.getEncoder().getPosition();
                }
                if (originalPosition + 3000 > motor.getEncoder().getPosition()) {
                    output = 0.5;
                    System.out.println(motor.getEncoder().getPosition());
                } else {
                    System.out.println("Done");
                    output = 0;
                    break;
                }
                break;
            default:
                output = 0;
                break;
        }
            if (output >= 0.8) {
                output = 0.8;
            } else if (output < 0.2 && output > -0.2) {
                output = 0;
            } else if (output <= -0.8) {
                output = -0.8;
            }
            System.out.println("Right before set: " + output);
            System.out.println("Current State " + currentState);
            motor.set(output);
        }




    public void setCurrentState (SimpleSubsystemState currentState){
        this.currentState = currentState;
    }


    public enum SimpleSubsystemState {
        STOP,
        TEMP,
        HALF_POWER,
        STICK,
        PULSE,
        RESET,
        CRUISE,
        REVOLUTION
    }
}

