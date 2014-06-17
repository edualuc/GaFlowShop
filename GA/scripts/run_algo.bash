#! /bin/bash

n_run="$1"

for in_file in ../dados/ta*.dat
do
    in_name=${in_file%.dat}
    in_name=${in_name##*/}
    echo $in_name

    for ((i=0; i < $n_run; ++i))
    do
        time java -cp .:../biblio/org.jenetics-2.0.0.jar Tasks < "$in_file"
    done 2>&1 | cat > out_$in_name
done

