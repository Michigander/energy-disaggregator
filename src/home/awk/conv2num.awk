BEGIN{
FS=","
OFS = ","
}

NR<2{
print $0
}

NR>1{
    $2 = $2 + 0
    for( i = 4; i<NF+1; i++ ){
	$i = $i + 0 
    }

print $0
}