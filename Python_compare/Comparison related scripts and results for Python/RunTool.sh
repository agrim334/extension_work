for i in `ls -d */`
do
	for j in `find $i -type f -name "*.py"`
	do
		`echo $j >> Outputs/totalimports.txt`
		cat $j | grep -E import.* >> Outputs/totalimports.txt
		python3 RunImpCheck.py -f $j
	done
done