# refine usage 
BEGIN{
FS = ","
OFS = ","
}
NR<2{print $0}
NR>1{
    $4 = ($4 -$6 - $16 - $17 - $19 - $27)
    if($4 < 0 ) $4 =0
    print $0
}
 