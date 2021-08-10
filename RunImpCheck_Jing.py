import os
import argparse
import time

parser = argparse.ArgumentParser(description='Process Py files and get run time.')
parser.add_argument('-d','--dest')
parser.add_argument('-f','--file')
args = parser.parse_args()
start = time.process_time()
os.system("java -cp src/:lib/ sarf.jingredients.Analysis final_db " + args.dest + " DEFAULT " + args.file)
v = str(time.process_time() - start)
os.system("echo " + v + " >> Jing_res.txt")
