package com.team2073.robot.subsystems;

 import com.revrobotics.CANSparkMax;
 import com.revrobotics.CANSparkMaxLowLevel;
 import com.team2073.common.periodic.AsyncPeriodicRunnable;
 import com.team2073.robot.ApplicationContext;

public class SimpleSubsystem implements AsyncPeriodicRunnable {
    private final ApplicationContext appCtx = ApplicationContext.getInstance();
    private final CANSparkMax motor = appCtx.getMotor();

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
                System.out.println("Printing Works");
                break;
            case STICK:
                if(appCtx.getController().getY() <= 0.8) {
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
                if(lt.getRawAxis(2))
                System.out.println(appCtx.getController().getY());
                System.out.println("Finally working.");
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
        STICK
    }
}
