import os
import argparse
import time

parser = argparse.ArgumentParser(description='Process Py files and get run time.')
parser.add_argument('-f','--file')
args = parser.parse_args()

start = time.process_time()
os.system("pycln -a -d " + args.file + " >> PC_res.txt")
v = str(time.process_time() - start)
os.system("echo " + v + " >> PC_res.txt")

start = time.process_time()
os.system("autoflake --remove-all-unused-imports --recursive " + args.file + " >> AF_res.txt")
v = str(time.process_time() - start)
os.system("echo " + v + " >> AF_res.txt")


#pycln -a -d $j
#autoflake --remove-all-unused-imports --ignore-init-module-imports --recursive $j