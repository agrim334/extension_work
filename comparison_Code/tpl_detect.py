import sys
import constants
import models
import getpass
import argparse
import subprocess
import os
import os.path
import glob
import itertools
import random
import traceback
import gensim
import mysql.connector
import mysql.connector.pooling
from time import localtime, strftime
from gensim.models.doc2vec import Doc2Vec, TaggedDocument
from concurrent.futures import ThreadPoolExecutor,as_completed
from sklearn.metrics.pairwise import cosine_similarity
import threading

MODELS = {"java":Doc2Vec.load("models/java_models/vec_5_ep_10.model")}

cnxpool = mysql.connector.pooling.MySQLConnectionPool(pool_name = "pool",pool_size = 31,pool_reset_session = True,host="localhost",user="root",passwd="55@Dewan",database="tpl_data")


def get_connection():
	db_conn = mysql.connector.connect(host=constants.DB_HOST_ID, database=constants.DB_NAME, user=constants.DB_USER_NAME, password=constants.DB_PASSWORD)
	if db_conn is not None:
		pass
	return db_conn

def obtain_sub_dir_paths(d):
	subdirs = [os.path.join(d, o) for o in os.listdir(d) if os.path.isdir(os.path.join(d,o))]
	return subdirs

def get_file_paths(d):
	path_names = []
	for file_name in os.listdir(d):
		path_name = os.path.join(d, file_name)
		if os.path.isdir(path_name):
			path_names = path_names + get_file_paths(path_name)
		else:
			path_names.append(os.path.join(d, file_name))
	return path_names

def remove_redundant_elems(path_names):
	distinct_paths = []
	distinct_paths = list(set(path_names))
	return distinct_paths

def detect_file_ext(file_path):
	ext = file_path[file_path.rfind(".")+1:]
	return ext

def detect_file_nature(top_cos_sim_list):
	count = 0
	for val in top_cos_sim_list:
		if val < 0:
			count = count + 1
	if count >= int(len(top_cos_sim_list)/4):
		return "likely-to-be-bloat-library"
	else:
		return "likely-to-be-non-bloat-library"

def get_abs_sorted_keys(aMap, val):
	sortedKeys = sorted(aMap, key=lambda dict_key: abs(aMap[dict_key]-val))
	sortedDict = {}
	for key in sortedKeys:
		sortedDict[key] = abs(aMap[key]-val)
	return sortedKeys, sortedDict


def fetch_top_K_match_results(sortedKeys, dataMap):
	simList = []
	count = 1
	for key in sortedKeys:
		count = count + 1
		if count >= 21:
			break
		else:
			simList.append(dataMap[key])
	return simList

def decompile_jar(test_jar_file):
	subprocess.run(["java","-jar", constants.JAR_DECOMPILE_LIB, "-od", constants.JAR_DECOMPILE_DIR, test_jar_file])
	for file_name in os.listdir(constants.JAR_DECOMPILE_DIR):
		path_name = os.path.join(constants.JAR_DECOMPILE_DIR, file_name)
		ext = detect_file_ext(path_name)
		if ext == "jar":
			decomp_dir = path_name[:path_name.rfind(".")]
			os.makedirs(decomp_dir)
			subprocess.run(["java","-jar", constants.JAR_DECOMPILE_LIB, "-od", decomp_dir, path_name])
	return

def obtain_file_vec(file_path, model):
	file_content = read_file_as_text(file_path)
	doc_words = text_preprocess(file_content)
	file_vec = model.infer_vector(doc_words)
	return file_vec

def trim_file_path(file_path):
	temp = file_path[file_path.find("/")+1:]
	new_temp = temp[temp.find("/")+1:]
	return new_temp


def update_dict(cmpl_data_map, sortedDict):
	for key in sortedDict.keys():
		cmpl_data_map[key] = cmpl_data_map[key] + sortedDict[key]
		return cmpl_data_map

def detect_nature():
	nature = "likely-to-be-non-bloat-library"
	test_jar_dir = constants.UPLOAD_JAR_FILE_DIR
	subprocess.run(["rm", "-r", constants.JAR_DECOMPILE_DIR])
	os.makedirs(constants.JAR_DECOMPILE_DIR)
	for file in os.listdir(test_jar_dir):
		test_jar_file = os.path.join(test_jar_dir, file)
		break
	decompile_jar(test_jar_file)
	file_paths = get_file_paths(constants.JAR_DECOMPILE_DIR)
	model = MODELS["java"]
	ref_vec = obtain_file_vec("ref_file.txt", model)
	dataMap = fetch_cos_sim_vals()
	#print(cos_sim_list)
	totl_count = 0
	cmpl_data_map = {}
	for file_path in file_paths:
		print("proceessing file: "+file_path)
		ext = detect_file_ext(file_path)
		if ext == "java":
			totl_count = totl_count + 1
			file_vec = obtain_file_vec(file_path, model)
			cos_sim = compute_cos_sim(file_vec, ref_vec)
			sortedKeys, sortedDict = get_abs_sorted_keys(dataMap, cos_sim)
			if len(cmpl_data_map) == 0:
				cmpl_data_map = sortedDict
			else:
				cmpl_data_map = update_dict(cmpl_data_map, sortedDict)
	print("all files processed")
	#sorted_list = sorted(cmpl_data_map, key=lambda dict_key: cmpl_data_map[dict_key])
	jar_sim_dict = obtain_jar_sim_dict(cmpl_data_map, len(file_paths))
	sorted_list = sorted(jar_sim_dict, key=lambda dict_key: jar_sim_dict[dict_key])
	
	sim_files = []
	sim_scores = []
	count = 0
	test_jar_name = test_jar_file[test_jar_file.find("/")+1:]
	flag = 0
	for key in sorted_list:
		if key == test_jar_name:
			flag = 1
			temp = key 
		val = 1-jar_sim_dict[key]
		if val < 0.30:
			count = count + 1
		sim_files.append(key)
		sim_scores.append(round(val,8))
	if val > int(len(sorted_list)/2):
		nature = "likely-to-be-bloat-library"
	if flag == 1:
		sim_files[0] = temp 
	print(jar_sim_dict)
	print(sorted_list)
	print(test_jar_name)
	return nature, sim_files[:5], sim_scores[:5] #sim_file_dict

