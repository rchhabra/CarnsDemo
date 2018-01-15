#!/bin/sh

path=$1
output=$2

if [ -z "$1" ]; then
	path="xsds"
fi

if [ -z "$2" ]; then
	output="NdcXsdWrapper.xsd"
fi

files='./'$path'/*.xsd'

echo '<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.iata.org/IATA/EDIST" targetNamespace="http://www.iata.org/IATA/EDIST" >'  > $output

for f in $files;
do
	echo '\t<xs:include schemaLocation="'$f'" />' >> $output
done

echo '</xs:schema>' >> $output
