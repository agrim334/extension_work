import os
import argparse
import time

parser = argparse.ArgumentParser(description='Process Py files and get run time.')
parser.add_argument('-f','--file')
args = parser.parse_args()

os.system("echo " + args.file + " >> Outputs/PC_res.txt")
start = time.process_time()
os.system("pycln -a -d " + args.file + " >> Outputs/PC_res.txt")
v = str(time.process_time() - start)
os.system("echo " + v + " >> Outputs/PC_RT.txt")

os.system("echo " + args.file + " >> Outputs/AF_res.txt")
start = time.process_time()
os.system("autoflake --remove-all-unused-imports --recursive " + args.file + " >> Outputs/AF_res.txt")
v = str(time.process_time() - start)
os.system("echo " + v + " >> Outputs/AF_RT.txt")

#running the tools
#pycln -a -d $j
#autoflake --remove-all-unused-imports --ignore-init-module-imports --recursive $j