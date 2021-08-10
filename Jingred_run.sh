javac -cp lib/:src/ src/sarf/jingredients/hash/*.java
javac -cp lib/:src/ src/sarf/jingredients/model/*.java
javac -cp lib/:src/ src/sarf/jingredients/util/*.java
javac -cp lib/:src/ src/sarf/jingredients/*.java
java -cp lib/:src/ sarf.jingredients.CreateValidJarList ../jar2db comp_db 12
java -cp lib/:src/ sarf.jingredients.CreateDB comp_db final_db 12
for i in `ls targets`
do
	python3 RunImpCheck_Jing.py -d targets/${i:0:-4} -f targets/$i 
done