def obtain_jar_file_count(jar_file, fileList):
	count = 0
	for file_nm in fileList:
		if file_nm.find(jar_file) > 0:
			count = count + 1
	return count

def obtain_jar_sim_dict(cmpl_data_map, fileCount):
	jar_sim_dict = {}
	fileList = list(cmpl_data_map.keys())
	for key in cmpl_data_map:
		jar_file = extract_jar_file_name(key)
		jar_file_count = obtain_jar_file_count(jar_file, fileList)
		val = float(cmpl_data_map[key])/(fileCount*jar_file_count)
		if jar_file in jar_sim_dict.keys():
			jar_sim_dict[jar_file] = jar_sim_dict[jar_file] + val
		else:
			jar_sim_dict[jar_file] = val
	return jar_sim_dict

 
def update_sim_list(fileName, val, sim_file_list):
	if fileName in sim_file_list.keys():
		if sim_file_list[fileName] < val:
			sim_file_list[fileName] = val
	else:
		sim_file_list[fileName] = val
	return sim_file_list

def extract_jar_file_name(fileName):
	return fileName[fileName.find("java_src/")+9:fileName.find(".jar")+4]  

def read_file_as_text(file_path):
		print("Reading file:" +str(file_path))
		file = open(file_path, 'r')
		file_content = file.read().strip()
		file.close()
		return(file_content)

def text_preprocess(file_content):
	return gensim.utils.simple_preprocess(file_content)

def compute_cos_sim(vec, refVec):
		return float(cosine_similarity([vec], [refVec])[0][0])

def obtain_vector(file_path, model, ref_vec):
	print("reading file:"+file_path)
	file_content = read_file_as_text(file_path)
	doc_words = text_preprocess(file_content)
	vector = model.infer_vector(doc_words)
	cos_sim = compute_cos_sim(vector, ref_vec)
	mydb = cnxpool.get_connection()
	cursor = mydb.cursor(buffered = True)
	query = "insert into vectors values(%s,%s);"
	cursor.execute(query, [file_path, cos_sim])
	mydb.commit()
	print(file_path+": inserted")
	cursor.close()
	mydb.close()
	return

def fetch_cos_sim_vals():
	data = {}
	db_conn = get_connection()
	if db_conn:
		print("connection established")
	cursor = db_conn.cursor(buffered = True)
	query = "select distinct file_path, cos_sim from vectors;"
	cursor.execute(query)
	res = cursor.fetchall()
	for row in res:
		data[row[0]] = row[1]
	cursor.close()
	db_conn.close()
	return data

def vectorize():
	model = MODELS["java"]
	path1 = "/home/ritu/Downloads/mvnCorpus/corpus/test/java_src"
	path2 = "/home/ritu/Downloads/mvnCorpus/corpus/train/java_src"
	subdirs1 = obtain_sub_dir_paths(path1)
	subdirs2 = obtain_sub_dir_paths(path2)
	subdirs = subdirs1 + subdirs2
	cmpl_file_paths = []
	for subdir in subdirs:
		file_paths = get_file_paths(subdir)
		#print(len(file_paths))
		cmpl_file_paths = cmpl_file_paths + file_paths
	distinct_file_paths = remove_redundant_elems(cmpl_file_paths)
	count = 0
	for file_path in distinct_file_paths:
		count = count + 1
		ext = detect_file_ext(file_path)
		if ext == "java":
			print("reading file:"+file_path)
			file_content = read_file_as_text(file_path)
			if count == 1:
				ref_file = open("ref_file.txt", 'w')
				ref_file.write(file_content)
				ref_file.close()
				doc_words = text_preprocess(file_content)
				ref_vec = model.infer_vector(doc_words)
			obtain_vector(file_path, model, ref_vec)
	return
		
if __name__ == "__main__":
	nature, sim_files, sim_scores = detect_nature()
	results = zip(sim_files, sim_scores)
	for f,score in results:
		print(f)
		print(score)