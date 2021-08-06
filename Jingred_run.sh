javac -cp lib/:src/ src/sarf/jingredients/hash/*.java
javac -cp lib/:src/ src/sarf/jingredients/model/*.java
javac -cp lib/:src/ src/sarf/jingredients/util/*.java
javac -cp lib/:src/ src/sarf/jingredients/*.java
java -cp lib/:src/ sarf.jingredients.CreateValidJarList components-dir output-jar-file-list.txt 2
java -cp lib/:src/ sarf.jingredients.CreateDB jar-file-list.txt db-name 2
java -cp lib/:src/ sarf.jingredients.Analysis db-name output-file-prefix DEFAULT target.jar
