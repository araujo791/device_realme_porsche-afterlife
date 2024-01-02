#
# Copyright (C) 2021-2022 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from those products. Most specific first.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Inherit from porsche device
$(call inherit-product, device/realme/porsche/device.mk)

# Inherit some common stuff.
$(call inherit-product, vendor/afterlife/config/common_full_phone.mk)

#Extras Flags
TARGET_BOOT_ANIMATION_RES := 1080
TARGET_BUILD_APERTURE_CAMERA := false

# Display
TARGET_SCREEN_DENSITY := 480

#Maintainer
AFTERLIFE_MAINTAINER := Covenant_Fuchsia

# Offline Charging
USE_PIXEL_CHARGING := true

# Gapps
AFTERLIFE_GAPPS=true

# disable/enable blur support, default is false
TARGET_SUPPORTS_BLUR := true

# Face Unlock
TARGET_FACE_UNLOCK_SUPPORTED := true

# OTA Assert
TARGET_OTA_ASSERT_DEVICE := porsche,RE58B2L1,RE52BL1

PRODUCT_NAME := afterlife_porsche
PRODUCT_DEVICE := porsche
PRODUCT_MANUFACTURER := realme
PRODUCT_BRAND := realme
PRODUCT_MODEL := RMX3311

PRODUCT_SYSTEM_NAME := RE58B2L1
PRODUCT_SYSTEM_DEVICE := RE58B2L1

PRODUCT_GMS_CLIENTID_BASE := android-realme
TARGET_VENDOR := realme
TARGET_VENDOR_PRODUCT_NAME := RMX3311
PRODUCT_BUILD_PROP_OVERRIDES += PRIVATE_BUILD_DESC=" 13 RMX3311GDPR_11_C.20_2023101316540130 1695796070671 release-keys"
    TARGET_DEVICE=$(PRODUCT_SYSTEM_DEVICE) \
    TARGET_PRODUCT=$(PRODUCT_SYSTEM_NAME)

# Set BUILD_FINGERPRINT variable to be picked up by both system and vendor build.prop
BUILD_FINGERPRINT := realme/RMX3311EEA/RE58B2L1:13/TP1A.220905.001/S.1383667_1_12f:user/release-keys
