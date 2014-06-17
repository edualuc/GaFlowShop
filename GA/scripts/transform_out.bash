#! /bin/bash

for file in out_*
do
    out_file="${file#out_}.out"
    temp_res="temp_res_${file}"
    temp_time="temp_time_${file}"

    sed -ne "s/^\[.\+\] --> \([0-9]\+\)$/\1/p" "$file" \
        | sort | head -n 1 \
        > "$temp_res"

    sed -ne "s/^real.\+m\([0-9]\)\.\([0-9]\+\)s$/\1.\2/p" "$file" \
        | awk '{sum+=$1};END{print sum/NR}' \
        > "$temp_time"

    (echo 'result'
    cat "$temp_res"
    echo 'time'
    cat "$temp_time"
    ) > "$out_file"

    rm "$temp_time" "$temp_res"
done

