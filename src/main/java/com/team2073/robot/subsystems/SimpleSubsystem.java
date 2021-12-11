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
    private final SimpleSubsystem simpleSubsystem = new SimpleSubsystem();
    private final double originalPosition = motor.getEncoder().getPosition();
    private SimpleSubsystemState currentState = SimpleSubsystemState.STICK;
    private double output = 0;
    private int count = 0;
    private double originalOutput = simpleSubsystem.CruiseOutput();
    public boolean isFinished = false;
    public SimpleSubsystem() {
        autoRegisterWithPeriodicRunner();
    }
    public double CruiseOutput(){
        if(simpleSubsystem.getCurrentState() == SimpleSubsystemState.CRUISE) {
            return appCtx.getController().getY();
        } else {
            return 0.0;
        }
    }
    @Override
    public void onPeriodicAsync() {
        System.out.println(motor.getEncoder().getPosition());


        switch(currentState) {
            case STOP:
                output = 0;
                break;
            case HALF_POWER:
                output = 0.2;
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
                System.out.println(-1 * appCtx.getController().getY());
                break;
            case PULSE:
                break;
            case RESET:
                int change = (int)motor.getEncoder().getPosition() - (int)originalPosition;
                if (!((int)motor.getEncoder().getPosition() < (int)originalPosition + 15)) {
                    while(!((int)motor.getEncoder().getPosition() < (int)originalPosition + 15)){
                        output = -0.1;
                        count++;
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
                        System.out.println(count);
                        System.out.println("-");
                        motor.set(output);
                    }

                } else if(!((int)motor.getEncoder().getPosition() > (int)originalPosition - 15)){
                    while(!((int)motor.getEncoder().getPosition() > (int)originalPosition - 15)) {
                        output = 0.1;
                        count++;
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
                        System.out.println(count);
                        System.out.println("+");
                        motor.set(output);
                    }
                } else {
                    output = 0;
                    setCurrentState(SimpleSubsystemState.STICK);
                }


                break;
            case CRUISE:
                if(originalOutput > 0) {
                    if ((-1 * appCtx.getController().getY()) > originalOutput) {
                        double yVal = appCtx.getController().getY();
                        while(-1 * appCtx.getController().getY() > originalOutput){
                            output = -1 * yVal;
                            yVal = appCtx.getController().getY();
                        }
                        output = originalOutput;

                    } else {
                        output = originalOutput;
                    }
                } else {
                    if ((-1 * appCtx.getController().getY()) < originalOutput) {
                        double yVal = appCtx.getController().getY();
                        while(-1 * appCtx.getController().getY() < originalOutput){
                            output = -1 * yVal;
                            yVal = appCtx.getController().getY();
                        }
                        while(-1 * appCtx.getController().getY() > originalOutput){
                            output = originalOutput;
                        }
                    } else {
                        output = originalOutput;
                    }
                }
                break;
            case REVOLUTION:
                break;
            default:
                output = 0;
                break;
        }
        if(output >= 0.8){
            output = 0.8;
        } else if(output < 0.2 && output > -0.2){
            output = 0;
        } else if(output <= -0.8){
            output = -0.8;
        }
        motor.set(output);
    }

    public void setCurrentState(SimpleSubsystemState currentState) {
        this.currentState = currentState;
    }

    public SimpleSubsystemState getCurrentState() {
        return this.currentState;
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
