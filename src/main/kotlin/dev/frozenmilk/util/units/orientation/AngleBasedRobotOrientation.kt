package dev.frozenmilk.util.units.orientation

import dev.frozenmilk.util.units.angle.Angle
import dev.frozenmilk.util.units.angle.AngleUnits
import dev.frozenmilk.util.units.angle.Wrapping

enum class Axis {
	X,
	Y,
	Z
}

/**
 * a system of extrinsic robot angles
 *
 * extrinsic means that these angles are measured as rotations about the axes of the field, rather than the axes of the robot, similar to robot-centric vs field centric drive
 *
 * see [rotations using the right hand rule](https://en.wikipedia.org/wiki/Right-hand_rule#Rotations)
 *
 * @property xRot the rotation of the robot about the positive x-axis of the field
 * @property yRot the rotation of the robot about the positive y-axis of the field
 * @property zRot the rotation of the robot about the positive z-axis of the field
 */
class AngleBasedRobotOrientation @JvmOverloads constructor(xRot: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING), yRot: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING), zRot: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) {
	val xRot = xRot.intoWrapping()
	val yRot = yRot.intoWrapping()
	val zRot = zRot.intoWrapping()

	/**
	 * a more programmatic way of getting an angle
	 */
	fun getAxis(axis: Axis): Angle {
		return when (axis) {
			Axis.X -> xRot
			Axis.Y -> yRot
			Axis.Z -> zRot
		}
	}

	/**
	 * non-mutating
	 */
	operator fun plus(other: AngleBasedRobotOrientation): AngleBasedRobotOrientation {
		return AngleBasedRobotOrientation(this.xRot + other.xRot, this.yRot + other.yRot, this.zRot + other.zRot)
	}

	/**
	 * non-mutating
	 */
	operator fun minus(other: AngleBasedRobotOrientation): AngleBasedRobotOrientation {
		return AngleBasedRobotOrientation(this.xRot - other.xRot, this.yRot - other.yRot, this.zRot - other.zRot)
	}

	/**
	 * non-mutating, has no effect
	 */
	operator fun unaryPlus(): AngleBasedRobotOrientation {
		return this
	}

	/**
	 * non-mutating, equal to the inverse rotation about all axis
	 */
	operator fun unaryMinus(): AngleBasedRobotOrientation {
		return AngleBasedRobotOrientation(-this.xRot, -this.yRot, -this.zRot)
	}
}