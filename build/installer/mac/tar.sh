#!/bin/bash
cd $1

rm Spark.tar
rm Spark.tar.gz

tar -cf Spark.tar Spark.app
gzip Spark.tar

