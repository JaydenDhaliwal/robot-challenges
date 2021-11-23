package com.team2073.robot;

import com.team2073.common.trigger.ControllerTriggerTrigger;
import com.team2073.robot.commands.*;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.POVButton;
import java.util.Timer;


public class OperatorInterface {
    private final ApplicationContext appCtx = ApplicationContext.getInstance();
    private final Joystick controller = appCtx.getController();
    private final JoystickButton a = new JoystickButton(controller, 1);
    private final JoystickButton lb = new JoystickButton(controller, 5);
    private Timer timer = new Timer();
    private final JoystickButton x = new JoystickButton(controller, 3);
    private final JoystickButton y = new JoystickButton(controller, 4);
    private final JoystickButton b = new JoystickButton(controller, 2);
    public void init() {
        a.whileHeld(new HalfPowerCommand());
        lb.whileHeld(new PulseCommand());
        x.whenPressed(new ResetCommand());
        y.whenPressed(new CruiseCommand());
        b.whenPressed(new RevolutionCommand());
    }
}
