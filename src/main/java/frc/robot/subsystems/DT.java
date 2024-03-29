// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.MutableMeasure;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DutyCycle;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import static edu.wpi.first.units.Units.Volts;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.MutableMeasure;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;
import static edu.wpi.first.units.MutableMeasure.mutable;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
public class DT extends SubsystemBase {
  public DifferentialDrive m_drive;
  private CommandXboxController xb1 = new CommandXboxController(0);
  public WPI_VictorSPX frontRight;
  public WPI_VictorSPX frontLeft;
  public WPI_VictorSPX backRight;
  public WPI_VictorSPX backLeft;
  public WPI_VictorSPX transferMotor;
  // public Encoder leftEncoder; 
  // public Encoder rightEncoder;
  // private final ADXRS450_Gyro m_gyro = new ADXRS450_Gyro();
  // private final DifferentialDriveOdometry m_odometry = new DifferentialDriveOdometry(
  //     m_gyro.getRotation2d(), leftEncoder.getDistance(), rightEncoder.getDistance()
  //   );
      private final MutableMeasure<Voltage> m_appliedVoltage = mutable(Volts.of(0));
  // Mutable holder for unit-safe linear distance values, persisted to avoid reallocation.
  private final MutableMeasure<Distance> m_distance = mutable(Meters.of(0));
  // Mutable holder for unit-safe linear velocity values, persisted to avoid reallocation.
  private final MutableMeasure<Velocity<Distance>> m_velocity = mutable(MetersPerSecond.of(0));
  /* Creates a new Motor Object */
  /** Creates a new Drivetrain. (Note the args passed through are getting the axis of the controller joystick) */
  
  public DT() {
    // Initializes all the motors. 
    backRight = new WPI_VictorSPX(7);
    frontLeft = new WPI_VictorSPX(1);
    frontRight = new WPI_VictorSPX(2);
    backLeft = new WPI_VictorSPX(3);
    // leftEncoder = new Encoder(OperatorConstants.leftEncoder[1], OperatorConstants.leftEncoder[2]);
    // rightEncoder = new Encoder(OperatorConstants.rightEncoder[1], OperatorConstants.rightEncoder[2]);
    
    // Initializes the drive train as a new instance of the DifferentialDrive class
    m_drive = new DifferentialDrive(
      (double output) -> {
        frontLeft.set(output);
        backLeft.set(output);
    },
    (double output) -> {
        frontRight.set(output);
        backRight.set(output);
    }
    );
  }
  // private final SysIdRoutine m_sysIdRoutine =
  //     new SysIdRoutine(
  //         // Empty config defaults to 1 volt/second ramp rate and 7 volt step voltage.
  //         new SysIdRoutine.Config(),
  //         new SysIdRoutine.Mechanism(
  //             // Tell SysId how to plumb the driving voltage to the motors.
  //             (Measure<Voltage> volts) -> {
  //               frontRight.setVoltage(volts.in(Volts));
  //               frontLeft.setVoltage(volts.in(Volts));
  //               backRight.setVoltage(volts.in(Volts));
  //               backLeft.setVoltage(volts.in(Volts));
  //             },
  //             // Tell SysId how to record a frame of data for each motor on the mechanism being
  //             // characterized.
  //             log -> {
  //               // Record a frame for the left motors.  Since these share an encoder, we consider
  //               // the entire group to be one motor.
  //               log.motor("drive-left(Front)")
  //                   .voltage(
  //                       m_appliedVoltage.mut_replace(
  //                           frontLeft.get() * RobotController.getBatteryVoltage(), Volts))
  //                   .linearPosition(m_distance.mut_replace(leftEncoder.getDistance(), Meters))
  //                   .linearVelocity(
  //                       m_velocity.mut_replace(leftEncoder.getRate(), MetersPerSecond));
  //               // Record a frame for the right motors.  Since these share an encoder, we consider
  //               // the entire group to be one motor.
  //                log.motor("drive-left(Back)")
  //                   .voltage(
  //                       m_appliedVoltage.mut_replace(
  //                           backLeft.get() * RobotController.getBatteryVoltage(), Volts))
  //                   .linearPosition(m_distance.mut_replace(leftEncoder.getDistance(), Meters))
  //                   .linearVelocity(
  //                       m_velocity.mut_replace(leftEncoder.getRate(), MetersPerSecond));
  //               log.motor("drive-right(Right)")
  //                   .voltage(
  //                       m_appliedVoltage.mut_replace(
  //                           frontRight.get() * RobotController.getBatteryVoltage(), Volts))
  //                   .linearPosition(m_distance.mut_replace(rightEncoder.getDistance(), Meters))
  //                   .linearVelocity(
  //                       m_velocity.mut_replace(rightEncoder.getRate(), MetersPerSecond));
  //              log.motor("drive-right(Back)")
  //                   .voltage(
  //                       m_appliedVoltage.mut_replace(
  //                           backRight.get() * RobotController.getBatteryVoltage(), Volts))
  //                   .linearPosition(m_distance.mut_replace(rightEncoder.getDistance(), Meters))
  //                   .linearVelocity(
  //                       m_velocity.mut_replace(rightEncoder.getRate(), MetersPerSecond));
  //             },
  //             // Tell SysId to make generated commands require this subsystem, suffix test state in
  //             // WPILog with this subsystem's name ("drive")
  //             this));
  // public Pose2d getPos(){
  //   return m_odometry.getPoseMeters();

  // }
  // public Command sysIdQuasistatic(SysIdRoutine.Direction direction) {
  //   return m_sysIdRoutine.quasistatic(direction);
  // }
  // public Command sysIdDynamic(SysIdRoutine.Direction direction) {
  //   return m_sysIdRoutine.dynamic(direction);
  // }
  // public DifferentialDriveWheelSpeeds getWheelSpeeds() {
  //   return new DifferentialDriveWheelSpeeds(leftEncoder.getRate(), rightEncoder.getRate());
  // }
  // public double getAverageEncoderDistance() {
  //   return (leftEncoder.getDistance() + rightEncoder.getDistance()) / 2.0;
  // }
  // public Command arcadeDriveCommand(double a, double b) {
  //   // A split-stick arcade command, with forward/backward controlled by the left
  //   // hand, and turning controlled by the right.
  //   return run(() -> m_drive.arcadeDrive(a, b))
  //       .withName("arcadeDrive");
  // }
  public Command driveForward(){
    return run(() -> {
     m_drive.arcadeDrive(1, 0);
    }).withTimeout(.77);
  }
  public Command driveIndefinitely(){
    return run(() -> {
     m_drive.arcadeDrive(1, 0);
    });
  }
  public Command stop(){
    return run(() -> {
     m_drive.tankDrive(0, 0);
    }).withTimeout(.5);
  }
  public Command driveBackwards(){
    return run(() -> {
       m_drive.arcadeDrive(-1, 0);
    }).withTimeout(.8);
  } 
  public Command waitUntil(){
    return run(() -> {
      
    }).withTimeout(.75);
  } 
  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    // Using the tankDrive (or arcadeDrive) method of the driveTrain class and flipping the right side inputs to fit driver's tastes and have both sides move the same way
    //driveTrain.tankDrive(xb1.getRightY() * -1, xb1.getLeftY());
    m_drive.arcadeDrive(xb1.getLeftY(), (xb1.getRightX()));
    // m_odometry.update(m_gyro.getRotation2d(), leftEncoder.getDistance(), rightEncoder.getDistance());
  }
}