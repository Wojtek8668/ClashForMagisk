cmake_minimum_required(VERSION 3.0)

add_custom_target(clash ALL
    COMMAND export "ANDROID_NDK=${ANDROID_NDK}"
    COMMAND bash "${CMAKE_CURRENT_SOURCE_DIR}/build.sh"
    WORKING_DIRECTORY "${PROJECT_BINARY_DIR}"
)
