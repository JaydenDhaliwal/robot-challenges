package com.team2073.robot.subsystems;

 import com.revrobotics.CANSparkMax;
 import com.revrobotics.CANSparkMaxLowLevel;
 import com.team2073.common.periodic.AsyncPeriodicRunnable;
 import com.team2073.robot.ApplicationContext;
 import com.revrobotics.CANEncoder;
 import com.team2073.common.util.*;
 import edu.wpi.first.wpilibj.Joystick;

public class SimpleSubsystem implements AsyncPeriodicRunnable {
    private final ApplicationContext appCtx = ApplicationContext.getInstance();
    private final CANSparkMax motor = appCtx.getMotor();
    private double originalPosition = 0;
    private SimpleSubsystemState currentState = SimpleSubsystemState.STICK;
    private double output = 0;
    public static double originalOutput = 0;
    public Timer timer = new Timer();
    private final Joystick controller = appCtx.getController();
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
                output += (output * (controller.getRawAxis(3) - controller.getRawAxis(2)));
                break;
            case STICK:
                output = -appCtx.getController().getY();
                output += (output * (controller.getRawAxis(3) - controller.getRawAxis(2)));
                break;
            case PULSE:
                if (timer.getElapsedTime() == 0)
                    timer.start();
                if (((int) timer.getElapsedTime() / 1000) % 2 == 0) {
                    output = 0.25;
                } else {
                    output = 0;
                }
                break;
            case RESET:
                if (!((int) motor.getEncoder().getPosition() < 50 && (int) motor.getEncoder().getPosition() > -50)) {
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
                }
                break;
            case CRUISE:
                if (originalOutput == 0) {
                    originalOutput = output;
                }
                if (originalOutput > 0) {
                    if (-appCtx.getController().getY() > originalOutput) {
                        output = -appCtx.getController().getY();
                    } else {
                        output = originalOutput;
                    }
                } else {
                    if (-appCtx.getController().getY() < originalOutput) {
                        output = -appCtx.getController().getY();
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
                } else {
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

