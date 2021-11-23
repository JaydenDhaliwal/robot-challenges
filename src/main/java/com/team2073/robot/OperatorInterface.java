package com.team2073.robot;

import com.team2073.common.trigger.ControllerTriggerTrigger;
import com.team2073.robot.commands.HalfPowerCommand;
import com.team2073.robot.commands.LeftStickMove;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.POVButton;


public class OperatorInterface {
    private final ApplicationContext appCtx = ApplicationContext.getInstance();
    private final Joystick controller = appCtx.getController();
    private final JoystickButton a = new JoystickButton(controller, 1);
    private final ControllerTriggerTrigger leftTrigger = new ControllerTriggerTrigger(controller, 1);
    public void init() {
        a.whileHeld(new HalfPowerCommand());


    }
}
