for i in `ls -d */`
do
	for j in `find $i -type f -name "*.py"`
	do
		cat $j | grep -E import.* >> totalimports.txt
	done
done