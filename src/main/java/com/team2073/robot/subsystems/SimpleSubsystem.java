package com.team2073.robot.subsystems;

 import com.revrobotics.CANSparkMax;
 import com.revrobotics.CANSparkMaxLowLevel;
 import com.team2073.common.periodic.AsyncPeriodicRunnable;
 import com.team2073.robot.ApplicationContext;
 import java.util.Timer;
 import com.revrobotics.CANEncoder;

public class SimpleSubsystem implements AsyncPeriodicRunnable {
    private final ApplicationContext appCtx = ApplicationContext.getInstance();
    private final CANSparkMax motor = appCtx.getMotor();
    private final CANEncoder position = new CANEncoder(motor);
    private final double originalPosition = position.getPosition();
    private SimpleSubsystemState currentState = SimpleSubsystemState.STICK;
    private double output = 0;
    public SimpleSubsystem() {
        autoRegisterWithPeriodicRunner();
    }
    @Override
    public void onPeriodicAsync() {

        switch(currentState) {
            case STOP:
                output = 0;
                break;
            case HALF_POWER:
                output = 0.5;
                if (appCtx.getController().getRawAxis(2) != 0 || appCtx.getController().getRawAxis(3) != 0) {
                    double totalChange = appCtx.getController().getRawAxis(3) - appCtx.getController().getRawAxis(2);
                    output = (output * totalChange) + output;
                }
                if (output > 0.8) {
                    output = 0.8;
                } else if (output < -0.8) {
                    output = -0.8;
                } else if (output < 0.2 && output > -0.2) {
                    output = 0;
                }
                System.out.println("Printing Works");
                break;
            case STICK:
                if (appCtx.getController().getY() <= 0.8) {
                    if (appCtx.getController().getY() >= 0.2) {
                        output = (appCtx.getController().getY()) * -1;
                    } else if (appCtx.getController().getY() >= -0.2 && appCtx.getController().getY() <= 0.2) {
                        output = 0;
                    } else if (appCtx.getController().getY() >= -0.8) {
                        output = (appCtx.getController().getY()) * -1;
                    } else if (appCtx.getController().getY() < -0.8) {
                        output = 0.8;
                    }
                } else {
                    output = -0.8;
                }
                if (appCtx.getController().getRawAxis(2) != 0 || appCtx.getController().getRawAxis(3) != 0) {
                    double totalChange = appCtx.getController().getRawAxis(3) - appCtx.getController().getRawAxis(2);
                    output = (output * totalChange) + output;
                    if (output > 0.8) {
                        output = 0.8;
                    } else if (output < -0.8) {
                        output = -0.8;
                    } else if (output < 0.2 && output > -0.2) {
                        output = 0;
                    }
                }
                System.out.println(appCtx.getController().getY());
                System.out.println("Finally working.");
                break;
            case PULSE:
                break;
            case RESET:
                //https://www.revrobotics.com/content/sw/max/sw-docs/java/com/revrobotics/CANEncoder.html
                int change = (int)position.getPosition() - (int)originalPosition;
                System.out.print(position.getPosition() + " ");
                System.out.print(originalPosition + " ");
                System.out.println(change);
                if ((int)change > 0) {
                    while ((int)position.getPosition() != (int)originalPosition) {
                        output = -0.1;
                        motor.set(output);
                        position.getPosition();
                        change = (int)position.getPosition() - (int)originalPosition;
                        System.out.print((int)position.getPosition() + " ");
                        System.out.print((int)originalPosition + " ");
                        System.out.println((int)change);
                    }
                    output = 0;
                } else if(change < 0){
                    while ((int)position.getPosition() != (int)originalPosition) {
                        output = 0.1;
                        motor.set(output);
                        position.getPosition();
                        change = (int)position.getPosition() - (int)originalPosition;
                        System.out.print((int)position.getPosition() + " ");
                        System.out.print((int)originalPosition + " ");
                        System.out.println(change);
                    }
                    output = 0;
                } else {
                    output = 0;
                }
                break;
            case CRUISE:
                break;
            case REVOLUTION:
                break;
            default:
                output = 0;
                break;
        }
        motor.set(output);
    }

    public void setCurrentState(SimpleSubsystemState currentState) {
        this.currentState = currentState;
    }
    public enum SimpleSubsystemState {
        STOP,
        HALF_POWER,
        STICK,
        PULSE,
        RESET,
        CRUISE,
        REVOLUTION
    }
}
