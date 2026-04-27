cmake_minimum_required(VERSION 3.22.1)

project("azoovip")

add_library(
        azoovip
        SHARED
        bypass_engine.cpp
        scanner.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        azoovip
        ${log-lib})
