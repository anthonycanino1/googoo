TARGET = libCPUScaler.so CPUScaler.o arch_spec.o msr.o dvfs.o
CFLAGS = -fPIC -g -c -Wall
JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVA_HOME)/include
JAVA_INCLUDE_LINUX = $(JAVA_INCLUDE)/linux

all: lib_shared_CPUScaler lib_shared_perfChecker 

common:
	echo $(JAVA_HOME)
	gcc $(CFLAGS) -I $(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) CPUScaler.c arch_spec.c msr.c dvfs.c

lib_shared_perfChecker: common
	gcc $(CFLAGS) -I./libpfm/include/ -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_LINUX) perfCheck.c 
	gcc -shared -Wl,-soname,libperfCheck.so -o libperfCheck.so perfCheck.o -lpfm

lib_shared_CPUScaler: common
	gcc -g -Wall -I $(JAVA_INCLUDE) -I $(JAVA_INCLUDE_LINUX) -shared -Wl,-soname,libCPUScaler.so -o libCPUScaler.so CPUScaler.o arch_spec.o msr.o dvfs.o -lc


clean:
	rm -f lib_share_CPUScaler $(TARGET) 
