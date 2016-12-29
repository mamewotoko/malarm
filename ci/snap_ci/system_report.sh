#! /bin/sh
uname -a

ls /dev/kvm
kvm-ok

cat /proc/cpuinfo
cat /proc/meminfo
