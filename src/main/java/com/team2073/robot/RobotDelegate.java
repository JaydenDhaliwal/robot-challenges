package com.team2073.robot;

import com.team2073.common.robot.AbstractRobotDelegate;
import com.team2073.robot.subsystems.SimpleSubsystem;

public class RobotDelegate extends AbstractRobotDelegate {
    SimpleSubsystem simpleSubsystem = new SimpleSubsystem();

    public RobotDelegate(double period) {
        super(period);
    }

    @Override
    public void robotInit() {
        OperatorInterface oi = new OperatorInterface();
        oi.init();
        //gonna set the robot encoder to 0 here so that the position is 0
    }

    @Override
    public void robotPeriodic() {

    }
}
