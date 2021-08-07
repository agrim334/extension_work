for i in `ls actdat`
do
    cp actdat/$i media/
    `python3 tpl_detect.py >> results.txt`
    `echo $i >> results.txt`
    rm media/$i
done 
cat results.txt | grep Response\ time.*  > rt.txt
`python3 RTcal.py`