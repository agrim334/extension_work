for i in `ls -d */`
do
	for j in `find $i -type f -name "*.py"`
	do
		`echo $j >> PC_res.txt && (time pycln -a -d $j) &>> PC_res.txt`
		`echo $j >> AF_res.txt && (time autoflake --remove-all-unused-imports --ignore-init-module-imports --recursive $j) &>> AF_res.txt`
	done
done