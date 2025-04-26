// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

#include <jni.h>

#include <wpi/jni_util.h>

#include "HALUtil.h"
#include "hal/DutyCycle.h"
#include "org_wpilib_hardware_hal_DutyCycleJNI.h"

using namespace hal;

extern "C" {
/*
 * Class:     org_wpilib_hardware_hal_DutyCycleJNI
 * Method:    initialize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_org_wpilib_hardware_hal_DutyCycleJNI_initialize
  (JNIEnv* env, jclass, jint channel)
{
  int32_t status = 0;
  auto stack = wpi::java::GetJavaStackTrace(env, "org.wpilib");
  auto handle = HAL_InitializeDutyCycle(channel, stack.c_str(), &status);
  CheckStatus(env, status);
  return handle;
}

/*
 * Class:     org_wpilib_hardware_hal_DutyCycleJNI
 * Method:    free
 * Signature: (I)V
 */
JNIEXPORT void JNICALL
Java_org_wpilib_hardware_hal_DutyCycleJNI_free
  (JNIEnv*, jclass, jint handle)
{
  if (handle != HAL_kInvalidHandle) {
    HAL_FreeDutyCycle(static_cast<HAL_DutyCycleHandle>(handle));
  }
}

/*
 * Class:     org_wpilib_hardware_hal_DutyCycleJNI
 * Method:    getFrequency
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_org_wpilib_hardware_hal_DutyCycleJNI_getFrequency
  (JNIEnv* env, jclass, jint handle)
{
  int32_t status = 0;
  auto retVal = HAL_GetDutyCycleFrequency(
      static_cast<HAL_DutyCycleHandle>(handle), &status);
  CheckStatus(env, status);
  return retVal;
}

/*
 * Class:     org_wpilib_hardware_hal_DutyCycleJNI
 * Method:    getOutput
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL
Java_org_wpilib_hardware_hal_DutyCycleJNI_getOutput
  (JNIEnv* env, jclass, jint handle)
{
  int32_t status = 0;
  auto retVal =
      HAL_GetDutyCycleOutput(static_cast<HAL_DutyCycleHandle>(handle), &status);
  CheckStatus(env, status);
  return retVal;
}

/*
 * Class:     org_wpilib_hardware_hal_DutyCycleJNI
 * Method:    getHighTime
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_org_wpilib_hardware_hal_DutyCycleJNI_getHighTime
  (JNIEnv* env, jclass, jint handle)
{
  int32_t status = 0;
  auto retVal = HAL_GetDutyCycleHighTime(
      static_cast<HAL_DutyCycleHandle>(handle), &status);
  CheckStatus(env, status);
  return retVal;
}

/*
 * Class:     org_wpilib_hardware_hal_DutyCycleJNI
 * Method:    getOutputScaleFactor
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_org_wpilib_hardware_hal_DutyCycleJNI_getOutputScaleFactor
  (JNIEnv* env, jclass, jint handle)
{
  int32_t status = 0;
  auto retVal = HAL_GetDutyCycleOutputScaleFactor(
      static_cast<HAL_DutyCycleHandle>(handle), &status);
  CheckStatus(env, status);
  return retVal;
}

/*
 * Class:     org_wpilib_hardware_hal_DutyCycleJNI
 * Method:    getFPGAIndex
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_org_wpilib_hardware_hal_DutyCycleJNI_getFPGAIndex
  (JNIEnv* env, jclass, jint handle)
{
  int32_t status = 0;
  auto retVal = HAL_GetDutyCycleFPGAIndex(
      static_cast<HAL_DutyCycleHandle>(handle), &status);
  CheckStatus(env, status);
  return retVal;
}

}  // extern "C"
