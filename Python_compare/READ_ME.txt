1.) Requirements
	Python3  (as of latest, python 3.8.10 was used to run experiments)
	PyCln (available on pip)
	Autoflake (available on pip)
	Linux based OS preferably Ubuntu ( to be able to use bash scripts and commands like ls, grep, find)

	All python files should be stored in a folder named Input_Data as the script searches through that folder to get .py files
	All outputs are recorded in Outputs folder

	RunTool.sh and RunImpCheck.py must be placed along the above folders in the same directory
	No such imposition on Calculate_Average_Response_Time.py

2.) Procedure for running this tool
	a) Execute RunTool.sh using by running 
			./RunTool.sh 
	   or
			bash RunTool.sh 

	(Ensure directory structure mentioned in 1) is followed)

	   Outputs will appear as follows:
	   		PC_res.txt ----- contains the output of pycln tool
	   		PC_RT.txt ----- contains response times for each program PyCln runs on
	   		AF_res.txt ----- contains the output of pycln tool
	   		AF_RT.txt ----- contains response times for each program PyCln runs on
	   		totalimports.txt ----- consists of all import statements in a .py file

	b) To get total number of imports, must manually check through totalimports.txt as there may be multiple imports on a single line.

	c) To get average response time, execute Calculate_Average_Response_Time.py by running 

		python3 Calculate_Average_Response_Time.py

	on a terminal and enter the locations of PC_RT.txt and AF_RT.txt.
	The program will output the number of instances (the .py files processed in a) )
	and the average Response Time.

	d) To compute whether the tools have correctly flagged imports or not, one will have to go through each .py file manually to confirm whether imports have been correctly flagged or not. 