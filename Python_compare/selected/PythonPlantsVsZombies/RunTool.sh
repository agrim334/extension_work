for i in `ls -d */`
do
	for j in `find $i -type f -name "*.py"`
	do
		subl $j
#		`echo $j >> PC_res.txt`
#		`echo $j >> AF_res.txt` 
#		python3 RunImpCheck.py -f $j
	done
done