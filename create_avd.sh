#!/bin/bash

avdmanager create avd \
  -name my_emulator \
  -platform android-29 \
  -device Pixel_4 \
  -arch x86_64 \
  -hw.ramSize 4096 \
  -hw.sdCardSize 2048
