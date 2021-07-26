import os
import constants

def get_file_paths(directory):
    pathNames = []
    for filename in os.listdir(directory):
        pathName = os.path.join(directory, filename)
        pathNames.append(pathName)
    return pathNames

def readFile(filePath):
    file = open(fname, 'r')
    fileContent = file.read().strip()
    file.close()
    return(fileContent)

def fetch_file_name(filePath):
    fileName = filePath[filePath.rfind("/")+1: filePath.rfind(".")]
    if fileName.find("-") > 0:
       fileName = fileName[:fileName.find("-")]
    if fileName.find("_") > 0:
       fileName = fileName[:fileName.find("_")]
    return fileName

def perform_fileName_existence_check(fileContent, fileName):
    if fileContent.find(fileName) > 0:
       return 1
    else:
       return 0

def perform_text_comp(fileContent, fileName, tool):
    if tool == "LiteRadar":
       perform_fileName_existence_check(fileContent, fileName)
    else:
       ind = fileContent.rfind("Full library matches")
       if ind > 0:
          newFileContent = fileContent[ind:]
          perform_fileName_existence_check(newFileContent, fileName)
    return


def extract_hash_val(fileContent, fileName):
    if fileContent.rfind(fileName) > 0:
       newFileContent = fileContent[fileContent.rfind(fileName):]
       return newFileContent[newFileContent.find(",")+2: newFileContent["}"]] 
    
def perform_hash_comp(hash_vals, hash_val, fileName):
    for key in hash_vals.keys():
        if key == fileName:
           if hash_vals[key] == hash_val
              return 1
           else:
              return 0
        else:
           continue
    return

def compute_acc(count):
    print("Accuracy of various tools:")
    for tool in count.keys():
        accuracy = (count[tool]/constants.TOTAL_LIB_COUNT) * 100
        print(tool+": "+str(accuracy))
    return

def main():
    tools =  ["LibScout", "LibD", "LiteRadar"]
    output_dir_paths = {}
    output_dir_paths["LibScout"] = path_constants.LIB_SCOUT_OUTPUT_DIR
    output_dir_paths["LiteRadar"] = path_constants.LITE_RADAR_OUTPUT_DIR
    output_dir_paths["LibD"] = path_constants.LIB_D_OUTPUT_DIR
    count = {}
    count["LibScout"] = count["LiteRadar"] = count["LibD"] = 0
    hash_vals = {}
    for tool in output_dir_paths.keys():
        output_dir = output_dir_paths[tool]
        filePaths = get_file_paths(output_dir)
        fileName = fetch_file_name(filePath).lower()
        fileContent = readFile(filePath).lower()
        if tool == "LibScout" or tool == "LiteRadar":
           result = perform_text_comp(fileContent, fileName, tool)
           count[tool] = count[tool] + result
        else:
           hash_val = extract_hash_val(fileContent, fileName)
           if hash_val is not None:
              if len(hash_vals) == 0:	#first entry
                 hash_vals[fileName] = hash_val
                 result = 1
              else:
                 result = perform_hash_comp(hash_vals, hash_val, fileName)
           else: 
              result = 0  #no hash found => lib not detected
           count[tool] = count[tool] + result
    compute_acc(count)
    return

if __name__ == "__main__":
    main()
