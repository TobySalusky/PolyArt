package util

import kotlin.math.*

/**
 * Math Functions
 */
object Maths {

    const val PI = Math.PI.toFloat()
    const val TwoPI = PI * 2
    const val HalfPI = PI / 2

    @JvmStatic
    fun chance(outOf : Float): Boolean {
        return random() * outOf < 1F
    }

    @JvmStatic
    fun cos(a : Float): Float {
        return kotlin.math.cos(a)
    }

    @JvmStatic
    fun sin(a : Float): Float {
        return kotlin.math.sin(a)
    }

    @JvmStatic
    fun randomPN(): Float {

        return random(-1F, 1F)
    }

    @JvmStatic
    fun randomPN(max : Float): Float {

        return random(-1F, 1F) * max
    }

    @JvmStatic
    fun random(): Float {

        return Math.random().toFloat()
    }

    @JvmStatic
    fun randomAngle(): Float {

        return Math.random().toFloat() * PI * 2F
    }

    @JvmStatic
    fun correctAngle(angle: Float, toAngle: Float): Float {

        if (abs(toAngle - (angle - PI * 2)) < abs(toAngle - angle)) {
            return angle - PI * 2
        } else if (abs(toAngle - (angle + PI * 2)) < abs(toAngle - angle)) {
            return angle + PI * 2
        }
        return angle
    }

    @JvmStatic
    fun flipAngleDegrees(angle: Float, facingLeft: Boolean): Float {

        if (facingLeft) {
            return angle
        }
        return 180f - (angle - 180f)
    }

    @JvmStatic
    fun flipAngleRadians(angle: Float, facingLeft: Boolean): Float {

        if (facingLeft) {
            return angle
        }
        return Math.PI.toFloat() - (angle - Math.PI.toFloat())
    }

    @JvmStatic
    fun randomInt(min: Int, max: Int): Int {
        return min + (Math.random() * (max - min + 1)).toInt()
    }

    @JvmStatic
    fun randomInt(max: Int): Int {
        return (Math.random() * max).toInt()
    }

    @JvmStatic
    fun random(max: Float): Float {
        return random(0F, max)
    }

    @JvmStatic
    fun random(min: Float, max: Float): Float {
        return min + (Math.random() * (max - min)).toFloat()
    }

    @JvmStatic
    fun approach(value: Float, to: Float, div: Float): Float {
        return value + (to - value) / div
    }

    @JvmStatic
    fun clamp(value: Float, min: Float, max: Float): Float {
        return min(max(value, min), max)
    }

    @JvmStatic
    fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((x2 - x1.toDouble()).pow(2.0) + (y2 - y1.toDouble()).pow(2.0)).toFloat()
    }

    @JvmStatic
    fun distance(x: Float, y: Float): Float {
        return sqrt(x.toDouble().pow(2.0) + y.toDouble().pow(2.0)).toFloat()
    }

    @JvmStatic
    fun findAngle(x: Float, y: Float): Float {
        return atan2(y.toDouble(), x.toDouble()).toFloat()
    }

    @JvmStatic
    fun findAngle(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return atan2(y2 - y1.toDouble(), x2 - x1.toDouble()).toFloat()
    }
}
