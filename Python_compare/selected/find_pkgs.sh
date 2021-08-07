for i in `ls -d */`
do
	for j in `find $i -type f -name "*.py"`
	do
		`echo $j >> totalimports.txt`
		cat $j | grep -E import.* >> totalimports.txt
	done
done