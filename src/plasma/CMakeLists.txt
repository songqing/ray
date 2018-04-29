cmake_minimum_required(VERSION 3.4)

project(plasma)

# Recursively include common
include(${CMAKE_CURRENT_LIST_DIR}/../common/cmake/Common.cmake)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} --std=c99 -O3")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} --std=c++11 -O3 -Werror -Wall")

if(UNIX AND NOT APPLE)
  link_libraries(rt)
endif()

include_directories("${ARROW_DIR}/cpp/src/")
# include_directories("${CMAKE_CURRENT_LIST_DIR}/../")

include_directories("${FLATBUFFERS_INCLUDE_DIR}")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC")

add_executable(plasma_manager
  plasma_manager.cc)

target_link_libraries(plasma_manager common ${PLASMA_STATIC_LIB} ray_static ${ARROW_STATIC_LIB} -lpthread ${Boost_SYSTEM_LIBRARY})

define_test(client_tests "")
define_test(manager_tests "" plasma_manager.cc)
target_link_libraries(manager_tests ${Boost_SYSTEM_LIBRARY})

# Compile flatbuffers

set(PLASMA_FBS_SRC "${CMAKE_CURRENT_LIST_DIR}/format/plasma.fbs")
set(OUTPUT_DIR ${CMAKE_CURRENT_LIST_DIR}/format/)

set(PLASMA_FBS_OUTPUT_FILES
  "${OUTPUT_DIR}/plasma_generated.h")

add_custom_command(
  OUTPUT ${PLASMA_FBS_OUTPUT_FILES}
  COMMAND ${FLATBUFFERS_COMPILER} -c -o ${OUTPUT_DIR} ${PLASMA_FBS_SRC}
  DEPENDS ${PLASMA_FBS_SRC}
  COMMENT "Running flatc compiler on ${PLASMA_FBS_SRC}"
  VERBATIM)

add_custom_target(gen_plasma_fbs DEPENDS ${PLASMA_FBS_OUTPUT_FILES})

add_dependencies(gen_plasma_fbs flatbuffers_ep)

if ("${CMAKE_RAY_LANG_PYTHON}" STREQUAL "YES")
  add_compile_options("-I${PYTHON_INCLUDE_DIRS}")

  include_directories("${CMAKE_CURRENT_LIST_DIR}/lib/python")

  file(GLOB PLASMA_LIBRARY_EXT_python_SRC
    lib/python/*.cc lib/*.cc)
  add_library(plasma_python SHARED
    ${PLASMA_LIBRARY_EXT_python_SRC}
    )

  if(APPLE)
    target_link_libraries(plasma_python common ${PLASMA_STATIC_LIB} ray_static ${ARROW_STATIC_LIB} "-undefined dynamic_lookup" -Wl,-force_load,${FLATBUFFERS_STATIC_LIB} ${PYTHON_LIBRARIES} ${FLATBUFFERS_STATIC_LIB} -lpthread)
  else(APPLE)
    target_link_libraries(plasma_python common ${PLASMA_STATIC_LIB} ray_static ${ARROW_STATIC_LIB} -Wl,--whole-archive ${FLATBUFFERS_STATIC_LIB} -Wl,--no-whole-archive ${PYTHON_LIBRARIES} ${FLATBUFFERS_STATIC_LIB} -lpthread)
  endif(APPLE)
endif()

if ("${CMAKE_RAY_LANG_JAVA}" STREQUAL "YES")
  add_compile_options("-I$ENV{JAVA_HOME}/include/")
  if(WIN32)
    add_compile_options("-I$ENV{JAVA_HOME}/include/win32")
  elseif(APPLE)
    add_compile_options("-I$ENV{JAVA_HOME}/include/darwin")
  else() # linux
    add_compile_options("-I$ENV{JAVA_HOME}/include/linux")
  endif()

  include_directories("${CMAKE_CURRENT_LIST_DIR}/lib/java")

  file(GLOB PLASMA_LIBRARY_EXT_java_SRC
    lib/java/*.cc lib/*.cc)
  add_library(plasma_java SHARED
    ${PLASMA_LIBRARY_EXT_java_SRC}
    )

  if(APPLE)
    target_link_libraries(plasma_java common ${PLASMA_STATIC_LIB} ray_static ${ARROW_STATIC_LIB} "-undefined dynamic_lookup" -Wl,-force_load,${FLATBUFFERS_STATIC_LIB} ${FLATBUFFERS_STATIC_LIB} -lpthread)
  else(APPLE)
    target_link_libraries(plasma_java common ${PLASMA_STATIC_LIB} ray_static ${ARROW_STATIC_LIB} -Wl,--whole-archive ${FLATBUFFERS_STATIC_LIB} -Wl,--no-whole-archive ${FLATBUFFERS_STATIC_LIB} -lpthread)
  endif(APPLE)
endif()

if (NOT EXISTS ${CMAKE_BINARY_DIR}/src/plasma/plasma_store)
  add_custom_target(copy_store ALL)
  foreach(file "plasma_store")
    add_custom_command(TARGET copy_store POST_BUILD
                       COMMAND ${CMAKE_COMMAND} -E
                          copy ${CMAKE_CURRENT_LIST_DIR}/../../thirdparty/pkg/arrow/cpp/build/cpp-install/bin/${file}
                               ${CMAKE_CURRENT_BINARY_DIR}/${file})
  endforeach()
endif()
