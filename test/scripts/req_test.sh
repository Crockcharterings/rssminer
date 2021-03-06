#! /bin/bash

set -u                          # Treat unset variables as an error
set -e #  Exit immediately if a simple command exits with a non-zero status

# SET it
# ID=
PORT=9090

TERMS=(
    'java' 'ios' "clojure" "谷歌四面树敌" "史诗般的战争"
    "debian" "做正确的加法" "编程" "网页中的平面构成" "企业开发者" "Nosql" "google"
    "2从原理分析PHP性能" "nginx" "排名算法" "电子商务" "software"
    "创新工场" "手机" "周鸿祎" "sql" "企业应用测试平台" "融资" 
)
COUNT=${#TERMS[@]}

URLS=(
    "http://127.0.0.1:$PORT/api/welcome?section=newest&limit=26&offset=0"
    "http://127.0.0.1:$PORT/api/subs"
    "http://127.0.0.1:$PORT/api/welcome?section=recommend&limit=26&offset=0"
    "http://127.0.0.1:$PORT/api/welcome?section=read&limit=26&offset=0"
    "http://127.0.0.1:$PORT/api/welcome?section=voted&limit=26&offset=0"
    "http://127.0.0.1:$PORT/api/subs/61-92-94-100-110-148-170-1271-1371?offset=0&limit=26&sort=recommend"
    "http://127.0.0.1:$PORT/api/subs/148?offset=0&limit=26&sort=recommend"
    "http://127.0.0.1:$PORT/api/subs/148?offset=0&limit=26&sort=newest"
    "http://127.0.0.1:$PORT/api/subs/61-92-94-100-110-148-170-1271-1371?offset=0&limit=26&sort=read"
    "http://127.0.0.1:$PORT/api/subs/30-76-95-97-99-101-113-138-162-217-1852-2393?offset=0&limit=26&sort=oldest"
)
URL_COUNT=${#URLS[@]}

function ab_test() {
    QPS=$(ab -n 5000 -c 50 -C _id_=$ID $1 2>&1 | grep "Requests")
    echo -e $1 ":\t"  $QPS
}

for i in {1..200}; do
    IDX=$(expr $i % $COUNT)
    TERM=${TERMS[$IDX]}
    ab_test "http://127.0.0.1:$PORT/api/search?q=${TERM}&limit=11"
    IDX=$(expr $i % $URL_COUNT)
    URL=${URLS[$IDX]}
    ab_test $URL
    sleep 1
done
